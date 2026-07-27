package bp2.upit;

import java.util.ArrayList;
import java.util.List;

public class Lekser {

    private static final List<String> KLJUCNE_RECI = List.of("SELECT", "FROM", "WHERE", "AND", "ORDER", "BY", "ASC", "DESC");

    public static List<Token> tokenizuj(String upit) {
        List<Token> tokeni = new ArrayList<>();
        int i = 0, n = upit.length();

        while (i < n) {
            char c = upit.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int start = i;

                while (i < n && (Character.isLetterOrDigit(upit.charAt(i)) || upit.charAt(i) == '_')) {
                    i++;
                }

                String rec = upit.substring(start, i);

                if (KLJUCNE_RECI.contains(rec.toUpperCase())) {
                    tokeni.add(new Token(TipTokena.KLJUCNA_REC, rec.toUpperCase(), start));
                } else {
                    tokeni.add(new Token(TipTokena.IDENTIFIKATOR, rec, start));
                }

                continue;
            }

            if (Character.isDigit(c)) {
                int start = i;

                while (i < n && (Character.isDigit(upit.charAt(i)) ||
                        upit.charAt(i) == '.' && i + 1 < n && Character.isDigit(upit.charAt(i + 1)))) {
                    i++;
                }

                tokeni.add(new Token(TipTokena.BROJ, upit.substring(start, i), start));
                continue;
            }

            if (c == '\'') {
                int start = ++i;

                while (i < n && upit.charAt(i) != '\'') {
                    i++;
                }

                if (i >= n) {
                    throw new IllegalArgumentException("Sintaksna greska: nezatvoren string na poziciji " + (start - 1));
                }

                tokeni.add(new Token(TipTokena.STRING, upit.substring(start, i), start));
                i++;
                continue;
            }

            if (c == '<') {
                if (i + 1 < n && upit.charAt(i + 1) == '=') {
                    tokeni.add(new Token(TipTokena.OPERATOR, "<=", i));
                    i += 2;
                } else if (i + 1 < n && upit.charAt(i + 1) == '>') {
                    tokeni.add(new Token(TipTokena.OPERATOR, "<>", i));
                    i += 2;
                } else {
                    tokeni.add(new Token(TipTokena.OPERATOR, "<", i));
                    i++;
                }

                continue;
            }

            if (c == '>') {
                if (i + 1 < n && upit.charAt(i + 1) == '=') {
                    tokeni.add(new Token(TipTokena.OPERATOR, ">=", i));
                    i += 2;
                } else {
                    tokeni.add(new Token(TipTokena.OPERATOR, ">", i));
                    i++;
                }

                continue;
            }

            if (c == '=') {
                tokeni.add(new Token(TipTokena.OPERATOR, "=", i));
                i++;
                continue;
            }

            if (c == '*') {
                tokeni.add(new Token(TipTokena.ZVEZDICA, "*", i));
                i++;
                continue;
            }

            if (c == '.') {
                tokeni.add(new Token(TipTokena.TACKA, ".", i));
                i++;
                continue;
            }

            if (c == ',') {
                tokeni.add(new Token(TipTokena.ZAREZ, ",", i));
                i++;
                continue;
            }

            throw new IllegalArgumentException("Sintaksna greska: neocekivan znak '" + c + "' na poziciji " + i);
        }

        tokeni.add(new Token(TipTokena.KRAJ, "", n));
        return tokeni;
    }
}