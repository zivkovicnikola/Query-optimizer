package bp2.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Index {

    @SerializedName("name")
    private String naziv;
    @SerializedName("attributes")
    private List<String> atributi;
    @SerializedName("type")
    private TipIndexa tip;
    @SerializedName("clustered")
    private boolean klasterovan;
    @SerializedName("treeHeight")
    private Integer visina;

    public String getNaziv() {
        return this.naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public List<String> getAtributi() {
        return this.atributi;
    }

    public void setAtributi(List<String> atributi) {
        this.atributi = atributi;
    }

    public TipIndexa getTip() {
        return this.tip;
    }

    public void setTip(TipIndexa tip) {
        this.tip = tip;
    }

    public boolean isKlasterovan() {
        return this.klasterovan;
    }

    public void setKlasterovan(boolean klasterovan) {
        this.klasterovan = klasterovan;
    }

    public Integer getVisina() {
        return this.visina;
    }

    public void setVisina(Integer visina) {
        this.visina = visina;
    }

    public boolean isLeadingAttribute(String attrName) {
        return !this.atributi.isEmpty() && this.atributi.get(0).equalsIgnoreCase(attrName);
    }

    @Override
    public String toString() {
        return naziv + " " + tip + "(" + String.join(", ", atributi) + ")"
                + (klasterovan ? " clustered" : "")
                + (tip == TipIndexa.B_PLUS ? " h=" + getVisina() : "");
    }

}
