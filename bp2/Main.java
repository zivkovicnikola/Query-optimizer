package bp2;

import bp2.model.SisUlaz;
import bp2.plan.Ispis;
import bp2.plan.Optimizator;
import bp2.plan.CvorPlana;
import bp2.ulaz.Ucitavanje;
import bp2.upit.Parser;
import bp2.upit.Razresivac;
import bp2.upit.Upit;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        String putanja = args.length > 0 ? args[0] : "input.json";

        SisUlaz ulaz;
        try {
            ulaz = Ucitavanje.ucitaj(putanja);
        } catch (Exception e) {
            System.out.println("Greska pri ucitavanju ulaza '" + putanja + "': " + e.getMessage());
            return;
        }

        System.out.println("Ucitana sema iz '" + putanja + "' (bafer: " + ulaz.getBuferBlokovi() + " blokova, tabela: " + ulaz.getSema().getTabele().size() + ")");
        System.out.println("Unesite SQL upit (ili 'exit' za kraj):");

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("\nupit: ");
            if (!scanner.hasNextLine()) break;
            String linija = scanner.nextLine().trim();

            if (linija.isEmpty()) continue;
            if (linija.equalsIgnoreCase("exit") || linija.equalsIgnoreCase("quit")) break;

            try {
                Upit upit = Parser.parsiraj(linija);
                Razresivac.razresi(upit, ulaz.getSema());
                CvorPlana plan = Optimizator.optimizuj(upit, ulaz.getSema(), ulaz.getBuferBlokovi());
                System.out.println();
                Ispis.ispisi(plan);
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                System.out.println("GRESKA: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Neocekivana greska: " + e);
            }
        }
        System.out.println("Kraj.");
    }
}