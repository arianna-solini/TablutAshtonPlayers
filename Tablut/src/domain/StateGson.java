package domain;

import java.io.Serializable;

import domain.Board.Pawn;
import domain.State.Turn;

/**
 * Utility class which is used to read the Gson state sent by the server
 * @author R.Vasumini, A.Solini
 */
public class StateGson implements Serializable{

	private static final long serialVersionUID = 1L;
	protected Pawn[][] board;
	protected Turn turn;

	public StateGson(){
		this.board = new Pawn[9][9];
		this.turn = Turn.BLACK;
	}
	 public Pawn[][] getBoard(){
		 return this.board;
	 }
	 public Turn getTurn(){
		 return this.turn;
	 }
	 public void setBoard(Pawn[][] board){
		 this.board = board;
	 }
	 public void setTurn(Turn turn){
		 this.turn = turn;
	 }
	
}