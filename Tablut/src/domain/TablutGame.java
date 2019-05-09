package domain;

import java.io.IOException;
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

	public final static int minValue = -100;
	public final static int maxValue = 100;
	private State initialState = new State();
	private int movesWithoutCapturingWhite, movesWithoutCapturingBlack;

	//TODO pensare a come fare un nostro eventuale costruttore di  TablutGame e se implementare il pareggio
	public TablutGame(){
		super();
		this.movesWithoutCapturingBlack = 0;
		this.movesWithoutCapturingWhite = 0;
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
						&& (board.getPawn(rowTo + 1, columnTo) == Pawn.BLACK)
						&& ((board.getPawn(rowTo + 2, columnTo) == Pawn.WHITE)
							|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
							|| (board.getPawn(rowTo + 2, columnTo) == Pawn.KING)
							|| ((positions.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)
								&& !(columnTo == 8 && rowTo + 2 == 4) 
								&& !(columnTo == 4 && rowTo + 2 == 0)
								&& !(columnTo == 4 && rowTo + 2 == 8) 
								&& !(columnTo == 0 && rowTo + 2 == 4))));

			case UP:
				return (rowTo > 1 
						&& (board.getPawn(rowTo - 1, columnTo) == Pawn.BLACK)
						&& ((board.getPawn(rowTo - 2, columnTo) == Pawn.WHITE)
							|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
							|| (board.getPawn(rowTo - 2, columnTo) == Pawn.KING)
							|| ((positions.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)
								&& !(columnTo == 8 && rowTo - 2 == 4) 
								&& !(columnTo == 4 && rowTo - 2 == 0)
								&& !(columnTo == 4 && rowTo - 2 == 8) 
								&& !(columnTo == 0 && rowTo - 2 == 4))));

			case RIGHT:
				return (columnTo < board.getLength() - 2 
						&& (board.getPawn(rowTo, columnTo + 1) == Pawn.BLACK)
						&& ((board.getPawn(rowTo, columnTo + 2) == Pawn.WHITE)
							|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.THRONE)
							|| (board.getPawn(rowTo, columnTo + 2) == Pawn.KING)
							|| ((positions.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)
								&& !(columnTo + 2 == 8 && rowTo == 4) 
								&& !(columnTo + 2 == 4 && rowTo == 0)
								&& !(columnTo + 2 == 4 && rowTo == 8) 
								&& !(columnTo + 2 == 0 && rowTo == 4))));

			case LEFT:
				return (columnTo > 1 
						&& (board.getPawn(rowTo, columnTo - 1) == Pawn.BLACK)
						&& ((board.getPawn(rowTo, columnTo - 2) == Pawn.WHITE)
							|| (positions.get(board.getBox(rowTo, columnTo - 2)) == Position.THRONE)
							|| (board.getPawn(rowTo, columnTo - 2) == Pawn.KING)
							|| ((positions.get(board.getBox(rowTo, columnTo - 2)) == Position.CITADEL)
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
						&& (board.getPawn(rowTo + 1, columnTo) == Pawn.WHITE)
						&& ((board.getPawn(rowTo + 2, columnTo) == Pawn.BLACK)
							|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
							|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)));

			case UP:
				return (rowTo > 1 
						&& (board.getPawn(rowTo - 1, columnTo) == Pawn.WHITE)
						&& ((board.getPawn(rowTo - 2, columnTo) == Pawn.BLACK)
							|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
							|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)));

			case RIGHT:
				return (columnTo < board.getLength() - 2 
						&& (board.getPawn(rowTo, columnTo + 1) == Pawn.WHITE)
						&& ((board.getPawn(rowTo, columnTo + 2) == Pawn.BLACK)
							|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.THRONE)
							|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)));

			case LEFT:
				return (columnTo > 1
						&& (board.getPawn(rowTo, columnTo - 1) == Pawn.WHITE)
						&& ((board.getPawn(rowTo, columnTo - 2) == Pawn.BLACK)
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
			return (board.getPawn(rowTo, columnTo) == Pawn.KING) 
				&& (rowTo == 0 || rowTo == board.getLength() - 1 || columnTo == 0 || columnTo == board.getLength() - 1);

		case BLACK:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 && board.getPawn(rowTo + 1, columnTo) == Pawn.KING
						&& ((board.getBox(rowTo + 1, columnTo).equals("e5") // king on the throne
							&& board.getPawn(5, 4) == Pawn.BLACK 
							&& board.getPawn(4, 5) == Pawn.BLACK
							&& board.getPawn(4, 3) == Pawn.BLACK)
							|| (board.getBox(rowTo + 1, columnTo).equals("e4") // king near the throne
								&& board.getPawn(3, 3) == Pawn.BLACK 
								&& board.getPawn(3, 5) == Pawn.BLACK)
							|| (board.getBox(rowTo + 1, columnTo).equals("d5")
								&& board.getPawn(4, 2) == Pawn.BLACK 
								&& board.getPawn(5, 3) == Pawn.BLACK)
							|| (board.getBox(rowTo + 1, columnTo).equals("f5")
								&& board.getPawn(4, 6) == Pawn.BLACK 
								&& board.getPawn(5, 5) == Pawn.BLACK)
							|| (!board.getBox(rowTo + 1, columnTo).equals("d5") // king outside the throne areas
								&& !board.getBox(rowTo + 1, columnTo).equals("e4")
								&& !board.getBox(rowTo + 1, columnTo).equals("f5")
								&& !board.getBox(rowTo + 1, columnTo).equals("e5")
								&& (board.getPawn(rowTo + 2, columnTo) == Pawn.BLACK
									|| (positions.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL))))

						);

			case UP:
				return (rowTo > 1 && board.getPawn(rowTo - 1, columnTo) == Pawn.KING
						&& ((board.getBox(rowTo - 1, columnTo).equals("e5") // king on the throne
							&& board.getPawn(3, 4) == Pawn.BLACK 
							&& board.getPawn(4, 5) == Pawn.BLACK
							&& board.getPawn(4, 3) == Pawn.BLACK)
							|| (board.getBox(rowTo - 1, columnTo).equals("e6") // king near the throne
								&& board.getPawn(5, 3) == Pawn.BLACK 
								&& board.getPawn(5, 5) == Pawn.BLACK)
							|| (board.getBox(rowTo - 1, columnTo).equals("d5")
								&& board.getPawn(4, 2) == Pawn.BLACK 
								&& board.getPawn(3, 3) == Pawn.BLACK)
							|| (board.getBox(rowTo - 1, columnTo).equals("f5")
								&& board.getPawn(4, 6) == Pawn.BLACK 
								&& board.getPawn(3, 5) == Pawn.BLACK)
							|| (!board.getBox(rowTo - 1, columnTo).equals("d5") // king outside the throne areas
								&& !board.getBox(rowTo - 1, columnTo).equals("e4")
								&& !board.getBox(rowTo - 1, columnTo).equals("f5")
								&& !board.getBox(rowTo - 1, columnTo).equals("e5")
								&& (board.getPawn(rowTo - 2, columnTo) == Pawn.BLACK
									|| (positions.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL))))
						);

			case RIGHT:
				return (columnTo < board.getLength() - 2 && board.getPawn(rowTo, columnTo + 1) == Pawn.KING
						&& ((board.getBox(rowTo, columnTo + 1).equals("e5") // king on the throne
							&& board.getPawn(3, 4) == Pawn.BLACK 
							&& board.getPawn(4, 5) == Pawn.BLACK
							&& board.getPawn(5, 4) == Pawn.BLACK)
							|| (board.getBox(rowTo, columnTo + 1).equals("e4") // king near the throne
								&& board.getPawn(2, 4) == Pawn.BLACK 
								&& board.getPawn(3, 5) == Pawn.BLACK)
							|| (board.getBox(rowTo, columnTo + 1).equals("e6")
								&& board.getPawn(5, 5) == Pawn.BLACK 
								&& board.getPawn(6, 4) == Pawn.BLACK)
							|| (board.getBox(rowTo, columnTo + 1).equals("d5")
								&& board.getPawn(3, 3) == Pawn.BLACK 
								&& board.getPawn(5, 3) == Pawn.BLACK)
							|| (!board.getBox(rowTo, columnTo + 1).equals("d5") // king outside the throne areas
								&& !board.getBox(rowTo, columnTo + 1).equals("e6")
								&& !board.getBox(rowTo, columnTo + 1).equals("e4")
								&& !board.getBox(rowTo, columnTo + 1).equals("e5")
								&& (board.getPawn(rowTo, columnTo + 2) == Pawn.BLACK 
									|| (positions.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL))))
						);

			case LEFT:
				return (columnTo > 1 && board.getPawn(rowTo, columnTo - 1) == Pawn.KING 
						&& ((board.getBox(rowTo, columnTo - 1).equals("e5") // king on the throne
							&& board.getPawn(3, 4) == Pawn.BLACK 
							&& board.getPawn(4, 3) == Pawn.BLACK
							&& board.getPawn(5, 4) == Pawn.BLACK)
							|| (board.getBox(rowTo, columnTo - 1).equals("e4") // king near the throne
								&& board.getPawn(2, 4) == Pawn.BLACK 
								&& board.getPawn(3, 3) == Pawn.BLACK)
							|| (board.getBox(rowTo, columnTo - 1).equals("f5")
								&& board.getPawn(3, 5) == Pawn.BLACK 
								&& board.getPawn(5, 5) == Pawn.BLACK)
							|| (board.getBox(rowTo, columnTo + 1).equals("e6")
								&& board.getPawn(5, 3) == Pawn.BLACK 
								&& board.getPawn(6, 4) == Pawn.BLACK)
							|| (!board.getBox(rowTo, columnTo - 1).equals("e5") // king outside the throne areas
								&& !board.getBox(rowTo, columnTo - 1).equals("e6")
								&& !board.getBox(rowTo, columnTo - 1).equals("e4")
								&& !board.getBox(rowTo, columnTo - 1).equals("f5")
								&& (board.getPawn(rowTo, columnTo - 2) == Pawn.BLACK 
									|| (positions.get(board.getBox(rowTo, columnTo - 2)) == Position.CITADEL))))
						);

			default:
				return false;
			}

		default:
			return false;
		}

	}

	//TODO: eventuale cambiamento drastico: essendo fatte bene le azioni di mosse possibili potremmo evitarci questo controllo costoso
	/**
	 * Throws different exceptions if it encounters a problem in the action to be executed in the specified state
	 * @param state
	 * @param action
	 * @throws BoardException
	 * @throws ActionException
	 * @throws StopException
	 * @throws PawnException
	 * @throws DiagonalException
	 * @throws ClimbingException
	 * @throws ThroneException
	 * @throws OccupitedException
	 * @throws ClimbingCitadelException
	 * @throws CitadelException
	 */
	private void checkMove(State state, Action action) throws BoardException, ActionException, StopException,PawnException, DiagonalException,
														ClimbingException, ThroneException, OccupitedException, ClimbingCitadelException, 
														CitadelException {

		Board board = state.getBoard();
		HashMap<String, Position> positions = board.getPositions();
		int rowFrom = action.getRowFrom(), columnFrom = action.getColumnFrom();
		int rowTo = action.getRowTo(), columnTo = action.getColumnTo();
		String boxFrom = board.getBox(rowFrom, columnFrom);
		String boxTo = board.getBox(rowTo, columnTo);
		Pawn pawnFrom = board.getPawn(rowFrom, columnFrom);
		Pawn pawnTo = board.getPawn(rowTo, columnTo);
		int length = board.getLength();
		Position positionFrom = positions.get(boxFrom);
		Position positionTo = positions.get(boxTo);
		Turn turn = state.getTurn();

		// Checks strings length 
		if (action.getTo().length() != 2 || action.getFrom().length() != 2) {
			throw new ActionException(action);
		}

		// Checks if iI try to go outside the board
		if (columnFrom > length - 1 || rowFrom > length - 1 || rowTo > length - 1
				|| columnTo > board.getLength() || columnFrom < 0 || rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			throw new BoardException(action);
		}

		// Checks if i try to go on the throne
		if (positionTo == Position.THRONE) {
			throw new ThroneException(action);
		}

		// Checks if my arrival position is occupied
		if (pawnTo != Pawn.EMPTY) {
			throw new OccupitedException(action);
		}
		if (positionTo == Position.CITADEL && positionFrom != Position.CITADEL) {
			throw new CitadelException(action);
		}
		if (positionTo == Position.CITADEL && positionFrom == Position.CITADEL) {
			if (rowFrom == rowTo) {
				if (columnFrom - columnTo > 5 || columnFrom - columnTo < -5) {
					throw new CitadelException(action);
				}
			} else {
				if (rowFrom - rowTo > 5 || rowFrom - rowTo < -5) {
					throw new CitadelException(action);
				}
			}
		}

		// Checks if  I try not to move
		if (rowFrom == rowTo && columnFrom == columnTo) {
			throw new StopException(action);
		}

		// Checks if I'm trying to move an adversarial pawn
		if (turn == Turn.WHITE) {
			if (pawnFrom != Pawn.WHITE && pawnFrom != Pawn.KING) {
				throw new PawnException(action);
			}
		}
		if (turn == Turn.BLACK) {
			if (pawnFrom != Pawn.BLACK) {
				throw new PawnException(action);
			}
		}

		// Checks if I try to do a diagonal move
		if (rowFrom != rowTo && columnFrom != columnTo) {
			throw new DiagonalException(action);
		}

		// Checks if I try to jump some pawns
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					Position position = positions.get(board.getBox(rowFrom, i));
					if (board.getPawn(rowFrom, i) != Pawn.EMPTY) {
						if (position == Position.THRONE) {
							throw new ClimbingException(action);
						} else {
							throw new ClimbingException(action);
						}
					}
					if (position == Position.CITADEL && positionFrom != Position.CITADEL) {
						throw new ClimbingCitadelException(action);
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					Position position = positions.get(board.getBox(rowFrom, i));
					if (board.getPawn(rowFrom, i) != Pawn.EMPTY) {
						if (positionTo == Position.THRONE) {
							throw new ClimbingException(action);
						} else {
							throw new ClimbingException(action);
						}
					}
					if (position == Position.CITADEL && positionFrom != Position.CITADEL) {
						throw new ClimbingCitadelException(action);
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					Position position = positions.get(board.getBox(i, columnFrom));
					if (board.getPawn(i, columnFrom) != Pawn.EMPTY){
						if (position == Position.THRONE) {
							throw new ClimbingException(action);
						} else {
							throw new ClimbingException(action);
						}
					}
					if (position == Position.CITADEL && positionFrom != Position.CITADEL) {
						throw new ClimbingCitadelException(action);
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					Position position = positions.get(board.getBox(i, columnFrom));
					if (board.getPawn(i, columnFrom) != Pawn.EMPTY) {
						if (position == Position.THRONE) {
							throw new ClimbingException(action);
						} else {
							throw new ClimbingException(action);
						}
					}
					if (position == Position.CITADEL && positionFrom != Position.CITADEL) {
						throw new ClimbingCitadelException(action);
					}
				}
			}
		}
	}

	/**
	 * @return The state with the move made
	 * @author R.Vasumini, A.Solini
	 */
	public State makeMove(State state, Action action){
		try{
			checkMove(state, action);
		} catch(Exception e){
			e.printStackTrace();
		}
		Turn turn = state.getTurn();
		// Move checked, makes the move
		state = this.movePawn(state, action);

		// Checks if  the move involves a capture
		if (turn == Turn.BLACK) {
			state = this.checkCaptureBlack(state, action);
		} else if (turn == Turn.WHITE) {
			state = this.checkCaptureWhite(state, action);
		}
		return state;
	}

	/**
	 * @param state
	 * @param action
	 * @return The state with the pawn moved
	 * @author R.Vasumini, A.Solini
	 */
	private State movePawn(State state, Action action) {
		Board board = state.getBoard();
		int rowTo = action.getRowTo(), columnTo = action.getColumnTo();
		int rowFrom = action.getRowFrom(), columnFrom = action.getColumnFrom();
		Pawn pawn = board.getPawn(rowFrom, columnFrom);
		Board newBoard = board;

		newBoard.removePawn(rowFrom, columnFrom);
		// Puts the pawn moved in the board
		newBoard.setPawn(rowTo, columnTo, pawn);
		//If the moved pawn is the king, changes his current position
		if(pawn == Pawn.KING)
			state.setCurrentKingPosition(action.getTo());
		// Updates the board
		state.setBoard(newBoard);
		//Updates last action
		state.setLastAction(action);
		//Increments the turn number since the action is done
		state.incrementTurnNumber();
		return state;
	}

	/**
	 * @param state
	 * @param action
	 * @return The state with the black pawns eaten by the white
	 */
	public State checkCaptureWhite(State state, Action action) {
		boolean captured = false;
		Board board = state.getBoard();
		int rowTo = action.getRowTo(), columnTo = action.getColumnTo();
		
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
			this.movesWithoutCapturingWhite = 0;
		else
			this.movesWithoutCapturingWhite++;

		if(checkWhiteWin(state, action))
			state.setTurn(Turn.WHITEWIN);

		return state;
	}

	/**
	 * @param state
	 * @param action
	 * @return The state with the white pawns eaten by the black
	 */
	public State checkCaptureBlack(State state, Action action) {
		boolean captured = false;
		Board board = state.getBoard();
		int rowTo = action.getRowTo(), columnTo = action.getColumnTo();

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
			this.movesWithoutCapturingBlack = 0;
		else
			this.movesWithoutCapturingBlack++;

		if(checkBlackWin(state, action))
			state.setTurn(Turn.BLACKWIN);

		return state;
	}
	
	/**
	 * @param state
	 * @param action
	 * @return {@code true} if the white wins with the specified action in current state, {@code false} otherwise
	 */
	public boolean checkWhiteWin(State state, Action action){
		Board board = state.getBoard();
		int rowTo = action.getRowTo(), columnTo = action.getColumnTo();
		return checkWin(board, rowTo, columnTo, Direction.ANY, Turn.WHITE);
	}

	/**
	 * @param state
	 * @param action
	 * @return {@code true} if the black wins with the specified action in current state, {@code false} otherwise
	 */
	public boolean checkBlackWin(State state, Action action){
		Board board = state.getBoard();
		int rowTo = action.getRowTo(), columnTo = action.getColumnTo();
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
	 * @param state the current state
	 *  @return A list of the possible actions in the current state
	 */	
	@Override
	public List<Action> getActions(State state) {
		try {
			return state.getActionList(state.getTurn());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @return the initial state of the game
	 */
	@Override
	public State getInitialState() {
		return initialState;
	}

	/**
	 * @param state the current state
	 * @return the current player 
	 */
	@Override
	public String getPlayer(State state) {
		if (state.getTurn() == Turn.WHITE) {
			return "W";
		} else {
			return "B";
		}
	}

	/**
	 * @return A String array of the players in the game
	 */
	@Override
	public String[] getPlayers() {
		return new String[] { Turn.WHITE.toString(), Turn.BLACK.toString() };
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
				//TODO setoldnumblack e eatenupdate posso metterli direttamente nelle check migliorando l efficienza non essendo svolte per forza sempre			
				result.setOldNumPawn(Turn.BLACK, result.getNumBlack());
				result.eatenUpdate(result.getBoard(), Turn.BLACK);
				result.updatePossibleActionsKeySet(action.getFrom(), action.getTo(), Turn.WHITE);
				result.updatePossibleActions(Turn.BLACK);
				if(!(result.getTurn() == Turn.WHITEWIN))
					result.setTurn(Turn.BLACK);
			}
			else if(action.getTurn() == Turn.BLACK){
				result = checkCaptureBlack(result, action);					
				result.setOldNumPawn(Turn.WHITE, result.getNumWhite());
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

	/**
	 * This function is called only by the eval function in TimeLimitedSearch class in case of end game
	 * @param state the current state
	 * @param player the player
	 * @return TablutGame's maxValue if the player wins, TablutGame's minValue if the player lose
	 */
	@Override
	public double getUtility(State state, String player) {
		
		String actual = state.getTurn().toString();
		if(player.equalsIgnoreCase(""+actual.charAt(0)))
			return TablutGame.maxValue; //If I win
		else 
			return TablutGame.minValue; //If I lose
	}

	/**
	 * @return {@code true} if the game ends, {@code false} otherwise
	 */
	@Override
	public boolean isTerminal(State state) {
		Turn turn = state.getTurn();
		//TODO se implementi draw devi aggiungerlo qua
		if(turn == Turn.BLACKWIN || turn == Turn.WHITEWIN)
			return true;
		else
			return false;
	}

	/**
	 * @return the movesWithoutCapturingWhite
	 */
	public int getMovesWithoutCapturingWhite() {
		return movesWithoutCapturingWhite;
	}

	/**
	 * @param movesWithoutCapturingWhite the movesWithoutCapturingWhite to set
	 */
	public void setMovesWithoutCapturingWhite(int movesWithoutCapturingWhite) {
		this.movesWithoutCapturingWhite = movesWithoutCapturingWhite;
	}

	/**
	 * @return the movesWithoutCapturingBlack
	 */
	public int getMovesWithoutCapturingBlack() {
		return movesWithoutCapturingBlack;
	}

	/**
	 * @param movesWithoutCapturingBlack the movesWithoutCapturingBlack to set
	 */
	public void setMovesWithoutCapturingBlack(int movesWithoutCapturingBlack) {
		this.movesWithoutCapturingBlack = movesWithoutCapturingBlack;
	}

}