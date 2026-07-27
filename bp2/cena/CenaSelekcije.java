package bp2.cena;

import bp2.model.Index;
import bp2.model.Tabela;
import bp2.model.TipIndexa;
import bp2.upit.Operator;
import bp2.upit.Uslov;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CenaSelekcije {

    private static final double HASH_KONST = 1.2;

    public static List<Pristup> kandidati(Tabela t, Uslov uslov) {
        if (uslov.jeSpajanje())
            throw new IllegalArgumentException("Uslov spajanja nije selekcija: " + uslov);

        List<Pristup> lista = new ArrayList<>();

        long n = t.getBrRedova();
        long b = t.getBrBlokova();
        int f = t.getBrRedovaPoBloku();
        boolean jedinstven = uslov.getLeva().getRazresenAtribut().isJedinstven();
        long m = brojPogodaka(t, uslov);

        if (uslov.getOperator() == Operator.JEDNAKO && jedinstven)
            lista.add(new Pristup("A1 linearno skeniranje (jedinstven, prosek b/2)", b / 2.0));
        else
            lista.add(new Pristup("A1 linearno skeniranje", b));

        String atr = uslov.getLeva().getRazresenAtribut().getNaziv();
        for (Index idx : t.getImajuIndex(atr)) {
            Pristup p = pristupPrekoIndeksa(idx, uslov, m, f, b);
            if (p != null) lista.add(p);
        }

        return lista;
    }

    public static Pristup najbolji(Tabela t, Uslov uslov) {
        return kandidati(t, uslov).stream()
                .min(Comparator.comparingDouble(Pristup::getCena))
                .orElseThrow();
    }

    public static long brojPogodaka(Tabela t, Uslov uslov) {
        long n = t.getBrRedova();
        long v = Math.max(1, uslov.getLeva().getRazresenAtribut().getRazliciteVrednosti());
        switch (uslov.getOperator()) {
            case JEDNAKO: return Math.max(1, n / v);
            case RAZLICITO: return Math.max(1, n - n / v);
            default: return Math.max(1, n / 2);
        }
    }

    private static Pristup pristupPrekoIndeksa(Index idx, Uslov uslov, long m, int f, long b) {
        Operator op = uslov.getOperator();

        if (op == Operator.RAZLICITO) return null;

        if (idx.getTip() == TipIndexa.HES) {
            if (op != Operator.JEDNAKO) return null;
            if (idx.getAtributi().size() > 1) return null;
            return new Pristup("hes indeks " + idx.getNaziv(), HASH_KONST + m);
        }

        int h = idx.getVisina();
        if (op == Operator.JEDNAKO) {
            if (idx.isKlasterovan()) {
                long blokovaSaPogocima = (m + f - 1) / f;
                return new Pristup("A2/A3 B+ klasterovan " + idx.getNaziv(), h + blokovaSaPogocima);
            }
            return new Pristup("A4 B+ neklasterovan " + idx.getNaziv(), h + m);
        }

        if (idx.isKlasterovan())
            return new Pristup("A5 B+ klasterovan, poredjenje " + idx.getNaziv(), h + b / 2.0);
        return new Pristup("A6 B+ neklasterovan, poredjenje " + idx.getNaziv(), h + m);
    }

    public static Pristup najboljiZaKonjunkciju(Tabela t, List<Uslov> uslovi) {
        if (uslovi.isEmpty())
            throw new IllegalArgumentException("Prazna lista uslova.");
        if (uslovi.size() == 1)
            return najbolji(t, uslovi.get(0));

        List<Pristup> kandidati = new ArrayList<>();

        Pristup najboljiPojedinacni = null;
        for (Uslov u : uslovi) {
            Pristup p = najbolji(t, u);
            if (najboljiPojedinacni == null || p.getCena() < najboljiPojedinacni.getCena())
                najboljiPojedinacni = p;
        }
        kandidati.add(new Pristup("A7 [" + najboljiPojedinacni.getOpis() + ", ostali uslovi u memoriji]", najboljiPojedinacni.getCena()));

        for (Index idx : t.getIndexi()) {
            Pristup p = pristupKompozitni(t, idx, uslovi);
            if (p != null) kandidati.add(p);
        }

        return kandidati.stream()
                .min(Comparator.comparingDouble(Pristup::getCena))
                .orElseThrow();
    }

    private static Pristup pristupKompozitni(Tabela t, Index idx, List<Uslov> uslovi) {
        if (idx.getAtributi().size() < 2) return null;

        long n = t.getBrRedova();
        int f = t.getBrRedovaPoBloku();

        double selektivnost = 1.0;
        int pokriveno = 0;
        boolean poslednjePoredjenje = false;

        for (String atrIndeksa : idx.getAtributi()) {
            Uslov u = nadjiUslovNadAtributom(uslovi, atrIndeksa);
            if (u == null) break;

            if (u.getOperator() == Operator.JEDNAKO) {
                long v = Math.max(1, u.getLeva().getRazresenAtribut().getRazliciteVrednosti());
                selektivnost *= 1.0 / v;
                pokriveno++;
            } else if (u.getOperator() != Operator.RAZLICITO && idx.getTip() == TipIndexa.B_PLUS) {
                selektivnost *= 0.5;
                pokriveno++;
                poslednjePoredjenje = true;
                break;
            } else {
                break;
            }
        }

        if (pokriveno < 2) return null;
        if (idx.getTip() == TipIndexa.HES && pokriveno < idx.getAtributi().size())
            return null;

        long m = Math.max(1, Math.round(n * selektivnost));

        double cena;
        if (idx.getTip() == TipIndexa.HES) {
            cena = HASH_KONST + m;
        } else if (idx.isKlasterovan()) {
            cena = idx.getVisina() + (m + f - 1) / f;
        } else {
            cena = idx.getVisina() + m;
        }

        return new Pristup("A8 kompozitni " + (idx.getTip() == TipIndexa.HES ? "hes " : "B+ ") + idx.getNaziv() + " (pokriva " + pokriveno + " uslova" + (poslednjePoredjenje ? ", poslednji poredjenje" : "") + ")", cena);
    }

    private static Uslov nadjiUslovNadAtributom(List<Uslov> uslovi, String atribut) {
        for (Uslov u : uslovi)
            if (u.getLeva().getRazresenAtribut().getNaziv().equalsIgnoreCase(atribut))
                return u;
        return null;
    }
}