package bp2.upit;

public class Uslov {

    private AtributRef leva;
    private Operator operator;
    private AtributRef desnaAtribut;
    private String desnaKonstanta;
    private boolean konstantaJeString;

    public static Uslov saKonstantom(AtributRef leva, Operator op, String vrednost, boolean jeString) {
        Uslov u = new Uslov();
        u.leva = leva;
        u.operator = op;
        u.desnaKonstanta = vrednost;
        u.konstantaJeString = jeString;
        return u;
    }

    public static Uslov saAtributom(AtributRef leva, Operator op, AtributRef desna) {
        Uslov u = new Uslov();
        u.leva = leva;
        u.operator = op;
        u.desnaAtribut = desna;
        return u;
    }

    public boolean jeSpajanje() {
        return desnaAtribut != null;
    }

    public AtributRef getLeva() {
        return leva;
    }

    public void setLeva(AtributRef leva) {
        this.leva = leva;
    }

    public Operator getOperator() {
        return operator;
    }

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public AtributRef getDesnaAtribut() {
        return desnaAtribut;
    }

    public void setDesnaAtribut(AtributRef desnaAtribut) {
        this.desnaAtribut = desnaAtribut;
    }

    public String getDesnaKonstanta() {
        return desnaKonstanta;
    }

    public void setDesnaKonstanta(String desnaKonstanta) {
        this.desnaKonstanta = desnaKonstanta;
    }

    public boolean isKonstantaJeString() {
        return konstantaJeString;
    }

    public void setKonstantaJeString(boolean konstantaJeString) {
        this.konstantaJeString = konstantaJeString;
    }

    @Override
    public String toString() {
        String desna = jeSpajanje() ? desnaAtribut.toString() : (konstantaJeString ? "'" + desnaKonstanta + "'" : desnaKonstanta);
        return leva + " " + operator + " " + desna;
    }

}