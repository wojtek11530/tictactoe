package pl.wojek11530.tictactoe.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
public class Player{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @Enumerated(value = EnumType.STRING)
    private SignOfPlayer signOfPlayer;
    private boolean isReal;
    private Difficulty difficulty;

    @OneToMany
    @JoinTable(name = "player_games",
            joinColumns = @JoinColumn(name = "player_id"),
            inverseJoinColumns = @JoinColumn(name = "game_id"))
    private Set<Game> gameSet = new HashSet<>();
    private int numberOfGames;
    private int numberOfWins;
    private int numberOfDefeats;
    private int numberOfDraws;

    public Player() {
    }

    public Player(String name, boolean isReal) {
        this.name = name;
        this.isReal = isReal;
    }

    public Player(String name, SignOfPlayer signOfPlayer, boolean isReal, Set<Game> gameSet, int numberOfGames, int numberOfWins, int numberOfDefeats, int numberOfDraws) {
        this.name = name;
        this.signOfPlayer = signOfPlayer;
        this.isReal = isReal;
        this.gameSet = gameSet;
        this.numberOfGames = numberOfGames;
        this.numberOfWins = numberOfWins;
        this.numberOfDefeats = numberOfDefeats;
        this.numberOfDraws = numberOfDraws;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setReal(boolean real) {
        isReal = real;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public SignOfPlayer getSignOfPlayer() {
        return signOfPlayer;
    }

    public void setSignOfPlayer(SignOfPlayer signOfPlayer) {
        this.signOfPlayer = signOfPlayer;
    }

    public boolean isReal() {
        return isReal;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Set<Game> getGameSet() {
        return gameSet;
    }

    public void setGameSet(Set<Game> gameSet) {
        this.gameSet = gameSet;
    }

    public int getNumberOfGames() {
        return numberOfGames;
    }

    public void setNumberOfGames(int numberOfGames) {
        this.numberOfGames = numberOfGames;
    }

    public int getNumberOfWins() {
        return numberOfWins;
    }

    public void setNumberOfWins(int numberOfWins) {
        this.numberOfWins = numberOfWins;
    }

    public int getNumberOfDefeats() {
        return numberOfDefeats;
    }

    public void setNumberOfDefeats(int numberOfDefeats) {
        this.numberOfDefeats = numberOfDefeats;
    }

    public int getNumberOfDraws() {
        return numberOfDraws;
    }

    public void setNumberOfDraws(int numberOfDraws) {
        this.numberOfDraws = numberOfDraws;
    }
}