package ai;

import java.util.ArrayList;

import domain.Action;
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
		//TODO lastAction potrebbe essere usata per vedere se certe pedine avversarie si stanno muovendo in una certa direzione
		int rowTo = this.state.getLastAction().getRowTo(), columnTo = this.state.getLastAction().getColumnTo();
		switch (player){
			case "W" :
				ArrayList<Action> possibleWinActions = new ArrayList<Action>();
				possibleWinActions = game.canKingWin(state);
				scoreWhite = state.getNumWhite() - state.getNumBlack() - game.numBlackNearTheKing(state);
				scoreWhite += (state.getOldNumBlack() - state.getNumBlack());
				if(possibleWinActions != null)
					scoreWhite += 20;
				return scoreWhite;
				
			case "B":
				scoreBlack = state.getNumBlack() - state.getNumWhite() + game.numBlackNearTheKing(state);
				scoreBlack += (state.getOldNumWhite() - state.getNumWhite());
				if (board.isColumnEmpty(columnTo) || board.isRowEmpty(rowTo))
					scoreBlack += 1;
				return scoreBlack;

			default:
				return -1;
		}

	}



}