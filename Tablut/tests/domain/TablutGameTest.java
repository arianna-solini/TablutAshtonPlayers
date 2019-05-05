package domain;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import domain.Board.Pawn;
import domain.State.Turn;

public class TablutGameTest {
	State state = new State();
	TablutGame game = new TablutGame();

	@Test
	public void testCheckCaptureConditionsBBW() throws IOException {
		Board board = state.getBoard().getEmptyBoard();
		board.setPawn("a4", Pawn.BLACK);
		board.setPawn("b4", Pawn.BLACK);
		board.setPawn("c4", Pawn.EMPTY);
		board.setPawn("d4", Pawn.WHITE);
		state.setBoard(board);
		state.setTurn(Turn.WHITE);

		state = game.makeMove(state, new Action("d4", "c4", Turn.WHITE));
		assertTrue(state.getBoard().getPawn("c4") == Pawn.WHITE);
		assertTrue(state.getBoard().getPawn("d4") == Pawn.EMPTY);
		assertTrue(state.getBoard().getPawn("b4") == Pawn.EMPTY);	
	}
	


}