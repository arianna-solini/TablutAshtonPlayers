package ai;

import java.util.ArrayList;
import java.util.Random;

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
		Random r = new Random(System.currentTimeMillis());
		Board board = state.getBoard();
		int captureMultiplier=1;
		int turnNumber = state.getTurnNumber();
		int numBlack = state.getNumBlack();
		int numWhite = state.getNumWhite();
		int oldNumBlack = state.getOldNumBlack();
		int oldNumWhite = state.getOldNumWhite();
		int numBlackNearTheKing = game.numBlackNearTheKing(state);
		String currentKingPosition = state.getCurrentKingPosition();

		//TODO lastAction potrebbe essere usata per vedere se certe pedine avversarie si stanno muovendo in una certa direzione
		int rowTo = this.state.getLastAction().getRowTo(), columnTo = this.state.getLastAction().getColumnTo();
		switch (player){
			case "W" :
				
				if(turnNumber < 20)
					captureMultiplier = 2;
				else if(turnNumber < 30)
					captureMultiplier = 5;
				//Differenza tra quelle che ho mangiato io e quelle che ha mangiato lui
				scoreWhite = ((16-numBlack)*captureMultiplier - (9-numWhite));
				//Negative score of opponent's pawns
				//scoreWhite -= numBlack;
				switch(currentKingPosition){
					case "e5":
						if(numBlackNearTheKing >= 2)
							scoreWhite -= numBlackNearTheKing;

					case "e4":
						if(numBlackNearTheKing >= 2)
							scoreWhite -= numBlackNearTheKing;

					case "e6":
						if(numBlackNearTheKing >= 2)
							scoreWhite -= numBlackNearTheKing;

					case "d5":
						if(numBlackNearTheKing >= 2)
							scoreWhite -= numBlackNearTheKing;

					case "f5":
						if(numBlackNearTheKing >= 2)
							scoreWhite -= numBlackNearTheKing;

					default:
						if(numBlackNearTheKing > 0)
							scoreWhite -= numBlackNearTheKing;
				}
				scoreWhite += (oldNumBlack- numBlack)*2;
				ArrayList<Action> possibleWinActions = game.canKingWin(state);
				if(possibleWinActions != null)
					scoreWhite += 20;
				return scoreWhite + r.nextDouble();
				
			case "B":
				if(turnNumber < 20)
					captureMultiplier = 5;
				else if(turnNumber < 30)
					captureMultiplier = 2;
				//Differenza tra quelle che ho mangiato io e quelle che ha mangiato lui
				scoreBlack = ((9-numWhite)*captureMultiplier - (16 - numBlack));
				//scoreBlack -= numWhite;
				scoreBlack += (oldNumWhite - numWhite)*2;
				//if (board.isColumnEmpty(columnTo) || board.isRowEmpty(rowTo))
					//scoreBlack += 1;
				return scoreBlack + r.nextDouble();

			default:
				return -1;
		}

	}



}