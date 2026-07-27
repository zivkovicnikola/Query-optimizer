package bp2.upit;

public class Token {

    private TipTokena tip;
    private String vrednost;
    private int pozicija;

    public Token(TipTokena tip, String vrednost, int pozicija) {
        this.tip = tip; this.vrednost = vrednost; this.pozicija = pozicija;
    }

    public TipTokena getTip() {
        return tip;
    }

    public void setTip(TipTokena tip) {
        this.tip = tip;
    }

    public String getVrednost() {
        return vrednost;
    }

    public void setVrednost(String vrednost) {
        this.vrednost = vrednost;
    }

    public int getPozicija() {
        return pozicija;
    }

    public void setPozicija(int pozicija) {
        this.pozicija = pozicija;
    }

    @Override
    public String toString() {
        return tip + "(" + vrednost + ")";
    }

}