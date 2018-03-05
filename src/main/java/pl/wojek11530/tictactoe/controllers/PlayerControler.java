package pl.wojek11530.tictactoe.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import pl.wojek11530.tictactoe.commands.PlayerCommand;
import pl.wojek11530.tictactoe.services.PlayerService;

import javax.validation.Valid;

@Slf4j
@Controller
public class PlayerControler {

    private final PlayerService playerService;

    public PlayerControler(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/tictactoe/player/list")
    public String getListOfPlayersPage(Model model){

        log.debug("Getting Page with List of Players");
        model.addAttribute("players", playerService.getPlayerList());

        return "player/list";
    }


    @GetMapping("/tictactoe/player/new")
    public String newPLayer(Model model){

        model.addAttribute("player",new PlayerCommand());

        return "player/new";
    }

    @PostMapping("player")
    public String savePlayer(@Valid @ModelAttribute("player") PlayerCommand command, BindingResult bindingResult){

        if(bindingResult.hasErrors()){

            bindingResult.getAllErrors().forEach(objectError -> {
                log.debug(objectError.toString());
            });

            return "player/new";
        }


        PlayerCommand savedCommand = playerService.savePlayerCommand(command);

        return "redirect:/tictactoe/player/list";
    }
}
