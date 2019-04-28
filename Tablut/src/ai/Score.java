package ai;

import domain.Board;
import domain.State;
import domain.TablutGame;
import domain.State.Turn;
import domain.Board.Direction;

/**
 * Class used for calculate the score of an action
 * @author R.Vasumini, A.Solini
 */
public class Score{

	private State state;
	private String player;

	// Values needed by the eval function 
	public double scoreWhite;
	public double scoreBlack;

	public Score(String player, State state){
		this.player = player;
		this.state = state;
		this.scoreBlack = 0;
		this.scoreWhite = 0;
	}

	public void setState(State state){
		this.state = state;
	}

	public void setPlayer(String player){
		this.player = player;
	}

	public double calculateScore(String player, TablutGame game){
		Board board = state.getBoard();
		int rowTo = this.state.getLastAction().getRowTo(), columnTo = this.state.getLastAction().getColumnTo();
		switch (player){
			//TODO valutare altre situazioni, magari con funzioni a parte
			case "W" :
				//Capture values, multiple captures are preferred
				if(game.checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.WHITE))
					scoreWhite += 2;
				if (game.checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.WHITE))
					scoreWhite += 2;
				if (game.checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.WHITE))
					scoreWhite += 2;
				if (game.checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.WHITE))
					scoreWhite += 2;
				//Win value
				if (game.checkWin(board, rowTo, columnTo, Direction.ANY, Turn.WHITE))
					scoreWhite += 10;
				//Default value
				scoreWhite += 1;

				return scoreWhite;
				
			case "B":
				//Capture values, multiple captures are preferred
				if(game.checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.BLACK))
					scoreBlack += 2;
				if (game.checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK))
					scoreBlack += 2;
				if (game.checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK))
					scoreBlack += 2;	
				if (game.checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK))
					scoreBlack += 2;
				
				//Win Value
				if( game.checkWin(board, rowTo, columnTo, Direction.UP, Turn.BLACK)
					|| game.checkWin(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)
					|| game.checkWin(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)
					|| game.checkWin(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK))
					scoreBlack += 10;
				
				//Default value
				scoreBlack += 1;
				return scoreBlack;

			default:
				return -1;
		}

	}

}