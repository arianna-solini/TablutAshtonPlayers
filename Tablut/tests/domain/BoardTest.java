package domain;

import org.junit.Assert;
import org.junit.Test;

import domain.Board.Pawn;

public class BoardTest {
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
		System.out.println("Hello");
	}


}