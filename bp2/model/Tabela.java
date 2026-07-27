package bp2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Tabela {

    @SerializedName("name")
    private String naziv;
    @SerializedName("rowCount")
    private long brRedova;
    @SerializedName("blockCount")
    private long brBlokova;
    @SerializedName("rowsPerBlock")
    private int brRedovaPoBloku;

    @SerializedName("attributes")
    private List<Atribut> atributi = new ArrayList<>();
    @SerializedName("indexes")
    private List<Index> indexi = new ArrayList<>();

    public String getNaziv() {
        return this.naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public long getBrRedova() {
        return this.brRedova;
    }

    public void setBrRedova(long brRedova) {
        this.brRedova = brRedova;
    }

    public long getBrBlokova() {
        return this.brBlokova;
    }

    public void setBrBlokova(long brBlokova) {
        this.brBlokova = brBlokova;
    }

    public int getBrRedovaPoBloku() {
        return this.brRedovaPoBloku;
    }

    public void setBrRedovaPoBloku(int brRedovaPoBloku) {
        this.brRedovaPoBloku = brRedovaPoBloku;
    }

    public List<Atribut> getAtributi() {
        return this.atributi;
    }

    public void setAtributi(List<Atribut> atributi) {
        this.atributi = atributi;
    }

    public List<Index> getIndexi() {
        return this.indexi;
    }

    public void setIndexi(List<Index> indexi) {
        this.indexi = indexi;
    }

    public Atribut getAtribut(String atr) {
        for (Atribut a : this.atributi) {
            if (a.getNaziv().equalsIgnoreCase(atr))
                return a;
        }
        return null;
    }

    public boolean imaAtribut(String atr) {
        return getAtribut(atr) != null;
    }

    public List<Index> getImajuIndex(String atr) {
        List<Index> res = new ArrayList<>();
        for (Index i : this.indexi) {
            if (i.isLeadingAttribute(atr)) res.add(i);
        }
        return res;
    }

}
