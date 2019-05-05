package domain;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import domain.Board.Pawn;
import domain.Board.Position;

/**
 * Class for the State of the game, we have a representation of the board and the turn
 * @author Andrea Piretti
 */
public class State implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * Turn represents the player that has to move or the end of the game (A win by a player or a draw)
	 * @author A.Piretti
	 */
	public enum Turn {
		WHITE("W"), BLACK("B"), WHITEWIN("WW"), BLACKWIN("BW"), DRAW("D");
		private final String turn;
		private Turn(String s) {
			turn = s;
		}
		public boolean equalsTurn(String otherName) {
			return (otherName == null) ? false : turn.equals(otherName);
		}
		public String toString() {
			return turn;
		}
	}

	protected Board board;
	protected Turn turn;
	private Action lastAction;
	private int oldNumWhite, oldNumBlack;
	private String currentKingPosition;
	private int turnNumber;
	private HashMap<String, ArrayList<String>> possibleWhiteActions = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> possibleBlackActions = new HashMap<String, ArrayList<String>>();

	public State() {
		this.board = new Board();
		this.turn = Turn.WHITE;
		init();
	}
	
	/**
	 * Initializes the possible white and black actions at the beginning of the game and other utility values
	 * @author R.Vasumini, A.Solini
	 */
	private void init() {
		this.oldNumBlack = 16;
		this.oldNumWhite = 9;
		this.currentKingPosition = "e5";
		this.turnNumber = 1;
		HashMap<String, Position> positions = this.board.getPositions();
		for (String box : positions.keySet()) {
			if (positions.get(box).equals(Position.CITADEL)) {
				this.possibleBlackActions.put(box, getPossibleTo(box));
			}

			else if (positions.get(box).equals(Position.STARTWHITE)) {
				this.possibleWhiteActions.put(box, getPossibleTo(box));

			} else if (positions.get(box).equals(Position.THRONE)) {
				this.possibleWhiteActions.put(box, getPossibleTo(box));
			}
		}
		//Set an initial action for the lastAction value which will be replaces every turn
		try {
			this.lastAction = new Action("z0", "z0", Turn.BLACK);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the possible moves for a pawn from a specified position of the board
	 * @param from The string which represents the current position of the pawn
	 * @return An ArrayList of strings which represents  the possible future positions of the pawn
	 * @author R.Vasumini, A.Solini
	 */
	private ArrayList<String> getPossibleTo(String from) {
		int row = this.board.getRow(from);
		int column = this.board.getColumn(from);
		ArrayList<String> result = new ArrayList<String>();
		HashMap<String, Position> positions = this.board.getPositions();
		// to the right...
		for (int i = column + 1; i < 9; i++) {
			if (this.board.getPawn(row, i) == Pawn.EMPTY 
				&& (positions.get(this.board.getBox(row, i)) != Position.CITADEL || positions.get(from) == Position.CITADEL)
				&& (positions.get(this.board.getBox(row, i)) != Position.THRONE || positions.get(from) == Position.THRONE)) {
				result.add(this.board.getBox(row, i));
			}
			else{
				break;
			}
		}

		// to the left...		(... we will fight to the death, to the edge of the earth. It's a brave new world from the last to the first)
		for (int i = column - 1; i > -1; i--) {
			if (this.board.getPawn(row, i) == Pawn.EMPTY 
				&& (positions.get(this.board.getBox(row, i)) != Position.CITADEL || positions.get(from) == Position.CITADEL)
				&& (positions.get(this.board.getBox(row, i)) != Position.THRONE || positions.get(from) == Position.THRONE)) {
				result.add(this.board.getBox(row, i));
			}
			else{
				break;
			}
		}

		// upward
		for (int i = row - 1; i > -1; i--) {
			if (this.board.getPawn(i, column) == Pawn.EMPTY  
				&& (positions.get(this.board.getBox(i, column)) != Position.CITADEL || positions.get(from) == Position.CITADEL)
				&& (positions.get(this.board.getBox(i, column)) != Position.THRONE || positions.get(from) == Position.THRONE)) {
				result.add(this.board.getBox(i, column));
			}
			else{
				break;
			}
		}

		// downward
		for (int i = row + 1; i < 9; i++) {
			if (this.board.getPawn(i, column) == Pawn.EMPTY  
				&& (positions.get(this.board.getBox(i, column)) != Position.CITADEL || positions.get(from) == Position.CITADEL)
				&& (positions.get(this.board.getBox(i, column)) != Position.THRONE || positions.get(from) == Position.THRONE)) {
				result.add(this.board.getBox(i, column));
			}
			else{
				break;
			}
		}
		return result;
	}

	/**
	 * Method which updates the possible actions' map of the choosen player changing the position of a moved pawn, 
	 * notice that  it doesn't change the possible actions but only the key "from" associated to the pawn
	 * @param oldFrom Pawn's old position
	 * @param newFrom Pawn's new position
	 * @param turn Current player
	 * @author R.Vasumini, A.Solini
	 */
	public void updatePossibleActionsKeySet(String oldFrom, String newFrom, Turn turn) {
		if (turn == Turn.BLACK) {
			this.possibleBlackActions.remove(oldFrom);
			this.possibleBlackActions.put(newFrom, null);
			
		}
		if (turn == Turn.WHITE) {
			this.possibleWhiteActions.remove(oldFrom);
			this.possibleWhiteActions.put(newFrom, null);
		}
	}

	/**
	 * Method which updates the possible actions' map of the choosen player 
	 * @param turn Current player
	 * @Author R.Vasumini, A.Solini
	 */
	public void updatePossibleActions(Turn turn){
		if (turn == Turn.BLACK) {
			for(String from : this.possibleBlackActions.keySet())
				this.possibleBlackActions.replace(from, getPossibleTo(from));
			
		}
		if (turn == Turn.WHITE) {
			for(String from : this.possibleWhiteActions.keySet())
				this.possibleWhiteActions.replace(from, getPossibleTo(from));
		}
	}

	/**
	 * Method which updates the possible actions' map of the choosen player  checking if  some of his pawns have been eaten
	 * @param board Current board
	 * @param turn Current player
	 * @author R.Vasumini, A.Solini
	 */
	public void eatenUpdate(Board board, Turn turn){
		ArrayList<String> toRemove = new ArrayList<String>();
		if (turn == Turn.BLACK){
			for(String from : this.possibleBlackActions.keySet())
				if(board.getPawn(board.getRow(from),board.getColumn(from)) != Pawn.BLACK)
					toRemove.add(from);

			for(String removing : toRemove)
				this.possibleBlackActions.remove(removing);
			
		}
		else if (turn == Turn.WHITE){
			for(String from : this.possibleWhiteActions.keySet()){
				Pawn temp = board.getPawn(board.getRow(from),board.getColumn(from));
				if(temp != Pawn.WHITE && temp != Pawn.KING)
					toRemove.add(from);
			}

			for(String removing : toRemove)
				this.possibleWhiteActions.remove(removing);
		}
	}

	/**
	 * @param turn Current player
	 * @return An {@code ArrayList<Action>} of the current player's possible moves
	 * @throws IOException
	 * @author R.Vasumini, A.Solini
	 */
	public ArrayList<Action> getActionList(Turn turn) throws IOException {
		ArrayList<Action> result = new ArrayList<Action>();
		if(turn == Turn.BLACK)
			for(String from : this.possibleBlackActions.keySet())
				for(String to : this.possibleBlackActions.get(from))
					result.add(new Action(from, to, Turn.BLACK));

		if(turn == Turn.WHITE)
			for(String from : this.possibleWhiteActions.keySet())
				for(String to : this.possibleWhiteActions.get(from))
					result.add(new Action(from, to, Turn.WHITE));
		
		return result;
	}

	public Board getBoard() {
		return board;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	/**
	 * @return The action that brought to this state
	 * @author R.Vasumini, A.Solini
	 */
	public Action getLastAction(){
		return this.lastAction;
	}

	/**
	 * @param action the action that brought to this state
	 * @author R.Vasumini, A.Solini
	 */
	public void setLastAction(Action action){
		this.lastAction = action;
	}

	/**
	 * @return A map of the possible actions that white pawns can do at the current state
	 * @author R.Vasumini, A.Solini
	 */
	public HashMap<String, ArrayList<String>> getPossibleWhiteActions(){
		return this.possibleWhiteActions;
	}

	/**
	 * @return A map of the possible actions that black pawns can do at the current state
	 * @author R.Vasumini, A.Solini
	 */
	public HashMap<String, ArrayList<String>> getPossibleBlackActions(){
		return this.possibleBlackActions;
	}

	/**
	 * @return The number of black pawns in the board in the previous turn
	 * @author R.Vasumini, A.Solini
	 */
	public int getOldNumWhite() {
		return oldNumWhite;
	}

	/**
	 * @param oldNumWhite the previous number of  white pawns in the board
	 * @author R.Vasumini, A.Solini
	 */
	public void setOldNumWhite(int oldNumWhite) {
		this.oldNumWhite = oldNumWhite;
	}

	/**
	 * @return The number of black pawns in the board in the previous turn
	 * @author R.Vasumini, A.Solini
	 */
	public int getOldNumBlack() {
		return oldNumBlack;
	}

	/**
	 * @param oldNumWhite the previous number of  black pawns in the board
	 * @author R.Vasumini, A.Solini
	 */
	public void setOldNumBlack(int oldNumBlack) {
		this.oldNumBlack = oldNumBlack;
	}

	/**
	 * @return The actual number of white pawns in the board
	 * @author R.Vasumini, A.Solini
	 */
	public int getNumWhite(){
		return getPossibleWhiteActions().keySet().size();
	}

	/**
	 * @return The actual number of black pawns in the board
	 * @author R.Vasumini, A.Solini
	 */
	public int getNumBlack(){
		return getPossibleBlackActions().keySet().size();
	}

	/**
	 * @param newPosition the new position of the king in the board
	 * @author R.Vasumini, A.Solini
	 */
	public void setCurrentKingPosition(String newPosition){
		this.currentKingPosition = newPosition;
	}

	/**
	 * @return The king current position on the board
	 * @author R.Vasumini, A.Solini
	 */
	public String getCurrentKingPosition(){
		return this.currentKingPosition;
	}
	
	/**
	 * Increments by one the turn number
	 * @author R.Vasumini, A.Solini
	 */
	public void incrementTurnNumber(){
		this.turnNumber++;
	}

	/**
	 * @return The current turn number
	 * @author R.Vasumini, A.Solini
	 */
	public int getTurnNumber(){
		return this.turnNumber;
	}

	/**
	 * @return A string representing the board
	 */
	public String boardString() {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < this.board.getLength(); i++) {
			for (int j = 0; j < this.board.getLength(); j++) {
				result.append(this.board.getBoard()[i][j].toString());
				if (j == 8) {
					result.append("\n");
				}
			}
		}
		return result.toString();
	}

	/**
	 * Modified toString function for the class State
	 */
	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		// Board
		result.append("");
		result.append(this.boardString());

		result.append("-");
		result.append("\n");

		// Turn
		result.append(this.turn.toString());

		return result.toString();
	}

	/**
	 * @return A string of the State without newline characters
	 */	
	public String toLinearString() {
		StringBuffer result = new StringBuffer();

		// Board
		result.append("");
		result.append(this.boardString().replace("\n", ""));
		result.append(this.turn.toString());

		return result.toString();
	}

	/**
	 * Modified equals function for State class
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (this.board.getBoard() == null) {
			if (other.board != null)
				return false;
		} else {
			if (other.board == null)
				return false;
			if (this.board.getBoard().length != other.getBoard().getLength())
				return false;
			if (this.board.getBoard()[0].length != other.board.getBoard()[0].length)
				return false;
			for (int i = 0; i < other.getBoard().getLength(); i++)
				for (int j = 0; j < other.board.getBoard()[i].length; j++)
					if (!this.board.getBoard()[i][j].equals(other.board.getBoard()[i][j]))
						return false;
		}
		if (this.turn != other.turn)
			return false;
		return true;
	}

	/**
	 * Modified hashcode function for State class
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.board.getBoard() == null) ? 0 : this.board.getBoard().hashCode());
		result = prime * result + ((this.turn == null) ? 0 : this.turn.hashCode());
		return result;
	}

	/**
	 * Clones the State
	 */
	@Override
	public State clone() {
		State result = new State();
		Board oldboard = this.board;
		Board newboard = result.getBoard();

		for (int i = 0; i < this.board.getBoard().length; i++) 
			for (int j = 0; j < this.board.getBoard()[i].length; j++) 
				newboard.setPawn(i, j, oldboard.getPawn(i, j));
				
		result.setBoard(newboard);
		result.setTurn(this.turn);
		return result;
	}

}
