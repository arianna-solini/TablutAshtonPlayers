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
 * Alternative implementation of IterativeDeepeningAlphaBetaSearch of aima
 * library
 * <p>
 * Implements an iterative deepening Minimax search with alpha-beta pruning and
 * action ordering. Maximal computation time is specified in seconds.
 */
public class TimeLimitedSearch implements AdversarialSearch<State, Action> {

	public final static String METRICS_NODES_EXPANDED = "nodesExpanded";
	public final static String METRICS_MAX_DEPTH = "maxDepth";

	protected TablutGame game;
	protected double utilMax;
	protected double utilMin;
	private Timer timer;
	public int numCuts;

	private Metrics metrics = new Metrics();

	// variabili per thread
	protected int K = 8;
	protected int[] currDepthLimit = new int[K];

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
	public static TimeLimitedSearch createFor(TablutGame game, double utilMin, double utilMax, int time) {
		return new TimeLimitedSearch(game, utilMin, utilMax, time);
	}

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

	/*
	 * Classe che implementa Callable, chiamata dai thread che eseguono
	 * concorrentemente la ricerca:
	 *
	 * le azioni iniziali vengono suddivise in K parti dove K corrisponde al numero
	 * di thread eseguiti concorrentemente
	 *
	 * ogni thread analizza in profondità il proprio gruppo di azioni fino allo
	 * scadere del timeout
	 *
	 * ogni thread restituisce lo score più alto del gruppo di azioni che ha
	 * analizzato
	 *
	 * il metodo makeDecision si occuperà di confrontare i K risultati e scegliere
	 * il più alto
	 */

	private class SearchCallable implements Callable<Action> {
		private int num;
		private State state;
		private String player;
		private List<Action> tempA;
		private ActionStore<Action> newResultsTemp;

		public SearchCallable(int num, State state, String player, List<Action> tempA) {
			this.num = num;
			this.state = state;
			this.player = player;
			this.tempA = tempA;
			this.newResultsTemp = new ActionStore<Action>();
		}

		@Override
		public Action call() {
			do {
				currDepthLimit[num]++;
				int threadDepth = currDepthLimit[num];
				for (Action action : tempA) {
					// minValue calculus are based on the simulated action's state obtained by game.getResult
					double value = minValue(game.getResult(state, action), player, Double.NEGATIVE_INFINITY,
							Double.POSITIVE_INFINITY, 1, threadDepth);
					if (timer.timeOutOccurred())
						break; // exit from action loop
					newResultsTemp.add(action, value);
				}
				if (newResultsTemp.size() > 0) {

					if (!timer.timeOutOccurred()) {
						if (hasSafeWinner(newResultsTemp.utilValues.get(0)))
							break;
						else if (newResultsTemp.size() > 1 && isSignificantlyBetter(newResultsTemp.utilValues.get(0),
								newResultsTemp.utilValues.get(1)))
							break;
					}
				}
			} while (!timer.timeOutOccurred());
			System.out.println("Thread " + num + " ha score migliore di: " + newResultsTemp.utilValues.get(0) + " con mossa " + newResultsTemp.actions.get(0).toString());
			newResultsTemp.actions.get(0).setScore(newResultsTemp.utilValues.get(0));
			return newResultsTemp.actions.get(0);
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
		List<Action> results = orderActions(state, game.getActions(state), player, 0);
		timer.start();

		for (int i = 0; i < K; i++) {
			currDepthLimit[i] = 0;
		}

		int part = results.size() / K;

		if (results.size() >= K) {
			for (int i = 0; i < K; i++) {
				temp.add(i, results.subList(part * i, part * (i + 1) - 1));
				if (i == K - 1)
					temp.add(i, results.subList(part * i, results.size() - 1));
			}

		} else {
			K--;
			while (K > 0) {
				if ((part = results.size() / K) > 0) {
					for (int i = 0; i < K; i++) {
						temp.add(i, results.subList(i, i));
					}
					break;
				} else {
					K--;
				}
			} // while
		}

		ArrayList<Callable<Action>> callableTasks = new ArrayList<Callable<Action>>(K);
		for (int i = 0; i < K; i++) {
			callableTasks.add(new SearchCallable(i, state, player, temp.get(i)));
		}

		List<Future<Action>> futures = new ArrayList<>();

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

		// max
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
		return result;
	}

	// returns an utility value
	public double maxValue(State state, String player, double alpha, double beta, int depth, int threadDepth) {
		updateMetrics(depth);
		if (game.isTerminal(state) || depth >= threadDepth || timer.timeOutOccurred()) {
			return -eval(state, getOtherPlayer(player));
		} else {
			double value = Double.NEGATIVE_INFINITY;
			//Current actions are calculated from the passed simulation state
			for (Action action : orderActions(state, game.getActions(state), player, depth)) {
				//Same as in makeDecision method's minValue
				value = Math.max(value, minValue(game.getResult(state, action), player, alpha, beta, depth + 1, threadDepth));
				if (value >= beta){
					numCuts++;
					return value;
				}
				alpha = Math.max(alpha, value);
			}
			return value;
		}
	}

	// returns a utility value, the opponent uses this method
	public double minValue(State state, String player, double alpha, double beta, int depth, int threadDepth) {
		updateMetrics(depth);
		if (game.isTerminal(state) || depth >= threadDepth || timer.timeOutOccurred()) {
			return eval(state, player);
		} else {
			double value = Double.POSITIVE_INFINITY;
			//Current actions are calculated from the passed simulation state
			for (Action action : orderActions(state, game.getActions(state), player, depth)) {
				//Same as in makeDecision method's minValue
				value = Math.min(value, maxValue(game.getResult(state, action), player, alpha, beta, depth + 1, threadDepth));
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
	 * Returns some statistic data from the last search.
	 */
	@Override
	public Metrics getMetrics() {
		return metrics;
	}

	/**
	 * Primitive operation which is called at the beginning of one depth limited
	 * search step. This implementation increments the current depth limit by
	 * one.
	 */
	/*protected void incrementDepthLimit() {
		currDepthLimit++;
	}*/

	/**
	 * Primitive operation which is used to stop iterative deepening search in
	 * situations where a clear best action exists. This implementation returns
	 * always false.
	 */
	protected boolean isSignificantlyBetter(double newUtility, double utility) {
		//return newUtility - utility > 10 ? true : false;
		return false;
		
	}

	/**
	 * Primitive operation which is used to stop iterative deepening search in
	 * situations where a safe winner has been identified. This implementation
	 * returns true if the given value (for the currently preferred action
	 * result) is the highest or lowest utility value possible.
	 */
	protected boolean hasSafeWinner(double resultUtility) {
		return resultUtility <= utilMin || resultUtility >= utilMax;
	}

	/**
	 * Primitive operation, which estimates the value for (not necessarily
	 * terminal) states. This implementation returns the utility value for
	 * terminal states and <code>(utilMin + utilMa results = newResults.actions;x) / 2</code> for non-terminal
	 * states. When overriding, first call the super implementation!
	 */
	protected double eval(State state, String player) {
		if (game.isTerminal(state)) {
			return game.getUtility(state, player);
		} else {
			//heuristicEvaluationUsed = true;
			return Score.calculateScore(game, state, player);
		}
	}

	public String getOtherPlayer(String player){
		if(player.equals("W"))
			return "B";
		else 
			return "W";
	}

	/**
	 * Primitive operation for action ordering. This implementation preserves
	 * the original order (provided by the game).
	 */
	//TODO implementandola sfruttando depth, magari tenendosi in memoria in Time Limited Search una struttura di ActionStore per livello
	//ottimizzo le ricerche, perché per ora ordiniamo solo quelle a depth 0
	public List<Action> orderActions(State state, List<Action> actions, String player, int depth) {
		return actions;
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	// nested helper classes

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

