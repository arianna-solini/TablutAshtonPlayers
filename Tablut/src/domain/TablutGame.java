package domain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import aima.core.search.adversarial.Game;

import domain.Board.Direction;
import domain.Board.Pawn;
import domain.Board.Position;
import domain.State.Turn;
import exceptions.*;

/**
 * 
 * Game engine inspired by the Ashton Rules of Tablut
 * 
 * 
 * @author A. Piretti, Andrea Galassi
 *
 */
public class TablutGame implements Game<State, Action, String> {

	/**
	 * Number of repeated states that can occur before a draw
	 */
	private int repeated_moves_allowed;

	/**
	 * Number of states kept in memory. negative value means infinite.
	 */
	private int cache_size;
	/**
	 * Counter for the moves without capturing that have occurred
	 */
	private int movesWithoutCapturing;
	private List<State> drawConditions;
	private State initialState = new State();

	// TODO: Draw conditions are not working

	public TablutGame(int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName,
			String blackName) {
		this(new State(), repeated_moves_allowed, cache_size, logs_folder, whiteName, blackName);
	}

	public TablutGame(State state, int repeated_moves_allowed, int cache_size, String logs_folder, String whiteName,
			String blackName) {
		super();
		this.repeated_moves_allowed = repeated_moves_allowed;
		this.cache_size = cache_size;
		this.movesWithoutCapturing = 0;
		drawConditions = new ArrayList<State>();

	}

	public boolean checkCaptureConditions(Board board, int rowTo, int columnTo, Direction d, Turn t) {
		switch (t) {
		case WHITE:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 && board.getPawn(rowTo + 1, columnTo).equalsPawn("B")
						&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
								|| board.getPawn(rowTo + 2, columnTo).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)
										// &&!(board.getPawn(rowTo+2,
										// columnTo).equalsPawn("B"))
										&& !(columnTo == 8 && rowTo + 2 == 4) && !(columnTo == 4 && rowTo + 2 == 0)
										&& !(columnTo == 4 && rowTo + 2 == 8) && !(columnTo == 0 && rowTo + 2 == 4))));

			case UP:
				return (rowTo > 1 && board.getPawn(rowTo - 1, columnTo).equalsPawn("B")
						&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
								|| board.getPawn(rowTo - 2, columnTo).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)
										// &&!(board.getPawn(rowTo-2, columnTo).equalsPawn("B"))
										&& !(columnTo == 8 && rowTo - 2 == 4) && !(columnTo == 4 && rowTo - 2 == 0)
										&& !(columnTo == 4 && rowTo - 2 == 8) && !(columnTo == 0 && rowTo - 2 == 4))));

			case RIGHT:
				return (columnTo < board.getLength() - 2 && board.getPawn(rowTo, columnTo + 1).equalsPawn("B")
						&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.THRONE)
								|| board.getPawn(rowTo, columnTo + 2).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)
										// &&!(board.getPawn(rowTo,
										// columnTo+2).equalsPawn("B"))
										&& !(columnTo + 2 == 8 && rowTo == 4) && !(columnTo + 2 == 4 && rowTo == 0)
										&& !(columnTo + 2 == 4 && rowTo == 8) && !(columnTo + 2 == 0 && rowTo == 4))));

			case LEFT:
				return (columnTo > 1 && board.getPawn(rowTo, columnTo - 1).equalsPawn("B")
						&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("W")
								|| (board.getPositions().get(board.getBox(rowTo, columnTo - 2)) == Position.THRONE)
								|| board.getPawn(rowTo, columnTo - 2).equalsPawn("K")
								|| ((board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)
										// &&!(board.getPawn(rowTo, columnTo-2).equalsPawn("B"))
										&& !(columnTo - 2 == 8 && rowTo == 4) && !(columnTo - 2 == 4 && rowTo == 0)
										&& !(columnTo - 2 == 4 && rowTo == 8) && !(columnTo - 2 == 0 && rowTo == 4))));

			default:
				return false;
			}

		case BLACK:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 && board.getPawn(rowTo + 1, columnTo).equalsPawn("W")
						&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)));

			case UP:
				return (rowTo > 1 && board.getPawn(rowTo - 1, columnTo).equalsPawn("W")
						&& (board.getPawn(rowTo - 2, columnTo).equalsPawn("B")
								|| (board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.THRONE)
								|| (board.getPositions().get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL)));

			case RIGHT:
				return (columnTo < board.getLength() - 2 && board.getPawn(rowTo, columnTo + 1).equalsPawn("W")
						&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("B")
								|| board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.THRONE
								|| (board.getPositions().get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)));

			case LEFT:
				return (rowTo < board.getLength() - 2 && board.getPawn(rowTo + 1, columnTo).equalsPawn("W")
						&& (board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.THRONE)
								|| (board.getPositions().get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)));

			default:
				return false;
			}

		default:
			return false;
		}
	}

	public boolean checkWin(Board board, int rowTo, int columnTo, Direction d, Turn t) {

		switch (t) {
		case WHITE:
			return (rowTo == 0 || rowTo == board.getLength() - 1 || columnTo == 0 || columnTo == board.getLength() - 1)
					&& (board.getPawn(rowTo, columnTo).equalsPawn("K"));

		case BLACK:
			switch (d) {
			case DOWN:
				return (rowTo < board.getLength() - 2 && board.getPawn(rowTo + 1, columnTo).equalsPawn("K") // re sotto
						&& ((board.getBox(rowTo + 1, columnTo).equals("e5") // re sul trono
								&& board.getPawn(5, 4).equalsPawn("B") && board.getPawn(4, 5).equalsPawn("B")
								&& board.getPawn(4, 3).equalsPawn("B"))
								|| (board.getBox(rowTo + 1, columnTo).equals("e4") // re
										// adiacente
										// al
										// trono
										&& board.getPawn(3, 3).equalsPawn("B") && board.getPawn(3, 5).equalsPawn("B"))
								|| (board.getBox(rowTo + 1, columnTo).equals("d5")
										&& board.getPawn(4, 2).equalsPawn("B") && board.getPawn(5, 3).equalsPawn("B"))
								|| (board.getBox(rowTo + 1, columnTo).equals("f5")
										&& board.getPawn(4, 6).equalsPawn("B") && board.getPawn(5, 5).equalsPawn("B"))
								|| (!board.getBox(rowTo + 1, columnTo).equals("d5") // re
										// fuori
										// dalle
										// zone
										// del
										// trono
										&& !board.getBox(rowTo + 1, columnTo).equals("e4")
										&& !board.getBox(rowTo + 1, columnTo).equals("f5")
										&& !board.getBox(rowTo + 1, columnTo).equals("e5")
										&& board.getPawn(rowTo + 2, columnTo).equalsPawn("B")
										|| (board.getPositions()
												.get(board.getBox(rowTo + 2, columnTo)) == Position.CITADEL)))

				);

			case UP:
				return (rowTo > 1 && board.getPawn(rowTo - 1, columnTo).equalsPawn("K") // re sopra
						&& ((board.getBox(rowTo - 1, columnTo).equals("e5") // re sul trono
								&& board.getPawn(3, 4).equalsPawn("B") && board.getPawn(4, 5).equalsPawn("B")
								&& board.getPawn(4, 3).equalsPawn("B"))
								|| (board.getBox(rowTo - 1, columnTo).equals("e6") // re
										// adiacente
										// al
										// trono
										&& board.getPawn(5, 3).equalsPawn("B") && board.getPawn(5, 5).equalsPawn("B"))
								|| (board.getBox(rowTo - 1, columnTo).equals("d5")
										&& board.getPawn(4, 2).equalsPawn("B") && board.getPawn(3, 3).equalsPawn("B"))
								|| (board.getBox(rowTo - 1, columnTo).equals("f5")
										&& board.getPawn(4, 4).equalsPawn("B") && board.getPawn(3, 5).equalsPawn("B"))
								|| (!board.getBox(rowTo - 1, columnTo).equals("d5") // re
										// fuori
										// dalle
										// zone
										// del
										// trono
										&& !board.getBox(rowTo - 1, columnTo).equals("e4")
										&& !board.getBox(rowTo - 1, columnTo).equals("f5")
										&& !board.getBox(rowTo - 1, columnTo).equals("e5")
										&& board.getPawn(rowTo - 2, columnTo).equalsPawn("B")
										|| (board.getPositions()
												.get(board.getBox(rowTo - 2, columnTo)) == Position.CITADEL))));

			case RIGHT:
				return (columnTo < board.getLength() - 2 && board.getPawn(rowTo, columnTo + 1).equalsPawn("K") // re
																												// sulla
																												// destra
						&& ((board.getBox(rowTo, columnTo + 1).equals("e5") // re sul trono
								&& board.getPawn(3, 4).equalsPawn("B") && board.getPawn(4, 5).equalsPawn("B")
								&& board.getPawn(5, 4).equalsPawn("B"))
								|| (board.getBox(rowTo, columnTo + 1).equals("e4") // re
										// adiacente
										// al
										// trono
										&& board.getPawn(2, 4).equalsPawn("B") && board.getPawn(3, 5).equalsPawn("B"))
								|| (board.getBox(rowTo, columnTo + 1).equals("e6")
										&& board.getPawn(5, 5).equalsPawn("B") && board.getPawn(6, 4).equalsPawn("B"))
								|| (board.getBox(rowTo, columnTo + 1).equals("d5")
										&& board.getPawn(3, 3).equalsPawn("B") && board.getPawn(3, 5).equalsPawn("B"))
								|| (!board.getBox(rowTo, columnTo + 1).equals("d5") // re
										// fuori
										// dalle
										// zone
										// del
										// trono
										&& !board.getBox(rowTo, columnTo + 1).equals("e6")
										&& !board.getBox(rowTo, columnTo + 1).equals("e4")
										&& !board.getBox(rowTo, columnTo + 1).equals("e5")
										&& (board.getPawn(rowTo, columnTo + 2).equalsPawn("B") || (board.getPositions()
												.get(board.getBox(rowTo, columnTo + 2)) == Position.CITADEL)))));

			case LEFT:
				return (columnTo > 1 && board.getPawn(rowTo, columnTo - 1).equalsPawn("K") // re sulla
				// sinistra
						&& ((board.getBox(rowTo, columnTo - 1).equals("e5") // re sul trono
								&& board.getPawn(3, 4).equalsPawn("B") && board.getPawn(4, 3).equalsPawn("B")
								&& board.getPawn(5, 4).equalsPawn("B"))
								|| (board.getBox(rowTo, columnTo - 1).equals("e4") // re
										// adiacente
										// al
										// trono
										&& board.getPawn(2, 4).equalsPawn("B") && board.getPawn(3, 3).equalsPawn("B"))
								|| (board.getBox(rowTo, columnTo - 1).equals("f5")
										&& board.getPawn(3, 5).equalsPawn("B") && board.getPawn(5, 5).equalsPawn("B"))
								|| (board.getBox(rowTo, columnTo + 1).equals("e6")
										&& board.getPawn(5, 3).equalsPawn("B") && board.getPawn(6, 4).equalsPawn("B"))
								|| (!board.getBox(rowTo, columnTo - 1).equals("e5") // re
										// fuori
										// dalle
										// zone
										// del
										// trono
										&& !board.getBox(rowTo, columnTo - 1).equals("e6")
										&& !board.getBox(rowTo, columnTo - 1).equals("e4")
										&& !board.getBox(rowTo, columnTo - 1).equals("f5")
										&& (board.getPawn(rowTo, columnTo - 2).equalsPawn("B") || (board.getPositions()
												.get(board.getBox(rowTo, columnTo - 2)) == Position.CITADEL)))));

			default:
				return false;
			}

		default:
			return false;
		}

	}

	public State checkMove(State state, Action a)
			throws BoardException, ActionException, StopException, PawnException, DiagonalException, ClimbingException,
			ThroneException, OccupitedException, ClimbingCitadelException, CitadelException {

		Board board = state.getBoard();

		// controllo la mossa
		if (a.getTo().length() != 2 || a.getFrom().length() != 2) {
			throw new ActionException(a);
		}

		int rowFrom = a.getRowFrom(), columnFrom = a.getColumnFrom();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();

		// controllo se sono fuori dal tabellone
		if (columnFrom > board.getLength() - 1 || rowFrom > board.getLength() - 1 || rowTo > board.getLength() - 1
				|| columnTo > board.getLength() || columnFrom < 0 || rowFrom < 0 || rowTo < 0 || columnTo < 0) {
			throw new BoardException(a);
		}

		// controllo che non vada sul trono
		if (board.getPositions().get(board.getBox(rowTo, columnTo)) == Position.THRONE) {
			throw new ThroneException(a);
		}

		// controllo la casella di arrivo
		if (!board.getPawn(rowTo, columnTo).equalsPawn(Pawn.EMPTY.toString())) {
			throw new OccupitedException(a);
		}
		if (board.getPositions().get(board.getBox(rowTo, columnTo)) == Position.CITADEL
				&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
			throw new CitadelException(a);
		}
		if (board.getPositions().get(board.getBox(rowTo, columnTo)) == Position.CITADEL
				&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) == Position.CITADEL) {
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

		// controllo se cerco di stare fermo
		if (rowFrom == rowTo && columnFrom == columnTo) {
			throw new StopException(a);
		}

		// controllo se sto muovendo una pedina giusta
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

		// controllo di non muovere in diagonale
		if (rowFrom != rowTo && columnFrom != columnTo) {
			throw new DiagonalException(a);
		}

		// controllo di non scavalcare pedine
		if (rowFrom == rowTo) {
			if (columnFrom > columnTo) {
				for (int i = columnTo; i < columnFrom; i++) {
					if (!board.getPawn(rowFrom, i).equalsPawn(Pawn.EMPTY.toString())) {
						if (board.getPositions().get(board.getBox(rowFrom, i)) == Position.THRONE) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (board.getPositions().get(board.getBox(rowFrom, i)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = columnFrom + 1; i <= columnTo; i++) {
					if (!board.getPawn(rowFrom, i).equalsPawn(Pawn.EMPTY.toString())) {
						if (board.getPositions().get(board.getBox(rowTo, columnTo))
								.equalsPosition(Position.THRONE.toString())) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (board.getPositions().get(board.getBox(rowFrom, i)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			}
		} else {
			if (rowFrom > rowTo) {
				for (int i = rowTo; i < rowFrom; i++) {
					if (!board.getPawn(i, columnFrom).equalsPawn(Pawn.EMPTY.toString())) {
						if (board.getPositions().get(board.getBox(i, columnFrom))
								.equalsPosition(Position.THRONE.toString())) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (board.getPositions().get(board.getBox(i, columnFrom)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			} else {
				for (int i = rowFrom + 1; i <= rowTo; i++) {
					if (!board.getPawn(i, columnFrom).equalsPawn(Pawn.EMPTY.toString())) {
						if (board.getPositions().get(board.getBox(i, columnFrom))
								.equalsPosition(Position.THRONE.toString())) {
							throw new ClimbingException(a);
						} else {
							throw new ClimbingException(a);
						}
					}
					if (board.getPositions().get(board.getBox(i, columnFrom)) == Position.CITADEL
							&& board.getPositions().get(board.getBox(rowFrom, columnFrom)) != Position.CITADEL) {
						throw new ClimbingCitadelException(a);
					}
				}
			}
		}

		// se sono arrivato qui, muovo la pedina
		state = this.movePawn(state, a);

		// a questo punto controllo lo stato per eventuali catture
		if (state.getTurn().equalsTurn("W")) {
			state = this.checkCaptureBlack(state, a);
		} else if (state.getTurn().equalsTurn("B")) {
			state = this.checkCaptureWhite(state, a);
		}

		// if something has been captured, clear cache for draws
		if (this.movesWithoutCapturing == 0) {
			this.drawConditions.clear();
		}

		// controllo pareggio
		int trovati = 0;
		for (State s : drawConditions) {

			System.out.println(s.toString());

			if (s.equals(state)) {
				// DEBUG: //
				// System.out.println("UGUALI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());

				trovati++;
				if (trovati > repeated_moves_allowed) {
					state.setTurn(State.Turn.DRAW);
					break;
				}
			} else {
				// DEBUG: //
				// System.out.println("DIVERSI:");
				// System.out.println("STATO VECCHIO:\t" + s.toLinearString());
				// System.out.println("STATO NUOVO:\t" +
				// state.toLinearString());
			}
		}
		if (trovati > 0) {
		}
		if (cache_size >= 0 && this.drawConditions.size() > cache_size) {
			this.drawConditions.remove(0);
		}
		this.drawConditions.add(state.clone());
		System.out.println("Stato:\n" + state.toString());

		return state;
	}

	private State checkCaptureWhiteRight(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio a destra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.WHITE)) {
			board.removePawn(rowTo, columnTo + 1);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkCaptureWhiteLeft(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio a sinistra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.WHITE)) {
			board.removePawn(rowTo, columnTo - 1);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkCaptureWhiteUp(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sopra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.WHITE)) {
			board.removePawn(rowTo - 1, columnTo);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkCaptureWhiteDown(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sotto
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.WHITE)) {
			board.removePawn(rowTo + 1, columnTo);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkWhiteWin(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se ho vinto
		if (checkWin(board, rowTo, columnTo, Direction.ANY, Turn.WHITE)) {
			state.setTurn(State.Turn.WHITEWIN);
		}
		return state;
	}

	private State checkCaptureWhite(State state, Action a) {
		checkCaptureWhiteRight(state, a);
		checkCaptureWhiteLeft(state, a);
		checkCaptureWhiteUp(state, a);
		checkCaptureWhiteDown(state, a);
		checkWhiteWin(state, a);

		this.movesWithoutCapturing++;
		return state;
	}

	private State checkCaptureBlackKingLeft(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// ho il re sulla sinistra
		if (checkWin(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)) {
			state.setTurn(State.Turn.BLACKWIN);
		}
		return state;
	}

	private State checkCaptureBlackKingRight(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// ho il re sulla destra
		if (checkWin(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)) {
			state.setTurn(State.Turn.BLACKWIN);
		}
		return state;
	}

	private State checkCaptureBlackKingDown(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// ho il re sotto
		if (checkWin(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)) {
			state.setTurn(State.Turn.BLACKWIN);
		}
		return state;
	}

	private State checkCaptureBlackKingUp(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// ho il re sopra
		if (rowTo > 1 && board.getPawn(rowTo - 1, columnTo).equalsPawn("K")) {
			state.setTurn(State.Turn.BLACKWIN);
		}
		return state;
	}

	private State checkCaptureBlackPawnRight(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// mangio a destra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)) {
			board.removePawn(rowTo, columnTo + 1);
			this.movesWithoutCapturing = -1;
		}

		return state;
	}

	private State checkCaptureBlackPawnLeft(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// mangio a sinistra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)) {
			board.removePawn(rowTo, columnTo - 1);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkCaptureBlackPawnUp(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sopra
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.BLACK)) {
			board.removePawn(rowTo - 1, columnTo);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkCaptureBlackPawnDown(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		// controllo se mangio sotto
		if (checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)) {
			board.removePawn(rowTo + 1, columnTo);
			this.movesWithoutCapturing = -1;
		}
		return state;
	}

	private State checkCaptureBlack(State state, Action a) {

		this.checkCaptureBlackPawnRight(state, a);
		this.checkCaptureBlackPawnLeft(state, a);
		this.checkCaptureBlackPawnUp(state, a);
		this.checkCaptureBlackPawnDown(state, a);
		this.checkCaptureBlackKingRight(state, a);
		this.checkCaptureBlackKingLeft(state, a);
		this.checkCaptureBlackKingDown(state, a);
		this.checkCaptureBlackKingUp(state, a);

		this.movesWithoutCapturing++;
		return state;
	}

	private State movePawn(State state, Action a) {
		Board board = state.getBoard();
		int rowTo = a.getRowTo(), columnTo = a.getColumnTo();
		int rowFrom = a.getRowFrom(), columnFrom = a.getColumnFrom();
		Pawn pawn = board.getPawn(rowFrom, columnFrom);
		Board newBoard = board;
		// State newState = new State();
		// libero una casella qualunque
		newBoard.getBoard()[rowFrom][columnFrom] = Pawn.EMPTY;
		// metto nel nuovo tabellone la pedina mossa
		newBoard.getBoard()[rowTo][columnTo] = pawn;
		// aggiorno il tabellone
		state.setBoard(newBoard);
		// cambio il turno
		if (state.getTurn() == Turn.WHITE) {
			state.setTurn(State.Turn.BLACK);
		} else {
			state.setTurn(State.Turn.WHITE);
		}
		//setto l'ultima azione eseguita
		state.setLastAction(a);
		return state;
	}

	public int getmovesWithoutCapturing() {
		return movesWithoutCapturing;
	}

	@SuppressWarnings("unused")
	private void setmovesWithoutCapturing(int movesWithoutCapturing) {
		this.movesWithoutCapturing = movesWithoutCapturing;
	}

	public int getRepeated_moves_allowed() {
		return repeated_moves_allowed;
	}

	public int getCache_size() {
		return cache_size;
	}

	public List<State> getDrawConditions() {
		return drawConditions;
	}

	public void clearDrawConditions() {
		drawConditions.clear();
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

	@Override
	public State getResult(State state, Action action) {
		State result = state.clone();
		result = movePawn(result, action);
		return result;
	}

	@Override
	public double getUtility(State state, String player) {
		
		String actual = state.getTurn().toString();
		
		if (isTerminal(state)){
			if(player.equals(""+actual.charAt(0))){
				return 20; //If I win
			} else {
				return -20; //If I lose
			}
		}
		return -1;

	}

	@Override
	public boolean isTerminal(State state) {
		Turn actual = state.getTurn();
		if ( actual == Turn.WHITEWIN || actual == Turn.BLACKWIN || actual == Turn.DRAW ){
			return true;
		} else {
			return false;
		}
	}

}