package bp2.model;

import com.google.gson.annotations.SerializedName;

public class SisUlaz {

    @SerializedName("bufferBlocks")
    private int buferBlokovi;
    @SerializedName("schema")
    private Sema sema;

    public int getBuferBlokovi() {
        return buferBlokovi;
    }

    public void setBuferBlokovi(int buferBlokovi) {
        this.buferBlokovi = buferBlokovi;
    }

    public Sema getSema() {
        return sema;
    }

    public void setSema(Sema sema) {
        this.sema = sema;
    }

}
