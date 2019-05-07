package domain;

import java.io.Serializable;
import java.util.HashMap;

public class Board implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Enum which represents the main position of the board:
	 * Throne(Starting position for the king), 
	 * Citadels(Starting position for black pawns),
	 * Goals(boxes that the white king must reach to win),
	 * Startwhite(Starting position for white pawns)
	 * @author R.Vasumini, A.Solini
	 */
	public enum Position{
		THRONE("T"), CITADEL("C"), GOAL("G"), STARTWHITE("S");
		private final String position;
		private Position(String s) {
			position = s;
		}
		public boolean equalsPosition(String otherPosition) {
			return (otherPosition == null) ? false : position.equals(otherPosition);
		}
		public String toString() {
			return position;
		}
	}
	/**
	 * Enum which represents the  pawns in the board
	 */
	public enum Pawn {
		EMPTY("O"), WHITE("W"), BLACK("B"), KING("K"), THRONE("T");
		private final String pawn;
		private Pawn(String s) {
			pawn = s;
		}
		public boolean equalsPawn(String otherPawn) {
			return (otherPawn == null) ? false : pawn.equals(otherPawn);
		}
		public String toString() {
			return pawn;
		}
	}
	/**
	 * HashMap that associates  board's boxes to the corresponding Position
	 */
	protected HashMap<String, Position> positions  = new HashMap<String, Position>();
	/**
	 * HashMap that associates board's  boxes to the corresponding Goal Position
	 */
	protected HashMap<String, Position> goals = new HashMap<String, Position>();
	/**
	 * Matrix of Pawns representing the board
	 */
	protected Pawn board[][];

	public Board(){
		setPositions();
		setGoals();
		this.board = new Pawn[9][9];
		//Sets all boxes to empty
		for (int i = 0; i < 9; i++) 
			for (int j = 0; j < 9; j++) 
				this.board[i][j] = Pawn.EMPTY;

		//Puts pawns on the board
		for(String box : positions.keySet()){
			if (positions.get(box).equals(Position.CITADEL))
				this.board[getRow(box)][getColumn(box)] = Pawn.BLACK;
			
			else if(positions.get(box).equals(Position.STARTWHITE))
				this.board[getRow(box)][getColumn(box)] = Pawn.WHITE;
			
			else if(positions.get(box).equals(Position.THRONE))
				this.board[getRow(box)][getColumn(box)] = Pawn.KING;		
		}
	}

	public Board getEmptyBoard(){
		Board board = new Board();
		for (int i = 0; i < 9; i++) 
			for (int j = 0; j < 9; j++) 
				board.setPawn(i, j, Pawn.EMPTY);
		return board;
	}

	private void setGoals(){
		this.goals.put("a2", Position.GOAL);
		this.goals.put("a3", Position.GOAL);
		this.goals.put("a7", Position.GOAL);
		this.goals.put("a8", Position.GOAL);

		this.goals.put("i2", Position.GOAL);
		this.goals.put("i3", Position.GOAL);
		this.goals.put("i7", Position.GOAL);
		this.goals.put("i8", Position.GOAL);

		this.goals.put("b9", Position.GOAL);
		this.goals.put("c9", Position.GOAL);
		this.goals.put("g9", Position.GOAL);
		this.goals.put("h9", Position.GOAL);

		this.goals.put("b1", Position.GOAL);
		this.goals.put("c1", Position.GOAL);
		this.goals.put("g1", Position.GOAL);
		this.goals.put("h1", Position.GOAL);
	}

	private void setPositions(){
		this.positions.put("a4", Position.CITADEL);
		this.positions.put("a5", Position.CITADEL);
		this.positions.put("a6", Position.CITADEL);
		this.positions.put("b5", Position.CITADEL);
		
		this.positions.put("i4", Position.CITADEL);
		this.positions.put("i5", Position.CITADEL);
		this.positions.put("i6", Position.CITADEL);
		this.positions.put("h5", Position.CITADEL);

		this.positions.put("d1", Position.CITADEL);
		this.positions.put("e1", Position.CITADEL);
		this.positions.put("f1", Position.CITADEL);
		this.positions.put("e2", Position.CITADEL);

		this.positions.put("d9", Position.CITADEL);
		this.positions.put("e9", Position.CITADEL);
		this.positions.put("f9", Position.CITADEL);
		this.positions.put("e8", Position.CITADEL);

		this.positions.put("e5", Position.THRONE);

		this.positions.put("c5", Position.STARTWHITE);
		this.positions.put("d5", Position.STARTWHITE);
		this.positions.put("f5", Position.STARTWHITE);
		this.positions.put("g5", Position.STARTWHITE);

		this.positions.put("e3", Position.STARTWHITE);
		this.positions.put("e4", Position.STARTWHITE);
		this.positions.put("e6", Position.STARTWHITE);
		this.positions.put("e7", Position.STARTWHITE);
	}

	public HashMap<String, Position> getPositions(){
		return this.positions;
	}

	public HashMap<String, Position> getGoals(){
		return this.goals;
	}

	public Pawn[][] getBoard(){
		return this.board;
	}
	public void setBoard(Pawn[][] board){
		this.board = board;
	}

	/**
	 * This function tells the pawn inside a specific box on the board
	 * @param row represents the row of the specific box
	 * @param column represents the column of the specific box
	 * @return is the pawn of the box
	 */
	public Pawn getPawn(int row, int column) {
		return this.board[row][column];
	}

	/**
	 * This function tells the pawn inside a specific box on the board
	 * @param position string of the box to search in
	 * @return is the pawn of the box
	 * @author R.Vasumini, A.Solini
	 */
	public Pawn getPawn(String position){
		return this.board[this.getRow(position)][this.getColumn(position)];
	}

	/**
	 * This function removes a specified pawn from the board
	 * @param row represents the row of the specific box
	 * @param column represents the column of the specific box
	 */
	public void removePawn(int row, int column) {
		this.board[row][column] = Pawn.EMPTY;
	}

	public void setPawn(int row, int column, Pawn pawn){
		this.board[row][column] = pawn;
	}

	public void setPawn(String position, Pawn pawn){
		this.board[this.getRow(position)][this.getColumn(position)] = pawn;
	}
	
	/**
	 * @param position string of the box to search in
	 * @return The Pawn under the specified position
	 * @author R.Vasumini, A.Solini
	 */
	public Pawn getPawnDown(String position){
		return this.board[this.getRow(position)+1][this.getColumn(position)];
	}

	/**
	 * @param position string of the box to search in
	 * @return The Pawn over the specified position
	 * @author R.Vasumini, A.Solini
	 */
	public Pawn getPawnUp(String position){
		return this.board[this.getRow(position)-1][this.getColumn(position)];
	}

	/**
	 * @param position string of the box to search in
	 * @return The Pawn to the left of the specified position
	 * @author R.Vasumini, A.Solini
	 */
	public Pawn getPawnLeft(String position){
		return this.board[this.getRow(position)][this.getColumn(position)-1];
	}

	/**
	 * @param position string of the box to search in
	 * @return The Pawn to the right of the specified position
	 * @author R.Vasumini, A.Solini
	 */
	public Pawn getPawnRight(String position){
		return this.board[this.getRow(position)][this.getColumn(position)+1];
	}

	/**
	 * @param row line to check
	 * @return {@code true} if the row is empty, {@code false} otherwise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean isRowEmpty(int row){
		boolean isEmpty = true;
		for(int i = 0; i < 9; i++)
			if(this.board[row][i] !=Pawn.EMPTY){
				isEmpty = false;
				break;
			}
		return isEmpty;
	}

	/**
	 * @param row column to check
	 * @return {@code true} if the row is empty or with white pawns, {@code false} otherwise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean isRowWhite(int row){
		boolean isWhite = true;
		for(int i = 0; i < 9; i++)
			if(this.board[row][i] == Pawn.BLACK){
				isWhite = false;
				break;
			}
		return isWhite;
	}

	/**
	 * @param row column to check
	 * @return {@code true} if the row is empty or with black pawns, {@code false} otherwise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean isRowBlack(int row){
		boolean isBlack = true;
		for(int i = 0; i < 9; i++)
			if(this.board[row][i] == Pawn.WHITE || this.board[row][i] == Pawn.KING){
				isBlack = false;
				break;
			}
		return isBlack;
	}

	/**
	 * @param column column to check
	 * @return {@code true} if the column is empty, {@code false} otherwise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean isColumnEmpty(int column){
		boolean isEmpty = true;
		for(int i = 0; i < 9; i++)
			if(this.board[i][column] !=Pawn.EMPTY){
				isEmpty = false;
				break;
			}
		return isEmpty;
	}

	/**
	 * @param column column to check
	 * @return {@code true} if the column is empty or with white pawns, {@code false} otherwise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean isColumnWhite(int column){
		boolean isWhite = true;
		for(int i = 0; i < 9; i++)
			if(this.board[i][column] == Pawn.BLACK){
				isWhite = false;
				break;
			}
		return isWhite;
	}

	/**
	 * @param column column to check
	 * @return {@code true} if the column is empty or with black pawns, {@code false} otherwise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean isColumnBlack(int column){
		boolean isBlack = true;
		for(int i = 0; i < 9; i++)
			if(this.board[i][column] == Pawn.WHITE || this.board[i][column] == Pawn.KING){
				isBlack = false;
				break;
			}
		return isBlack;
	}

	public  int getLength(){
		return this.board.length;
	}

	/**
	 * @param row  row of the board
	 * @param column columntof the board
	 * @return The string representing the box  found
	 */
	public String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}

	/**
	 * @param box box of the board
	 * @return the integer of the column that corresponds to the specified box
	 */
	public int getColumn(String box){
		return Character.toLowerCase(box.charAt(0)) - 97;
	}

	/**
	 * @param box box of the board
	 * @return the integer of the raw that corresponds to the specified box
	 */
	public int getRow(String box){
		return Integer.parseInt(box.charAt(1) + "") - 1;
	}
	
}