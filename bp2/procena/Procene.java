package bp2.procena;

import bp2.upit.*;
import java.util.List;
import java.util.Map;

public class Procene {

    public static Rezultat selekcija(Rezultat ulaz, Uslov uslov) {
        if (uslov.jeSpajanje()) {
            throw new IllegalArgumentException("Uslov spajanja nije selekcija: " + uslov);
        }

        AtributRef atribut = uslov.getLeva();

        long stariBrRedova = ulaz.getBrRedova();
        long brojRazlicitihVrednosti = ulaz.getV(atribut);

        long noviBrRedova = proceniBrojRedovaSelekcije(stariBrRedova, brojRazlicitihVrednosti, uslov.getOperator());

        Rezultat izlaz = napraviOsnovniRezultat(ulaz, noviBrRedova);

        azurirajVrednostiPosleSelekcije(ulaz, izlaz, atribut, uslov.getOperator(), stariBrRedova, noviBrRedova);

        return izlaz;
    }

    public static Rezultat spajanje(Rezultat levi, Rezultat desni, Uslov uslov) {
        if (!uslov.jeSpajanje() || uslov.getOperator() != Operator.JEDNAKO) {
            throw new IllegalArgumentException("Ocekivan uslov spajanja oblika A = B: " + uslov);
        }

        AtributRef refLevog;
        AtributRef refDesnog;

        if (levi.sadrziTabelu(uslov.getLeva().getRazresenaTabela().getNaziv())) {
            refLevog = uslov.getLeva();
            refDesnog = uslov.getDesnaAtribut();
        } else {
            refLevog = uslov.getDesnaAtribut();
            refDesnog = uslov.getLeva();
        }

        long noviBrRedova = proceniBrojRedovaSpajanja(levi, desni, refLevog, refDesnog);

        Rezultat izlaz = new Rezultat();
        izlaz.setBrRedova(noviBrRedova);
        izlaz.setBrRedovaPoBloku(proceniBrRedovaPoBlokuSpajanja(levi, desni));

        izlaz.getTabele().addAll(levi.getTabele());
        izlaz.getTabele().addAll(desni.getTabele());

        kopirajVMape(levi, izlaz, noviBrRedova);
        kopirajVMape(desni, izlaz, noviBrRedova);

        return izlaz;
    }

    public static Rezultat projekcija(Rezultat ulaz, List<AtributRef> atributi) {
        Rezultat izlaz = new Rezultat();

        izlaz.setBrRedova(ulaz.getBrRedova());
        izlaz.getTabele().addAll(ulaz.getTabele());

        int ukupanBrojAtributa = ulaz.getBrAtributa();
        int zadrzanBrojAtributa = dodajAtributeProjekcije(ulaz, izlaz, atributi);

        if (zadrzanBrojAtributa == 0) {
            throw new IllegalArgumentException("Projekcija bez ijednog atributa iz rezultata.");
        }

        long noviFaktorBlokiranja =
                (long) ulaz.getBrRedovaPoBloku() * ukupanBrojAtributa / zadrzanBrojAtributa;

        izlaz.setBrRedovaPoBloku((int) Math.max(1, noviFaktorBlokiranja));

        return izlaz;
    }

    private static long proceniBrojRedovaSelekcije(long brRedova, long brojRazlicitih, Operator operator) {
        long rezultat;

        switch (operator) {
            case JEDNAKO:
                rezultat = brRedova / Math.max(1, brojRazlicitih);
                break;
            case RAZLICITO:
                rezultat = brRedova - brRedova / Math.max(1, brojRazlicitih);
                break;
            default:
                rezultat = brRedova / 2;
                break;
        }

        return Math.max(1, rezultat);
    }

    private static Rezultat napraviOsnovniRezultat(Rezultat ulaz, long brRedova) {
        Rezultat izlaz = new Rezultat();

        izlaz.setBrRedova(brRedova);
        izlaz.setBrRedovaPoBloku(ulaz.getBrRedovaPoBloku());
        izlaz.getTabele().addAll(ulaz.getTabele());

        return izlaz;
    }

    private static void azurirajVrednostiPosleSelekcije(Rezultat ulaz, Rezultat izlaz, AtributRef atribut, Operator operator, long stariBrRedova, long noviBrRedova) {
        String kljucSelekcije = Rezultat.kljuc(atribut);
        double selektivnost = (double) noviBrRedova / stariBrRedova;

        for (Map.Entry<String, Long> entry : ulaz.getVMape().entrySet()) {
            String kljuc = entry.getKey();
            long staraVrednost = entry.getValue();

            long novaVrednost;

            if (kljuc.equals(kljucSelekcije)) {
                novaVrednost = proceniVrednostSelekcionogAtributa(staraVrednost, operator, selektivnost);
            } else {
                novaVrednost = Math.min(staraVrednost, noviBrRedova);
            }

            izlaz.getVMape().put(kljuc, novaVrednost);
        }
    }

    private static long proceniVrednostSelekcionogAtributa(long staraVrednost, Operator operator, double selektivnost) {
        switch (operator) {
            case JEDNAKO:
                return 1;
            case RAZLICITO:
                return Math.max(1, staraVrednost - 1);
            default:
                return Math.max(1, Math.round(staraVrednost * selektivnost));
        }
    }

    private static long proceniBrojRedovaSpajanja(Rezultat levi, Rezultat desni, AtributRef refLevog, AtributRef refDesnog) {
        long nL = levi.getBrRedova();
        long nD = desni.getBrRedova();

        long vL = levi.getV(refLevog);
        long vD = desni.getV(refDesnog);

        return Math.max(1, nL * nD / Math.max(1, Math.max(vL, vD)));
    }

    private static int proceniBrRedovaPoBlokuSpajanja(Rezultat levi, Rezultat desni) {
        double fL = levi.getBrRedovaPoBloku();
        double fD = desni.getBrRedovaPoBloku();

        return (int) Math.max(1, Math.floor(1.0 / (1.0 / fL + 1.0 / fD)));
    }

    private static void kopirajVMape(Rezultat izvor, Rezultat odrediste, long noviBrRedova) {
        for (Map.Entry<String, Long> entry : izvor.getVMape().entrySet()) {
            odrediste.getVMape().put(entry.getKey(), Math.min(entry.getValue(), noviBrRedova));
        }
    }

    private static int dodajAtributeProjekcije(Rezultat ulaz, Rezultat izlaz, List<AtributRef> atributi) {
        int brojDodatih = 0;

        for (AtributRef ref : atributi) {
            String kljuc = Rezultat.kljuc(ref);

            if (ulaz.getVMape().containsKey(kljuc) && !izlaz.getVMape().containsKey(kljuc)) {
                izlaz.getVMape().put(kljuc, ulaz.getVMape().get(kljuc));
                brojDodatih++;
            }
        }

        return brojDodatih;
    }
}