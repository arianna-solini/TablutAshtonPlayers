package domain;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import domain.Board.Pawn;
import domain.Board.Position;

/**
 * Class for a State of a game We have a representation of the board and the
 * turn
 * 
 * @author Andrea Piretti
 *
 */
public class State implements Serializable, Cloneable {
	private static final long serialVersionUID = 1L;

	/**
	 * Turn represent the player that has to move or the end of the game(A win by a
	 * player or a draw)
	 * 
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
	private HashMap<String, ArrayList<String>> possibleWhiteActions = new HashMap<String, ArrayList<String>>();
	private HashMap<String, ArrayList<String>> possibleBlackActions = new HashMap<String, ArrayList<String>>();

	private void InitActions() {
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
	}

	private ArrayList<String> getPossibleTo(String from) {
		int row = this.board.getRow(from);
		int column = this.board.getColumn(from);
		ArrayList<String> result = new ArrayList<String>();

		// verso destra
		for (int i = column + 1; i < 9; i++) {
			if (this.board.getPawn(row, i) == Pawn.EMPTY) {
				result.add(this.board.getBox(row, i));
			}
		}

		// verso sinistra
		for (int i = 0; i < column; i++) {
			if (this.board.getPawn(row, i) == Pawn.EMPTY) {
				result.add(this.board.getBox(row, i));
			}
		}

		// verso l'alto
		for (int i = 0; i < row; i++) {
			if (this.board.getPawn(i, column) == Pawn.EMPTY) {
				result.add(this.board.getBox(i, column));
			}
		}

		// verso il basso
		for (int i = row + 1; i < 9; i++) {
			if (this.board.getPawn(i, column) == Pawn.EMPTY) {
				result.add(this.board.getBox(i, column));
			}
		}
		return result;
	}

	public void updatePossibleActions(String oldFrom, String newFrom, Turn turn) {
		if (turn == Turn.BLACK) {
			this.possibleBlackActions.remove(oldFrom);
			this.possibleBlackActions.put(newFrom, getPossibleTo(newFrom));
		}
		if (turn == Turn.WHITE) {
			this.possibleWhiteActions.remove(oldFrom);
			this.possibleWhiteActions.put(newFrom, getPossibleTo(newFrom));
		}
	}

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

	public State() {
		this.board = new Board();
		this.turn = Turn.BLACK;
		InitActions();
	}
	
	public State(Board board, Turn turn){
		this.board = board;
		this.turn = turn;
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

	public HashMap<String, ArrayList<String>> getPossibleWhiteActions(){
		return this.possibleWhiteActions;
	}

	public HashMap<String, ArrayList<String>> getPossibleBlackActions(){
		return this.possibleBlackActions;
	}

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

	@Override
	public String toString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString());

		result.append("-");
		result.append("\n");

		// TURNO
		result.append(this.turn.toString());

		return result.toString();
	}
	
	public String toLinearString() {
		StringBuffer result = new StringBuffer();

		// board
		result.append("");
		result.append(this.boardString().replace("\n", ""));
		result.append(this.turn.toString());

		return result.toString();
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.board.getBoard() == null) ? 0 : this.board.getBoard().hashCode());
		result = prime * result + ((this.turn == null) ? 0 : this.turn.hashCode());
		return result;
	}

	@Override
	public State clone() {
		State result = new State();

		Board oldboard = this.board;
		Board newboard = result.getBoard();

		for (int i = 0; i < this.board.getBoard().length; i++) {
			for (int j = 0; j < this.board.getBoard()[i].length; j++) {
				newboard.setPawn(i, j, oldboard.getPawn(i, j));
			}
		}

		result.setBoard(newboard);
		result.setTurn(this.turn);
		return result;
	}

}
