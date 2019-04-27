package ai;

import domain.Board;
import domain.State;
import domain.TablutGame;
import domain.State.Turn;
import domain.Board.Direction;

public class Score{

    private State state;
    private String player;

    /* punteggio da considerare per la funzione di valutazione */
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
                if(game.checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.WHITE) 
                || game.checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.WHITE)
                || game.checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.WHITE)
                || game.checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.WHITE)){
                    scoreWhite = 2;
                }
                else if (game.checkWin(board, rowTo, columnTo, Direction.UP, Turn.WHITE) 
                || game.checkWin(board, rowTo, columnTo, Direction.DOWN, Turn.WHITE)
                || game.checkWin(board, rowTo, columnTo, Direction.LEFT, Turn.WHITE)
                || game.checkWin(board, rowTo, columnTo, Direction.RIGHT, Turn.WHITE)){
                    scoreWhite = 11;
                }
                else{
                    scoreWhite = 1;
                }
                return scoreWhite;
            case "B":
                if(game.checkCaptureConditions(board, rowTo, columnTo, Direction.UP, Turn.BLACK)
                || game.checkCaptureConditions(board, rowTo, columnTo, Direction.DOWN, Turn.BLACK)
                || game.checkCaptureConditions(board, rowTo, columnTo, Direction.LEFT, Turn.BLACK)
                || game.checkCaptureConditions(board, rowTo, columnTo, Direction.RIGHT, Turn.BLACK)){
                    scoreBlack = 2;
                }
                else{
                    scoreBlack = 1;
                }
                return scoreBlack;
            default:
                return -1;
        }
        
    }
}