package domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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
		Board board=Board.getEmptyBoard();
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

	@Test
	public void testChangeCurrentKingPositionAndHashMap(){
		assertTrue(state.getCurrentKingPosition().equals("e5"));
		state.setCurrentKingPosition("e3");
		assertTrue(state.getCurrentKingPosition().equals("e3"));
		assertNotNull(state.getPossibleWhiteActions().get("e4"));

		State change = new State();
		change.updatePossibleActionsKeySet("e4", "f4", Turn.WHITE);
		change.updatePossibleActions(Turn.WHITE);
		state.setPossibleWhiteActions(change.getPossibleWhiteActions());
		assertNotNull(state.getPossibleWhiteActions().get("f4"));

		state.updatePossibleActionsKeySet("f4", "e4", Turn.WHITE);
		state.updatePossibleActions(Turn.WHITE);
		assertNull(change.getPossibleWhiteActions().get("f4"));

		change.currentKingPosition = state.currentKingPosition;
		assertTrue(change.currentKingPosition.equals("e3"));
		change.currentKingPosition = "e4";
		assertTrue(state.currentKingPosition.equals("e3"));
		//Dopo questi test si capisce che le stringhe non subiscono variazioni, come variabili primitive, le hashmap invece si
	}

	
	


}