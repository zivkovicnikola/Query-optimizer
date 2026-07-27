package bp2.procena;

import bp2.model.Atribut;
import bp2.model.Tabela;
import bp2.upit.AtributRef;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Rezultat {

    private long brRedova;
    private int brRedovaPoBloku;

    private Map<String, Long> vMape = new HashMap<>();

    private Set<String> tabele = new HashSet<>();

    public static Rezultat izTabele(Tabela t) {
        Rezultat r = new Rezultat();
        r.brRedova = t.getBrRedova();
        r.brRedovaPoBloku = t.getBrRedovaPoBloku();
        r.tabele.add(t.getNaziv().toLowerCase());
        for (Atribut a : t.getAtributi())
            r.vMape.put(kljuc(t.getNaziv(), a.getNaziv()), a.getRazliciteVrednosti());
        return r;
    }

    public static String kljuc(String tabela, String atribut) {
        return tabela.toLowerCase() + "." + atribut.toLowerCase();
    }

    public static String kljuc(AtributRef ref) {
        return kljuc(ref.getRazresenaTabela().getNaziv(), ref.getRazresenAtribut().getNaziv());
    }

    public long getV(AtributRef ref) {
        Long v = vMape.get(kljuc(ref));
        if (v == null)
            throw new IllegalStateException("Rezultat ne sadrzi atribut " + ref);
        return v;
    }

    public long getBrRedova() {
        return brRedova;
    }

    public void setBrRedova(long brRedova) {
        this.brRedova = brRedova;
    }

    public int getBrRedovaPoBloku() {
        return brRedovaPoBloku;
    }

    public void setBrRedovaPoBloku(int f) {
        this.brRedovaPoBloku = f;
    }

    public long getBrBlokova() {
        return (brRedova + brRedovaPoBloku - 1) / brRedovaPoBloku;
    }

    public int getBrAtributa() {
        return vMape.size();
    }

    public Map<String, Long> getVMape() {
        return vMape;
    }

    public Set<String> getTabele() {
        return tabele;
    }

    public boolean sadrziTabelu(String naziv) {
        return tabele.contains(naziv.toLowerCase());
    }

    @Override
    public String toString() {
        return "n=" + brRedova + ", f=" + brRedovaPoBloku + ", b=" + getBrBlokova() + ", tabele=" + tabele;
    }
}