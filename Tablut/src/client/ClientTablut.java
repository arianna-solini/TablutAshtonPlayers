package client;

import domain.Action;
import domain.State;
import domain.StateGson;
import domain.TablutGame;
import domain.State.Turn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import aima.core.search.adversarial.AdversarialSearch;
import aima.core.search.adversarial.IterativeDeepeningAlphaBetaSearch;
import utils.StreamUtils;

public class ClientTablut implements Runnable{

	private State.Turn player; 
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
			this.player = State.Turn.WHITE;
			port = 5800;
		}else if(player.equalsIgnoreCase("black")){
			this.player = State.Turn.BLACK;
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
		
        /* CONTROLLO ARGOMENTI */
		try {
			if (args.length == 2) {
                name = args[0];
                player = args[1];
				if (!(name.equalsIgnoreCase("aiofdtiger")))
				{ 
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

		System.out.println("-----Inizio partita AshtonTablut-----");
		TablutGame rules = new TablutGame(99, 0, "localLogs", "test", "test");
		AdversarialSearch<State, Action> search = IterativeDeepeningAlphaBetaSearch.createFor(rules, 0, 1, 5);
		System.out.println("You are player " + this.player.toString() + "!");
		State state = new State(); // istanziando State inizializzo anche la board (vedi costruttore)
		state.setTurn(State.Turn.WHITE); //iniziano i bianchi

		try{
			while(true){
				StateGson temp =  this.gson.fromJson(StreamUtils.readString(in), StateGson.class);	//Avendo personalizzato state abbiamo introdotto StateGson per una lettura corretta
				state.getBoard().setBoard(temp.getBoard());
				state.setTurn(temp.getTurn()); 
				this.currentState = state;
				System.out.println("Current state:");
				System.out.println(this.currentState.toString());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				if(this.player == Turn.WHITE){
					imWhite(currentState, rules, search);
				}else if(this.player == Turn.BLACK){
					imBlack(currentState, rules, search);
				}
			}

		}catch(JsonSyntaxException | IOException e1){
			e1.printStackTrace();
			System.exit(2);
		}

	}//run

	public void imWhite(State state, TablutGame rules, AdversarialSearch<State, Action> search){
				
		if (this.currentState.getTurn().equals(Turn.WHITE)) {

			long inizio = System.currentTimeMillis();
			boolean done = false;
			Action selectedAction = null;

			try {
				selectedAction = new Action("z0", "z0" , State.Turn.WHITE);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

			while (!done) {

				/** parte di intelligenza **/
				
				System.out.println(rules.getPlayer(state) + "  playing ... ");
				selectedAction = search.makeDecision(state);
				System.out.println("mossa fatta");
				

				try {
					rules.checkMove(state, selectedAction);
					state.updatePossibleActions(selectedAction.getFrom(), selectedAction.getTo(), Turn.WHITE);
					done = true;
				} catch (Exception e) {}
			}
			long fine = System.currentTimeMillis();
			System.out.println("Mossa scelta: " + selectedAction.toString() + " in "+ (fine - inizio));
			System.out.println(search.getMetrics().toString());

			try {
				StreamUtils.writeString(out, this.gson.toJson(selectedAction));
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Turno dell'avversario
		}else if (state.getTurn().equals(State.Turn.BLACK)) {
			System.out.println("Waiting for your opponent move... ");
		}
		// ho vinto
		else if (state.getTurn().equals(State.Turn.WHITEWIN)) {
			System.out.println("YOU WIN!");
			System.exit(0);
		}
		// ho perso
		else if (state.getTurn().equals(State.Turn.BLACKWIN)) {
			System.out.println("YOU LOSE!");
			System.exit(0);
		}
		// pareggio
		else if (state.getTurn().equals(State.Turn.DRAW)) {
			System.out.println("DRAW!");
			System.exit(0);
		}
	}

	public void imBlack(State state, TablutGame rules, AdversarialSearch<State, Action> search){
				
		if (this.currentState.getTurn().equals(Turn.BLACK)) {//per ora lasciamo così
			
			long inizio = System.currentTimeMillis();
			boolean done = false;
			Action selectedAction = null;

			try {
				selectedAction = new Action("z0", "z0" , State.Turn.BLACK);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while (!done) {

				/** parte di intelligenza **/

				System.out.println(rules.getPlayer(state) + "  playing ... ");
				selectedAction = search.makeDecision(state);
				System.out.println("mossa fatta");
				

				try {
					rules.checkMove(state, selectedAction);
					state.updatePossibleActions(selectedAction.getFrom(), selectedAction.getTo(), Turn.BLACK);
					done = true;
				} catch (Exception e) {}
			}
			long fine = System.currentTimeMillis();
			System.out.println("Mossa scelta: " + selectedAction.toString() + " in "+ (fine - inizio));
			System.out.println(search.getMetrics().toString());

			try {
				StreamUtils.writeString(out, this.gson.toJson(selectedAction));
			} catch (IOException e) {
				e.printStackTrace();
			}

		// Turno dell'avversario
		}else if (state.getTurn().equals(State.Turn.WHITE)) {
			System.out.println("Waiting for your opponent move... ");
		}
		// ho vinto
		else if (state.getTurn().equals(State.Turn.BLACKWIN)) {
			System.out.println("YOU WIN!");
			System.exit(0);
		}
		// ho perso
		else if (state.getTurn().equals(State.Turn.WHITEWIN)) {
			System.out.println("YOU LOSE!");
			System.exit(0);
		}
		// pareggio
		else if (state.getTurn().equals(State.Turn.DRAW)) {
			System.out.println("DRAW!");
			System.exit(0);
		}

	}
}
