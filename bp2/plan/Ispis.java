package bp2.plan;

import java.util.ArrayList;
import java.util.List;

public class Ispis {

    public static void ispisi(CvorPlana koren) {
        System.out.println("PLAN IZVRSAVANJA");
        System.out.println("----------------");
        ispisiStablo(koren, "", true, true);

        System.out.println();
        System.out.println("KORACI (redosled izvrsavanja):");
        List<CvorPlana> koraci = new ArrayList<>();
        poredjajKorake(koren, koraci);
        int br = 1;
        double ukupno = 0;
        for (CvorPlana c : koraci) {
            if (c.getVrsta() == VrstaOperacije.PRISTUP) continue;
            System.out.printf("%d. %-11s %s%n", br++, c.getVrsta() + ":", c.getOpis());
            System.out.printf("      izlaz: n=%d, b=%d | cena operacije: %s%s%n",
                    c.getIzlaz().getBrRedova(), c.getIzlaz().getBrBlokova(),
                    fmt(c.getCenaOperacije()),
                    c.getCenaUpisa() > 0 ? " | upis medjurezultata: " + fmt(c.getCenaUpisa()) : "");
            ukupno += c.getCenaOperacije() + c.getCenaUpisa();
        }
        System.out.println();
        System.out.println("UKUPNA CENA: " + fmt(ukupno) + " blok transfera");
    }

    private static void ispisiStablo(CvorPlana cvor, String prefiks, boolean koren, boolean poslednje) {
        System.out.println(prefiks
                + (koren ? "" : (poslednje ? "\\-- " : "|-- "))
                + cvor.getVrsta() + ": " + cvor.getOpis()
                + "  [cena=" + fmt(cvor.getCenaOperacije())
                + (cvor.getCenaUpisa() > 0 ? " +upis=" + fmt(cvor.getCenaUpisa()) : "")
                + "]");
        List<CvorPlana> deca = cvor.getDeca();
        for (int i = 0; i < deca.size(); i++)
            ispisiStablo(deca.get(i), prefiks + (koren ? "" : (poslednje ? "    " : "|   ")), false, i == deca.size() - 1);
    }

    private static void poredjajKorake(CvorPlana cvor, List<CvorPlana> lista) {
        for (CvorPlana d : cvor.getDeca())
            poredjajKorake(d, lista);
        lista.add(cvor);
    }

    private static String fmt(double x) {
        return (x == Math.floor(x)) ? String.valueOf((long) x) : String.format("%.1f", x);
    }
}