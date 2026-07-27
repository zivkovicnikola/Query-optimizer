package bp2.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Sema {

    @SerializedName("tables")
    private List<Tabela> tabele = new ArrayList<>();

    public List<Tabela> getTabele() {
        return this.tabele;
    }

    public void setTabele(List<Tabela> tabele) {
        this.tabele = tabele;
    }

    public Tabela getTabela(String tabela) {
        for (Tabela t : this.tabele) {
            if (t.getNaziv().equalsIgnoreCase(tabela)) return t;
        }
        return null;
    }

}
