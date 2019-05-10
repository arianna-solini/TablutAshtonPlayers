package ai;

import java.util.Random;
import domain.Board;
import domain.State;
import domain.TablutGame;

/**
 * Class used for calculate the score of an action
 * @author R.Vasumini, A.Solini
 */
public class Score{

	public static double calculateScore(TablutGame game, State state, String player){
		int scoreWhite = 0;
		int scoreBlack = 0;

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

		switch (player){
			case "W" :
				//PUNTEGGI POSITIVI
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
				
				//PUNTEGGI NEGATIVI
				scoreWhite -= numBlackNearTheKing;
				return scoreWhite;
				
				
			case "B":
				//PUNTEGGI POSITIVI
				//Numero di bianchi mangiati nel gioco
				scoreBlack += (9-numWhite);
				//Numero di neri vicino al re
				scoreBlack += numBlackNearTheKing;
				//Numero di bianchi mangiati  dopo la mia azione
				scoreBlack += (oldNumWhite - numWhite);
				
				//TODO se non si è nella situazione di scacco, la mangiata vale 2 e la vicinanza al re solo 1, tenerne conto

				//Manca una pedina alla vittoria
				switch(currentKingPosition){
					case "e5":
						if(numBlackNearTheKing >= 3)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "e4":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "e6":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "d5":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "f5":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					default:
						if(numBlackNearTheKing >= 1)
							scoreBlack += (20 + numBlackNearTheKing);
						break;
				}

				//PUNTEGGI NEGATIVI
				//scoreBlack -= numWhiteNearTheKing;
				return scoreBlack;

			default:
				return -1;
		}

	}



}