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
	 * Team name
	 */
	private String name;
	/**
	 * Player socket used to connect to the server
	 */
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;
	/**
	 * State used after the updates
	 */
	private State currentState;
	private  final static  int searchTime = 20;    

	public ClientTablut(String player, String name) throws UnknownHostException, IOException {
		
		int port = -1;
		this.name = name;
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

		if (port < 1024 || port > 65535) {
			System.out.println("port " + port + " is out of range");
			System.out.println("Client: closing..");
			System.exit(1);
		}
        
		playerSocket = new Socket("localhost", port);
		out = new DataOutputStream(playerSocket.getOutputStream());
		in = new DataInputStream(playerSocket.getInputStream());
	}

	/**
	 * Client's main, it has to be launched specifiying squad's name and role
	 * @param args aiofdtiger (white|black)
	 * @throws Exception
	 * @author R.Vasumini, A.Solini
	 */
    	public static void main(String[] args) throws Exception {
        
		String player = null;
		String name = null;
		//Checks Argument
		try {
			if (args.length == 2) {
                		name = args[0];
                		player = args[1];
				if (!(name.equalsIgnoreCase("aiofdtiger"))){ 
                   			 System.out.println("Wrong team name, it's AIofDtiger or aiofdtiger\n");
                   			 System.exit(1);
				}
				if (!(player.equalsIgnoreCase("white") || player.equalsIgnoreCase("black"))){	
					System.out.println("You must specify which player you are (WHITE or BLACK)\n");
                    			System.exit(2);
				}
			} else {
				System.out.println("Usage: <TeamName> (White|Black) \n");// Usage
				System.out.println("Client: closing...");
				System.exit(3);
			}
		} catch (InvalidParameterException e) {
			System.out.println("Something's wrong with the input parameters");
			e.printStackTrace();
			System.out.println("Client: closing...");
			System.exit(4);
		}

		ClientTablut client = new ClientTablut(player, name);
		client.run();
	}//main

	/**
	 * Main Thread code
	 * @author R.Vasumini, A.Solini
	 */
	@Override
	public void run() {
		try{
			//Tells to the server squad's name
			StreamUtils.writeString(out, this.gson.toJson(this.name));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		State state = new State();
		TablutGame rules = new TablutGame();
		TimeLimitedSearch search = new TimeLimitedSearch(rules, TablutGame.minValue, TablutGame.maxValue, searchTime);
		System.out.println("/ASHTON TABLUT\\");
		System.out.println("You are player " + this.player.toString() + "!");
		try{
			while(true){
				//The use of StateGson is due to correctly receive the state from the server
				StateGson temp =  this.gson.fromJson(StreamUtils.readString(in), StateGson.class);
				//Our state must be updated consequently
				state.getBoard().setBoard(temp.getBoard());
				state.setTurn(temp.getTurn());

				//TODO Sistema l'alternanza dei giocatori stando fermo quando tocca all'altro
				//If it's my turn I've to check if a pawn of mine has been eaten and update my PossibleActions
				if(this.player == state.getTurn() && state.getTurnNumber() != 1){
					//Updates old number of  pawns
					if(this.player == Turn.WHITE)
						state.setOldNumWhite(state.getNumWhite());
					else
						state.setOldNumBlack(state.getNumBlack());
						
					state.eatenUpdate(state.getBoard(), player);
					state.updatePossibleActions(player);
					//Updates the turn number after the opponent's move
					state.incrementTurnNumber();
				}

				this.currentState = state;
				System.out.println("Current state:");
				System.out.println(this.currentState.toString());
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
					done = true;
				} catch (Exception e) {
					System.out.println("Eccezione: " + selectedAction.toString());
					e.printStackTrace();
				}
			}

			System.out.println("Mossa scelta: " + selectedAction.toString());
			try {
				//Tells to the server the chosen move
				StreamUtils.writeString(out, this.gson.toJson(selectedAction));
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Opponent turn
		}else if (turn == opponent) {
			System.out.println("Waiting for your opponent move... ");
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