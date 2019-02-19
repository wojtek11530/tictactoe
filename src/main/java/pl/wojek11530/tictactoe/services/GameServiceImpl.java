package pl.wojek11530.tictactoe.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.converters.GameCommandToGame;
import pl.wojek11530.tictactoe.converters.GameToGameCommand;
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
    public boolean putSign(Long id, int x, int y) {
        Game game = findById(id);

        if(!game.getGameBoard()[x - 1][y - 1].equals(Game.DEFAULT_BOARD_CHARACTER)) {
            return false;
        }
        else{
            findById(id).getGameBoard()[x-1][y-1] =  findById(id).getCurrentPlayer().getSignOfPlayer().getSign();
            gameRepository.save(game);
        }

        return true;
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

    @Override
    public void changeCurrentPlayer(Long gameId) {
        Game game = findById(gameId);
        if(game.getCurrentPlayer().equals(game.getPlayer1())){
            game.setCurrentPlayer(game.getPlayer2());
        }else{
            game.setCurrentPlayer(game.getPlayer1());
        }
        gameRepository.save(game);

    }

    @Override
    public boolean isBoardFull(Long gameId) {
        Game game = findById(gameId);

        String [][] gameBoard = game.getGameBoard();
        for (int i = 0; i < gameBoard.length; i++) {
            for (int j = 0; j < gameBoard[i].length; j++) {
                if (gameBoard[i][j].equals(Game.DEFAULT_BOARD_CHARACTER)) {
                    return false;
                }
            }
        }

        game.getPlayer1().setNumberOfDraws(game.getPlayer1().getNumberOfDraws()+1);
        game.getPlayer1().setNumberOfGames(game.getPlayer1().getNumberOfGames()+1);

        game.getPlayer2().setNumberOfDraws(game.getPlayer2().getNumberOfDraws()+1);
        game.getPlayer2().setNumberOfGames(game.getPlayer2().getNumberOfGames()+1);

        gameRepository.save(game);

        playerRepository.save(game.getPlayer1());
        playerRepository.save(game.getPlayer2());

        return true;
    }

    @Override
    public boolean checkIfTHereIsAWInner(Long gameId) {
        Game game = findById(gameId);
        Player winner;

        if (checkIfAnyRowHasTheSameSigns(gameId)!=null){
            winner = checkIfAnyRowHasTheSameSigns(gameId);
        } else if (checkIfAnyColHasTheSameSigns(gameId)!=null){
            winner = checkIfAnyColHasTheSameSigns(gameId);

        } else if (checkIfAnyDiagonalHasTheSameSigns(gameId)!=null){
            winner = checkIfAnyDiagonalHasTheSameSigns(gameId);
        }else {
            return false;
        }
        game.setWinner(winner);

        game.getPlayer1().setNumberOfGames(game.getPlayer1().getNumberOfGames()+1);
        game.getPlayer2().setNumberOfGames(game.getPlayer2().getNumberOfGames()+1);


        if(winner.equals(game.getPlayer1())){
            game.getPlayer1().setNumberOfWins(game.getPlayer1().getNumberOfWins()+1);
            game.getPlayer2().setNumberOfDefeats(game.getPlayer2().getNumberOfDefeats()+1);

        }else {
            game.getPlayer2().setNumberOfWins(game.getPlayer2().getNumberOfWins()+1);
            game.getPlayer1().setNumberOfDefeats(game.getPlayer1().getNumberOfDefeats()+1);
        }
        gameRepository.save(game);
        playerRepository.save(game.getPlayer1());
        playerRepository.save(game.getPlayer2());
        return true;

    }

    @Override
    public Player checkIfAnyRowHasTheSameSigns(Long gameId) {
        Player player = null;
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();

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

            if ((!row[0].equals(Game.DEFAULT_BOARD_CHARACTER)) && hasTheRowTheSameSigns) {
                if (row[0].equals(game.getPlayer1().getSignOfPlayer().getSign())) {
                    player =  game.getPlayer1();
                } else {
                    player =  game.getPlayer2();
                }
            }
        }
        return player;
    }

    @Override
    public Player checkIfAnyColHasTheSameSigns(Long gameId) {
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();

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

            if ((!col[0].equals(Game.DEFAULT_BOARD_CHARACTER)) && hasColTheSameSigns) {
                if (col[0].equals(game.getPlayer1().getSignOfPlayer().getSign())) {
                    return game.getPlayer1();
                } else {
                    return game.getPlayer2();
                }
            }
        }
        return null;

    }

    @Override
    public Player checkIfAnyDiagonalHasTheSameSigns(Long gameId) {
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();

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
        if ((!gameBoard[0][0].equals(Game.DEFAULT_BOARD_CHARACTER) && hasDiagonalOneTheSameSigns) ||
                (!gameBoard[0][2].equals(Game.DEFAULT_BOARD_CHARACTER) && hasDiagonalTwoTheSameSigns)) {
            if (gameBoard[1][1].equals(game.getPlayer1().getSignOfPlayer().getSign())) {
                return game.getPlayer1();
            } else {
                return game.getPlayer1();
            }
        } else {
            return null;
        }
    }

    public String determineNextPhaseOfGame(Long gameId) {
        if (checkIfTHereIsAWInner(gameId)){
            return "redirect:/tictactoe/game/" + gameId + "/win";
        }
        else if (isBoardFull(gameId)){
            return "redirect:/tictactoe/game/" + gameId + "/draw";
        }
        else {
            return "redirect:/tictactoe/game/" + gameId + "/play";
        }
    }

    @Override
    public String playNextMove(Long gameId, int x, int y) {
        if (putSign(gameId, x, y)) {
            changeCurrentPlayer(gameId);
        }
        return determineNextPhaseOfGame(gameId);
    }

    @Override
    public String playNextAIMove(Long gameId) {
        Game game = findById(gameId);
        String [][]gameBoard = game.getGameBoard();
        boolean isOk = false;
        do {
            isOk = putSign(gameId, r.nextInt(gameBoard.length)+1, r.nextInt(gameBoard.length)+1);
        }while (!isOk);
        changeCurrentPlayer(gameId);

        return determineNextPhaseOfGame(gameId);

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
