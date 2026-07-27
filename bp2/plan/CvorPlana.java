package bp2.plan;

import bp2.procena.Rezultat;

import java.util.ArrayList;
import java.util.List;

public class CvorPlana {

    private final VrstaOperacije vrsta;
    private String opis;
    private final double cenaOperacije;
    private double cenaUpisa;
    private final Rezultat izlaz;
    private final List<CvorPlana> deca = new ArrayList<>();

    public CvorPlana(VrstaOperacije vrsta, String opis, double cenaOperacije, Rezultat izlaz) {
        this.vrsta = vrsta;
        this.opis = opis;
        this.cenaOperacije = cenaOperacije;
        this.izlaz = izlaz;
    }

    public void dodajDete(CvorPlana dete) {
        deca.add(dete);
        if (dete.vrsta == VrstaOperacije.PRISTUP || this.vrsta == VrstaOperacije.PROJEKCIJA)
            dete.cenaUpisa = 0;
        else
            dete.cenaUpisa = dete.izlaz.getBrBlokova();
    }

    public double ukupnaCena() {
        double suma = cenaOperacije + cenaUpisa;
        for (CvorPlana d : deca) suma += d.ukupnaCena();
        return suma;
    }

    public VrstaOperacije getVrsta() {
        return vrsta;
    }

    public String getOpis() {
        return opis;
    }

    public double getCenaOperacije() {
        return cenaOperacije;
    }

    public double getCenaUpisa() {
        return cenaUpisa;
    }

    public Rezultat getIzlaz() {
        return izlaz;
    }

    public List<CvorPlana> getDeca() {
        return deca;
    }

    public void dopuniOpis(String dodatak) {
        this.opis += dodatak;
    }
    
}