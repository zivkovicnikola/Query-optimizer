package bp2.upit;

import bp2.model.Atribut;
import bp2.model.Tabela;

public class AtributRef {

    private String tabela;
    private String atribut;

    private Tabela razresenaTabela;
    private Atribut razresenAtribut;

    public AtributRef(String tabela, String atribut) {
        this.tabela = tabela;
        this.atribut = atribut;
    }

    public String getTabela() {
        return tabela;
    }
    public void setTabela(String tabela) {
        this.tabela = tabela;
    }

    public String getAtribut() {
        return atribut;
    }

    public void setAtribut(String atribut) {
        this.atribut = atribut;
    }

    public Tabela getRazresenaTabela() {
        return razresenaTabela;
    }

    public void setRazresenaTabela(Tabela razresenaTabela) {
        this.razresenaTabela = razresenaTabela;
    }

    public Atribut getRazresenAtribut() {
        return razresenAtribut;
    }

    public void setRazresenAtribut(Atribut razresenAtribut) {
        this.razresenAtribut = razresenAtribut;
    }

    public void razresi(Tabela t, Atribut a) {
        this.razresenaTabela = t;
        this.razresenAtribut = a;
    }

    @Override
    public String toString() {
        if (razresenaTabela != null)
            return razresenaTabela.getNaziv() + "." + razresenAtribut.getNaziv();
        return tabela != null ? tabela + "." + atribut : atribut;
    }

}