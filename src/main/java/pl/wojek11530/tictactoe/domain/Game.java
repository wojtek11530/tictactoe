package pl.wojek11530.tictactoe.domain;

import javax.persistence.*;

@Entity

public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public static final String DEFAULT_BOARD_CHARACTER = "";
    private String[][] gameBoard;

    @OneToOne
    private Player player1;

    @OneToOne
    private Player player2;

    @OneToOne
    private Player currentPlayer;

    @OneToOne
    private Player winner;



    public Game() {
        this.initGameBoard(3, 3);
    }

    private void initGameBoard(int boardWidth, int boardHeight) {
        this.gameBoard = new String[boardHeight][boardWidth];
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                this.gameBoard[i][j] = DEFAULT_BOARD_CHARACTER;
            }
        }

    }

    public Game(String[][] gameBoard, Player player1, Player player2, Player currentPlayer, Player winner) {
        this.gameBoard = gameBoard;
        this.player1 = player1;
        this.player2 = player2;
        this.currentPlayer = currentPlayer;
        this.winner = winner;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String[][] getGameBoard() {
        return gameBoard;
    }

    public void setGameBoard(String[][] gameBoard) {
        this.gameBoard = gameBoard;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }


    public Player getCurrentPlayer() {
        return currentPlayer;
    }


    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }
}
