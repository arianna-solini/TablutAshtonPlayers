package ai;

import domain.Board;
import domain.State;
import domain.TablutGame;
import domain.Board.Pawn;

/**
 * Class used to calculate the score of an action
 * @author R.Vasumini, A.Solini
 */
public class Score{

	public static double calculateScore(TablutGame game, State state, String player){
		int scoreWhite = 0;
		int scoreBlack = 0;

		//Random r = new Random(System.currentTimeMillis());
		Board board = state.getBoard();

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
				if(rowKing == 2 || rowKing == 6)
					if(board.isRowEmpty(rowKing))
						scoreWhite += (20 + numWhiteNearTheKing);

				//Doppio scacco del re 
				if(columnKing == 2 || columnKing == 6)
					if(board.isColumnEmpty(columnKing))
						scoreWhite += (20 + numWhiteNearTheKing);

				//PUNTEGGI NEUTRI
				//Differenza tra mangiati neri e mangiati bianchi
				scoreWhite+=(16-numBlack) - (9-numWhite);
				
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
							scoreBlack += (30 + numBlackNearTheKing);
						else if(numBlackNearTheKing >= 2)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "e4":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (30 + numBlackNearTheKing);
						else if(numBlackNearTheKing >= 1)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "e6":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (30 + numBlackNearTheKing);
						else if(numBlackNearTheKing >= 1)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "d5":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (30 + numBlackNearTheKing);
						else if(numBlackNearTheKing >= 1)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					case "f5":
						if(numBlackNearTheKing >= 2)
							scoreBlack += (30 + numBlackNearTheKing);
						else if(numBlackNearTheKing >= 1)
							scoreBlack += (20 + numBlackNearTheKing);
						break;

					default:
						if(numBlackNearTheKing >= 1)
							scoreBlack += (30 + numBlackNearTheKing);
						else if(numBlackNearTheKing >= 0)
							scoreBlack += (20 + numBlackNearTheKing);
						break;
				}

				//PUNTEGGI NEUTRI
				//Differenza tra mangiati bianchi e mangiati neri
				scoreBlack+=(9-numWhite) - (16-numBlack); 

				//PUNTEGGI NEGATIVI
				scoreBlack -= numWhiteNearTheKing;
				
				/*if(board.isColumnWhite(2) || board.isColumnWhite(6))
					scoreBlack -= 1;
				if(board.isRowWhite(2) || board.isRowWhite(6)){
					scoreBlack -= 1;
				}*/
				/*if(board.isRowEmpty(rowKing) || board.isColumnEmpty(columnKing)){
					scoreBlack -= 20;
				}*/
				return scoreBlack;

			default:
				return -1;
		}

	}
	/*
	public boolean kingInE7E3Protected (Board board, int rowKing, int columnKing){
		boolean checkBlack = false;
		if(rowKing == 2){
			for(int i=columnKing + 1 ;i< board.getLength() - 1; i++){
				if(board.getPawn(rowKing + 1, i) == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board.getPawn(rowKing + 1, i) == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
			if(checkBlack == false){
				for(int i=columnKing - 1;i > - 1; i--){
					if(board.getPawn(rowKing + 1, i) == Pawn.BLACK){
						checkBlack = true;
						break;
					}
					if(board.getPawn(rowKing + 1, i) == Pawn.WHITE){
						checkBlack = false;
						break;
					}
				}
			}
		} //rowKing == 2

		if(rowKing == 6){
			for(int i=columnKing + 1 ;i< board.getLength() - 1; i++){
				if(board.getPawn(rowKing - 1, i) == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board.getPawn(rowKing - 1, i) == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
			if(checkBlack == false){
				for(int i=columnKing - 1;i > - 1; i--){
					if(board.getPawn(rowKing - 1, i) == Pawn.BLACK){
						checkBlack = true;
						break;
					}
					if(board.getPawn(rowKing - 1, i) == Pawn.WHITE){
						checkBlack = false;
						break;
					}
				}
			}

		} // rowKing = 6

		return !checkBlack;
	}

	public boolean kingInC5G5Protected (Board board, int rowKing, int columnKing){
		boolean checkBlack = false;
		if(columnKing == 2){
			for(int i=rowKing + 1 ;i< board.getLength() - 1; i++){
				if(board.getPawn(i, columnKing + 1) == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board.getPawn(i, columnKing + 1) == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
			if(checkBlack == false){
				for(int i=rowKing - 1;i > - 1; i--){
					if(board.getPawn(i, columnKing + 1) == Pawn.BLACK){
						checkBlack = true;
						break;
					}
					if(board.getPawn(i, columnKing + 1) == Pawn.WHITE){
						checkBlack = false;
						break;
					}
				}
			}
		}// columnKing == 2
		else if(columnKing == 6){
			for(int i=rowKing + 1 ;i< board.getLength() - 1; i++){
				if(board.getPawn(i, columnKing - 1) == Pawn.BLACK){
					checkBlack = true;
					break;
				}
				if(board.getPawn(i, columnKing - 1) == Pawn.WHITE){
					checkBlack = false;
					break;
				}
			}
			if(checkBlack == false){
				for(int i=rowKing - 1;i > - 1; i--){
					if(board.getPawn(i, columnKing - 1) == Pawn.BLACK){
						checkBlack = true;
						break;
					}
					if(board.getPawn(i, columnKing - 1) == Pawn.WHITE){
						checkBlack = false;
						break;
					}
				}
			}
		}//columnKing == 6
		return !checkBlack;
	}*/

}