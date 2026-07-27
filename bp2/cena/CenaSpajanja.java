package bp2.cena;

import bp2.model.Index;
import bp2.model.Tabela;
import bp2.model.TipIndexa;
import bp2.procena.Rezultat;
import bp2.upit.AtributRef;
import bp2.upit.Operator;
import bp2.upit.Uslov;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CenaSpajanja {

    private static final double HASH_KONST = 1.2;

    public static List<Pristup> kandidati(Rezultat spoljna, Rezultat unutrasnja, Uslov uslov, int M, Tabela unutrasnjaOsnovna, boolean spoljnaSortirana, boolean unutrasnjaSortirana) {
        List<Pristup> lista = new ArrayList<>();

        long nR = spoljna.getBrRedova(), bR = spoljna.getBrBlokova();
        long bS = unutrasnja.getBrBlokova();

        AtributRef atrUnutrasnje = atributStrane(unutrasnja, uslov);
        boolean ekvi = uslov.getOperator() == Operator.JEDNAKO;

        // ugnjezdena petlja
        lista.add(new Pristup("ugnjezdena petlja", (double) nR * bS + bR));

        // blok ugnjezdena petlja
        long grupaSpoljne = (bR + (M - 2) - 1) / (M - 2); // ceil(bR / (M-2))
        lista.add(new Pristup("blok ugnjezdena petlja", (double) grupaSpoljne * bS + bR));

        if (ekvi) {
            // indeksirana ugnjezdena petlja
            if (unutrasnjaOsnovna != null) {
                long mPoPretrazi = Math.max(1,
                        unutrasnja.getBrRedova() / Math.max(1, unutrasnja.getV(atrUnutrasnje)));
                for (Index idx : unutrasnjaOsnovna.getImajuIndex(atrUnutrasnje.getRazresenAtribut().getNaziv())) {
                    Double c = cenaJednePretrage(idx, mPoPretrazi, unutrasnjaOsnovna.getBrRedovaPoBloku());
                    if (c != null)
                        lista.add(new Pristup("indeksirana ugnjezdena petlja preko " + idx.getNaziv(), bR + nR * c));
                }
            }

            // merge join
            double cenaSortiranja = 0;
            String opisSort = "";
            if (!spoljnaSortirana) { cenaSortiranja += CenaSortiranja.cena(bR, M); opisSort += " +sort(spoljna)"; }
            if (!unutrasnjaSortirana) { cenaSortiranja += CenaSortiranja.cena(bS, M); opisSort += " +sort(unutrasnja)"; }
            lista.add(new Pristup("merge join" + opisSort, cenaSortiranja + bR + bS));

            // hes spajanje
            long bBuild = Math.min(bR, bS);
            double cenaHes;
            String opisHes;
            if (bBuild <= M) {
                cenaHes = bR + bS;
                opisHes = "hes spajanje (build staje u memoriju)";
            } else if (bBuild <= (long) (M - 1) * (M - 2)) {
                cenaHes = 3.0 * (bR + bS);
                opisHes = "hes spajanje (jedno particionisanje)";
            } else {
                int prolaza = CenaSortiranja.brojProlazaObjedinjavanja(bBuild, M);
                cenaHes = 2.0 * (bR + bS) * prolaza + bR + bS;
                opisHes = "hes spajanje (rekurzivno particionisanje, " + prolaza + " prolaza)";
            }
            lista.add(new Pristup(opisHes, cenaHes));
        }

        return lista;
    }

    public static Pristup najbolji(Rezultat spoljna, Rezultat unutrasnja, Uslov uslov, int M, Tabela unutrasnjaOsnovna, boolean spoljnaSortirana, boolean unutrasnjaSortirana) {
        return kandidati(spoljna, unutrasnja, uslov, M, unutrasnjaOsnovna,
                spoljnaSortirana, unutrasnjaSortirana).stream()
                .min(Comparator.comparingDouble(Pristup::getCena))
                .orElseThrow();
    }

    public static AtributRef atributStrane(Rezultat rezultat, Uslov uslov) {
        if (rezultat.sadrziTabelu(uslov.getLeva().getRazresenaTabela().getNaziv()))
            return uslov.getLeva();
        return uslov.getDesnaAtribut();
    }

    private static Double cenaJednePretrage(Index idx, long m, int f) {
        if (idx.getTip() == TipIndexa.HES) {
            if (idx.getAtributi().size() > 1) return null;
            return HASH_KONST + m;
        }
        if (idx.isKlasterovan())
            return (double) idx.getVisina() + (m + f - 1) / f;
        return (double) idx.getVisina() + m;
    }
}