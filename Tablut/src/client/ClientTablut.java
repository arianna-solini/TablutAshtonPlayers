package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidParameterException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import utils.StreamUtils;

public class ClientTablut implements Runnable{

    private String player; //String invece di State.Turn
	private String name;
	private Socket playerSocket;
	private DataInputStream in;
	private DataOutputStream out;
	private Gson gson;
    private String currentState; //String invece di State
	
	/** mancano 2 costruttori distinti per bianchi e neri **/

    public ClientTablut(String player, String name, int port) throws UnknownHostException, IOException {
        
        if (port < 1024 || port > 65535) {
            System.out.println("port " + port + " is out of range");
            System.out.println("Client: closing..");
            System.exit(1);
        }
        this.name = name;
        this.gson = new Gson();
        
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

        //sono un bianco, per il nero porta 5801
		ClientTablut client = new ClientTablut(player, name, 5800);
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

		System.out.println("You are player " + this.player + "!");

		try{
			while(true){
				currentState = this.gson.fromJson(StreamUtils.readString(in), String.class);
				System.out.println("Current state: " + currentState);
				if(this.player.equalsIgnoreCase("white")){
					imWhite();
				}else{
					imBlack();
				}
			}

			/*chiusura docket?
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

	public void imWhite(){
		
		if (this.currentState.equals("Turn WHITE")) { //da modificare dopo aver fatto la classe State
			/*
			* Da legare alla classe di dominio State:
			* raccogliere le caselle con sopra pedine bianche
			* raccogliere tutte le caselle vuote 
			*/
			

			int[] selected = null;
			boolean done = false;
			//Action a = null;

			/*try {
				creo un istanza di Action
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (!done) {

				/* caso Random
				*selected = pawns.get(new Random().nextInt(pawns.size() - 1));
				*from = this.getCurrentState().getBox(selected[0], selected[1]);
				*selected = empty.get(new Random().nextInt(empty.size() - 1));
				*to = this.getCurrentState().getBox(selected[0], selected[1]);
				*/

				/*try {
					//a = new Action(from, to, State.Turn.WHITE);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

				try {
					//rules.checkMove(state, a);
					done = true;
				} catch (Exception e) {

				}


			//System.out.println("Mossa scelta: " + a.toString());
			try {
				//this.write(a);
			} catch (ClassNotFoundException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//pawns.clear();
			//empty.clear();

		}
		// Turno dell'avversario
		/*else if (state.getTurn().equals(StateTablut.Turn.BLACK)) {
			System.out.println("Waiting for your opponent move... ");
		}
		// ho vinto
		else if (state.getTurn().equals(StateTablut.Turn.WHITEWIN)) {
			System.out.println("YOU WIN!");
			System.exit(0);
		}
		// ho perso
		else if (state.getTurn().equals(StateTablut.Turn.BLACKWIN)) {
			System.out.println("YOU LOSE!");
			System.exit(0);
		}
		// pareggio
		else if (state.getTurn().equals(StateTablut.Turn.DRAW)) {
			System.out.println("DRAW!");
			System.exit(0);
		}*/
		}
	}

	public void imBlack(){

	}
		
}