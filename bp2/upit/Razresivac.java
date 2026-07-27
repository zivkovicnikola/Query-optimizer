package bp2.upit;

import bp2.model.Atribut;
import bp2.model.Sema;
import bp2.model.Tabela;

import java.util.ArrayList;
import java.util.List;

public class Razresivac {

    public static void razresi(Upit upit, Sema sema) {
        List<Tabela> tabeleUpita = new ArrayList<>();

        for (String nazivTabele : upit.getTabele()) {
            Tabela tabela = sema.getTabela(nazivTabele);

            if (tabela == null) {
                throw new IllegalArgumentException("Tabela '" + nazivTabele + "' ne postoji u semi.");
            }

            tabeleUpita.add(tabela);
        }

        if (upit.isSelectSve()) {
            dodajSveAtribute(upit, tabeleUpita);
        }

        for (AtributRef ref : upit.getSelectLista()) {
            razresiRef(ref, tabeleUpita);
        }

        for (Uslov uslov : upit.getUslovi()) {
            razresiRef(uslov.getLeva(), tabeleUpita);

            if (uslov.jeSpajanje()) {
                razresiRef(uslov.getDesnaAtribut(), tabeleUpita);
            }
        }

        if (upit.getOrderBy() != null) {
            razresiRef(upit.getOrderBy(), tabeleUpita);
        }
    }

    private static void dodajSveAtribute(Upit upit, List<Tabela> tabeleUpita) {
        for (Tabela tabela : tabeleUpita) {
            for (Atribut atribut : tabela.getAtributi()) {
                AtributRef ref = new AtributRef(tabela.getNaziv(), atribut.getNaziv());
                ref.razresi(tabela, atribut);
                upit.getSelectLista().add(ref);
            }
        }
    }

    private static void razresiRef(AtributRef ref, List<Tabela> tabeleUpita) {
        if (ref.getRazresenaTabela() != null) {
            return;
        }

        if (ref.getTabela() != null) {
            razresiSaNazivomTabele(ref, tabeleUpita);
            return;
        }

        razresiBezNazivaTabele(ref, tabeleUpita);
    }

    private static void razresiSaNazivomTabele(AtributRef ref, List<Tabela> tabeleUpita) {
        Tabela tabela = nadjiTabelu(ref.getTabela(), tabeleUpita);

        if (tabela == null) {
            throw new IllegalArgumentException("Tabela '" + ref.getTabela() + "' iz reference '" + ref + "' nije navedena u FROM.");
        }

        Atribut atribut = tabela.getAtribut(ref.getAtribut());

        if (atribut == null) {
            throw new IllegalArgumentException("Atribut '" + ref.getAtribut() + "' ne postoji u tabeli '" + tabela.getNaziv() + "'.");
        }

        ref.razresi(tabela, atribut);
    }

    private static void razresiBezNazivaTabele(AtributRef ref, List<Tabela> tabeleUpita) {
        Tabela nadjenaTabela = null;
        Atribut nadjenAtribut = null;

        for (Tabela tabela : tabeleUpita) {
            Atribut atribut = tabela.getAtribut(ref.getAtribut());

            if (atribut == null) {
                continue;
            }

            if (nadjenaTabela != null) {
                throw new IllegalArgumentException("Atribut '" + ref.getAtribut() + "' je dvosmislen (postoji u tabelama '" + nadjenaTabela.getNaziv() + "' i '" + tabela.getNaziv() + "').");
            }

            nadjenaTabela = tabela;
            nadjenAtribut = atribut;
        }

        if (nadjenaTabela == null) {
            throw new IllegalArgumentException("Atribut '" + ref.getAtribut() + "' ne postoji ni u jednoj tabeli iz FROM.");
        }

        ref.razresi(nadjenaTabela, nadjenAtribut);
    }

    private static Tabela nadjiTabelu(String naziv, List<Tabela> tabele) {
        for (Tabela tabela : tabele) {
            if (tabela.getNaziv().equalsIgnoreCase(naziv)) {
                return tabela;
            }
        }

        return null;
    }
}