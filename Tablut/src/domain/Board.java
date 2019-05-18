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
		THRONE("T"), CITADEL("C"), STARTWHITE("S");
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

	public enum Diagonal{
		LEFTUPBIG("LUB"), LEFTUPSMALL("LUS"), LEFTDOWNBIG("LDB"), LEFTDOWNSMALL("LDS"),
		RIGHTUPBIG("RUB"), RIGHTUPSMALL("RUS"), RIGHTDOWNBIG("RDB"), RIGHTDOWNSMALL("RDS");
		private final String diagonal;
		private Diagonal(String s){
			diagonal = s;
		}
		public boolean equalsDiagonal(String otherDiagonal) {
			return (otherDiagonal == null) ? false : diagonal.equals(otherDiagonal);
		}
		public String toString() {
			return diagonal;
		}
			
	}

	public enum Citadel{
		LEFT("CL"), RIGHT("CR"), UP("CU"), DOWN("CD");
		private final String citadel;
		private Citadel(String s){
			citadel = s;
		}
		public boolean equalsCitadel(String otherCitadel) {
			return (otherCitadel== null) ? false : citadel.equals(otherCitadel);
		}
		public String toString() {
			return citadel;
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

	protected HashMap<Diagonal, String[]> diagonals  = new HashMap<Diagonal, String[]>();

	protected HashMap<Diagonal, String[]> backDiagonals  = new HashMap<Diagonal, String[]>();

	protected HashMap<Citadel, String[]> citadels  = new HashMap<Citadel, String[]>();
	/**
	 * Matrix of Pawns representing the board
	 */
	protected Pawn board[][];

	public Board(){
		setBackDiagonals();
		setDiagonals();
		setPositions();
		setCitadels();
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

	public static Board getEmptyBoard(){
		Board board = new Board();
		for (int i = 0; i < 9; i++) 
			for (int j = 0; j < 9; j++) 
				board.setPawn(i, j, Pawn.EMPTY);
		return board;
	}

	private void setDiagonals(){

		this.diagonals.put(Diagonal.LEFTDOWNBIG, new String[]{"b7", "c8"});

		this.diagonals.put(Diagonal.LEFTDOWNSMALL, new String[]{"c6", "d7"});

		this.diagonals.put(Diagonal.LEFTUPBIG, new String[]{"b3", "c2"});

		this.diagonals.put(Diagonal.LEFTUPSMALL, new String[]{"c4", "d3"});

		this.diagonals.put(Diagonal.RIGHTDOWNBIG, new String[]{"g8", "h7"});

		this.diagonals.put(Diagonal.RIGHTDOWNSMALL, new String[]{"f7", "g6"});

		this.diagonals.put(Diagonal.RIGHTUPBIG, new String[]{"g2", "h3"});

		this.diagonals.put(Diagonal.RIGHTUPSMALL, new String[]{"f3", "g4"});
	}

	private void setBackDiagonals(){

		this.diagonals.put(Diagonal.LEFTDOWNBIG, new String[]{"a7", "a8", "a9", "b8", "b9", "c9"});

		this.diagonals.put(Diagonal.LEFTDOWNSMALL, new String[]{"a7", "a8", "a9", "b8", "b9", "c9", "b6", "b7", "c7", "c8", "d8"});

		this.diagonals.put(Diagonal.LEFTUPBIG, new String[]{"a1", "b1", "c1", "a2", "a3", "b2"});

		this.diagonals.put(Diagonal.LEFTUPSMALL, new String[]{"a1", "b1", "c1", "a2", "a3", "b2", "b3", "c2", "b4", "c3", "d2"});

		this.diagonals.put(Diagonal.RIGHTDOWNBIG, new String[]{"i7", "i8", "i9", "g9", "h9", "h8"});

		this.diagonals.put(Diagonal.RIGHTDOWNSMALL, new String[]{"i7", "i8", "i9", "g9", "h9", "h8", "g8", "h7", "f8", "g7", "h6"});

		this.diagonals.put(Diagonal.RIGHTUPBIG, new String[]{"i3", "i2", "i1", "g1", "h1", "h2"});

		this.diagonals.put(Diagonal.RIGHTUPSMALL, new String[]{"i3", "i2", "i1", "g1", "h1", "h2", "g2", "h3", "f2", "g3", "h4"});
	}

	private void setCitadels(){
		this.citadels.put(Citadel.LEFT, new String[]{"a4", "a5", "a6", "b5"});

		this.citadels.put(Citadel.RIGHT, new String[]{"i4", "i5", "i6", "h5"});

		this.citadels.put(Citadel.UP, new String[]{"d1", "e1", "f1", "e2"});

		this.citadels.put(Citadel.DOWN, new String[]{"d9", "e9", "f9", "e8"});
	}

	public HashMap<Diagonal, String[]> getDiagonals(){
		return this.diagonals;
	}

	public HashMap<Diagonal, String[]> getBackDiagonals(){
		return this.backDiagonals;
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

	public Pawn getPawnDiagonalLeftUp(String position){
		return this.board[this.getRow(position)-1][this.getColumn(position)-1];
	}

	public Pawn getPawnDiagonalLeftDown(String position){
		return this.board[this.getRow(position)+1][this.getColumn(position)-1];
	}

	public Pawn getPawnDiagonalRightUp(String position){
		return this.board[this.getRow(position)-1][this.getColumn(position)+1];
	}

	public Pawn getPawnDiagonalRightDown(String position){
		return this.board[this.getRow(position)+1][this.getColumn(position)+1];
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

	public boolean isWhiteDown(String position){
		boolean isWhite = true;
		int column = this.getColumn(position);
		for(int i = this.getRow(position) + 1; i < 9; i++)
			if(this.board[i][column] == Pawn.BLACK){
				isWhite = false;
				break;
			}
		return isWhite;
	}

	public boolean isWhiteUp(String position){
		boolean isWhite = true;
		int column = this.getColumn(position);
		for(int i = this.getRow(position) -1;  i > -1; i--)
			if(this.board[i][column] == Pawn.BLACK){
				isWhite = false;
				break;
			}
		return isWhite;
	}

	public boolean isWhiteRight(String position){
		boolean isWhite = true;
		int row = this.getRow(position);
		for(int i = this.getColumn(position) + 1; i < 9; i++)
			if(this.board[row][i] == Pawn.BLACK){
				isWhite = false;
				break;
			}
		return isWhite;
	}

	public boolean isWhiteLeft(String position){
		boolean isWhite = true;
		int row = this.getRow(position);
		for(int i = this.getColumn(position) - 1; i > -1; i--)
			if(this.board[row][i] == Pawn.BLACK){
				isWhite = false;
				break;
			}
		return isWhite;
	}

	public boolean isEmptyDown(String position){
		boolean isEmpty = true;
		int column = this.getColumn(position);
		for(int i = this.getRow(position) + 1; i < 9; i++)
			if(this.board[i][column] != Pawn.EMPTY){
				isEmpty= false;
				break;
			}
		return isEmpty;
	}

	public boolean isEmptyUp(String position){
		boolean isEmpty = true;
		int column = this.getColumn(position);
		for(int i = this.getRow(position) -1;  i > -1; i--)
			if(this.board[i][column] != Pawn.EMPTY){
				isEmpty = false;
				break;
			}
		return isEmpty;
	}

	public boolean isEmptyRight(String position){
		boolean isEmpty = true;
		int row = this.getRow(position);
		for(int i = this.getColumn(position) + 1; i < 9; i++)
			if(this.board[row][i] != Pawn.EMPTY){
				isEmpty = false;
				break;
			}
		return isEmpty;
	}

	public boolean isEmptyLeft(String position){
		boolean isEmpty = true;
		int row = this.getRow(position);
		for(int i = this.getColumn(position) - 1; i > -1; i--)
			if(this.board[row][i] != Pawn.EMPTY){
				isEmpty = false;
				break;
			}
		return isEmpty;
	}

	public boolean kingProtectedUp(int rowKing, int columnKing){
		boolean checkBlack = false;

		for(int i=columnKing + 1 ; i < 9; i++){
			if(board[rowKing -1][i] == Pawn.BLACK){
				checkBlack = true;
				break;
			}
			if(board[rowKing -1][i] == Pawn.WHITE){
				checkBlack = false;
				break;
			}
		}
		if(checkBlack == false){
			for(int i=columnKing - 1;i > - 1; i--){
				if(board[rowKing -1][i] == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board[rowKing -1][i] == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
		}

		return !checkBlack;
	}

	public boolean kingProtectedDown(int rowKing, int columnKing){
		boolean checkBlack = false;

		for(int i=columnKing + 1 ; i < 9; i++){
			if(board[rowKing +1][i] == Pawn.BLACK){
				checkBlack = true;
				break;
			}
			if(board[rowKing +1][i] == Pawn.WHITE){
				checkBlack = false;
				break;
			}
		}
		if(checkBlack == false){
			for(int i=columnKing - 1;i > - 1; i--){
				if(board[rowKing +1][i] == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board[rowKing +1][i] == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
		}

		return !checkBlack;
	}

	public boolean kingProtectedLeft(int rowKing, int columnKing){
		boolean checkBlack = false;

		for(int i=rowKing + 1 ; i < 9; i++){
			if(board[i][columnKing - 1] == Pawn.BLACK){
				checkBlack = true;
				break;
			}
			if(board[i][columnKing - 1] == Pawn.WHITE){
				checkBlack = false;
				break;
			}
		}
		if(checkBlack == false){
			for(int i=rowKing - 1;i > - 1; i--){
				if(board[i][columnKing - 1] == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board[i][columnKing - 1] == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
		}

		return !checkBlack;
	}

	public boolean kingProtectedRight(int rowKing, int columnKing){
		boolean checkBlack = false;

		for(int i=rowKing + 1 ; i < 9; i++){
			if(board[i][columnKing + 1] == Pawn.BLACK){
				checkBlack = true;
				break;
			}
			if(board[i][columnKing + 1] == Pawn.WHITE){
				checkBlack = false;
				break;
			}
		}
		if(checkBlack == false){
			for(int i=rowKing - 1;i > - 1; i--){
				if(board[i][columnKing + 1] == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board[i][columnKing + 1] == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
		}

		return !checkBlack;
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