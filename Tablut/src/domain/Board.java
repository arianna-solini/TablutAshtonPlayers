package domain;

import java.util.HashMap;

public class Board {

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

		this.positions.put("a2", Position.GOAL);
		this.positions.put("a3", Position.GOAL);
		this.positions.put("a7", Position.GOAL);
		this.positions.put("a8", Position.GOAL);

		this.positions.put("i2", Position.GOAL);
		this.positions.put("i3", Position.GOAL);
		this.positions.put("i7", Position.GOAL);
		this.positions.put("i8", Position.GOAL);

		this.positions.put("b9", Position.GOAL);
		this.positions.put("c9", Position.GOAL);
		this.positions.put("g9", Position.GOAL);
		this.positions.put("h9", Position.GOAL);

		this.positions.put("b1", Position.GOAL);
		this.positions.put("c1", Position.GOAL);
		this.positions.put("g1", Position.GOAL);
		this.positions.put("h1", Position.GOAL);

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

	protected Pawn board[][];

	public Board(){
		setPositions();
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				this.board[i][j] = Pawn.EMPTY;
			}
		}
		for(String box : positions.keySet()){
			if (positions.get(box).equals(Position.CITADEL)){
				this.board[getColumn(box)][getRow(box)] = Pawn.BLACK;
			}
			
			else if(positions.get(box).equals(Position.STARTWHITE)){
				this.board[getColumn(box)][getRow(box)] = Pawn.WHITE;
			}
			else if(positions.get(box).equals(Position.THRONE)){
				this.board[getColumn(box)][getRow(box)] = Pawn.KING;
			}
		}


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