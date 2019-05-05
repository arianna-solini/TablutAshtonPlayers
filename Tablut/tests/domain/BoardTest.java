package domain;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import domain.Board.Pawn;
import domain.Board.Position;

public class BoardTest {
	Board board = new Board();
	@Before
	public void emptyBoard(){
		Pawn[][] board = new Pawn[9][9];
		for(int i = 0; i < 9; i++)
			for(int j = 0; j < 9; j++)
				board[i][j] = Pawn.EMPTY;
		
		this.board.setBoard(board);
	}
	@Test
	public void testGetColumn(){
		Board board = new Board();
		Assert.assertEquals(4,board.getColumn("e5"));
	}

	@Test
	public void testGetLength(){
		Board board = new Board();
		Assert.assertEquals(9, board.getLength());
		Assert.assertEquals(9, board.getBoard()[0].length);
	}

	@Test
	public void testGetPawn(){
		Board board = new Board();
		Assert.assertTrue(board.getPawn(4, 4) == Pawn.KING);
		Assert.assertTrue(board.getPawn(4, 0) == Pawn.BLACK);
		Assert.assertTrue(board.getPawn(4, 3) == Pawn.WHITE);
	}

	@Test
	public void testIsColumnEmpty(){
		//this.board.setPawn("c3", Pawn.KING);
		assertTrue("OK", this.board.isColumnEmpty(2));
	}

	@Test
	public void testIsRowEmpty(){
		//this.board.setPawn("c", Pawn.KING);
		assertTrue("OK", this.board.isRowEmpty(2));
	}

	@Test
	public void testGetPawnNear(){
		Board board = new Board();
		assertTrue(board.getPawnDown("e5") == Pawn.WHITE);
		assertTrue(board.getPawnUp("e5") == Pawn.WHITE);
		assertTrue(board.getPawnLeft("e5") == Pawn.WHITE);
		assertTrue(board.getPawnRight("e5") == Pawn.WHITE);
	}

	@Test
	public void testGetPositions(){
		Board board = new Board();
		assertTrue(board.getPositions().get("e5") == Position.THRONE);
		assertTrue(board.getPositions().get("e1") == Position.CITADEL);
		assertTrue(board.getPositions().get("e3") == Position.STARTWHITE);
		

	}

}