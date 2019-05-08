package ai;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.framework.Metrics;
import domain.Action;
import domain.State;
import domain.TablutGame;

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
	// protected int currDepthLimit;
	// Indicates that non-terminal nodes have been evaluated
	private boolean heuristicEvaluationUsed;
	private Timer timer;
	public int numCuts;

	private Metrics metrics = new Metrics();

	// variabili per thread
	int K = 8;
	// ArrayList<ArrayList<Action>> temp;
	// ArrayList<ActionStore<Action>> newResultsTemp;
	List<Action> resultsT;
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

	/**
	 * It is based on iterative deepening and tries to make to a good decision in
	 * limited time. Credit goes to Behi Monsio who had the idea of ordering actions
	 * by utility in subsequent depth-limited search runs.
	 */

	private class SearchCallable implements Callable<Double> {
		private int num;
		private State state;
		private String player;
		private List<Action> tempA;
		private ArrayList<ActionStore<Action>> newResultsTemp;

		public SearchCallable(int num, State state, String player, List<Action> tempA) {
			this.num = num;
			this.state = state;
			this.player = player;
			this.tempA = tempA;
			this.newResultsTemp = new ArrayList<>(K);
			for (int i = 0; i < K; i++)
				newResultsTemp.add(i, new ActionStore<Action>());
		}

		@Override
		public Double call() {
			do {
				currDepthLimit[num]++;
				int threadDepth = currDepthLimit[num];
				for (Action action : tempA) {
					// minValue calculus are based on the simulated action's state obtained by
					// game.getResult
					double value = minValue(game.getResult(state, action), player, Double.NEGATIVE_INFINITY,
							Double.POSITIVE_INFINITY, 1, threadDepth);
					if (timer.timeOutOccurred())
						break; // exit from action loop
					//System.out.println("newResultsTemp: " + newResultsTemp.size());
					//System.out.println("temp: " + tempA.size());
					newResultsTemp.get(num).add(action, value);
				}
				if (newResultsTemp.get(num).size() > 0) {

					resultsT = newResultsTemp.get(num).actions;
					//System.out.println("Miglior score: " + newResultsTemp.get(num).utilValues.get(0) + " dell'azione "
					//		+ newResultsTemp.get(num).actions.get(0));
					if (!timer.timeOutOccurred()) {
						if (hasSafeWinner(newResultsTemp.get(num).utilValues.get(0)))
							break;
						else if (newResultsTemp.get(num).size() > 1 && isSignificantlyBetter(
								newResultsTemp.get(num).utilValues.get(0), newResultsTemp.get(num).utilValues.get(1)))
							break;
					}
				}
			} while (!timer.timeOutOccurred());
			System.out.println("Thread " + num + " ha score migliore di: " + newResultsTemp.get(num).utilValues.get(0));
			return newResultsTemp.get(num).utilValues.get(0);
		}// call

	}

	@Override
	public Action makeDecision(State state) {
		numCuts = 0;
		ActionStore<Action> newResults;
		ArrayList<List<Action>> temp = new ArrayList<List<Action>>(K);
		// ArrayList<ActionStore<Action>> newResultsTemp = new
		// ArrayList<ActionStore<Action>>(K);
		metrics = new Metrics();
		String player = game.getPlayer(state);
		List<Action> results = orderActions(state, game.getActions(state), player, 0);
		timer.start();
		// currDepthLimit = 0;
		for (int i = 0; i < K; i++) {
			currDepthLimit[i] = 0;
		}

		// do {
		// incrementDepthLimit();
		// System.out.println(""+currDepthLimit);
		// heuristicEvaluationUsed = false;
		// newResults = new ActionStore<>();
		// ExecutorService pool = Executors.newFixedThreadPool(K);
		int part = results.size() / K;

		if (results.size() >= K) {
			for (int i = 0; i < K; i++) {
				temp.add(i, results.subList(part * i, part * (i + 1) - 1));
				if (i == K - 1)
					temp.add(i, results.subList(part * (K - 1), results.size() - 1));
			}

		} else {
			K--;
			while (K > 0) {
				if ((part = results.size() / K) > 0) {
					for (int i = 0; i < K; i++) {
						temp.add(i, results.subList(0, 0));// metto subList anche se è un solo elemento altrimenti rompe
					}
					break;
				} else {
					K--;
				}
			} // while
		}

		// temp.add(1, results.subList(part, (part * 2) - 1));
		// temp.add(2, results.subList(part * 2, (part * 3) - 1));
		// temp.add(3, results.subList(part * 3, results.size() - 1));

		// System.out.println(""+temp.get(3).size()); per vedere che in temp ci fossero
		// le azioni

		/*
		 * for(int i=0;i<K;i++) newResultsTemp.add(i, new ActionStore<Action>());
		 * 
		 * Thread t[] = new Thread[K];
		 */

		// for (int i = 0; i < K; i++) {

		/*
		 * t[i] = new Thread("" + i) {
		 * 
		 * @Override public void run() { do{
		 * currDepthLimit[Integer.parseInt(this.getName())]++; int num =
		 * Integer.parseInt(this.getName()); int threadDepth =
		 * currDepthLimit[Integer.parseInt(this.getName())]; for (Action action :
		 * temp.get(num)) { // minValue calculus are based on the simulated action's
		 * state obtained by // game.getResult double value =
		 * minValue(game.getResult(state, action), player, Double.NEGATIVE_INFINITY,
		 * Double.POSITIVE_INFINITY, 1, threadDepth); if (timer.timeOutOccurred())
		 * break; // exit from action loop newResultsTemp.get(num).add(action, value); }
		 * for (ActionStore<Action> as : newResultsTemp) { for (int i = 0; i <
		 * as.actions.size(); i++) { newResults.add(as.actions.get(i),
		 * as.utilValues.get(i)); } }
		 * 
		 * if (newResults.size() > 0) {
		 * 
		 * resultsT = newResults.actions; System.out.println("Miglior score: " +
		 * newResults.utilValues.get(0) + " dell'azione " + newResults.actions.get(0));
		 * if (!timer.timeOutOccurred()) { if
		 * (hasSafeWinner(newResults.utilValues.get(0))) break; else if
		 * (newResults.size() > 1 && isSignificantlyBetter(newResults.utilValues.get(0),
		 * newResults.utilValues.get(1))) break; } } //temp.clear(); } while
		 * (!timer.timeOutOccurred());
		 * 
		 * } }; t[i].start(); } for (Thread thread : t) { try { thread.join();
		 * System.out.println("Thread " + thread.getName() +
		 * ": Stato "+thread.getState().toString()); } catch (InterruptedException e) {
		 * // TODO Auto-generated catch block e.printStackTrace(); } }
		 */
		// results = resultsT;

		ArrayList<Callable<Double>> callableTasks = new ArrayList<Callable<Double>>(K);
		for (int i = 0; i < K; i++) {
			callableTasks.add(new SearchCallable(i, state, player, temp.get(i)));
		}
		// Vector<Future<Double>> evaluationValues = new Vector<Future<Double>>(K);
		// evaluationValues.setSize(K); // forse non occorre

		List<Future<Double>> futures = new ArrayList<>();

		ExecutorService exec = Executors.newFixedThreadPool(K);
		try {
			// for (int i = 0; i < K; i++) {
			try {
				futures = exec.invokeAll(callableTasks);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				//evaluationValues.add(i, futures);	
				/*try {
					System.out.println("Thread " + i + " ha score migliore di: " + evaluationValues.get(i).get());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
			//} // for
		} finally {exec.shutdown();}
		for(int i=0;i<K;i++){
			Future<Double> f = futures.get(i);
			if(f.isDone()) System.out.println("Thread/Future "+ i + " TERMINATED.");
		}
		// max
		results = resultsT;
		int maxi = -1;
		double max = Double.NEGATIVE_INFINITY;
		for(int i = 0; i < K; i++) {
			double res;
			try {
				res = futures.get(i).get();
			} catch (Exception e) {
				try {
					Thread.sleep(300);
				} catch (InterruptedException e1) {}
				continue;
			}
			if(res > max) {
				max = res;
				maxi = i;
			}
		}
		System.out.println("Tagli effettuati: " + numCuts);
		System.out.println("Mossa migliore: "+ results.get(maxi).toString()+ " del thread "+ maxi);
		return results.get(maxi);
		//return results.get(0);
	}

	// returns an utility value
	public double maxValue(State state, String player, double alpha, double beta, int depth, int threadDepth) {
		double res = 0;
		
		updateMetrics(depth);
		if (game.isTerminal(state) || depth >= threadDepth || timer.timeOutOccurred()) {
			res = -eval(state, getOtherPlayer(player));
		} else {
			double value = Double.NEGATIVE_INFINITY;
			//Current actions are calculated from the passed simulation state
			for (Action action : orderActions(state, game.getActions(state), player, depth)) {
				//Same as in makeDecision method's minValue
				value = Math.max(value, minValue(game.getResult(state, action), player, alpha, beta, depth + 1, threadDepth));
				if (value >= beta){
					numCuts++;
					res = value;
					break;
				}
				alpha = Math.max(alpha, value);
			}
			if(value < beta)
				res = value;
		}
		
		return res;
	}

	// returns a utility value, the opponent uses this method
	public double minValue(State state, String player, double alpha, double beta, int depth, int threadDepth) {
		double res = 0;
	
		updateMetrics(depth);
		if (game.isTerminal(state) || depth >= threadDepth || timer.timeOutOccurred()) {
			res = eval(state, player);
			//return eval(state, player);
		} else {
			double value = Double.POSITIVE_INFINITY;
			//Current actions are calculated from the passed simulation state
			for (Action action : orderActions(state, game.getActions(state), player, depth)) {
				//Same as in makeDecision method's minValue
				value = Math.min(value, maxValue(game.getResult(state, action), player, alpha, beta, depth + 1, threadDepth));
				if (value <= alpha){
					numCuts++;
					res = value;
					break;
				}
				beta = Math.min(beta, value);
			}
			if(value > alpha)
				res = value;
		}
		
		return res;
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
		Score score = new Score(player, state);
		if (game.isTerminal(state)) {
			return game.getUtility(state, player);
		} else {
			//heuristicEvaluationUsed = true;
			return score.calculateScore(game);
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

