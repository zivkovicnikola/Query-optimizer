package bp2.upit;

public enum Operator {
    JEDNAKO("="),
    RAZLICITO("<>"),
    MANJE("<"),
    MANJE_JEDNAKO("<="),
    VECE(">"),
    VECE_JEDNAKO(">=");

    private final String simbol;

    Operator(String simbol) {
        this.simbol = simbol;
    }

    public String getSimbol() {
        return simbol;
    }

    @Override
    public String toString() {
        return simbol;
    }

}