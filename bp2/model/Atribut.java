package bp2.model;

import com.google.gson.annotations.SerializedName;

public class Atribut {

    @SerializedName("name")
    private String naziv;
    @SerializedName("type")
    private TipPodatka tip;
    @SerializedName("unique")
    private boolean jedinstven;
    @SerializedName("distinctValues")
    private long razliciteVrednosti;

    public String getNaziv() {
        return this.naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public TipPodatka getTip() {
        return this.tip;
    }

    public void setTip(TipPodatka tip) {
        this.tip = tip;
    }

    public boolean isJedinstven() {
        return this.jedinstven;
    }

    public void setJedinstven(boolean jedinstven) {
        this.jedinstven = jedinstven;
    }

    public long getRazliciteVrednosti() {
        return this.razliciteVrednosti;
    }

    public void setRazliciteVrednosti(long razliciteVrednosti) {
        this.razliciteVrednosti = razliciteVrednosti;
    }

    @Override
    public String toString() {
        return naziv + " : " + tip + (jedinstven ? " [unique]" : "") + " (V=" + razliciteVrednosti + ")";
    }

}
