package bp2.upit;

import java.util.ArrayList;
import java.util.List;

public class Upit {

    private boolean selectZvezda;
    private List<AtributRef> selectLista = new ArrayList<>();
    private List<String> tabele = new ArrayList<>();
    private List<Uslov> uslovi = new ArrayList<>();
    private AtributRef orderBy;
    private boolean orderOpadajuce;

    public boolean isSelectSve() {
        return selectZvezda;
    }

    public void setSelectSve(boolean s) {
        selectZvezda = s;
    }

    public List<AtributRef> getSelectLista() {
        return selectLista;
    }

    public void setSelectLista(List<AtributRef> selectLista) {
        this.selectLista = selectLista;
    }

    public List<String> getTabele() {
        return tabele;
    }

    public void setTabele(List<String> tabele) {
        this.tabele = tabele;
    }

    public List<Uslov> getUslovi() {
        return uslovi;
    }

    public void setUslovi(List<Uslov> uslov) {
        this.uslovi = uslov;
    }

    public AtributRef getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(AtributRef o) {
        orderBy = o;
    }

    public boolean isOrderOpadajuce() {
        return orderOpadajuce;
    }

    public void setOrderOpadajuce(boolean o) {
        orderOpadajuce = o;
    }

}