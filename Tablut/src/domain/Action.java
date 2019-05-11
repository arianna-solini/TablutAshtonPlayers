package domain;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;

import domain.State.Turn;

/**
* This class represents an action of a player
* @author A.Piretti
*/
public class Action implements Serializable {

	/**
	 * Enum which represents the direction of an action
	 */
	public enum Direction {
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

	private static final long serialVersionUID = 1L;

	private String from;
	private String to;
	private Turn turn;
	private double score;

	public Action(String from, String to, Turn t) throws IOException {
		if (from.length() != 2 || to.length() != 2) {
			throw new InvalidParameterException("The FROM and TO string must have length=2");
		} 
		else {
			this.from = from;
			this.to = to;
			this.turn = t;
		}
	}

	public String getFrom() {
		return this.from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Turn getTurn() {
		return turn;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	public String toString() {
		return "Turn: " + this.turn + " " + "Pawn from " + from + " to " + to;
	}

	/**
	 * @return means the index of the column where the pawn is moved from
	 */
	public int getColumnFrom() {
		return Character.toLowerCase(this.from.charAt(0)) - 97;
	}

	/**
	 * @return means the index of the column where the pawn is moved to
	 */
	public int getColumnTo() {
		return Character.toLowerCase(this.to.charAt(0)) - 97;
	}

	/**
	 * @return means the index of the row where the pawn is moved from
	 */
	public int getRowFrom() {
		return Integer.parseInt(this.from.charAt(1) + "") - 1;
	}

	/**
	 * @return means the index of the row where the pawn is moved to
	 */
	public int getRowTo() {
		return Integer.parseInt(this.to.charAt(1) + "") - 1;
	}

	/**
	 * @return The Direction of the action
	 * @author R.Vasumini, A.Solini
	 */
	public Direction getDirection(){
		if(this.getRowFrom() == this.getRowTo())
			if(this.getColumnFrom() > this.getColumnTo())
				return Direction.LEFT;
			else
				return Direction.RIGHT;
		else
			if(this.getRowFrom() > this.getRowTo())
				return Direction.UP;
			else
				return Direction.DOWN;
	}

	//Metodi per ricavare lo score caratteristico di uno stato provocato dall'azione

	public void setScore(double score){
		this.score = score;
	}

	public double getScore(){
		return this.score;
	}

}
