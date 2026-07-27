package bp2.plan;

import bp2.cena.*;
import bp2.model.*;
import bp2.procena.*;
import bp2.upit.*;

import java.util.*;

public class Optimizator {

    public static CvorPlana optimizuj(Upit upit, Sema sema, int M) {
        Map<String, List<Uslov>> selekcije = new HashMap<>();
        List<Uslov> spajanja = new ArrayList<>();
        for (Uslov u : upit.getUslovi()) {
            if (u.jeSpajanje()) spajanja.add(u);
            else selekcije.computeIfAbsent(
                    u.getLeva().getRazresenaTabela().getNaziv().toLowerCase(),
                    k -> new ArrayList<>()).add(u);
        }

        CvorPlana telo;
        if (upit.getTabele().size() == 1) {
            telo = ulazniCvor(sema.getTabela(upit.getTabele().get(0)), selekcije);
        } else {
            telo = planViseTabela(upit, sema, M, selekcije, spajanja);
        }

        if (upit.getOrderBy() != null) {
            Tabela osnovnaTela = null;
            if (telo.getVrsta() == VrstaOperacije.PRISTUP)
                osnovnaTela = sema.getTabela(telo.getIzlaz().getTabele().iterator().next());

            if (osnovnaTela != null && sortiranaPo(osnovnaTela, upit.getOrderBy())) {
                telo.dopuniOpis(" [kroz klasterovan B+ -> vec sortirano po " + upit.getOrderBy() + ", sortiranje preskoceno]");
            } else {
                double cs = CenaSortiranja.cena(telo.getIzlaz().getBrBlokova(), M);
                CvorPlana sort = new CvorPlana(VrstaOperacije.SORTIRANJE, "spoljno sortiranje po " + upit.getOrderBy() + (upit.isOrderOpadajuce() ? " DESC" : " ASC"), cs, telo.getIzlaz());
                sort.dodajDete(telo);
                telo = sort;
            }
        }

        Rezultat rezProj = Procene.projekcija(telo.getIzlaz(), upit.getSelectLista());
        double cenaProj = (telo.getVrsta() == VrstaOperacije.PRISTUP) ? telo.getIzlaz().getBrBlokova() : 0;
        CvorPlana proj = new CvorPlana(VrstaOperacije.PROJEKCIJA, "projekcija na " + upit.getSelectLista(), cenaProj, rezProj);
        proj.dodajDete(telo);
        return proj;
    }

    private static CvorPlana ulazniCvor(Tabela t, Map<String, List<Uslov>> selekcije) {
        List<Uslov> uslovi = selekcije.getOrDefault(t.getNaziv().toLowerCase(), List.of());
        Rezultat rez = Rezultat.izTabele(t);
        if (uslovi.isEmpty())
            return new CvorPlana(VrstaOperacije.PRISTUP, "tabela " + t.getNaziv(), 0, rez);

        Pristup p = CenaSelekcije.najboljiZaKonjunkciju(t, uslovi);
        for (Uslov u : uslovi) rez = Procene.selekcija(rez, u);
        return new CvorPlana(VrstaOperacije.SELEKCIJA, t.getNaziv() + ": " + p.getOpis(), p.getCena(), rez);
    }

    private static boolean sortiranaPo(Tabela t, AtributRef atr) {
        for (Index idx : t.getIndexi())
            if (idx.getTip() == TipIndexa.B_PLUS && idx.isKlasterovan() && idx.isLeadingAttribute(atr.getRazresenAtribut().getNaziv()))
                return true;
        return false;
    }

    private static CvorPlana planViseTabela(Upit upit, Sema sema, int M, Map<String, List<Uslov>> selekcije, List<Uslov> spajanja) {
        List<Tabela> tabele = new ArrayList<>();
        for (String naziv : upit.getTabele())
            tabele.add(sema.getTabela(naziv));

        CvorPlana najbolji = null;
        for (List<Tabela> redosled : permutacije(tabele)) {
            CvorPlana plan = planZaRedosled(redosled, M, selekcije, spajanja);
            if (plan != null && (najbolji == null || plan.ukupnaCena() < najbolji.ukupnaCena()))
                najbolji = plan;
        }
        if (najbolji == null)
            throw new UnsupportedOperationException("Tabele nisu povezane uslovima spajanja (Dekartov proizvod nije podrzan).");
        return najbolji;
    }

    private static CvorPlana planZaRedosled(List<Tabela> redosled, int M, Map<String, List<Uslov>> selekcije, List<Uslov> spajanja) {

        CvorPlana acc = ulazniCvor(redosled.get(0), selekcije);
        Tabela accOsnovna = (acc.getVrsta() == VrstaOperacije.PRISTUP) ? redosled.get(0) : null;

        for (int i = 1; i < redosled.size(); i++) {
            Tabela t = redosled.get(i);
            List<Uslov> povezujuci = nadjiPovezujuce(spajanja, acc.getIzlaz(), t);
            if (povezujuci.isEmpty()) return null;
            Uslov spoj = povezujuci.get(0);

            CvorPlana cvorT = ulazniCvor(t, selekcije);
            Tabela tOsnovna = (cvorT.getVrsta() == VrstaOperacije.PRISTUP) ? t : null;

            boolean accSort = accOsnovna != null && sortiranaPo(accOsnovna, CenaSpajanja.atributStrane(acc.getIzlaz(), spoj));
            boolean tSort = tOsnovna != null && sortiranaPo(t, CenaSpajanja.atributStrane(cvorT.getIzlaz(), spoj));

            Pristup p1 = CenaSpajanja.najbolji(acc.getIzlaz(), cvorT.getIzlaz(), spoj, M, tOsnovna, accSort, tSort);
            Pristup p2 = CenaSpajanja.najbolji(cvorT.getIzlaz(), acc.getIzlaz(), spoj, M, accOsnovna, tSort, accSort);

            Rezultat rez = Procene.spajanje(acc.getIzlaz(), cvorT.getIzlaz(), spoj);

            String dodatni = povezujuci.size() > 1 ? " (+" + (povezujuci.size() - 1) + " uslova spajanja provereno u memoriji)" : "";

            CvorPlana spajanjeCvor;
            if (p1.getCena() <= p2.getCena()) {
                spajanjeCvor = new CvorPlana(VrstaOperacije.SPAJANJE, "(dosadasnje) x " + t.getNaziv() + ": " + p1.getOpis() + dodatni, p1.getCena(), rez);
                spajanjeCvor.dodajDete(acc);
                spajanjeCvor.dodajDete(cvorT);
            } else {
                spajanjeCvor = new CvorPlana(VrstaOperacije.SPAJANJE, t.getNaziv() + " x (dosadasnje): " + p2.getOpis() + dodatni, p2.getCena(), rez);
                spajanjeCvor.dodajDete(cvorT);
                spajanjeCvor.dodajDete(acc);
            }
            acc = spajanjeCvor;
            accOsnovna = null;
        }
        return acc;
    }

    private static List<Uslov> nadjiPovezujuce(List<Uslov> spajanja, Rezultat acc, Tabela t) {
        List<Uslov> rezultat = new ArrayList<>();
        for (Uslov u : spajanja) {
            String t1 = u.getLeva().getRazresenaTabela().getNaziv();
            String t2 = u.getDesnaAtribut().getRazresenaTabela().getNaziv();
            boolean povezuje = (acc.sadrziTabelu(t1) && t2.equalsIgnoreCase(t.getNaziv())) || (acc.sadrziTabelu(t2) && t1.equalsIgnoreCase(t.getNaziv()));
            if (povezuje) rezultat.add(u);
        }
        return rezultat;
    }

    private static List<List<Tabela>> permutacije(List<Tabela> tabele) {
        List<List<Tabela>> sve = new ArrayList<>();
        permutuj(new ArrayList<>(tabele), 0, sve);
        return sve;
    }

    private static void permutuj(List<Tabela> lista, int od, List<List<Tabela>> sve) {
        if (od == lista.size() - 1) {
            sve.add(new ArrayList<>(lista));
            return;
        }
        for (int i = od; i < lista.size(); i++) {
            Collections.swap(lista, od, i);
            permutuj(lista, od + 1, sve);
            Collections.swap(lista, od, i);
        }
    }

}