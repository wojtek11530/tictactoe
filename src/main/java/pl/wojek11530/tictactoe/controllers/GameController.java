package pl.wojek11530.tictactoe.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.wojek11530.tictactoe.commands.GameCommand;
import pl.wojek11530.tictactoe.domain.Game;
import pl.wojek11530.tictactoe.domain.SignOfPlayer;
import pl.wojek11530.tictactoe.exceptions.NotFoundException;
import pl.wojek11530.tictactoe.services.GameService;
import pl.wojek11530.tictactoe.services.PlayerService;

import javax.validation.Valid;
import java.util.EnumSet;

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

        GameCommand command=new GameCommand();
        command.setPlayer2(playerService.getNotRealPLayer());
        model.addAttribute("game", command);

        model.addAttribute("playerList", playerService.listAllCommandPlayers() );

        EnumSet<SignOfPlayer> allSigns = EnumSet.allOf( SignOfPlayer.class );
        model.addAttribute("allSigns", allSigns);

        return "game/oneplayer";
    }

    @GetMapping("/tictactoe/game/new/twoplayers")
    public String newGameWithTwoPlayers(Model model){
        model.addAttribute("game",new GameCommand());
        model.addAttribute("playerList", playerService.listAllCommandPlayers() );
        return "game/twoplayers";
    }

    @PostMapping("gameForOne")
    public String saveGameOne(@Valid @ModelAttribute("game") GameCommand command, BindingResult bindingResult, Model model){

        if(bindingResult.hasErrors()){

            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });

            model.addAttribute("playerList", playerService.listAllCommandPlayers() );

            EnumSet<SignOfPlayer> allSigns = EnumSet.allOf( SignOfPlayer.class );
            model.addAttribute("allSigns", allSigns);
            return "game/oneplayer";
        }

        if (command.getPlayer1().getSignOfPlayer()==SignOfPlayer.CIRCLE){
            command.getPlayer2().setSignOfPlayer(SignOfPlayer.CROSS);
        }else{
            command.getPlayer2().setSignOfPlayer(SignOfPlayer.CIRCLE);
        }

        GameCommand savedCommand = gameService.saveGameCommand(command);
        gameService.setRandomlyAFirstPlayer(savedCommand.getId());

        return "redirect:/tictactoe/game/" + savedCommand.getId() + "/play";
    }


    @PostMapping("gameForTwo")
    public String saveGameTwo(@Valid @ModelAttribute("game") GameCommand command, BindingResult bindingResult){

        if(bindingResult.hasErrors()){

            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });
            return "game/twoplayers";
        }

        command.getPlayer1().setSignOfPlayer(SignOfPlayer.CROSS);
        command.getPlayer2().setSignOfPlayer(SignOfPlayer.CIRCLE);

        GameCommand savedCommand = gameService.saveGameCommand(command);
        gameService.setRandomlyAFirstPlayer(savedCommand.getId());

        return "redirect:/tictactoe/game/" + savedCommand.getId() + "/play";
    }

    @GetMapping("/tictactoe/game/{id}/play")
    public String play(@PathVariable String id, Model model){

        Long gameId =new Long(id);
        model.addAttribute("game", gameService.findById(gameId ));

        if (!gameService.findById(gameId).getCurrentPlayer().isReal()){
            return "redirect:/tictactoe/game/" + gameId + "/playAI";
        }else {
            return "game/play";
        }
    }

    @GetMapping("/tictactoe/game/{id}/playAI")
    public String playAI(@PathVariable String id, Model model){

        Long gameId = new Long(id);
        String nextPage = gameService.playNextAIMove(gameId);
        model.addAttribute("game", gameService.findById( new Long(id)));

        return nextPage;

    }

    @GetMapping("/tictactoe/game/{id}/play/move/{xcord}/{ycord}")
    public String playNextMove(@PathVariable String id,@PathVariable String xcord,@PathVariable String ycord, Model model){
        Long gameId = new Long(id);

        String nextPage = gameService.playNextMove(gameId,Integer.valueOf(xcord), Integer.valueOf(ycord));
        model.addAttribute("game", gameService.findById(gameId));

        return nextPage;
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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public ModelAndView handleNotFound(Exception exception){

        log.error("Handling not found exception");
        log.error(exception.getMessage());

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("404error");
        modelAndView.addObject("exception", exception);

        return modelAndView;
    }

}
