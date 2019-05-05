package domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import domain.Board.Pawn;
import domain.State.Turn;

public class StateTest {

	State state = new State();
	
	@Test
	public void testEqualsAndClone(){
		assertTrue(state.equals(state.clone()));
		assertTrue(state.equals(new State()));
		assertFalse(state == state.clone());
	}

	@Test
	public void testNumPawns(){
		assertTrue(state.getNumBlack() == 16);
		assertTrue(state.getNumWhite() == 9);
	}

	@Test
	public void testPossibleActions(){
		Board board= state.getBoard().getEmptyBoard();
		state.setBoard(board);
		state.eatenUpdate(board, Turn.WHITE);
		board.setPawn("e3", Pawn.KING);
		state.getPossibleWhiteActions().put("e3", null);
		state.eatenUpdate(board, Turn.BLACK);
		state.updatePossibleActions(Turn.WHITE);
		assertTrue(state.getPossibleWhiteActions().keySet().size() == 1);
		assertTrue(state.getPossibleBlackActions().keySet().size() == 0);
		for (String to : state.getPossibleWhiteActions().get("e3"))
				System.out.println(to);
	}
	@Test
	public void testPrintBoard(){
		System.out.println(state.toString());
		assertTrue(true);
	}
	@Test
	public void checkStateChanging(){
		state.setTurn(Turn.WHITE);
		changeState(state);
		assertTrue(state.getTurn() == Turn.BLACK);
	}
	public void changeState(State state){
		if(state.getTurn() == Turn.WHITE)
			state.setTurn(Turn.BLACK);
		else
		state.setTurn(Turn.WHITE);
	}
	


}