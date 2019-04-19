package domain;

import java.util.HashMap;

public class Board {

	public enum Direction{
		
		LEFT("L"), RIGHT("R"), UP("U"), DOWN("D"), ANY("A");
		private final String direction;

		private Direction(String s) {
			direction = s;
		}

		public boolean equalsDirection(String otherDirection) {
			return (otherDirection == null) ? false : direction.equals(otherDirection);
		}

		public String toString() {
			return direction;
		}

	}

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

	public enum Pawn {

		EMPTY("O"), WHITE("W"), BLACK("B"),  KING("K");
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

	protected HashMap<String, Position> positions  = new HashMap<String, Position>();
	protected HashMap<String, Position> goals = new HashMap<String, Position>();

	protected Pawn board[][];

	public Board(){
		setPositions();
		setGoals();
		this.board = new Pawn[9][9];

		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.board[i][j] = Pawn.EMPTY;
			}
		}

		for(String box : positions.keySet()){
			if (positions.get(box).equals(Position.CITADEL)){
				this.board[getRow(box)][getColumn(box)] = Pawn.BLACK;
			}
			
			else if(positions.get(box).equals(Position.STARTWHITE)){
				this.board[getRow(box)][getColumn(box)] = Pawn.WHITE;
			}
			else if(positions.get(box).equals(Position.THRONE)){
				this.board[getRow(box)][getColumn(box)] = Pawn.KING;
			}
		}


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

	/**
	 * this function tells the pawn inside a specific box on the board
	 * 
	 * @param row
	 *            represents the row of the specific box
	 * @param column
	 *            represents the column of the specific box
	 * @return is the pawn of the box
	 */
	public Pawn getPawn(int row, int column) {
		return this.board[row][column];
	}

	/**
	 * this function remove a specified pawn from the board
	 * 
	 * @param row
	 *            represents the row of the specific box
	 * @param column
	 *            represents the column of the specific box
	 * 
	 */
	public void removePawn(int row, int column) {
		this.board[row][column] = Pawn.EMPTY;
	}

	public void setPawn(int row, int column, Pawn pawn){
		this.board[row][column] = pawn;
	}

	public  int getLength(){
		return this.board.length;
	}

	public String getBox(int row, int column) {
		String ret;
		char col = (char) (column + 97);
		ret = col + "" + (row + 1);
		return ret;
	}

	public int getColumn(String box){
		return Character.toLowerCase(box.charAt(0)) - 97;
	}

	public int getRow(String box){
		return Integer.parseInt(box.charAt(1) + "") - 1;
	}

}