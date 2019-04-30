package ai;

import domain.Board;
import domain.State;
import domain.TablutGame;
//TODO decidere se farla statica
/**
 * Class used for calculate the score of an action
 * @author R.Vasumini, A.Solini
 */
public class Score{

	private State state;
	private String player;

	// Values needed by the eval function 
	public double scoreWhite;
	public double scoreBlack;

	public Score(String player, State state){
		this.player = player;
		this.state = state;
		this.scoreBlack = 0;
		this.scoreWhite = 0;
	}

	public void setState(State state){
		this.state = state;
	}

	public void setPlayer(String player){
		this.player = player;
	}

	public double calculateScore(TablutGame game){
		Board board = state.getBoard();
		int rowTo = this.state.getLastAction().getRowTo(), columnTo = this.state.getLastAction().getColumnTo();
		switch (player){
			case "W" :
				scoreWhite += state.getNumWhite() - state.getNumBlack();
				
				
				

				return scoreWhite;
				
			case "B":
				scoreBlack += state.getNumBlack() - state.getNumWhite();
				
				
				return scoreBlack;

			default:
				return -1;
		}

	}

}