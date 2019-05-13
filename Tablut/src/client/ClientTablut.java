package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import domain.Action;
import domain.State;
import domain.StateGson;
import domain.TablutGame;
import domain.State.Turn;
import ai.TimeLimitedSearch;
import utils.StreamUtils;

/**
 * Client class representing the Tablut player connecting to the server
 * @author R.Vasumini, A.Solini
 */
public class ClientTablut implements Runnable {

	/**
	 * Player role, it can be white or black
	 */
	private Turn player; 
	/**
	 * Opponent role
	 */
	private Turn opponent;
	/**
	 * TeamName
	 */
	private String teamName = "aiofdtiger";
	/**
	 * Player socket used to connect to the server
	 */
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;

	/**
	 * Time to chose the best action during debug mode
	 */
	private int debugTimeSearch = 5;
	/**
	 * Time to chose the best action
	 */
	private int searchTime = debugTimeSearch; 
	/**
	 * The action choosen must be sent to the server before this timeout
	 */
	private int timeoutServer = 60; 

	public ClientTablut(String player) throws UnknownHostException, IOException {
		this(player, 60, "localhost", -1 );
	}

	public ClientTablut(String player, int timeoutServer) throws UnknownHostException, IOException {
		this(player, timeoutServer, "localhost", -1);
	}

	public ClientTablut(String player, int timeoutServer, String address) throws UnknownHostException, IOException {
		this(player, timeoutServer, address, -1);
	}

	public ClientTablut(String player, int timeoutServer, String address, int debugTimeSearch) throws UnknownHostException, IOException{
		int port = -1;
		this.gson = new Gson();

		if(player.equalsIgnoreCase("white")){
			this.player = Turn.WHITE;
			this.opponent = Turn.BLACK;
			port = 5800;
		}else if(player.equalsIgnoreCase("black")){
			this.player = Turn.BLACK;
			this.opponent = Turn.WHITE;
			port = 5801;
		}

		if(timeoutServer > 0)
			this.timeoutServer = timeoutServer;

		this.debugTimeSearch = debugTimeSearch;		

		playerSocket = new Socket(address, port);
		out = new DataOutputStream(playerSocket.getOutputStream());
		in = new DataInputStream(playerSocket.getInputStream());
	}

	/**
	 * @param args (white|black) [[[serverTimeout] serverAddress] debugTimeSearch]
	 * @throws Exception
	 * @author R.Vasumini, A.Solini
	 */
    	public static void main(String[] args) throws Exception {
        
		String player = null;
		ClientTablut client = null;
		//Checks Argument
		try {
			if(args.length >= 1 && args.length <= 4){
				player = args[0];
				
				if (!(player.equalsIgnoreCase("white") || player.equalsIgnoreCase("black"))){	
					System.out.println("You must specify which player you are (WHITE or BLACK)\n");
					System.exit(2);
				}

				if (args.length == 1) {
					client = new ClientTablut(player);
				} else if(args.length == 2){
					int timeoutServer = Integer.parseInt(args[1]);
					client = new ClientTablut(player, timeoutServer);

				} else if(args.length == 3){
					int timeoutServer = Integer.parseInt(args[1]);
					client = new ClientTablut(player, timeoutServer, args[2]);
				} else if(args.length == 4){
					int timeoutServer = Integer.parseInt(args[1]);
					int debugTimeSearch = Integer.parseInt(args[3]);
					client = new ClientTablut(player, timeoutServer, args[2], debugTimeSearch);
				}	
			}
		} catch (InvalidParameterException e) {
			System.out.println("Something's wrong with the input parameters");
			e.printStackTrace();
			System.out.println("Client: closing...");
			System.exit(4);
		}

		
		client.run();
	}//main

	/**
	 * Main Thread code
	 * @author R.Vasumini, A.Solini
	 */
	@Override
	public void run() {
		try{
			//Tells to the server squad's teamName
			StreamUtils.writeString(out, this.gson.toJson(this.teamName));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		State state = new State();
		TablutGame rules = new TablutGame();

		if(debugTimeSearch > 0)
			searchTime = debugTimeSearch;
		else if(timeoutServer >= 20)
			searchTime = timeoutServer - 10;

		TimeLimitedSearch search = new TimeLimitedSearch(rules, TablutGame.minValue, TablutGame.maxValue, searchTime);
		//Prints	AI of d Tiger
		System.out.println("          _____          __       _   _______ _                 ");
		System.out.println("    /\\   |_   _|        / _|     | | |__   __(_)                ");
		System.out.println("   /  \\    | |     ___ | |_    __| |    | |   _  __ _  ___ _ __ ");
		System.out.println("  / /\\ \\   | |    / _ \\|  _|  / _` |    | |  | |/ _` |/ _ \\ '__|");

		System.out.println(" / ____ \\ _| |_  | (_) | |   | (_| |    | |  | | (_| |  __/ |   ");
		System.out.println("/_/    \\_\\_____|  \\___/|_|    \\__,_|    |_|  |_|\\__, |\\___|_|   ");
		System.out.println("                                                 __/ |          ");
		System.out.println("                                                |___/           ");

		System.out.println("You are player " + this.player.toString() + "!");
		try{
			while(true){
				//The use of StateGson is due to correctly receive the state from the server
				StateGson temp =  this.gson.fromJson(StreamUtils.readString(in), StateGson.class);
				// Opponent turn
				if (temp.getTurn() == opponent) {
					System.out.println("Waiting for your opponent move... ");
					continue;
				}
				//Our state must be updated consequently
				state.getBoard().setBoard(temp.getBoard());
				state.setTurn(temp.getTurn());

				//If it's my turn I've to check if a pawn of mine has been eaten and update my PossibleActions
				if(this.player == state.getTurn() && (state.getTurnNumber() != 1 || player != Turn.WHITE)){
					//Updates my old number of  pawns
					if(player == Turn.WHITE)
						state.setOldNumPawn(player, state.getNumWhite());
					else
						state.setOldNumPawn(player, state.getNumBlack());
						
					//Updates opponent's actions' map keyset after his move
					state.updateOpponentPossibleActionsKeySet(opponent);
					//Updates opponent's possible actions
					state.updatePossibleActions(opponent);
					
					//Removes from my actions' keyset the pawns that I've lost
					state.eatenUpdate(state.getBoard(), player);
					//Updates my possible actions
					state.updatePossibleActions(player);
					//Updates the turn number after the opponent's move
					state.incrementTurnNumber();
				}

				System.out.println("Turn: " + state.getTurnNumber());	
				System.out.println("Current state:");
				System.out.println(state.toString());
				play(state, rules, search);
			}

		}catch(JsonSyntaxException | IOException e){
			e.printStackTrace();
			System.exit(2);
		}
	}//run

	/**
	 * Plays the current turn
	 * @param state Actual state
	 * @param rules Instance of the game class used
	 * @param search Instance of the intelligent search class used
	 * @author R.Vasumini, A.Solini
	 */
	public void play(State state, TablutGame rules, TimeLimitedSearch search){
		Turn turn = state.getTurn();
		//Player turn
		if (turn == player) {
			long inizio = System.currentTimeMillis();
			Action selectedAction = null;
			boolean done = false;
			while (!done) {			
				System.out.println(rules.getPlayer(state) + "  playing ... ");
				//Selects an action using iterative deepening alpha-beta search
				selectedAction = search.makeDecision(state);
				System.out.println(search.getMetrics().toString());
				long fine = System.currentTimeMillis();
				System.out.println("Ricerca della mossa effettuata in " + (fine-inizio) + "ms");
				try {
					//Checks move validity and executes it
					state = rules.makeMove(state, selectedAction);
					//After my move updates the key "from" of the moved pawn
					state.updatePossibleActionsKeySet(selectedAction.getFrom(), selectedAction.getTo(), player);
					//Updates opponent's old pawns number in case I've captured some of them
					if(opponent == Turn.WHITE)
						state.setOldNumPawn(opponent, state.getNumWhite());
					else
						state.setOldNumPawn(opponent, state.getNumBlack());
					//Removes the opponent's captured pawns from his actions'map
					state.eatenUpdate(state.getBoard(), opponent);
					//Updates opponent's possible actions
					state.updatePossibleActions(opponent);
					done = true;
				} catch (Exception e) {
					System.out.println("Eccezione: " + selectedAction.toString());
					e.printStackTrace();
				}
			}

			System.out.println("Mossa scelta: " + selectedAction.toString());
			System.out.println("Current state:");
			state.setTurn(opponent);
			System.out.println(state.toString());
			try {
				//Tells to the server the chosen move
				StreamUtils.writeString(out, this.gson.toJson(selectedAction));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Win or Lost
		else if (turn == Turn.WHITEWIN || turn == Turn.BLACKWIN) {
			if(turn.toString().charAt(0) == player.toString().charAt(0))
				System.out.println("YOU WIN!");
			else
				System.out.println("YOU LOST!");
			System.exit(0);
		}
		// Draw
		else if (turn == Turn.DRAW){
			System.out.println("DRAW!");
			System.exit(0);
		}
	}//play
}