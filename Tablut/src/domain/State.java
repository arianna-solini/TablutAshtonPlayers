package domain;

import java.io.Serializable;

/**
 * Class for a State of a game We have a representation of the board
 * and the turn
 * 
 * @author Andrea Piretti
 *
 */
public class State implements Serializable, Cloneable{
	private static final long serialVersionUID = 1L;
	/**
	 * Turn represent the player that has to move or the end of the game(A win
	 * by a player or a draw)
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

	public State() {
		this.board = new Board();
		this.turn = Turn.BLACK;
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
