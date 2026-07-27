package bp2.upit;

import java.util.List;

public class Parser {

    private final List<Token> tokeni;
    private int poz = 0;

    private Parser(List<Token> tokeni) {
        this.tokeni = tokeni;
    }

    public static Upit parsiraj(String tekstUpita) {
        Parser p = new Parser(Lekser.tokenizuj(tekstUpita));
        return p.parsirajUpit();
    }

    private Token tekuci() {
        return tokeni.get(poz);
    }

    private boolean jeKljucnaRec(String rec) {
        return tekuci().getTip() == TipTokena.KLJUCNA_REC && tekuci().getVrednost().equals(rec);
    }

    private Token ocekuj(TipTokena tip, String opis) {
        if (tekuci().getTip() != tip)
            throw new IllegalArgumentException("Sintaksna greska: ocekivano " + opis + ", nadjeno '" + tekuci().getVrednost() + "' na poziciji " + tekuci().getPozicija());
        return tokeni.get(poz++);
    }

    private void ocekujKljucnuRec(String rec) {
        if (!jeKljucnaRec(rec))
            throw new IllegalArgumentException("Sintaksna greska: ocekivano '" + rec + "', nadjeno '" + tekuci().getVrednost() + "' na poziciji " + tekuci().getPozicija());
        poz++;
    }

    private Upit parsirajUpit() {
        Upit upit = new Upit();
        parsirajSelect(upit);
        parsirajFrom(upit);
        if (jeKljucnaRec("WHERE"))
            parsirajWhere(upit);
        if (jeKljucnaRec("ORDER"))
            parsirajOrderBy(upit);
        ocekuj(TipTokena.KRAJ, "kraj upita");
        return upit;
    }

    private void parsirajSelect(Upit upit) {
        ocekujKljucnuRec("SELECT");
        if (tekuci().getTip() == TipTokena.ZVEZDICA) {
            upit.setSelectSve(true);
            poz++;
            return;
        }

        upit.getSelectLista().add(parsirajAtributRef());

        while (tekuci().getTip() == TipTokena.ZAREZ) {
            poz++;
            upit.getSelectLista().add(parsirajAtributRef());
        }
    }

    private void parsirajFrom(Upit upit) {
        ocekujKljucnuRec("FROM");
        upit.getTabele().add(ocekuj(TipTokena.IDENTIFIKATOR, "naziv tabele").getVrednost());

        while (tekuci().getTip() == TipTokena.ZAREZ) {
            poz++;
            upit.getTabele().add(ocekuj(TipTokena.IDENTIFIKATOR, "naziv tabele").getVrednost());
        }

        if (upit.getTabele().size() > 4)
            throw new IllegalArgumentException("Upit moze imati najvise 4 tabele, navedeno " + upit.getTabele().size());
    }

    private void parsirajWhere(Upit upit) {
        ocekujKljucnuRec("WHERE");
        upit.getUslovi().add(parsirajUslov());

        while (jeKljucnaRec("AND")) {
            poz++;
            upit.getUslovi().add(parsirajUslov());
        }

        if (upit.getUslovi().size() > 6)
            throw new IllegalArgumentException("WHERE moze imati najvise 6 uslova, navedeno " + upit.getUslovi().size());
    }

    private Uslov parsirajUslov() {
        AtributRef leva = parsirajAtributRef();
        String simbol = ocekuj(TipTokena.OPERATOR, "operator poredjenja").getVrednost();
        Operator op = operatorIzSimbola(simbol);

        Token t = tekuci();
        switch (t.getTip()) {
            case BROJ:
                poz++;
                return Uslov.saKonstantom(leva, op, t.getVrednost(), false);
            case STRING:
                poz++;
                return Uslov.saKonstantom(leva, op, t.getVrednost(), true);
            case IDENTIFIKATOR:
                return Uslov.saAtributom(leva, op, parsirajAtributRef());
            default:
                throw new IllegalArgumentException("Sintaksna greska: ocekivana konstanta ili atribut, nadjeno '" + t.getVrednost() + "' na poziciji " + t.getPozicija());
        }
    }

    private void parsirajOrderBy(Upit upit) {
        ocekujKljucnuRec("ORDER");
        ocekujKljucnuRec("BY");
        upit.setOrderBy(parsirajAtributRef());
        if (jeKljucnaRec("DESC")) {
            upit.setOrderOpadajuce(true);
            poz++;
        }
        else if (jeKljucnaRec("ASC")) {
            poz++;
        }
    }

    private AtributRef parsirajAtributRef() {
        String prvi = ocekuj(TipTokena.IDENTIFIKATOR, "naziv atributa").getVrednost();
        if (tekuci().getTip() == TipTokena.TACKA) {
            poz++;
            String drugi = ocekuj(TipTokena.IDENTIFIKATOR, "naziv atributa posle tacke").getVrednost();
            return new AtributRef(prvi, drugi);
        }
        return new AtributRef(null, prvi);
    }

    private Operator operatorIzSimbola(String s) {
        for (Operator op : Operator.values())
            if (op.getSimbol().equals(s))
                return op;
        throw new IllegalArgumentException("Nepoznat operator: " + s);
    }
}