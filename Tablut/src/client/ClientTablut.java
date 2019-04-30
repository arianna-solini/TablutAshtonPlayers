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

public class ClientTablut implements Runnable {

	private Turn player; 
	private String name;
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;
    private State currentState;

	public ClientTablut(String player, String name) throws UnknownHostException, IOException {
		
		int port = -1;
		this.name = name;
		this.gson = new Gson();

		if(player.equalsIgnoreCase("white")){
			this.player = Turn.WHITE;
			port = 5800;
		}else if(player.equalsIgnoreCase("black")){
			this.player = Turn.BLACK;
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
				System.out.println("Usage: <TeamName> [White|Black] \n");// Usage
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

	@Override
	public void run() {
		try{
			StreamUtils.writeString(out, this.gson.toJson(this.name));
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("/ASHTON TABLUT\\");
		TablutGame rules = new TablutGame(99, 0);
		TimeLimitedSearch search = new TimeLimitedSearch(rules, -50, 50, 20);
		System.out.println("You are player " + this.player.toString() + "!");
		State state = new State();
		int countTurn = 0;
		try{
			while(true){
				countTurn++;		
				StateGson temp =  this.gson.fromJson(StreamUtils.readString(in), StateGson.class);	//Avendo personalizzato state abbiamo introdotto StateGson per una lettura corretta
				state.getBoard().setBoard(temp.getBoard());
				state.setTurn(temp.getTurn());
				
				//TODO Sistema l'alternanza dei giocatori stando fermo quando tocca all'altro
				//If it's my turn I've to check if a pawn of mine has been eaten and update my PossibleActions
				if(this.player == state.getTurn() && countTurn != 1){
					state.eatenUpdate(state.getBoard(), player);
					state.updatePossibleActions(player);
				}

				if(this.player == Turn.WHITE)
					state.setOldNumWhite(state.getNumWhite());
				else
					state.setOldNumBlack(state.getNumBlack());

				this.currentState = state;
				System.out.println("Current state:");
				System.out.println(this.currentState.toString());

				if(this.player == Turn.WHITE){
					imWhite(currentState, rules, search);
				}else if(this.player == Turn.BLACK){
					imBlack(currentState, rules, search);
				}
			}

		}catch(JsonSyntaxException | IOException e){
			e.printStackTrace();
			System.exit(2);
		}

	}//run

	public void imWhite(State state, TablutGame rules, TimeLimitedSearch search){
				
		if (this.currentState.getTurn().equals(Turn.WHITE)) {

			long inizio = System.currentTimeMillis();
			boolean done = false;
			Action selectedAction = null;

			try {
				selectedAction = new Action("z0", "z0" , Turn.WHITE);
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (!done) {

				/** AI**/
				
				System.out.println(rules.getPlayer(state) + "  playing ... ");
				selectedAction = search.makeDecision(state);
				System.out.println(search.getMetrics().toString());
				long fine = System.currentTimeMillis();
				System.out.println("mossa fatta in " + (fine-inizio));

				try {
					State updatedState = rules.checkMove(state, selectedAction);
					//After my move updates the key "from" of the moved pawn
					updatedState.updatePossibleActionsKeySet(selectedAction.getFrom(), selectedAction.getTo(), Turn.WHITE);
					done = true;
				} catch (Exception e) {
					System.out.println("Eccezione: " + selectedAction.toString());
					e.printStackTrace();
				}
			}

			System.out.println("Mossa scelta: " + selectedAction.toString());
			try {
				StreamUtils.writeString(out, this.gson.toJson(selectedAction));
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Opponent turn
		}else if (state.getTurn().equals(Turn.BLACK)) {
			System.out.println("Waiting for your opponent move... ");
		}
		// Win
		else if (state.getTurn().equals(Turn.WHITEWIN)) {
			System.out.println("YOU WIN!");
			System.exit(0);
		}
		// Lost
		else if (state.getTurn().equals(Turn.BLACKWIN)) {
			System.out.println("YOU LOSE!");
			System.exit(0);
		}
		// Draw
		else if (state.getTurn().equals(Turn.DRAW)) {
			System.out.println("DRAW!");
			System.exit(0);
		}
	}

	public void imBlack(State state, TablutGame rules, TimeLimitedSearch search){
				
		if (this.currentState.getTurn().equals(Turn.BLACK)) {
			
			long inizio = System.currentTimeMillis();
			boolean done = false;
			Action selectedAction = null;

			try {
				selectedAction = new Action("z0", "z0" , Turn.BLACK);
			} catch (IOException e) {
				e.printStackTrace();
			}

			while (!done) {

				/** AI**/

				System.out.println(rules.getPlayer(state) + "  playing ... ");
				selectedAction = search.makeDecision(state);
				System.out.println(search.getMetrics().toString());
				long fine = System.currentTimeMillis();
				System.out.println("mossa fatta in " + (fine-inizio));
				
				try {
					State updatedState = rules.checkMove(state, selectedAction);
					//After my move updates the key "from" of the moved pawn
					updatedState.updatePossibleActionsKeySet(selectedAction.getFrom(), selectedAction.getTo(), Turn.BLACK);
					done = true;
				} catch (Exception e) {
					System.out.println("Eccezione: " + selectedAction.toString());
					e.printStackTrace();
				}
			}
			long fine = System.currentTimeMillis();
			System.out.println("Mossa scelta: " + selectedAction.toString() + " in "+ (fine - inizio));
			System.out.println(search.getMetrics().toString());

			try {
				StreamUtils.writeString(out, this.gson.toJson(selectedAction));
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Opponent turn
		}else if (state.getTurn().equals(Turn.WHITE)) {
			System.out.println("Waiting for your opponent move... ");
		}
		// Win
		else if (state.getTurn().equals(Turn.BLACKWIN)) {
			System.out.println("YOU WIN!");
			System.exit(0);
		}
		// Lost
		else if (state.getTurn().equals(Turn.WHITEWIN)) {
			System.out.println("YOU LOSE!");
			System.exit(0);
		}
		// Draw
		else if (state.getTurn().equals(Turn.DRAW)) {
			System.out.println("DRAW!");
			System.exit(0);
		}
	}
}
