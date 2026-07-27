package bp2.ulaz;

import bp2.model.Index;
import bp2.model.SisUlaz;
import bp2.model.Tabela;
import bp2.model.TipIndexa;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Ucitavanje {

    public static SisUlaz ucitaj(String putanja) throws IOException {
        String json = new String(Files.readAllBytes(Paths.get(putanja)));
        SisUlaz ulaz;
        try {
            ulaz = new Gson().fromJson(json, SisUlaz.class);
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Neispravan JSON: " + e.getMessage());
        }
        proveri(ulaz);
        return ulaz;
    }

    private static void proveri(SisUlaz ulaz) {
        if (ulaz == null || ulaz.getSema() == null || ulaz.getSema().getTabele() == null || ulaz.getSema().getTabele().isEmpty())
            throw new IllegalArgumentException("Ulaz mora sadrzati bar jednu tabelu.");
        if (ulaz.getBuferBlokovi() < 3)
            throw new IllegalArgumentException("Bafer mora da ima bar 3 bloka.");

        for (Tabela t : ulaz.getSema().getTabele()) {
            if (t.getNaziv() == null || t.getNaziv().isEmpty())
                throw new IllegalArgumentException("Tabela nema naziv.");
            if (t.getAtributi() == null || t.getAtributi().isEmpty())
                throw new IllegalArgumentException("Tabela " + t.getNaziv() + " nema atribute.");

            for (Index idx : t.getIndexi()) {
                for (String atr : idx.getAtributi())
                    if (!t.imaAtribut(atr))
                        throw new IllegalArgumentException("Indeks " + idx.getNaziv() + " referencira nepostojeci atribut '" + atr + "'.");
                if (idx.getTip() == TipIndexa.B_PLUS && (idx.getVisina() == null || idx.getVisina() <= 0))
                    throw new IllegalArgumentException("B+ indeks " + idx.getNaziv() + " mora imati visinu > 0.");
            }
        }
    }

}
