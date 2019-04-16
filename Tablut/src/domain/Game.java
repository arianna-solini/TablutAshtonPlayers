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

import domain.Board.Pawn;
import domain.Board.Position;
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
		int columnFrom = a.getColumnFrom();
		int columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom();
		int rowTo = a.getRowTo();

		// controllo se sono fuori dal tabellone
		if (columnFrom > board.getLength()- 1 || rowFrom > board.getLength() - 1
				|| rowTo > board.getLength()-1 || columnTo > board.getLength() || columnFrom < 0
				|| rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			this.loggGame.warning("Mossa fuori tabellone");
			throw new BoardException(a);
		}

		// controllo che non vada sul trono
		if(board.getPositions().get(board.getBox(rowTo, columnTo)).equals(Position.THRONE)){
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
							&& board.getPositions().get(board.getBox(a.getRowFrom(),a.getColumnFrom())) !=Position.CITADEL ){
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
							&& board.getPositions().get(board.getBox(a.getRowFrom(),a.getColumnFrom())) !=Position.CITADEL ){
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
							&& board.getPositions().get(board.getBox(a.getRowFrom(),a.getColumnFrom())) !=Position.CITADEL ){
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
							&& board.getPositions().get(board.getBox(a.getRowFrom(),a.getColumnFrom())) !=Position.CITADEL ){
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
		// controllo se mangio a destra
		if (a.getColumnTo() < board.getLength() - 2
				&& board.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("B")
				&& (board.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("W")
						|| (board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()+2)) == Position.THRONE)
						|| board.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("K")
						|| ((board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo() + 2)) == Position.CITADEL)
							//&&!(board.getPawn(a.getRowTo(), a.getColumnTo()+2).equalsPawn("B")) 
							&&!(a.getColumnTo()+2==8&&a.getRowTo()==4)
							&&!(a.getColumnTo()+2==4&&a.getRowTo()==0)
							&&!(a.getColumnTo()+2==4&&a.getRowTo()==8)
							&&!(a.getColumnTo()+2==0&&a.getRowTo()==4)))){
			board.removePawn(a.getRowTo(), a.getColumnTo() + 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
		}
		return state;
	}
	
	private State checkCaptureWhiteLeft(State state, Action a){
		Board board = state.getBoard();
		// controllo se mangio a sinistra
		if (a.getColumnTo() > 1 && board.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("B")
				&& (board.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("W")
						|| (board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()-2)) == Position.THRONE)
						|| board.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("K")
						|| ((board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo() + 2)) == Position.CITADEL)
							//&&!(board.getPawn(a.getRowTo(), a.getColumnTo()-2).equalsPawn("B")) 
							&&!(a.getColumnTo()-2==8&&a.getRowTo()==4)
							&&!(a.getColumnTo()-2==4&&a.getRowTo()==0)
							&&!(a.getColumnTo()-2==4&&a.getRowTo()==8)
							&&!(a.getColumnTo()-2==0&&a.getRowTo()==4)))) {
			board.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
		}
		return state;
	}
	
	private State checkCaptureWhiteUp(State state, Action a){
		Board board = state.getBoard();
		// controllo se mangio sopra
		if (a.getRowTo() > 1 && board.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("B")
				&& (board.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("W")
						|| (board.getPositions().get(board.getBox(a.getRowTo() - 2, a.getColumnTo())) == Position.THRONE)
						|| board.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("K")
						|| ((board.getPositions().get(board.getBox(a.getRowTo() - 2, a.getColumnTo())) == Position.CITADEL)
							//&&!(board.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B"))
							&&!(a.getColumnTo()==8&&a.getRowTo()-2==4)
							&&!(a.getColumnTo()==4&&a.getRowTo()-2==0)
							&&!(a.getColumnTo()==4&&a.getRowTo()-2==8)
							&&!(a.getColumnTo()==0&&a.getRowTo()-2==4)) )) {
			board.removePawn(a.getRowTo() - 1, a.getColumnTo());
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(a.getRowTo() - 1, a.getColumnTo()));
		}
		return state;
	}

	private State checkCaptureWhiteDown(State state, Action a){
		Board board = state.getBoard();
		// controllo se mangio sotto
		if (a.getRowTo() < board.getLength() - 2
				&& board.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("B")
				&& (board.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("W")
						|| (board.getPositions().get(board.getBox(a.getRowTo() + 2, a.getColumnTo())) == Position.THRONE)
						|| board.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("K")
						|| ((board.getPositions().get(board.getBox(a.getRowTo() + 2, a.getColumnTo())) == Position.CITADEL)
							//&&!(board.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B"))
							&&!(a.getColumnTo()==8&&a.getRowTo()+2==4)
							&&!(a.getColumnTo()==4&&a.getRowTo()+2==0)
							&&!(a.getColumnTo()==4&&a.getRowTo()+2==8)
							&&!(a.getColumnTo()==0&&a.getRowTo()+2==4)))) {
			board.removePawn(a.getRowTo() + 1, a.getColumnTo());
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina nera rimossa in: " + board.getBox(a.getRowTo() + 1, a.getColumnTo()));
		}
		return state;
	}
	
	private State checkWhiteWin(State state, Action a){
		Board board = state.getBoard();		
		// controllo se ho vinto
		if (a.getRowTo() == 0 || a.getRowTo() == board.getLength() - 1 || a.getColumnTo() == 0
				|| a.getColumnTo() == board.getLength() - 1) {
			if (board.getPawn(a.getRowTo(), a.getColumnTo()).equalsPawn("K")) {
				state.setTurn(State.Turn.WHITEWIN);
				this.loggGame.fine("Bianco vince con re in " + a.getTo());
			}
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
		//ho il re sulla sinistra
		if (a.getColumnTo()>1&&board.getPawn(a.getRowTo(), a.getColumnTo()-1).equalsPawn("K"))
		{
			//re sul trono
			if(board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e5"))
			{
				if(board.getPawn(3, 4).equalsPawn("B")
						&& board.getPawn(4, 3).equalsPawn("B")
						&& board.getPawn(5, 4).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			//re adiacente al trono
			if(board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e4"))
			{
				if(board.getPawn(2, 4).equalsPawn("B")
						&& board.getPawn(3, 3).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			if(board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e6"))
			{
				if(board.getPawn(5, 3).equalsPawn("B")
						&& board.getPawn(6, 4).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			if(board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("f5"))
			{
				if(board.getPawn(3, 5).equalsPawn("B")
						&& board.getPawn(5, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}
			}
			//sono fuori dalle zone del trono
			if(!board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e5")
					&& !board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e6")
					&& !board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("e4")
					&& !board.getBox(a.getRowTo(), a.getColumnTo()-1).equals("f5"))
			{
				if(board.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| (board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()-2)) == Position.CITADEL))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
				}					
			}
		}		
		return state;
	}
	
	private State checkCaptureBlackKingRight(State state, Action a){
		Board board = state.getBoard();
		//ho il re sulla destra
		if (a.getColumnTo()<board.getLength()-2&&(board.getPawn(a.getRowTo(),a.getColumnTo()+1).equalsPawn("K")))				
		{
			//re sul trono
			if(board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e5"))
			{
				if(board.getPawn(3, 4).equalsPawn("B")
						&& board.getPawn(4, 5).equalsPawn("B")
						&& board.getPawn(5, 4).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			//re adiacente al trono
			if(board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e4"))
			{
				if(board.getPawn(2, 4).equalsPawn("B")
						&& board.getPawn(3, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			if(board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e6"))
			{
				if(board.getPawn(5, 5).equalsPawn("B")
						&& board.getPawn(6, 4).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			if(board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("d5"))
			{
				if(board.getPawn(3, 3).equalsPawn("B")
						&& board.getPawn(3, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}
			}
			//sono fuori dalle zone del trono
			if(!board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("d5")
					&& !board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e6")
					&& !board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e4")
					&& !board.getBox(a.getRowTo(), a.getColumnTo()+1).equals("e5"))
			{
				if(board.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B")
						|| (board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()+2)) == Position.CITADEL))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
				}					
			}
		}
		return state;
	}
	
	private State checkCaptureBlackKingDown(State state, Action a){
		Board board = state.getBoard();
		//ho il re sotto
		if (a.getRowTo()<board.getLength()-2&&board.getPawn(a.getRowTo()+1,a.getColumnTo()).equalsPawn("K"))
		{
			System.out.println("Ho il re sotto");
			//re sul trono
			if(board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e5"))
			{
				if(board.getPawn(5, 4).equalsPawn("B")
						&& board.getPawn(4, 5).equalsPawn("B")
						&& board.getPawn(4, 3).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
			}
			//re adiacente al trono
			if(board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e4"))
			{
				if(board.getPawn(3, 3).equalsPawn("B")
						&& board.getPawn(3, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
			}
			if(board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("d5"))
			{
				if(board.getPawn(4, 2).equalsPawn("B")
						&& board.getPawn(5, 3).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
			}
			if(board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("f5"))
			{
				if(board.getPawn(4, 6).equalsPawn("B")
						&& board.getPawn(5, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()+1, a.getColumnTo()));
				}
			}
			//sono fuori dalle zone del trono
			if(!board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("d5")
					&& !board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e4")
					&& !board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("f5")
					&& !board.getBox(a.getRowTo()+1, a.getColumnTo()).equals("e5"))
			{
				if(board.getPawn(a.getRowTo()+2, a.getColumnTo()).equalsPawn("B")
						|| (board.getPositions().get(board.getBox(a.getRowTo()+2, a.getColumnTo())) == Position.CITADEL))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()+1, a.getColumnTo()));
				}					
			}			
		}		
		return state;
	}
	
	private State checkCaptureBlackKingUp(State state, Action a){
		Board board = state.getBoard();
		//ho il re sopra
		if (a.getRowTo()>1&&board.getPawn(a.getRowTo()-1, a.getColumnTo()).equalsPawn("K"))
		{
			//re sul trono
			if(board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e5"))
			{
				if(board.getPawn(3, 4).equalsPawn("B")
						&& board.getPawn(4, 5).equalsPawn("B")
						&& board.getPawn(4, 3).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}
			//re adiacente al trono
			if(board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e6"))
			{
				if(board.getPawn(5, 3).equalsPawn("B")
						&& board.getPawn(5, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}
			if(board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("d5"))
			{
				if(board.getPawn(4, 2).equalsPawn("B")
						&& board.getPawn(3, 3).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}
			if(board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("f5"))
			{
				if(board.getPawn(4, 4).equalsPawn("B")
						&& board.getPawn(3, 5).equalsPawn("B"))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()-1, a.getColumnTo()));
				}
			}
			//sono fuori dalle zone del trono
			if(!board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("d5")
					&& !board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e4")
					&& !board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("f5")
					&& !board.getBox(a.getRowTo()-1, a.getColumnTo()).equals("e5"))
			{
				if(board.getPawn(a.getRowTo()-2, a.getColumnTo()).equalsPawn("B")
						|| (board.getPositions().get(board.getBox(a.getRowTo()-2, a.getColumnTo())) == Position.CITADEL))
				{
					state.setTurn(State.Turn.BLACKWIN);
					this.loggGame.fine("Nero vince con re catturato in: " + board.getBox(a.getRowTo()-1, a.getColumnTo()));
				}					
			}	
		}
		return state;
	}
	
	private State checkCaptureBlackPawnRight(State state, Action a)	{
		Board board = state.getBoard();
		//mangio a destra
		if (a.getColumnTo() < board.getLength() - 2 && board.getPawn(a.getRowTo(), a.getColumnTo() + 1).equalsPawn("W"))
		{
			if(board.getPawn(a.getRowTo(), a.getColumnTo() + 2).equalsPawn("B"))
			{
				board.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithoutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			if(board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()+2)) == Position.THRONE)
			{
				board.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithoutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			if((board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()+2)) == Position.CITADEL))
			{
				board.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithoutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			if(board.getBox(a.getRowTo(), a.getColumnTo()+2).equals("e5"))
			{
				board.removePawn(a.getRowTo(), a.getColumnTo() + 1);
				this.movesWithoutCapturing = -1;
				this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() + 1));
			}
			
		}
		
		return state;
	}
	
	private State checkCaptureBlackPawnLeft(State state, Action a){
		Board board = state.getBoard();
		//mangio a sinistra
		if (a.getColumnTo() > 1
				&& board.getPawn(a.getRowTo(), a.getColumnTo() - 1).equalsPawn("W")
				&& (board.getPawn(a.getRowTo(), a.getColumnTo() - 2).equalsPawn("B")
						|| (board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()-2)) == Position.THRONE)
						|| (board.getPositions().get(board.getBox(a.getRowTo(), a.getColumnTo()-2)) == Position.CITADEL)
						|| (board.getBox(a.getRowTo(), a.getColumnTo()-2).equals("e5"))))
		{
			board.removePawn(a.getRowTo(), a.getColumnTo() - 1);
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo(), a.getColumnTo() - 1));
		}
		return state;
	}
	
	private State checkCaptureBlackPawnUp(State state, Action a){
		Board board = state.getBoard();
		// controllo se mangio sopra
		if (a.getRowTo() > 1
				&& board.getPawn(a.getRowTo() - 1, a.getColumnTo()).equalsPawn("W")
				&& (board.getPawn(a.getRowTo() - 2, a.getColumnTo()).equalsPawn("B")
						||(board.getPositions().get(board.getBox(a.getRowTo() - 2, a.getColumnTo())) == Position.THRONE)
						|| (board.getPositions().get(board.getBox(a.getRowTo()-2, a.getColumnTo())) == Position.CITADEL)
						|| (board.getBox(a.getRowTo()-2, a.getColumnTo()).equals("e5"))))
		{
			board.removePawn(a.getRowTo()-1, a.getColumnTo());
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo()-1, a.getColumnTo() ));
		}
		return state;
	}
	
	private State checkCaptureBlackPawnDown(State state, Action a){
		Board board = state.getBoard();
		// controllo se mangio sotto
		if (a.getRowTo() < board.getLength() - 2
				&& board.getPawn(a.getRowTo() + 1, a.getColumnTo()).equalsPawn("W")
				&& (board.getPawn(a.getRowTo() + 2, a.getColumnTo()).equalsPawn("B")
						|| (board.getPositions().get(board.getBox(a.getRowTo() + 2, a.getColumnTo())) == Position.THRONE)
						|| (board.getPositions().get(board.getBox(a.getRowTo()+2, a.getColumnTo())) == Position.CITADEL)
						|| (board.getBox(a.getRowTo()+2, a.getColumnTo()).equals("e5"))))
		{
			board.removePawn(a.getRowTo()+1, a.getColumnTo());
			this.movesWithoutCapturing = -1;
			this.loggGame.fine("Pedina bianca rimossa in: " + board.getBox(a.getRowTo()+1, a.getColumnTo() ));
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
		Pawn pawn = board.getPawn(a.getRowFrom(), a.getColumnFrom());
		Board newBoard = board;
		// State newState = new State();
		this.loggGame.fine("Movimento pedina");
		// libero il trono o una casella qualunque
		if (a.getColumnFrom() == 4 && a.getRowFrom() == 4) {
			//newBoard.getBoard()[a.getRowFrom()][a.getColumnFrom()] = State.Pawn.THRONE;
		} else {
			newBoard.getBoard()[a.getRowFrom()][a.getColumnFrom()] = Pawn.EMPTY;
		}

		// metto nel nuovo tabellone la pedina mossa
		newBoard.getBoard()[a.getRowTo()][a.getColumnTo()] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
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