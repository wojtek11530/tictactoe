package pl.wojek11530.tictactoe.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.domain.Game;
import pl.wojek11530.tictactoe.services.GameService;
import pl.wojek11530.tictactoe.services.PlayerService;

import javax.validation.Valid;

@Slf4j
@Controller
public class GameController {

    GameService gameService;
    PlayerService playerService;

    public GameController(GameService gameService, PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @GetMapping("/tictactoe/game/new")
    public String newGame(Model model){

        return "game/new";
    }

    @GetMapping("/tictactoe/game/new/oneplayer")
    public String newGameWithOnePLayer(Model model){

        model.addAttribute("game",new GameCommand());

        model.addAttribute("playerList", playerService.listAllCommandPlayers() );

        return "game/oneplayer";
    }

    @GetMapping("/tictactoe/game/new/twoplayers")
    public String newGameWithTwoPlayers(Model model){

        model.addAttribute("game",new GameCommand());

        model.addAttribute("playerList", playerService.listAllCommandPlayers() );

        return "game/twoplayers";
    }

    @PostMapping("game")
    public String saveGame(@Valid @ModelAttribute("game") GameCommand command, BindingResult bindingResult){

        if(bindingResult.hasErrors()){

            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });

            return "game/new";
        }

        GameCommand savedCommand = gameService.saveGameCommand(command);

        gameService.setRandomlyAFirstPlayer(savedCommand.getId());

        return "redirect:/tictactoe/game/" + savedCommand.getId() + "/play";
    }

    @GetMapping("/tictactoe/game/{id}/play")
    public String play(@PathVariable String id, Model model){


        model.addAttribute("game", gameService.findById( new Long(id)));

        return "game/play";

    }

    @GetMapping("/tictactoe/game/{id}/play/move/{xcord}/{ycord}")
    public String playNextMove(@PathVariable String id,@PathVariable String xcord,@PathVariable String ycord, Model model){
        Long gameId = new Long(id);

        String whereNext = gameService.playNextMove(gameId,Integer.valueOf(xcord), Integer.valueOf(ycord));
        model.addAttribute("game", gameService.findById(gameId));

        return whereNext;
        /*if (gameService.putSign(gameId, Integer.valueOf(xcord), Integer.valueOf(ycord))) {
            gameService.changeCurrentPlayer(gameId);
        }
        model.addAttribute("game", gameService.findById(gameId));

        if (gameService.checkIfTHereIsAWInner(gameId)){
            return "redirect:/tictactoe/game/" + id + "/win";
        }
        else if (gameService.isBoardFull(gameId)){
            return "redirect:/tictactoe/game/" + id + "/draw";
        }
        else {
            return "game/play";
        }
        */


    }

    @GetMapping("/tictactoe/game/{id}/draw")
    public String draw(@PathVariable String id, Model model){
        model.addAttribute("game", gameService.findById( new Long(id)));
        return "game/draw";
    }

    @GetMapping("/tictactoe/game/{id}/win")
    public String win(@PathVariable String id, Model model){
        model.addAttribute("game", gameService.findById( new Long(id)));
        return "game/win";
    }


    @GetMapping("/tictactoe/game/{id}/repeat")
    public String repeatAGame(@PathVariable String id, Model model){
        Game newGame = gameService.repeatAGame(new Long(id));
        return "redirect:/tictactoe/game/" + newGame.getId() + "/play";
    }


    @GetMapping("tictactoe/game/{id}/delete")
    public String deleteById(@PathVariable String id){

        log.debug("Deleting id: " + id);

        gameService.deleteById(Long.valueOf(id));
        return "redirect:/tictactoe";
    }

    @GetMapping("/tictactoe/game/list")
    public String getListOfGamesPage(Model model){

        log.debug("Getting Page with List of Games");
        model.addAttribute("games", gameService.getGameList());

        return "game/list";
    }

}
