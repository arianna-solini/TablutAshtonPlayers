package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import aima.core.search.adversarial.Game;

import domain.Action.Direction;
import domain.Board.Pawn;
import domain.Board.Position;
import domain.State.Turn;
import exceptions.*;

/**
 * Game engine inspired by the Ashton Rules of Tablut
 * @author A. Piretti, Andrea Galassi
 */
public class TablutGame implements Game<State, Action, String> {

	private State initialState = new State();
	private int movesWithoutCapturing;

	//TODO pensare a come fare un nostro eventuale costruttore di  TablutGame e se implementare il pareggio
	public TablutGame(){
		super();
	}

	/**
	 * Method which controls if a pawn will capture an adversarial pawn given the following parameters:
	 * @param board Current board
	 * @param rowTo arrival row
	 * @param columnTo arrival column
	 * @param d Direction to control, NOT THE ACTION'S DIRECTION
	 * @param t Player which tries to capture
	 * @return {@code true} if the conditions are favorable to capture, {@code false} otherwhise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean checkCaptureConditions(Board board, int rowTo, int columnTo, Direction d, Turn t) {
		HashMap<String, Position> positions = board.getPositions();
		switch (t) {
		case WHITE:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 
						&& board.getPawn(rowTo + 1, columnTo).equalsPawn("B")
						&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("W")
							|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
							|| board.getPawn(rowTo + 2, columnTo).equalsPawn("K")
							|| ((positions.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)
								// &&!(board.getPawn(rowTo+2, columnTo).equalsPawn("B"))
								&& !(columnTo == 8 && rowTo + 2 == 4) 
								&& !(columnTo == 4 && rowTo + 2 == 0)
								&& !(columnTo == 4 && rowTo + 2 == 8) 
								&& !(columnTo == 0 && rowTo + 2 == 4))));

			case UP:
				return (rowTo > 1 
						&& board.getPawn(rowTo - 1, columnTo).equalsPawn("B")
						&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("W")
							|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
							|| board.getPawn(rowTo - 2, columnTo).equalsPawn("K")
							|| ((positions.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)
								// &&!(board.getPawn(rowTo-2, columnTo).equalsPawn("B"))
								&& !(columnTo == 8 && rowTo - 2 == 4) 
								&& !(columnTo == 4 && rowTo - 2 == 0)
								&& !(columnTo == 4 && rowTo - 2 == 8) 
								&& !(columnTo == 0 && rowTo - 2 == 4))));

			case RIGHT:
				return (columnTo < board.getLength() - 2 
						&& board.getPawn(rowTo, columnTo + 1).equalsPawn("B")
						&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("W")
							|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.THRONE)
							|| board.getPawn(rowTo, columnTo + 2).equalsPawn("K")
							|| ((positions.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)
								// &&!(board.getPawn(rowTo, columnTo+2).equalsPawn("B"))
								&& !(columnTo + 2 == 8 && rowTo == 4) 
								&& !(columnTo + 2 == 4 && rowTo == 0)
								&& !(columnTo + 2 == 4 && rowTo == 8) 
								&& !(columnTo + 2 == 0 && rowTo == 4))));

			case LEFT:
				return (columnTo > 1 
						&& board.getPawn(rowTo, columnTo - 1).equalsPawn("B")
						&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("W")
							|| (positions.get(board.getBox(rowTo, columnTo - 2)) == Position.THRONE)
							|| board.getPawn(rowTo, columnTo - 2).equalsPawn("K")
							|| ((positions.get(board.getBox(rowTo, columnTo - 2)) == Position.CITADEL)
								// &&!(board.getPawn(rowTo, columnTo-2).equalsPawn("B"))
								&& !(columnTo - 2 == 8 && rowTo == 4) 
								&& !(columnTo - 2 == 4 && rowTo == 0)
								&& !(columnTo - 2 == 4 && rowTo == 8) 
								&& !(columnTo - 2 == 0 && rowTo == 4))));

			default:
				return false;
			}

		case BLACK:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 
						&& board.getPawn(rowTo + 1, columnTo).equalsPawn("W")
						&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
							|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
							|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)));

			case UP:
				return (rowTo > 1 
						&& board.getPawn(rowTo - 1, columnTo).equalsPawn("W")
						&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("B")
							|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
							|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)));

			case RIGHT:
				return (columnTo < board.getLength() - 2 
						&& board.getPawn(rowTo, columnTo + 1).equalsPawn("W")
						&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("B")
							|| positions.get(board.getBox(rowTo, columnTo + 2)) == Position.THRONE
							|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)));

			case LEFT:
				return (columnTo > 1
						&& board.getPawn(rowTo, columnTo - 1).equalsPawn("W")
						&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("B")
							|| (positions.get(board.getBox(rowTo, columnTo - 2)) == Position.THRONE)
							|| (positions.get(board.getBox(rowTo, columnTo - 2)) == Position.CITADEL)));

			default:
				return false;
			}

		default:
			return false;
		}
	}

	/**
	 * Method which controls the win of a specified player given the following parameters: 
	 * @param board Current board
	 * @param rowTo arrival row
	 * @param columnTo arrival column
	 * @param d Direction to control, NOT THE ACTION'S DIRECTION
	 * @param t Player which tries to win
	 * @return {@code true} if the conditions are favorable to win, {@code false} otherwhise
	 * @author R.Vasumini, A.Solini
	 */
	public boolean checkWin(Board board, int rowTo, int columnTo, Direction d, Turn t) {
		HashMap<String, Position> positions = board.getPositions();
		switch (t) {
		case WHITE:
			return (rowTo == 0 || rowTo == board.getLength() - 1 || columnTo == 0 || columnTo == board.getLength() - 1)
					&& (board.getPawn(rowTo, columnTo).equalsPawn("K"));

		case BLACK:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 && board.getPawn(rowTo + 1, columnTo).equalsPawn("K")
						&& ((board.getBox(rowTo + 1, columnTo).equals("e5") // king on the throne
							&& board.getPawn(5, 4).equalsPawn("B") 
							&& board.getPawn(4, 5).equalsPawn("B")
							&& board.getPawn(4, 3).equalsPawn("B"))
							|| (board.getBox(rowTo + 1, columnTo).equals("e4") // king near the throne
								&& board.getPawn(3, 3).equalsPawn("B") 
								&& board.getPawn(3, 5).equalsPawn("B"))
							|| (board.getBox(rowTo + 1, columnTo).equals("d5")
								&& board.getPawn(4, 2).equalsPawn("B") 
								&& board.getPawn(5, 3).equalsPawn("B"))
							|| (board.getBox(rowTo + 1, columnTo).equals("f5")
								&& board.getPawn(4, 6).equalsPawn("B") 
								&& board.getPawn(5, 5).equalsPawn("B"))
							|| (!board.getBox(rowTo + 1, columnTo).equals("d5") // king outside the throne areas
								&& !board.getBox(rowTo + 1, columnTo).equals("e4")
								&& !board.getBox(rowTo + 1, columnTo).equals("f5")
								&& !board.getBox(rowTo + 1, columnTo).equals("e5")
								&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
									|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL))))

						);

			case UP:
				return (rowTo > 1 && board.getPawn(rowTo - 1, columnTo).equalsPawn("K")
						&& ((board.getBox(rowTo - 1, columnTo).equals("e5") // king on the throne
							&& board.getPawn(3, 4).equalsPawn("B") 
							&& board.getPawn(4, 5).equalsPawn("B")
							&& board.getPawn(4, 3).equalsPawn("B"))
							|| (board.getBox(rowTo - 1, columnTo).equals("e6") // king near the throne
								&& board.getPawn(5, 3).equalsPawn("B") 
								&& board.getPawn(5, 5).equalsPawn("B"))
							|| (board.getBox(rowTo - 1, columnTo).equals("d5")
								&& board.getPawn(4, 2).equalsPawn("B") 
								&& board.getPawn(3, 3).equalsPawn("B"))
							|| (board.getBox(rowTo - 1, columnTo).equals("f5")
								&& board.getPawn(4, 4).equalsPawn("B") 
								&& board.getPawn(3, 5).equalsPawn("B"))
							|| (!board.getBox(rowTo - 1, columnTo).equals("d5") // king outside the throne areas
								&& !board.getBox(rowTo - 1, columnTo).equals("e4")
								&& !board.getBox(rowTo - 1, columnTo).equals("f5")
								&& !board.getBox(rowTo - 1, columnTo).equals("e5")
								&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("B")
									|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL))))
						);

			case RIGHT:
				return (columnTo < board.getLength() - 2 && board.getPawn(rowTo, columnTo + 1).equalsPawn("K")
						&& ((board.getBox(rowTo, columnTo + 1).equals("e5") // king on the throne
							&& board.getPawn(3, 4).equalsPawn("B") 
							&& board.getPawn(4, 5).equalsPawn("B")
							&& board.getPawn(5, 4).equalsPawn("B"))
							|| (board.getBox(rowTo, columnTo + 1).equals("e4") // king near the throne
								&& board.getPawn(2, 4).equalsPawn("B") 
								&& board.getPawn(3, 5).equalsPawn("B"))
							|| (board.getBox(rowTo, columnTo + 1).equals("e6")
								&& board.getPawn(5, 5).equalsPawn("B") 
								&& board.getPawn(6, 4).equalsPawn("B"))
							|| (board.getBox(rowTo, columnTo + 1).equals("d5")
								&& board.getPawn(3, 3).equalsPawn("B") 
								&& board.getPawn(3, 5).equalsPawn("B"))
							|| (!board.getBox(rowTo, columnTo + 1).equals("d5") // king outside the throne areas
								&& !board.getBox(rowTo, columnTo + 1).equals("e6")
								&& !board.getBox(rowTo, columnTo + 1).equals("e4")
								&& !board.getBox(rowTo, columnTo + 1).equals("e5")
								&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("B") 
									|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL))))
						);

			case LEFT:
				return (columnTo > 1 && board.getPawn(rowTo, columnTo - 1).equalsPawn("K") 
						&& ((board.getBox(rowTo, columnTo - 1).equals("e5") // king on the throne
							&& board.getPawn(3, 4).equalsPawn("B") 
							&& board.getPawn(4, 3).equalsPawn("B")
							&& board.getPawn(5, 4).equalsPawn("B"))
							|| (board.getBox(rowTo, columnTo - 1).equals("e4") // king near the throne
								&& board.getPawn(2, 4).equalsPawn("B") 
								&& board.getPawn(3, 3).equalsPawn("B"))
							|| (board.getBox(rowTo, columnTo - 1).equals("f5")
								&& board.getPawn(3, 5).equalsPawn("B") 
								&& board.getPawn(5, 5).equalsPawn("B"))
							|| (board.getBox(rowTo, columnTo + 1).equals("e6")
								&& board.getPawn(5, 3).equalsPawn("B") 
								&& board.getPawn(6, 4).equalsPawn("B"))
							|| (!board.getBox(rowTo, columnTo - 1).equals("e5") // king outside the throne areas
								&& !board.getBox(rowTo, columnTo - 1).equals("e6")
								&& !board.getBox(rowTo, columnTo - 1).equals("e4")
								&& !board.getBox(rowTo, columnTo - 1).equals("f5")
								&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("B") 
									|| (positions.get(board.getBox(rowTo, columnTo - 2)) == Position.CITADEL))))
						);

			default:
				return false;
			}

		default:
			return false;
		}

	}

	public State checkMove(State state, Action a)
		throws BoardException, ActionException, StopException, PawnException, DiagonalException, 
		ClimbingException, ThroneException, OccupitedException, ClimbingCitadelException, CitadelException {

		Board board = state.getBoard();
		HashMap<String, Position> positions = board.getPositions();

		// Checks strings length 
		if (a.getTo().length() != 2 || a.getFrom().length() != 2) {
			throw new ActionException(a);
		}

		int rowFrom = a.getRowFrom(), columnFrom = a.getColumnFrom();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();

		// Checks if iI try to go outside the board
		if (columnFrom > board.getLength() - 1 || rowFrom > board.getLength() - 1 || rowTo > board.getLength() - 1
				|| columnTo > board.getLength() || columnFrom < 0 || rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			throw new BoardException(a);
		}

		// Checks if i try to go on the throne
		if (positions.get(board.getBox(rowTo, columnTo)) == Position.THRONE) {
			throw new ThroneException(a);
		}

		// Checks if my arrival position is occupied
		if (!board.getPawn(rowTo, columnTo).equalsPawn(Pawn.EMPTY.toString())) {
			throw new OccupitedException(a);
		}
		if (positions.get(board.getBox(rowTo, columnTo)) == Position.CITADEL
				&& positions.get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
			throw new CitadelException(a);
		}
		if (positions.get(board.getBox(rowTo, columnTo)) == Position.CITADEL
				&& positions.get(board.getBox(rowFrom, columnFrom)) == Position.CITADEL) {
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					throw new CitadelException(a);
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					throw new CitadelException(a);
				}
			}

		}

		// Checks if  I try not to move
		if (rowFrom == rowTo && columnFrom == columnTo) {
			throw new StopException(a);
		}

		// Checks if I'm trying to move an adversarial pawn
		if (state.getTurn().equalsTurn(State.Turn.WHITE.toString())) {
			if (!board.getPawn(rowFrom, columnFrom).equalsPawn("W")
					&& !board.getPawn(rowFrom, columnFrom).equalsPawn("K")) {
				throw new PawnException(a);
			}
		}
		if (state.getTurn().equalsTurn(State.Turn.BLACK.toString())) {
			if (!board.getPawn(rowFrom, columnFrom).equalsPawn("B")) {
				throw new PawnException(a);
			}
		}

		// Checks if I try to do a diagonal move
		if (rowFrom != rowTo && columnFrom != columnTo) {
			throw new DiagonalException(a);
		}

		// Checks if I try to jump some pawns
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!board.getPawn(rowFrom, i).equalsPawn(Pawn.EMPTY.toString())) {
						if (positions.get(board.getBox(rowFrom, i)) == Position.THRONE) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (positions.get(board.getBox(rowFrom, i)) == Position.CITADEL
							&& positions.get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!board.getPawn(rowFrom, i).equalsPawn(Pawn.EMPTY.toString())) {
						if (positions.get(board.getBox(rowTo, columnTo)) == Position.THRONE) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (positions.get(board.getBox(rowFrom, i)) == Position.CITADEL
							&& positions.get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!board.getPawn(i, columnFrom).equalsPawn(Pawn.EMPTY.toString())) {
						if (positions.get(board.getBox(i, columnFrom))
								.equalsPosition(Position.THRONE.toString())) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (positions.get(board.getBox(i, columnFrom)) == Position.CITADEL
							&& positions.get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!board.getPawn(i, columnFrom).equalsPawn(Pawn.EMPTY.toString())) {
						if (positions.get(board.getBox(i, columnFrom))
								.equalsPosition(Position.THRONE.toString())) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (positions.get(board.getBox(i, columnFrom)) == Position.CITADEL
							&& positions.get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			}
		}

		// Move checked, makes the move
		state = this.movePawn(state, a);

		// Checks if  the move involves a capture
		if (state.getTurn().equalsTurn("B")) {
			state = this.checkCaptureBlack(state, a);
		} else if (state.getTurn().equalsTurn("W")) {
			state = this.checkCaptureWhite(state, a);
		}

		return state;
	}

	private State movePawn(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom(), columnFrom = a.getColumnFrom();
		Pawn pawn = board.getPawn(rowFrom, columnFrom);
		Board newBoard = board;

		newBoard.removePawn(rowFrom, columnFrom);
		// Puts the pawn moved in the board
		newBoard.setPawn(rowTo, columnTo, pawn);
		//If the moved pawn is the king, changes his current position
		if(pawn == Pawn.KING)
			state.setCurrentKingPosition(a.getTo());
		// Updates the board
		state.setBoard(newBoard);
		//Updates last action
		state.setLastAction(a);
		//Increments the turn number since the action is done
		state.incrementTurnNumber();
		return state;
	}

	private State checkCaptureWhite(State state, Action a) {
		boolean captured = false;
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.WHITE)) {
			board.removePawn(rowTo + 1, columnTo);
			captured = true;
		}
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.WHITE)) {
			board.removePawn(rowTo - 1, columnTo);
			captured = true;
		}
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.WHITE)) {
			board.removePawn(rowTo, columnTo - 1);
			captured = true;
		}
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.WHITE)) {
			board.removePawn(rowTo, columnTo + 1);
			captured = true;	
		}
		if(captured)
			this.movesWithoutCapturing = 0;
		else
			this.movesWithoutCapturing++;
		if(checkWhiteWin(state, a))
			state.setTurn(Turn.WHITEWIN);

		return state;
	}

	private State checkCaptureBlack(State state, Action a) {
		boolean captured = false;
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)) {
			board.removePawn(rowTo, columnTo + 1);
			captured = true;
		}
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)) {
			board.removePawn(rowTo, columnTo - 1);
			captured = true;
		}
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.BLACK)) {
			board.removePawn(rowTo - 1, columnTo);
			captured = true;
		}
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)) {
			board.removePawn(rowTo + 1, columnTo);
			captured = true;
		}
		if (captured)
			this.movesWithoutCapturing = 0;
		else
			this.movesWithoutCapturing++;
		if(checkBlackWin(state, a))
			state.setTurn(Turn.BLACKWIN);

		return state;
	}

	public boolean checkWhiteWin(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		return checkWin(board, rowTo, columnTo, Direction.ANY, Turn.WHITE);
	}

	public boolean checkBlackWin(State state, Action a){
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		return (checkWin(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)
			|| checkWin(board, rowTo, columnTo, Direction.UP, Turn.BLACK)
			|| checkWin(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)
			|| checkWin(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)) ;
	}

	/**
	 * Checks how many white pawns are near the king, the position checked are only Down, Up, Right and Left, not the diagonals
	 * @return Number of white pawns near the king
	 * @author R.Vasumini, A.Solini
	 */
	public int numWhiteNearTheKing(State state){
		int result = 0;
		Board board = state.getBoard();
		String currentKingPosition = state.getCurrentKingPosition();

		if(board.getPawnDown(currentKingPosition) == Pawn.WHITE)
			result++;
		if(board.getPawnUp(currentKingPosition) == Pawn.WHITE)
			result++;
		if(board.getPawnLeft(currentKingPosition) == Pawn.WHITE)
			result++;
		if(board.getPawnRight(currentKingPosition) == Pawn.WHITE)
			result++;

		return result;
	}

	/**
	 * Checks how many black pawns are near the king, the position checked are only Down, Up, Right and Left, not the diagonals
	 * @return Number of black pawns near the king
	 * @author R.Vasumini, A.Solini
	 */
	public int numBlackNearTheKing(State state){
		int result = 0;
		Board board = state.getBoard();
		String currentKingPosition = state.getCurrentKingPosition();

		if(board.getPawnDown(currentKingPosition) == Pawn.BLACK)
			result++;
		if(board.getPawnUp(currentKingPosition) == Pawn.BLACK)
			result++;
		if(board.getPawnLeft(currentKingPosition) == Pawn.BLACK)
			result++;
		if(board.getPawnRight(currentKingPosition) == Pawn.BLACK)
			result++;

		return result;
	}

	/**
	 * @return An ArrayList of Action that can make the King escape and win
	 * @author R.Vasumini, A.Solini
	 */
	public ArrayList<Action> canKingWin(State state){
		ArrayList<Action> result = new ArrayList<Action>();
		Board board = state.getBoard();
		String currentKingPosition = state.getCurrentKingPosition();
		HashMap<String, ArrayList<String>> possibleWhiteActions = state.getPossibleBlackActions();
		
		//Checks if the king has possible actions to do
		if(possibleWhiteActions.get(currentKingPosition) != null){
			for(String to :  possibleWhiteActions.get(currentKingPosition)){
				try{
					//Checks if the column or the row in which the king can go is empty, if it is the king the next turn can win unless is captured
					if(board.isColumnEmpty(board.getColumn(to)))
						result.add(new Action(currentKingPosition, to, Turn.WHITE));
					if(board.isRowEmpty(board.getRow(to)))
						result.add(new Action(currentKingPosition, to, Turn.WHITE));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}	
		} 
		return result;
	}
	
	@Override
	public List<Action> getActions(State state) {
		try {
			return state.getActionList(state.getTurn());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public State getInitialState() {
		return initialState;
	}

	@Override
	public String getPlayer(State state) {
		if (state.getTurn() == Turn.WHITE) {
			return "W";
		} else {
			return "B";
		}
	}

	@Override
	public String[] getPlayers() {
		return new String[] { Board.Pawn.WHITE.toString(), Board.Pawn.BLACK.toString() };
	}

	/**
	 * @param state The state in which the action is to be simulated
	 * @param action The action to simulate
	 * @return A clone of the specified state in which the action is simulated
	 * @author R.Vasumini, A.Solini
	 */
	@Override
	public State getResult(State state, Action action) {
		State result = state.clone();
		try{
			result = movePawn(result, action);
			if(action.getTurn() == Turn.WHITE){
				result = checkCaptureWhite(result, action);			
				result.setOldNumBlack(result.getNumBlack());
				result.eatenUpdate(result.getBoard(), Turn.BLACK);
				result.updatePossibleActionsKeySet(action.getFrom(), action.getTo(), Turn.WHITE);
				result.updatePossibleActions(Turn.BLACK);
				if(!(result.getTurn() == Turn.WHITEWIN))
					result.setTurn(Turn.BLACK);
			}
			else if(action.getTurn() == Turn.BLACK){
				result = checkCaptureBlack(result, action);					
				result.setOldNumWhite(result.getNumWhite());
				result.eatenUpdate(result.getBoard(), Turn.WHITE);
				result.updatePossibleActionsKeySet(action.getFrom(), action.getTo(), Turn.BLACK);
				result.updatePossibleActions(Turn.WHITE);
				if(!(result.getTurn() == Turn.BLACKWIN))
					result.setTurn(Turn.WHITE);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return result;
	}
	//TODO Controllare se bisogna controllare lo stato o utilizzare checkwin
	@Override
	public double getUtility(State state, String player) {
		
		String actual = state.getTurn().toString();
		if(player.equalsIgnoreCase(""+actual.charAt(0)))
			return 50; //If I win
		else 
			return -50; //If I lose
	}

	@Override
	public boolean isTerminal(State state) {
		Turn turn = state.getTurn();
		if(turn == Turn.BLACKWIN || turn == Turn.WHITEWIN)
			return true;
		else
			return false;
	}

}