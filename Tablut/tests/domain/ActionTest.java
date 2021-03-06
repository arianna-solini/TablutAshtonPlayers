package domain;

import domain.Action;
import domain.Action.Direction;
import domain.State.Turn;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class ActionTest {
	@Test
	public void testAction() throws IOException {
		Action a = new Action("e5", "e4", Turn.WHITE);
		Assert.assertEquals("e5", a.getFrom());
		Assert.assertEquals("e4", a.getTo());
	}

	@Test
	public void testGetDirection() throws IOException {
		Action a = new Action("e5", "e4", Turn.WHITE);
		assertTrue(a.getDirection() == Direction.UP);
	}

}