package ai;

import java.util.HashMap;

import domain.Board;
import domain.State;
import domain.TablutGame;
import domain.Board.Diagonal;
import domain.Board.Pawn;

/**
 * Class used to calculate the score of an action
 * @author R.Vasumini, A.Solini
 */
public class Score{

	public static double calculateScore(TablutGame game, State state, String player){
		int scoreWhite = 0;
		int scoreBlack = 0;

		Board board = state.getBoard();
		int numBlack = state.getNumBlack();
		int numWhite = state.getNumWhite();

		int numBlackNearTheKing = game.numBlackNearTheKing(state);

		String currentKingPosition = state.getCurrentKingPosition();
		int rowKing = board.getRow(currentKingPosition);
		int columnKing = board.getColumn(currentKingPosition);

		HashMap<Diagonal, String[]> backDiagonals = board.getDiagonals();

		boolean leftUpDiagonalSet = false;
		boolean leftDownDiagonalSet = false;
		boolean rightUpDiagonalSet = false;
		boolean rightDownDiagonalSet = false;
		boolean trovato;

		//Checks if the diagonals are completed by black pawns
		if(board.getPawn("g8") == Pawn.BLACK && board.getPawn("h7") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.RIGHTDOWNBIG)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				rightDownDiagonalSet = true;
			}
			
		} else if(board.getPawn("f7") == Pawn.BLACK && board.getPawn("g6") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.RIGHTDOWNSMALL)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				rightDownDiagonalSet = true;
			}
		}
		if(board.getPawn("g2") == Pawn.BLACK && board.getPawn("h3") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.RIGHTUPBIG)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				rightUpDiagonalSet = true;
			}

		} else if(board.getPawn("f3") == Pawn.BLACK && board.getPawn("g4") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.RIGHTUPSMALL)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				rightUpDiagonalSet = true;
			}
		}
		if(board.getPawn("b3") == Pawn.BLACK && board.getPawn("c2") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.LEFTUPBIG)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato  = true;
					break;
				}
			}
			if(!trovato){
				leftUpDiagonalSet = true;
			}

		} else if(board.getPawn("c4") == Pawn.BLACK && board.getPawn("d3") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.LEFTUPSMALL)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				leftUpDiagonalSet = true;
			}
		}
		if(board.getPawn("b7") == Pawn.BLACK && board.getPawn("c8") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.LEFTDOWNBIG)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				leftDownDiagonalSet = true;
			}

		} else if(board.getPawn("c6") == Pawn.BLACK && board.getPawn("d7") == Pawn.BLACK){
			trovato = false;
			for(String box : backDiagonals.get(Diagonal.LEFTDOWNSMALL)){
				if(board.getPawn(box) != Pawn.EMPTY){
					trovato = true;
					break;
				}
			}
			if(!trovato){
				leftDownDiagonalSet = true;
			}
		}

		switch (player){
			case "W" :
				//PUNTEGGI POSITIVI
				//Numero di neri mangiati nel gioco
				scoreWhite += (16-numBlack)*3;

				//Doppio scacco del re 
				if(rowKing == 2){
					if(board.isEmptyLeft(currentKingPosition) && board.isEmptyRight(currentKingPosition) ){
						if(currentKingPosition.equals("e3")){
							if(board.kingProtectedDown(rowKing, columnKing)){ 
								scoreWhite += 50;
							}
						} else if(board.getPawnUp(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteDown(currentKingPosition) && board.kingProtectedDown(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnDown(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteUp(currentKingPosition) && board.kingProtectedUp(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else{
							scoreWhite += 50;
						}
					}
				}

				//Doppio scacco del re 
				if(rowKing == 6){
					if(board.isEmptyLeft(currentKingPosition) && board.isEmptyRight(currentKingPosition)){
						if(currentKingPosition.equals("e7")){
							if(board.kingProtectedUp(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnUp(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteDown(currentKingPosition) && board.kingProtectedDown(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnDown(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteUp(currentKingPosition) && board.kingProtectedUp(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else{
							scoreWhite += 50;
						}
					}
				}

				//Doppio scacco del re 
				if(columnKing == 2){
					if(board.isEmptyDown(currentKingPosition) && board.isEmptyUp(currentKingPosition)){
						if(currentKingPosition.equals("c5")){
							if(board.kingProtectedRight(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnRight(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteLeft(currentKingPosition) && board.kingProtectedLeft(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnLeft(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteRight(currentKingPosition) && board.kingProtectedRight(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else{
							scoreWhite += 50;
						}
					}
				}

				//Doppio scacco del re 
				if(columnKing == 6){
					if(board.isEmptyDown(currentKingPosition) && board.isEmptyUp(currentKingPosition)){
						if(currentKingPosition.equals("g5")){
							if(board.kingProtectedLeft(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnRight(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteLeft(currentKingPosition) && board.kingProtectedLeft(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else if(board.getPawnLeft(currentKingPosition) == Pawn.BLACK){
							if(board.isWhiteRight(currentKingPosition) && board.kingProtectedRight(rowKing, columnKing)){
								scoreWhite += 50;
							}
						} else{
							scoreWhite += 50;
						}
					}
				}

				//Creazione di aperture protette per il re, il controllo del numero di catture maggiore di quello dei neri è dovuto al
				//non essere troppo difensivi
				if(currentKingPosition.equals("e5")){
					if(board.getPawnDiagonalLeftDown(currentKingPosition) == Pawn.WHITE){
						scoreWhite += 2;
					}
					
					if(board.getPawnDiagonalLeftUp(currentKingPosition) == Pawn.WHITE){
						scoreWhite += 2;
					}

					if(board.getPawnDiagonalRightDown(currentKingPosition) == Pawn.WHITE){
						scoreWhite += 2;
					}

					if(board.getPawnDiagonalRightUp(currentKingPosition) == Pawn.WHITE){
						scoreWhite += 2;
					}
				}

				if(rowKing == 2 && board.isEmptyLeft(currentKingPosition) && board.isEmptyRight(currentKingPosition)){
					scoreWhite += 10;
				}
				if(rowKing == 6 && board.isEmptyLeft(currentKingPosition) && board.isEmptyRight(currentKingPosition)){
					scoreWhite += 10;
				}
				if(columnKing == 2 && board.isEmptyDown(currentKingPosition) && board.isEmptyUp(currentKingPosition)){
					scoreWhite += 10;
				}
				if(columnKing == 6 && board.isEmptyDown(currentKingPosition) && board.isEmptyUp(currentKingPosition)){
					scoreWhite += 10;
				}

				//PUNTEGGI NEUTRI
				//Differenza tra mangiati neri e mangiati bianchi
				//scoreWhite+=(16-numBlack) - (9-numWhite);
				
				//PUNTEGGI NEGATIVI
				scoreWhite -= numBlackNearTheKing;
				
				return scoreWhite;
				
				
			case "B":
				//PUNTEGGI POSITIVI
				//Numero di bianchi mangiati nel gioco
				scoreBlack += ((9-numWhite)*2);
				//Numero di neri vicino al re
				scoreBlack += (numBlackNearTheKing*2);

				//Manca una pedina alla vittoria o due
				switch(currentKingPosition){
					case "e5":
						if(numBlackNearTheKing >= 3){
							scoreBlack += (50 + numBlackNearTheKing);
						}
						else if(numBlackNearTheKing >= 2){
							scoreBlack += (40 + numBlackNearTheKing);
						}
						break;

					case "e4":
						if(numBlackNearTheKing >= 2){
							scoreBlack += (50 + numBlackNearTheKing);
						}
						else if(numBlackNearTheKing >= 1){
							scoreBlack += (40 + numBlackNearTheKing);
						}
						break;

					case "e6":
						if(numBlackNearTheKing >= 2){
							scoreBlack += (50 + numBlackNearTheKing);
						}
						else if(numBlackNearTheKing >= 1){
							scoreBlack += (40 + numBlackNearTheKing);
						}
						break;

					case "d5":
						if(numBlackNearTheKing >= 2){
							scoreBlack += (50 + numBlackNearTheKing);
						}
						else if(numBlackNearTheKing >= 1){
							scoreBlack += (40 + numBlackNearTheKing);
						}
						break;

					case "f5":
						if(numBlackNearTheKing >= 2){
							scoreBlack += (50 + numBlackNearTheKing);
						}
						else if(numBlackNearTheKing >= 1){
							scoreBlack += (40 + numBlackNearTheKing);
						}
						break;

					default:
						if(numBlackNearTheKing >= 1){
							scoreBlack += (40 + numBlackNearTheKing);
						}
						break;
				}

				
				//Coppia di diagonali che evita il doppio scacco del re
				if(leftDownDiagonalSet && rightUpDiagonalSet){
					scoreBlack += 10;
				}
				if(rightDownDiagonalSet && leftUpDiagonalSet){
					scoreBlack+= 10;
				}

				//Punteggio per ogni diagonale settata
				if(leftDownDiagonalSet){
					scoreBlack += 5;
				}
				if(leftUpDiagonalSet){
					scoreBlack += 5;
				}
				if(rightDownDiagonalSet){
					scoreBlack +=5;
				}
				if(rightUpDiagonalSet){
					scoreBlack += 5;
				}

				//PUNTEGGI NEUTRI
				//Differenza tra mangiati bianchi e mangiati neri
				//scoreBlack+=(9-numWhite) - (16-numBlack); 

				//PUNTEGGI NEGATIVI
				//Lasciar scoperte le righe e colonne con possibile doppio scacco da parte del re
				if(board.isColumnEmpty(2) || board.isColumnEmpty(6))
					scoreBlack -= 1;
				if(board.isRowEmpty(2) || board.isRowEmpty(6)){
					scoreBlack -= 1;
				}

				return scoreBlack;

			default:
				return -1;
		}

	}

}