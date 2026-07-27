package bp2.cena;

public class Pristup {

    private final String opis;
    private final double cena;

    public Pristup(String opis, double cena) {
        this.opis = opis;
        this.cena = cena;
    }

    public String getOpis() {
        return opis;
    }

    public double getCena() {
        return cena;
    }

    @Override
    public String toString() {
        return opis + " (cena=" + String.format("%.1f", cena) + ")";
    }
}