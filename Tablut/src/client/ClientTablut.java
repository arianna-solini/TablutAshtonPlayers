package client;

import domain.Action;
import domain.State;
import domain.State.Pawn;
import domain.State.Turn;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

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
		}else{
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

		System.out.println("You are player " + this.player.toString() + "!");
		State state = new State();
		state.setTurn(State.Turn.WHITE); //iniziano i bianchi

		try{
			while(true){
				currentState = this.gson.fromJson(StreamUtils.readString(in), State.class);
				state = this.currentState;	 
				System.out.println("Current state: " + state.toString());
				if(this.player.equals(Turn.WHITE)){
					imWhite(state);
				}else{
					imBlack(state);
				}
			}

			/*chiusura socket?
			playerSocket.shutdownOutput(); 
			String esito = StreamUtils.readString(in); 
			System.out.println(esito); 
			playerSocket.shutdownInput();
			playerSocket.close();*/

		}catch(JsonSyntaxException | IOException e1){
			e1.printStackTrace();
			System.exit(2);
		}

	}//run

	public void imWhite(State state){

		List<int[]> whitePawns = new ArrayList<int[]>();
		List<int[]> emptyPawns = new ArrayList<int[]>();
				
		if (this.currentState.getTurn().equals(Turn.WHITE)) {
			/*
			* Da legare alla classe di dominio State:
			* raccogliere le caselle con sopra pedine bianche
			* raccogliere tutte le caselle vuote 
			*/
			int[] buf = new int[2];
			for (int i=0;i< state.getBoard().length;i++){
				for(int j=0; j<state.getBoard().length;j++){
					Pawn curPawn = state.getPawn(i, j);
					if(curPawn.equalsPawn(State.Pawn.WHITE.toString()) || curPawn.equalsPawn(State.Pawn.KING.toString())){
						buf[0] = i;
						buf[1] = j;
						whitePawns.add(buf);
					}else if (curPawn.equalsPawn(State.Pawn.EMPTY.toString())){
						buf[0] = i;
						buf[1] = j;
						emptyPawns.add(buf);
					}
				}

			}//for matrix
			

			int[] selected = new int[2]; // [0]: riga, [1]: colonna
			boolean done = false;
			Action a = null;

			try {
				a = new Action("z0", "z0" , State.Turn.WHITE);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (!done) {

				/** parametro selected da modificare con la parte di intelligenza, per ora random **/

				selected = whitePawns.get(new Random().nextInt(whitePawns.size() - 1));
				String from = this.currentState.getBox(selected[0], selected[1]);
				
				selected = emptyPawns.get(new Random().nextInt(emptyPawns.size() - 1));
				String to = this.currentState.getBox(selected[0], selected[1]);

				a.setFrom(from);
				a.setTo(to);

				try {
					//rules.checkMove(state, a);
					done = true;
				} catch (Exception e) {}

				System.out.println("Mossa scelta: " + a.toString());

				try {
					StreamUtils.writeString(out, a.toString());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				whitePawns.clear();
				emptyPawns.clear();

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

	public void imBlack(State state){

	}
		
}