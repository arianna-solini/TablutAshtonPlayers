package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.framework.Metrics;

import domain.Action;
import domain.State;
import domain.TablutGame;
import domain.State.Turn;

/**
 * Alternative implementation of IterativeDeepeningAlphaBetaSearch of aima library<p>
 * Original class at: <p>
 * {@linkplain https://github.com/aimacode/aima-java/blob/AIMA3e/aima-core/src/main/java/aima/core/search/adversarial/IterativeDeepeningAlphaBetaSearch.java}
 * <p>
 * Implements an iterative deepening Minmax search with alpha-beta pruning, thread pool and
 * action ordering. Maximal computation time is specified in seconds.
 */
public class TimeLimitedSearch implements AdversarialSearch<State, Action> {

	public final static String METRICS_NODES_EXPANDED = "nodesExpanded";
	public final static String METRICS_MAX_DEPTH = "maxDepth";

	protected TablutGame game;
	protected double utilMax;
	protected double utilMin;
	private Timer timer;

	private Metrics metrics = new Metrics();

	public int numCuts;
	private static int numberOfThread = 8;
	protected int K = numberOfThread;
	protected int[] currDepthLimit = new int[K];
	protected boolean[] heuristicEvaluationUsed = new boolean[K];

	/**
	 * Creates a new search object for a given game.
	 *
	 * @param game    The game.
	 * @param utilMin Utility value of worst state for this player. Supports
	 *                evaluation of non-terminal states and early termination in
	 *                situations with a safe winner.
	 * @param utilMax Utility value of best state for this player. Supports
	 *                evaluation of non-terminal states and early termination in
	 *                situations with a safe winner.
	 * @param time    Maximal computation time in seconds.
	 */
	public TimeLimitedSearch(TablutGame game, double utilMin, double utilMax, int time) {
		this.game = game;
		this.utilMin = utilMin;
		this.utilMax = utilMax;
		this.timer = new Timer(time);
	}

	/**
	 * Implementation of the interface Callable for multithreading
	 */

	private class SearchCallable implements Callable<Action> {
		private int num;
		private State state;
		private String player;
		private List<Action> tempA;

		/**
		 * Create a new Callable object
		 * 
		 * @param num		Index of the callableTask
		 * @param state		state of the game
		 * @param player 	player type (white or black)
		 * @param tempA	 	sub-list of the action list at depth 0
		 */

		public SearchCallable(int num, State state, String player, List<Action> tempA) {
			this.num = num;
			this.state = state;
			this.player = player;
			this.tempA = tempA;
		}

		@Override
		public Action call() {
			
			do {
				currDepthLimit[num]++;

				heuristicEvaluationUsed[num] = false;

				ActionStore<Action> lastValuedActions = new ActionStore<Action>();

				for (Action action : tempA) {
					// minValue calculus are based on the simulated action's state obtained by game.getResult
					double value = minValue(game.getResult(state, action), player, Double.NEGATIVE_INFINITY, 
											Double.POSITIVE_INFINITY, 1, num);

					if (timer.timeOutOccurred())
						break; // exit from action loop

					lastValuedActions.add(action, value);
				}

				if(lastValuedActions.size() > 0){
					lastValuedActions.actions.get(0).setScore(lastValuedActions.utilValues.get(0));
					tempA = lastValuedActions.actions;
					if (!timer.timeOutOccurred()) {
						if (hasSafeWinner(tempA.get(0).getScore()))
							break;
						else if (lastValuedActions.size() > 1 && isSignificantlyBetter(tempA.get(0).getScore(), lastValuedActions.utilValues.get(1)))
							break;
					}
				}
				
			} while (!timer.timeOutOccurred() && heuristicEvaluationUsed[num]);
			System.out.println("Thread " + num + " ha score migliore di: " + tempA.get(0).getScore() + " con mossa " + tempA.get(0).toString());
			return tempA.get(0);
		}// call

	}

	/**
	 * It is based on iterative deepening and tries to make to a good decision in
	 * limited time. Credit goes to Behi Monsio who had the idea of ordering actions
	 * by utility in subsequent depth-limited search runs.
	 */

	@Override
	public Action makeDecision(State state) {
		numCuts = 0;
		ArrayList<List<Action>> temp = new ArrayList<List<Action>>(K);
		metrics = new Metrics();
		String player = game.getPlayer(state);
		List<Action> results = game.getActions(state);
		timer.start();

		for (int i = 0; i < K; i++) {
			currDepthLimit[i] = 0;
		}

		/*
		 * The action list at depth 0 is organized in K sub-lists where 
		 * K is the number of thread executed at the same time.
		 */

		int part = results.size() / K;

		if (results.size() >= K) {
			for (int i = 0; i < K; i++) {
				temp.add(i, results.subList(part * i, part * (i + 1) ));
				if (i == K - 1)
					temp.add(i, results.subList(part * i, results.size()));
			}

		} else {
			K--;
			while (K > 0) {
				if ((part = results.size() / K) > 0) {
					for (int i = 0; i < K; i++) {
						temp.add(i, results.subList(i, i+1));
					}
					break;
				} else {
					K--;
				}
			} // while
		}

		/**
		 * Each thread analyzes the tree of its sub-list until timeout is reached 
		 * then it returns the action with the higher score 
		 */

		ArrayList<Callable<Action>> callableTasks = new ArrayList<Callable<Action>>(K);
		for (int i = 0; i < K; i++) {
			callableTasks.add(new SearchCallable(i, state, player, temp.get(i)));
		}

		List<Future<Action>> futures = new ArrayList<Future<Action>>();

		ExecutorService exec = Executors.newFixedThreadPool(K);
		try {
			try {
				futures = exec.invokeAll(callableTasks);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			exec.shutdown();
		}

		/**
		 * makeDecision chooses, among eight actions, the one with the higher score.
		 */
		int maxi = -1;
		double max = Double.NEGATIVE_INFINITY;
		Action result = null;
		for (int i = 0; i < K; i++) {
			double futureScore = Double.NEGATIVE_INFINITY;
			Action futureAction = null;
			try {
				futureAction = futures.get(i).get();
				futureScore = futureAction.getScore();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			if (Double.compare(futureScore, TablutGame.maxValue) == 0) {
				State s = game.getResult(state, futureAction);
				if (player.equals("W") && s.getTurn() == Turn.WHITEWIN) {
					System.out.println("HAI VINTO!");
					return futureAction;

				} else if (player.equals("B") && s.getTurn() == Turn.BLACKWIN) {
					System.out.println("HAI VINTO!");
					return futureAction;
				}
			}

			if (futureScore > max) {
				max = futureScore;
				maxi = i;
			}
		}//for

		try {
			result = futures.get(maxi).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("Tagli effettuati: " + numCuts);
		//Reset K to numberOfThread
		K = numberOfThread;
		return result;
	}

	public double maxValue(State state, String player, double alpha, double beta, int depth, int numThread) {
		updateMetrics(depth);
		if (game.isTerminal(state) || depth >= currDepthLimit[numThread] || timer.timeOutOccurred()) {
			return -eval(state, getOtherPlayer(player), numThread);
		} else {
			double value = Double.NEGATIVE_INFINITY;
			//Current actions are calculated from the passed simulation state
			for (Action action : game.getActions(state)) {
				//Same as in makeDecision method's minValue
				value = Math.max(value, minValue(game.getResult(state, action), player, alpha, beta, depth + 1, numThread));
				if (value >= beta){
					numCuts++;
					return value;
				}
				alpha = Math.max(alpha, value);
			}
			return value;
		}
	}

	public double minValue(State state, String player, double alpha, double beta, int depth, int numThread) {
		updateMetrics(depth);
		if (game.isTerminal(state) || depth >= currDepthLimit[numThread] || timer.timeOutOccurred()) {
			return eval(state, player, numThread);
		} else {
			double value = Double.POSITIVE_INFINITY;
			//Current actions are calculated from the passed simulation state
			for (Action action : game.getActions(state)) {
				//Same as in makeDecision method's minValue
				value = Math.min(value, maxValue(game.getResult(state, action), player, alpha, beta, depth + 1, numThread));
				if (value <= alpha){
					numCuts++;
					return value;
				}
				beta = Math.min(beta, value);
			}
			return value;
		}
	}

	private void updateMetrics(int depth) {
		metrics.incrementInt(METRICS_NODES_EXPANDED);
		metrics.set(METRICS_MAX_DEPTH, Math.max(metrics.getInt(METRICS_MAX_DEPTH), depth));
	}

	/**
	 * @return some statistic data from the last search.
	 */
	@Override
	public Metrics getMetrics() {
		return metrics;
	}

	/**
	 * Is used to stop iterative deepening search in situations where a clear best action exists. 
	 */
	protected boolean isSignificantlyBetter(double newUtility, double utility) {
		//return newUtility - utility > 10 ? true : false;
		return false;
		
	}

	/**
	 * Is used to stop iterative deepening search in situations where a safe winner has been identified.
	 * <p>This implementation returns true if the given value (for the currently preferred action result) 
	 * is the highest or lowest utility value possible.
	 */
	protected boolean hasSafeWinner(double resultUtility) {
		return resultUtility <= utilMin || resultUtility >= utilMax;
	}

	/**
	 * Estimates the value for (not necessarily terminal) states.
	 */
	protected double eval(State state, String player, int numThread) {
		if (game.isTerminal(state)) {
			return game.getUtility(state, player);
		} else {
			heuristicEvaluationUsed[numThread] = true;
			return Score.calculateScore(game, state, player);
		}
	}

	public String getOtherPlayer(String player){
		if(player.equals("W"))
			return "B";
		else 
			return "W";
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	//Nested helper classes

	private static class Timer {
		private long duration;
		private long startTime;

		Timer(int maxSeconds) {
			this.duration = 1000 * maxSeconds;
		}

		void start() {
			startTime = System.currentTimeMillis();
		}

		boolean timeOutOccurred() {
			return System.currentTimeMillis() > startTime + duration;
		}
	}

	/**
	 * Orders actions by utility.
	 */
	public static class ActionStore<A> {
		public List<A> actions = new ArrayList<>();
		public List<Double> utilValues = new ArrayList<>();

		void add(A action, double utilValue) {
			int idx = 0;
			//Puts at the bottom of the list actions with lower values
			while (idx < actions.size() && utilValue <= utilValues.get(idx))
				idx++;
			actions.add(idx, action);
			utilValues.add(idx, utilValue);
		}

		int size() {
			return actions.size();
		}
	}
}

