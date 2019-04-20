package domain;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import domain.Board.Direction;
import domain.Board.Pawn;
import domain.Board.Position;
import domain.State.Turn;
import exceptions.*;

/**
 * 
 * Game engine inspired by the Ashton Rules of Tablut
 * 
 * 
 * @author A. Piretti, Andrea Galassi
 *
 */
public class Game  {

	/**
	 * Number of repeated states that can occur before a draw
	 */
	private int repeated_moves_allowed;

	/**
	 * Number of states kept in memory. negative value means infinite.
	 */
	private int cache_size;
	/**
	 * Counter for the moves without capturing that have occurred
	 */
	private int movesWithoutCapturing;
	private String gameLogName;
	private File gameLog;
	private FileHandler fh;
	private Logger loggGame;
	private List<State> drawConditions;

	// TODO: Draw conditions are not working

	public Game(int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName, String blackName) {
		this(new State(), repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
	}

	public Game(State state, int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName, String blackName) {
		super();
		this.repeated_moves_allowed = repeated_moves_allowed;
		this.cache_size = cache_size;
		this.movesWithoutCapturing = 0;

		Path p = Paths.get(logs_folder + File.separator + "_" + whiteName + "_vs_" + blackName + "_"
				+ new Date().getTime() + "_gameLog.txt");
		p = p.toAbsolutePath();
		this.gameLogName = p.toString();
		File gamefile = new File(this.gameLogName);
		try {
			File f = new File(logs_folder);
			f.mkdirs();
			if (!gamefile.exists()) {
				gamefile.createNewFile();
			}
			this.gameLog = gamefile;
			fh = null;
			fh = new FileHandler(gameLogName, true);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.loggGame = Logger.getLogger("GameLog");
		loggGame.addHandler(this.fh);
		this.fh.setFormatter(new SimpleFormatter());
		loggGame.setLevel(Level.FINE);
		loggGame.fine("Players:\tWhite:\t" + whiteName + "\tvs\t" + blackName);
		loggGame.fine("Repeated moves allowed:\t" + repeated_moves_allowed + "\tCache:\t" + cache_size);
		loggGame.fine("Inizio partita");
		loggGame.fine("Stato:\n" + state.toString());
		drawConditions = new ArrayList<State>();
		
	}

	public boolean checkCaptureConditions(Board board, int rowTo, int columnTo, Direction d, Turn t){
		switch(t){
			case WHITE:
				switch(d){
					case DOWN:
						return (rowTo < board.getLength() - 2
							&& board.getPawn(rowTo + 1, columnTo).equalsPawn("B")
							&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
								|| board.getPawn(rowTo + 2, columnTo).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)
									//&&!(board.getPawn(rowTo+2, columnTo).equalsPawn("B"))
									&&!(columnTo==8&&rowTo+2==4)
									&&!(columnTo==4&&rowTo+2==0)
									&&!(columnTo==4&&rowTo+2==8)
									&&!(columnTo==0&&rowTo+2==4))));
		
					case UP:
						return (rowTo > 1 
							&& board.getPawn(rowTo - 1, columnTo).equalsPawn("B")
							&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
								|| board.getPawn(rowTo - 2, columnTo).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)
									//&&!(board.getPawn(rowTo-2, columnTo).equalsPawn("B"))
									&&!(columnTo==8&&rowTo-2==4)
									&&!(columnTo==4&&rowTo-2==0)
									&&!(columnTo==4&&rowTo-2==8)
									&&!(columnTo==0&&rowTo-2==4))));
				
					case RIGHT:
						return (columnTo< board.getLength() - 2
							&& board.getPawn(rowTo, columnTo+ 1).equalsPawn("B")
							&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo, columnTo+2)) == Position.THRONE)
								|| board.getPawn(rowTo, columnTo + 2).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)
									//&&!(board.getPawn(rowTo, columnTo+2).equalsPawn("B")) 
									&&!(columnTo+2==8&&rowTo==4)
									&&!(columnTo+2==4&&rowTo==0)
									&&!(columnTo+2==4&&rowTo==8)
									&&!(columnTo+2==0&&rowTo==4)))); 
									
					case LEFT:
						return (columnTo > 1 
							&& board.getPawn(rowTo, columnTo - 1).equalsPawn("B")
							&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo, columnTo-2)) == Position.THRONE)
								|| board.getPawn(rowTo, columnTo - 2).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)
									//&&!(board.getPawn(rowTo, columnTo-2).equalsPawn("B")) 
									&&!(columnTo-2==8&&rowTo==4)
									&&!(columnTo-2==4&&rowTo==0)
									&&!(columnTo-2==4&&rowTo==8)
									&&!(columnTo-2==0&&rowTo==4))));
		
					default:
						return false;
				}

			case BLACK:
				switch(d){
					case DOWN:
						return(rowTo < board.getLength() - 2
							&& board.getPawn(rowTo + 1, columnTo).equalsPawn("W")
							&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
								|| (board.getPositions().get(board.getBox(rowTo+2, columnTo)) == Position.CITADEL)));
		
					case UP:
						return (rowTo > 1
							&& board.getPawn(rowTo - 1, columnTo).equalsPawn("W")
							&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("B")
								||(board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
								|| (board.getPositions().get(board.getBox(rowTo-2, columnTo)) == Position.CITADEL)));
		
					case RIGHT:
						return (columnTo < board.getLength() - 2 
							&& board.getPawn(rowTo, columnTo + 1).equalsPawn("W")
							&&(board.getPawn(rowTo, columnTo + 2).equalsPawn("B")
								|| board.getPositions().get(board.getBox(rowTo, columnTo+2)) == Position.THRONE
								|| (board.getPositions().get(board.getBox(rowTo, columnTo+2)) == Position.CITADEL)));
		
					case LEFT:
						return (rowTo < board.getLength() - 2
							&& board.getPawn(rowTo + 1, columnTo).equalsPawn("W")
							&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
								|| (board.getPositions().get(board.getBox(rowTo+2, columnTo)) == Position.CITADEL)));
		
					default:
						return false;
				}

			default:
				return false;
		}
	}

	public boolean checkWin(Board board, int rowTo, int columnTo, Direction d, Turn t){

		switch(t){
			case WHITE:
				return (rowTo == 0 || rowTo == board.getLength() - 1 || columnTo == 0 || columnTo == board.getLength() - 1)
					&& (board.getPawn(rowTo, columnTo).equalsPawn("K"));

			case BLACK:
				switch(d){
					case DOWN:
						return (rowTo<board.getLength()-2
							&&board.getPawn(rowTo+1,columnTo).equalsPawn("K")	//re sotto
							&&
							(
								(board.getBox(rowTo+1, columnTo).equals("e5")		//re sul trono
								&& board.getPawn(5, 4).equalsPawn("B")
								&& board.getPawn(4, 5).equalsPawn("B")
								&& board.getPawn(4, 3).equalsPawn("B"))
								||
								(board.getBox(rowTo+1, columnTo).equals("e4")		//re adiacente al trono
								&& board.getPawn(3, 3).equalsPawn("B")
								&& board.getPawn(3, 5).equalsPawn("B"))
								||
								(board.getBox(rowTo+1, columnTo).equals("d5")
								&& board.getPawn(4, 2).equalsPawn("B")
								&& board.getPawn(5, 3).equalsPawn("B"))
								||
								(board.getBox(rowTo+1, columnTo).equals("f5")
								&& board.getPawn(4, 6).equalsPawn("B")
								&& board.getPawn(5, 5).equalsPawn("B"))
								||
								(!board.getBox(rowTo+1, columnTo).equals("d5")		//re fuori dalle zone del trono
								&& !board.getBox(rowTo+1, columnTo).equals("e4")
								&& !board.getBox(rowTo+1, columnTo).equals("f5")
								&& !board.getBox(rowTo+1, columnTo).equals("e5")
								&& board.getPawn(rowTo+2, columnTo).equalsPawn("B")
									|| (board.getPositions().get(board.getBox(rowTo+2, columnTo)) == Position.CITADEL))
							)
						
						);

					case UP:
						return (rowTo>1
							&&board.getPawn(rowTo-1, columnTo).equalsPawn("K")	//re sopra
							&&
							(
								(board.getBox(rowTo-1, columnTo).equals("e5")		//re sul trono
								&& board.getPawn(3, 4).equalsPawn("B")
								&& board.getPawn(4, 5).equalsPawn("B")
								&& board.getPawn(4, 3).equalsPawn("B"))
								||
								(board.getBox(rowTo-1, columnTo).equals("e6")		//re adiacente al trono
								&& board.getPawn(5, 3).equalsPawn("B")
								&& board.getPawn(5, 5).equalsPawn("B"))
								||
								(board.getBox(rowTo-1, columnTo).equals("d5")
								&& board.getPawn(4, 2).equalsPawn("B")
								&& board.getPawn(3, 3).equalsPawn("B"))
								||
								(board.getBox(rowTo-1, columnTo).equals("f5")
								&& board.getPawn(4, 4).equalsPawn("B")
								&& board.getPawn(3, 5).equalsPawn("B"))
								||
								(!board.getBox(rowTo-1, columnTo).equals("d5")		//re fuori dalle zone del trono
								&& !board.getBox(rowTo-1, columnTo).equals("e4")
								&& !board.getBox(rowTo-1, columnTo).equals("f5")
								&& !board.getBox(rowTo-1, columnTo).equals("e5")
								&& board.getPawn(rowTo-2, columnTo).equalsPawn("B")
									|| (board.getPositions().get(board.getBox(rowTo-2, columnTo)) == Position.CITADEL))
							)
						);

					case RIGHT:
						return (columnTo<board.getLength()-2
							&& board.getPawn(rowTo,columnTo+1).equalsPawn("K")	//re sulla destra
							&&
							(
								(board.getBox(rowTo, columnTo+1).equals("e5")		//re sul trono
								&& board.getPawn(3, 4).equalsPawn("B")
								&& board.getPawn(4, 5).equalsPawn("B")
								&& board.getPawn(5, 4).equalsPawn("B"))
								||
								(board.getBox(rowTo, columnTo+1).equals("e4")		//re adiacente al trono
								&&board.getPawn(2, 4).equalsPawn("B")
								&& board.getPawn(3, 5).equalsPawn("B"))
								||
								(board.getBox(rowTo, columnTo+1).equals("e6")
								&& board.getPawn(5, 5).equalsPawn("B")
								&& board.getPawn(6, 4).equalsPawn("B"))
								||
								(board.getBox(rowTo, columnTo+1).equals("d5")
								&& board.getPawn(3, 3).equalsPawn("B")
								&& board.getPawn(3, 5).equalsPawn("B"))
								||
								(!board.getBox(rowTo, columnTo+1).equals("d5")		//re fuori dalle zone del trono
								&& !board.getBox(rowTo, columnTo+1).equals("e6")
								&& !board.getBox(rowTo, columnTo+1).equals("e4")
								&& !board.getBox(rowTo, columnTo+1).equals("e5")
								&&(board.getPawn(rowTo, columnTo + 2).equalsPawn("B")
									|| (board.getPositions().get(board.getBox(rowTo, columnTo+2)) == Position.CITADEL)))
							)
						);	

					case LEFT:
						return (columnTo>1
							&& board.getPawn(rowTo, columnTo-1).equalsPawn("K")	//re sulla sinistra
							&&
							(
								(board.getBox(rowTo, columnTo-1).equals("e5") 		//re sul trono
								&& board.getPawn(3, 4).equalsPawn("B")
								&& board.getPawn(4, 3).equalsPawn("B")
								&& board.getPawn(5, 4).equalsPawn("B"))
								||
								(board.getBox(rowTo, columnTo-1).equals("e4")		//re adiacente al trono
								&& board.getPawn(2, 4).equalsPawn("B")
								&& board.getPawn(3, 3).equalsPawn("B"))
								||
								(board.getBox(rowTo, columnTo-1).equals("f5")
								&& board.getPawn(3, 5).equalsPawn("B")
								&& board.getPawn(5, 5).equalsPawn("B"))
								||
								(board.getBox(rowTo, columnTo+1).equals("e6")
								&& board.getPawn(5, 3).equalsPawn("B")
								&& board.getPawn(6, 4).equalsPawn("B"))
								||
								(!board.getBox(rowTo, columnTo-1).equals("e5")		//re fuori dalle zone del trono
								&& !board.getBox(rowTo, columnTo-1).equals("e6")
								&& !board.getBox(rowTo, columnTo-1).equals("e4")
								&& !board.getBox(rowTo, columnTo-1).equals("f5")
								&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("B")
									|| (board.getPositions().get(board.getBox(rowTo, columnTo-2)) == Position.CITADEL)))
							)
						);

					default:
						return false;
				}

			default:
				return false;
		}

	}
	
	public State checkMove(State state, Action a)
			throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException,
			ThroneException, OccupitedException, ClimbingCitadelException, CitadelException {
		this.loggGame.fine(a.toString());

		Board board = state.getBoard();

		// controllo la mossa
		if (a.getTo().length() != 2 || a.getFrom().length() != 2) {
			this.loggGame.warning("Formato mossa errato");
			throw new ActionException(a);
		}

		int rowFrom = a.getRowFrom(), columnFrom = a.getColumnFrom();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();

		// controllo se sono fuori dal tabellone
		if (columnFrom > board.getLength()- 1 || rowFrom > board.getLength() - 1
				|| rowTo > board.getLength()-1 || columnTo > board.getLength() || columnFrom < 0
				|| rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			this.loggGame.warning("Mossa fuori tabellone");
			throw new BoardException(a);
		}

		// controllo che non vada sul trono
		if(board.getPositions().get(board.getBox(rowTo, columnTo)) == Position.THRONE){
			this.loggGame.warning("Mossa sul trono");
			throw new ThroneException(a);
		}

		// controllo la casella di arrivo
		if (!board.getPawn(rowTo, columnTo).equalsPawn(Pawn.EMPTY.toString())) {
			this.loggGame.warning("Mossa sopra una casella occupata");
			throw new OccupitedException(a);
		}
		if(board.getPositions().get(board.getBox(rowTo, columnTo)) == Position.CITADEL
				&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL ){
			this.loggGame.warning("Mossa che arriva sopra una citadel");
			throw new CitadelException(a);
		}
		if(board.getPositions().get(board.getBox(rowTo, columnTo)) == Position.CITADEL
				&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) == Position.CITADEL ){
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(a);
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					this.loggGame.warning("Mossa che arriva sopra una citadel");
					throw new CitadelException(a);
				}
			}

		}

		// controllo se cerco di stare fermo
		if (rowFrom == rowTo && columnFrom == columnTo) {
			this.loggGame.warning("Nessuna mossa");
			throw new StopException(a);
		}

		// controllo se sto muovendo una pedina giusta
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (!board.getPawn(rowFrom, columnFrom).equalsPawn("W")
					&& !board.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
				this.loggGame.warning("Giocatore " + a.getTurn() + " cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}
		if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
			if (!board.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
				this.loggGame.warning("Giocatore " + a.getTurn() + " cerca di muovere una pedina avversaria");
				throw new PawnException(a);
			}
		}

		// controllo di non muovere in diagonale
		if (rowFrom != rowTo && columnFrom != columnTo) {
			this.loggGame.warning("Mossa in diagonale");
			throw new DiagonalException(a);
		}

		// controllo di non scavalcare pedine
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!board.getPawn(rowFrom, i).equalsPawn(Pawn.EMPTY.toString())) {
						if(board.getPositions().get(board.getBox(rowFrom, i)).equalsPosition(Position.THRONE.toString())){
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(board.getPositions().get(board.getBox(rowFrom, i)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom,columnFrom)) !=Position.CITADEL ){
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!board.getPawn(rowFrom, i).equalsPawn(Pawn.EMPTY.toString())) {
						if(board.getPositions().get(board.getBox(rowTo, columnTo)).equalsPosition(Position.THRONE.toString())){
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(board.getPositions().get(board.getBox(rowFrom, i)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom,columnFrom)) !=Position.CITADEL ){
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!board.getPawn(i, columnFrom).equalsPawn(Pawn.EMPTY.toString())) {
						if(board.getPositions().get(board.getBox(i, columnFrom)).equalsPosition(Position.THRONE.toString())){
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(board.getPositions().get(board.getBox(i, columnFrom)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom,columnFrom)) !=Position.CITADEL ){
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!board.getPawn(i, columnFrom).equalsPawn(Pawn.EMPTY.toString())) {
						if(board.getPositions().get(board.getBox(i, columnFrom)).equalsPosition(Position.THRONE.toString())){
							this.loggGame.warning("Mossa che scavalca il trono");
							throw new ClimbingException(a);
						} else {
							this.loggGame.warning("Mossa che scavalca una pedina");
							throw new ClimbingException(a);
						}
					}
					if(board.getPositions().get(board.getBox(i, columnFrom)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom,columnFrom)) !=Position.CITADEL ){
						this.loggGame.warning("Mossa che scavalca una citadel");
						throw new ClimbingCitadelException(a);
					}
				}
			}
		}

		// se sono arrivato qui, muovo la pedina
		state = this.movePawn(state, a);

		// a questo punto controllo lo stato per eventuali catture
		if (state.getTurn().equalsTurn("W")) {
			state = this.checkCaptureBlack(state, a);
		} else if (state.getTurn().equalsTurn("B")) {
			state = this.checkCaptureWhite(state, a);
		}

		// if something has been captured, clear cache for draws
		if (this.movesWithoutCapturing == 0) {
			this.drawConditions.clear();
			this.loggGame.fine("Capture! Draw cache cleared!");
		}

		// controllo pareggio
		int trovati = 0;
		for (State s : drawConditions) {

			System.out.println(s.toString());

			if (s.equals(state)) {
				// DEBUG: //
				// System.out.println("UGUALI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());

				trovati++;
				if (trovati > repeated_moves_allowed) {
					state.setTurn(State.Turn.DRAW);
					this.loggGame.fine("Partita terminata in pareggio per numero di stati ripetuti");
					break;
				}
			} else {
				// DEBUG: //
				// System.out.println("DIVERSI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());
			}
		}
		if (trovati > 0) {
			this.loggGame.fine("Equal states found: " + trovati);
		}
		if (cache_size >= 0 && this.drawConditions.size() > cache_size) {
			this.drawConditions.remove(0);
		}
		this.drawConditions.add(state.clone());

		this.loggGame.fine("Current draw cache size: " + this.drawConditions.size());

		this.loggGame.fine("Stato:\n" + state.toString());
		System.out.println("Stato:\n" + state.toString());

		return state;
	}

	private State checkCaptureWhiteRight(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio a destra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.WHITE)){
			board.removePawn(rowTo, columnTo + 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(rowTo, columnTo + 1));
		}
		return state;
	}
	
	private State checkCaptureWhiteLeft(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio a sinistra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.WHITE)){
			board.removePawn(rowTo, columnTo - 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(rowTo, columnTo - 1));
		}
		return state;
	}
	
	private State checkCaptureWhiteUp(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sopra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.WHITE)) {
			board.removePawn(rowTo - 1, columnTo);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(rowTo - 1, columnTo));
		}
		return state;
	}

	private State checkCaptureWhiteDown(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sotto
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.WHITE)) {
			board.removePawn(rowTo + 1, columnTo);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(rowTo + 1, columnTo));
		}
		return state;
	}
	
	private State checkWhiteWin(State state, Action a){
		Board board = state.getBoard();	
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se ho vinto
		if (checkWin(board, rowTo, columnTo, Direction.ANY, Turn.WHITE)) {
				state.setTurn(State.Turn.WHITEWIN);
				this.loggGame.fine("Bianco vince con re in " + a.getTo());
		}
		return state;
	}
	
	private State checkCaptureWhite(State state, Action a) {
		checkCaptureWhiteRight(state, a);
		checkCaptureWhiteLeft(state, a);
		checkCaptureWhiteUp(state, a);
		checkCaptureWhiteDown(state, a);
		checkWhiteWin(state, a);
		
		this.movesWithoutCapturing++;
		return state;
	}

	private State checkCaptureBlackKingLeft(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		//ho il re sulla sinistra
		if (checkWin(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)){
			state.setTurn(State.Turn.BLACKWIN);
			this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(rowTo, columnTo - 1));
		}		
		return state;
	}
	
	private State checkCaptureBlackKingRight(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		//ho il re sulla destra
		if (checkWin(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)){
			state.setTurn(State.Turn.BLACKWIN);
			this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(rowTo, columnTo + 1));
		}
		return state;
	}
	
	private State checkCaptureBlackKingDown(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		//ho il re sotto
		if (checkWin(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)){
			state.setTurn(State.Turn.BLACKWIN);
			this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(rowTo+1, columnTo));
		}		
		return state;
	}
	
	private State checkCaptureBlackKingUp(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		//ho il re sopra
		if (rowTo>1&&board.getPawn(rowTo-1, columnTo).equalsPawn("K")){
			state.setTurn(State.Turn.BLACKWIN);
			this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(rowTo-1, columnTo));
		}
		return state;
	}
	
	private State checkCaptureBlackPawnRight(State state, Action a)	{
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		//mangio a destra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)){
			board.removePawn(rowTo, columnTo + 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(rowTo, columnTo + 1));	
		}
		
		return state;
	}
	
	private State checkCaptureBlackPawnLeft(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		//mangio a sinistra
		if (checkCaptureConditions(board, rowTo, columnTo,Direction.LEFT, Turn.BLACK)){
			board.removePawn(rowTo, columnTo - 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(rowTo, columnTo - 1));
		}
		return state;
	}
	
	private State checkCaptureBlackPawnUp(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sopra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.BLACK)){
			board.removePawn(rowTo-1, columnTo);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(rowTo-1, columnTo ));
		}
		return state;
	}
	
	private State checkCaptureBlackPawnDown(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sotto
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK))	{
			board.removePawn(rowTo+1, columnTo);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(rowTo+1, columnTo ));
		}
		return state;
	}
	
	private State checkCaptureBlack(State state, Action a) {
		
		this.checkCaptureBlackPawnRight(state, a);
		this.checkCaptureBlackPawnLeft(state, a);
		this.checkCaptureBlackPawnUp(state, a);
		this.checkCaptureBlackPawnDown(state, a);
		this.checkCaptureBlackKingRight(state, a);
		this.checkCaptureBlackKingLeft(state, a);
		this.checkCaptureBlackKingDown(state, a);
		this.checkCaptureBlackKingUp(state, a);
		
		this.movesWithoutCapturing++;
		return state;
	}

	private State movePawn(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom(), columnFrom = a.getColumnFrom();
		Pawn pawn = board.getPawn(rowFrom, columnFrom);
		Board newBoard = board;
		// State newState = new State();
		this.loggGame.fine("Movimento pedina");
		// libero una casella qualunque
		newBoard.getBoard()[rowFrom][columnFrom] = Pawn.EMPTY;
		// metto nel nuovo tabellone la pedina mossa
		newBoard.getBoard()[rowTo][columnTo] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		if (state.getTurn() == Turn.WHITE) {
			state.setTurn(State.Turn.BLACK);
		} else {
			state.setTurn(State.Turn.WHITE);
		}
		return state;
	}

	public File getGameLog() {
		return gameLog;
	}

	public int getmovesWithoutCapturing() {
		return movesWithoutCapturing;
	}

	@SuppressWarnings("unused")
	private void setmovesWithoutCapturing(int movesWithoutCapturing) {
		this.movesWithoutCapturing = movesWithoutCapturing;
	}

	public int getRepeated_moves_allowed() {
		return repeated_moves_allowed;
	}

	public int getCache_size() {
		return cache_size;
	}

	public List<State> getDrawConditions() {
		return drawConditions;
	}

	public void clearDrawConditions() {
		drawConditions.clear();
	}

}