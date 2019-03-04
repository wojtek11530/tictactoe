package pl.wojek11530.tictactoe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.converters.GameCommandToGame;
import pl.wojek11530.tictactoe.converters.GameToGameCommand;
import pl.wojek11530.tictactoe.domain.Difficulty;
import pl.wojek11530.tictactoe.domain.Game;
import pl.wojek11530.tictactoe.domain.Player;
import pl.wojek11530.tictactoe.exceptions.NotFoundException;
import pl.wojek11530.tictactoe.repositories.GameRepository;
import pl.wojek11530.tictactoe.repositories.PlayerRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Slf4j
@Service
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final GameCommandToGame gameCommandToGame;
    private final GameToGameCommand gameToGameCommand;
    private final PlayerRepository playerRepository;


    private final Random r = new Random();


    public GameServiceImpl(GameRepository gameRepository, GameCommandToGame gameCommandToGame, GameToGameCommand gameToGameCommand, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.gameCommandToGame = gameCommandToGame;
        this.gameToGameCommand = gameToGameCommand;
        this.playerRepository = playerRepository;
    }

    @Override
    public Game findById(Long l) {
        Optional<Game> gameOptional = gameRepository.findById(l);

        if(!gameOptional.isPresent()){
            throw new NotFoundException("Game Not Found. For ID value: " + l.toString() );
        }
        return gameOptional.get();
    }



    @Override
    @Transactional
    public GameCommand saveGameCommand(GameCommand command) {
        Game detachedGame = gameCommandToGame.convert(command);

        Game savedGame = gameRepository.save(detachedGame);
        log.debug("Saved GameId:" + savedGame.getId());
        return gameToGameCommand.convert(savedGame);
    }

    @Override
    @Transactional
    public GameCommand findCommandById(Long l) {
        return gameToGameCommand.convert(findById(l));
    }

    @Override
    public void setRandomlyAFirstPlayer(Long gameId) {

        Game game = findById(gameId);
        if(Math.random()<=0.5) {
            game.setCurrentPlayer(game.getPlayer1());
        }else {
            game.setCurrentPlayer(game.getPlayer2());
        }

        log.debug("Game" + game.getId() + ": Current Player name is " + game.getCurrentPlayer().getName());
        gameRepository.save(game);
    }


    private void changeCurrentPlayer(Long gameId) {
        Game game = findById(gameId);
        if(game.getCurrentPlayer().equals(game.getPlayer1())){
            game.setCurrentPlayer(game.getPlayer2());
        }else{
            game.setCurrentPlayer(game.getPlayer1());
        }
        gameRepository.save(game);

    }


    private boolean draw(Long gameId) {
        Game game = findById(gameId);
        String [][] gameBoard = game.getGameBoard();

        if (!drawAux(gameBoard)) return false;

        game.getPlayer1().setNumberOfDraws(game.getPlayer1().getNumberOfDraws() + 1);
        game.getPlayer1().setNumberOfGames(game.getPlayer1().getNumberOfGames() + 1);

        game.getPlayer2().setNumberOfDraws(game.getPlayer2().getNumberOfDraws() + 1);
        game.getPlayer2().setNumberOfGames(game.getPlayer2().getNumberOfGames() + 1);

        gameRepository.save(game);

        playerRepository.save(game.getPlayer1());
        playerRepository.save(game.getPlayer2());

        return true;
    }

    private boolean drawAux(String[][] gameBoard) {
        boolean draw = true;
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j].equals(Game.DEFAULT_BOARD_CHARACTER)) {
                    draw = false;
                }
            }
        }
        return draw;
    }


    private boolean checkIfThereIsAWInner(Long gameId) {
        Game game = findById(gameId);
        boolean winning = winning(game.getGameBoard(), game.getCurrentPlayer());


        Player winner = game.getCurrentPlayer();
        game.setWinner(winner);

        game.getPlayer1().setNumberOfGames(game.getPlayer1().getNumberOfGames() + 1);
        game.getPlayer2().setNumberOfGames(game.getPlayer2().getNumberOfGames() + 1);

        if (winner.equals(game.getPlayer1())) {
            game.getPlayer1().setNumberOfWins(game.getPlayer1().getNumberOfWins() + 1);
            game.getPlayer2().setNumberOfDefeats(game.getPlayer2().getNumberOfDefeats() + 1);

        } else {
            game.getPlayer2().setNumberOfWins(game.getPlayer2().getNumberOfWins() + 1);
            game.getPlayer1().setNumberOfDefeats(game.getPlayer1().getNumberOfDefeats() + 1);
        }
        gameRepository.save(game);
        playerRepository.save(game.getPlayer1());
        playerRepository.save(game.getPlayer2());

        return winning;

    }

    private boolean winning(String[][] gameBoard, Player currentPlayer) {

        return (checkIfAnyRowHasTheSameSigns(gameBoard, currentPlayer) ||
                checkIfAnyColHasTheSameSigns(gameBoard, currentPlayer) ) ||
                checkIfAnyDiagonalHasTheSameSigns(gameBoard, currentPlayer);
    }

    private boolean checkIfAnyRowHasTheSameSigns(String[][] gameBoard, Player currentPlayer) {
        String currentSign = currentPlayer.getSignOfPlayer().getSign();

        for (int i = 0; i < gameBoard.length; i++) {
            String [] row = new String[gameBoard.length];

            for (int j=0; j<gameBoard[i].length; j++){
                row[j] = gameBoard[j][i];
            }

            boolean hasTheRowTheSameSigns = true;

            for (int j=0; j<gameBoard[0].length; j++){
                if(!row[0].equals(row[j])){
                    hasTheRowTheSameSigns = false;
                }
            }

            if (row[0].equals(currentSign) && hasTheRowTheSameSigns) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfAnyColHasTheSameSigns(String[][] gameBoard, Player currentPlayer) {

        String currentSign = currentPlayer.getSignOfPlayer().getSign();

        for (int i = 0; i < gameBoard[0].length; i++) {
            String [] col = new String[gameBoard.length];

            for (int j=0; j<gameBoard[0].length; j++){
                col[j] = gameBoard[i][j];
            }

            boolean hasColTheSameSigns=true;

            for (int j=0; j<gameBoard[0].length; j++){
                if(!col[0].equals(col[j])){
                    hasColTheSameSigns=false;
                }
            }

            if (col[0].equals(currentSign) && hasColTheSameSigns) {
                return true;
            }
        }
        return false;
    }

    private boolean checkIfAnyDiagonalHasTheSameSigns(String[][] gameBoard, Player currentPlayer) {
        String currentSign = currentPlayer.getSignOfPlayer().getSign();

        boolean hasDiagonalOneTheSameSigns = true;
        boolean hasDiagonalTwoTheSameSigns = true;

        for (int j=0; j<gameBoard[0].length; j++){
            if(!gameBoard[1][1].equals(gameBoard[j][j])){
                hasDiagonalOneTheSameSigns=false;
            }
        }

        for (int j=0; j<gameBoard.length; j++){
            if(!gameBoard[1][1].equals(gameBoard[j][gameBoard.length-1-j])){
                hasDiagonalTwoTheSameSigns=false;
            }
        }

        return (gameBoard[0][0].equals(currentSign) && hasDiagonalOneTheSameSigns) ||
                    (gameBoard[0][2].equals(currentSign) && hasDiagonalTwoTheSameSigns);

    }

    private String determineNextPhaseOfGame(Long gameId, boolean changePlayer) {
        if (checkIfThereIsAWInner(gameId)){
            return "redirect:/tictactoe/game/" + gameId + "/win";
        }
        else if (draw(gameId)){
            return "redirect:/tictactoe/game/" + gameId + "/draw";
        }
        else {
            if (changePlayer){
                changeCurrentPlayer(gameId);
            }
            return "redirect:/tictactoe/game/" + gameId + "/play";
        }
    }

    private boolean putSign(Long id, int x, int y) {
        Game game = findById(id);

        if (putSignAux(x, y, game.getGameBoard(), game.getCurrentPlayer())){
            gameRepository.save(game);
            return true;
        }else {
            return false;
        }
    }

    private boolean putSignAux(int x, int y, String[][] gameBoard, Player player) {
        if(!gameBoard[x - 1][y - 1].equals(Game.DEFAULT_BOARD_CHARACTER)) {
            return false;
        }
        else{
            gameBoard[x-1][y-1] = player.getSignOfPlayer().getSign();
        }
        return true;
    }

    private void putDefaultSign(int x, int y, String[][] gameBoard) {
        gameBoard[x-1][y-1] =  Game.DEFAULT_BOARD_CHARACTER;
    }

    @Override
    public String playNextMove(Long gameId, int x, int y) {
        boolean changePlayer = false;
        if (putSign(gameId, x, y)) {
            changePlayer = true;
        }
        return determineNextPhaseOfGame(gameId, changePlayer);
    }

    @Override
    public String playNextAIMove(Long gameId) {

        Difficulty diff = findById(gameId).getCurrentPlayer().getDifficulty();
        switch (diff) {
            case EASY:
                randomAIMove(gameId);
                break;

            case MEDIUM:
                mediumAIMove(gameId);
                break;
            case DIFFICULT:
                difficultAIMove(gameId);
                break;
            case IMPOSSIBLE:
                minmaxAIMove(gameId);
                break;

        }
        return determineNextPhaseOfGame(gameId, true);

    }

    private void mediumAIMove(Long gameId) {
        if (r.nextDouble()<0.8){
            difficultAIMove(gameId);
        }else{
            randomAIMove(gameId);
        }
    }


    private void difficultAIMove(Long gameId) {
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();

        String currentSign = game.getCurrentPlayer().getSignOfPlayer().getSign();
        String opponentSign = game.getCurrentPlayer().equals(game.getPlayer1()) ?
                game.getPlayer2().getSignOfPlayer().getSign():game.getPlayer1().getSignOfPlayer().getSign();

        int x = 0;
        int y = 0;

        int[] corr = new int[2];
        corr[0]=x;
        corr[1]=y;

        if (findBestSpot(gameBoard, currentSign, corr)){
            putSign(gameId, corr[0]+1, corr[1]+1);
        }else if (findBestSpot(gameBoard, opponentSign, corr)) {
            putSign(gameId, corr[0]+1, corr[1]+1);
        }else{
            randomAIMove(gameId);
        }
    }

    private boolean findBestSpot(String[][] gameBoard, String sign, int corr[]) {
        boolean isSpotDetermined = false;

        for (int i = 0; i < gameBoard.length; i++) {
            if (!isSpotDetermined) {
                String[] row = new String[gameBoard.length];
                String[] col = new String[gameBoard.length];

                for (int j = 0; j < gameBoard[i].length; j++) {
                    row[j] = gameBoard[j][i];
                    col[j] = gameBoard[i][j];
                }
                if (row[0].equals(sign) && row[1].equals(sign) && row[2].equals(Game.DEFAULT_BOARD_CHARACTER)) {
                    isSpotDetermined = true;
                    corr[0] = 2;
                    corr[1] = i;
                } else if (row[0].equals(sign) && row[1].equals(Game.DEFAULT_BOARD_CHARACTER) && row[2].equals(sign)) {
                    isSpotDetermined = true;
                    corr[0] = 1;
                    corr[1] = i;
                } else if (row[0].equals(Game.DEFAULT_BOARD_CHARACTER) && row[1].equals(sign) && row[2].equals(sign)) {
                    isSpotDetermined = true;
                    corr[0] = 0;
                    corr[1] = i;
                } else if (col[0].equals(sign) && col[1].equals(sign) && col[2].equals(Game.DEFAULT_BOARD_CHARACTER)) {
                    isSpotDetermined = true;
                    corr[0] = i;
                    corr[1] = 2;
                } else if (col[0].equals(sign) && col[1].equals(Game.DEFAULT_BOARD_CHARACTER) && col[2].equals(sign)) {
                    isSpotDetermined = true;
                    corr[0] = i;
                    corr[1] = 1;
                } else if (col[0].equals(Game.DEFAULT_BOARD_CHARACTER) && col[1].equals(sign) && col[2].equals(sign)) {
                    isSpotDetermined = true;
                    corr[0] = i;
                    corr[1] = 0;
                }
            }
        }
        if (!isSpotDetermined) {
            if (gameBoard[0][0].equals(sign) && gameBoard[1][1].equals(sign) && gameBoard[2][2].equals(Game.DEFAULT_BOARD_CHARACTER)) {
                isSpotDetermined = true;
                corr[0] = 2;
                corr[1] = 2;
            } else if (gameBoard[0][0].equals(sign) && gameBoard[1][1].equals(Game.DEFAULT_BOARD_CHARACTER) && gameBoard[2][2].equals(sign)) {
                isSpotDetermined = true;
                corr[0] = 1;
                corr[1] = 1;
            } else if (gameBoard[0][0].equals(Game.DEFAULT_BOARD_CHARACTER) && gameBoard[1][1].equals(sign) && gameBoard[2][2].equals(sign)) {
                isSpotDetermined = true;
                corr[0] = 0;
                corr[1] = 0;
            } else if (gameBoard[0][2].equals(sign) && gameBoard[1][1].equals(sign) && gameBoard[2][0].equals(Game.DEFAULT_BOARD_CHARACTER)) {
                isSpotDetermined = true;
                corr[0] = 2;
                corr[1] = 0;
            } else if (gameBoard[0][2].equals(sign) && gameBoard[1][1].equals(Game.DEFAULT_BOARD_CHARACTER) && gameBoard[2][0].equals(sign)) {
                isSpotDetermined = true;
                corr[0] = 1;
                corr[1] = 1;
            } else if (gameBoard[0][2].equals(Game.DEFAULT_BOARD_CHARACTER) && gameBoard[1][1].equals(sign) && gameBoard[2][0].equals(sign)) {
                isSpotDetermined = true;
                corr[0] = 0;
                corr[1] = 2;
            }
        }
        return isSpotDetermined;
    }

    private void randomAIMove(Long gameId) {
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();
        boolean isOk;
        do {
            isOk = putSign(gameId, r.nextInt(gameBoard.length)+1, r.nextInt(gameBoard.length)+1);
        }while (!isOk);
    }

    private void minmaxAIMove(Long gameId){
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();

        Player playerAI = game.getCurrentPlayer();
        Player playerHuman = game.getPlayer1().equals(playerAI) ? game.getPlayer2(): game.getPlayer1();
        int minimaxResult;
        int xCorrMove=0;
        int yCorrMove=0;
        int bestResult = -1000;

        for(int i = 1; i <= 3; i++){
            for(int j = 1; j <= 3; j++){

                if(gameBoard[i-1][j-1].equals(Game.DEFAULT_BOARD_CHARACTER))
                {
                    putSignAux(i, j, gameBoard, playerAI);
                    minimaxResult = minimax(gameBoard, playerAI, playerHuman);
                    putDefaultSign(i, j, gameBoard);

                    if(minimaxResult > bestResult)
                    {
                        bestResult = minimaxResult;
                        xCorrMove = i;
                        yCorrMove = j;
                    }
                }
            }
        }

        putSign(gameId, xCorrMove, yCorrMove);

    }


    private int minimax(String[][] gameBoard, Player currentPlayer, Player secondPlayer)  {

        if(winning(gameBoard,currentPlayer)) {
            if (currentPlayer.isReal()){
                return -10;
            }else{
                return 10;
            }
        }
        else if (drawAux(gameBoard)){
            return 0;
        }

        else {
            int minmaxResult;
            int bestResult;

            Player temp = secondPlayer;
            secondPlayer = currentPlayer;
            currentPlayer = temp;

            if (currentPlayer.isReal()){ //case when human
                bestResult = 1000 ;
            }else{                        //case when AI
                bestResult = -1000;
            }

            for (int i = 1; i <= 3; i++) {
                for (int j = 1; j <= 3; j++) {

                    if (gameBoard[i - 1][j - 1].equals(Game.DEFAULT_BOARD_CHARACTER)) {

                        putSignAux(i, j, gameBoard, currentPlayer);
                        minmaxResult = minimax(gameBoard, currentPlayer, secondPlayer);
                        putDefaultSign(i, j, gameBoard);

                        if ( (!currentPlayer.isReal() && minmaxResult > bestResult) ||
                                (currentPlayer.isReal() && minmaxResult < bestResult) ) {
                            bestResult = minmaxResult;
                        }
                    }
                }
            }
            return bestResult;
        }
    }

    @Override
    public Game repeatAGame(Long gameId) {
        Game previousGame = this.findById(gameId);
        Game repeatedGame = new Game();

        repeatedGame.setPlayer1(previousGame.getPlayer1());
        repeatedGame.setPlayer2(previousGame.getPlayer2());

        gameRepository.save(repeatedGame);

        if (previousGame.getWinner()!=null){
            if(previousGame.getWinner().equals(repeatedGame.getPlayer1())){
                repeatedGame.setCurrentPlayer(repeatedGame.getPlayer2());
            }else {
                repeatedGame.setCurrentPlayer(repeatedGame.getPlayer1());
            }
        } else {
            setRandomlyAFirstPlayer(repeatedGame.getId());
        }

        gameRepository.save(repeatedGame);

        return repeatedGame;
    }

    @Override
    public void deleteById(Long gameId) {
        gameRepository.deleteById(gameId);
    }

    @Override
    public Set<Game> getGameList() {
        Set<Game> gameSet = new HashSet<>();
        gameRepository.findAll().iterator().forEachRemaining(gameSet::add);

        return gameSet;

    }
}
