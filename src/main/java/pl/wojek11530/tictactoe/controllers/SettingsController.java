package pl.wojek11530.tictactoe.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
public class SettingsController {

    public SettingsController() {
    }

    @RequestMapping("/tictactoe/settings")
    public String getSettingPage(Model model){

        log.debug("Getting Main Menu Page");

        return "settings";
    }
}
