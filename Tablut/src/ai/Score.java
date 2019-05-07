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
		int numWhiteNearTheKing = game.numWhiteNearTheKing(state);
		int numBlackNearTheKing = game.numBlackNearTheKing(state);
		String currentKingPosition = state.getCurrentKingPosition();
		int rowKing = board.getRow(currentKingPosition);
		int columnKing = board.getColumn(currentKingPosition);

		int weightBlackNearTheKing = 0;

		switch (player){
			case "W" :
				//Numero di neri mangiati nel gioco
				scoreWhite += (16-numBlack);
				//Numero di neri mangiati dopo la mia azione
				scoreWhite += (oldNumBlack - numBlack);
				//Doppio scacco del re 
				if(rowKing == 2 || rowKing == 6 )
					if(board.isRowEmpty(rowKing))
						scoreWhite += (20 + numWhiteNearTheKing);

				//Doppio scacco del re 
				if(columnKing == 2 || columnKing == 6)
					if(board.isColumnEmpty(columnKing))
						scoreWhite += (20 + numWhiteNearTheKing);

				return scoreWhite;
				
				
			case "B":
				//Numero di bianchi mangiati nel gioco
				scoreBlack += (9-numWhite);
				//Numero di neri vicino al re
				scoreBlack += numBlackNearTheKing;
				//Numero di bianchi mangiati  dopo la mia azione
				scoreBlack += (oldNumWhite - numWhite);

				//Manca una pedina alla vittoria
				switch(currentKingPosition){
					case "e5":
						if(numBlackNearTheKing >= 3)
							scoreBlack += (20 + numBlackNearTheKing);

					case "e4":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);

					case "e6":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);

					case "d5":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);

					case "f5":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);

					default:
						if(numBlackNearTheKing >= 1)
							scoreBlack += (20 + numBlackNearTheKing);
				}
				return scoreBlack;

			default:
				return -1;
		}

	}



}