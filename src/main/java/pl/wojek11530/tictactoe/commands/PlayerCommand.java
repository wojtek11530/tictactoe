package pl.wojek11530.tictactoe.commands;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.wojek11530.tictactoe.domain.SignOfPlayer;

@Setter
@Getter
@NoArgsConstructor
public class PlayerCommand {
    private Long id;
    private String name;
    private SignOfPlayer signOfPlayer;
    private boolean isReal;
    private int numberOfGames;
    private int numberOfWins;
    private int numberOfDefeats;
    private int numberOfDraws;

}
