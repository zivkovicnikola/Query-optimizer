package bp2.cena;

public class CenaSortiranja {

    public static double cena(long b, int M) {
        if (b <= 0) return 0;
        if (b <= M) return b;

        int brojProlaza = brojProlazaObjedinjavanja(b, M);
        return b * (2.0 * brojProlaza + 1);
    }

    public static int brojProlazaObjedinjavanja(long b, int M) {
        long delova = (b + M - 1) / M;
        int fanIn = M - 1;
        int prolaza = 0;
        while (delova > 1) {
            delova = (delova + fanIn - 1) / fanIn;
            prolaza++;
        }
        return prolaza;
    }
}