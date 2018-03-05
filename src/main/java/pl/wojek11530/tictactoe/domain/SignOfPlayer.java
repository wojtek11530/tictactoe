package pl.wojek11530.tictactoe.domain;

public enum SignOfPlayer {
    CIRCLE("O"),
    CROSS("X");

    private final String sign;

    SignOfPlayer(String sign) {
        this.sign = sign;
    }

    public String getSign() {
        return sign;
    }
}
