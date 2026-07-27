package bp2.upit;

public enum TipTokena {
    KLJUCNA_REC,   // SELECT, FROM, WHERE, AND, ORDER, BY, ASC, DESC
    IDENTIFIKATOR, // imena tabela i atributa
    BROJ,          // 123, 8.5
    STRING,        // 'RTI'
    OPERATOR,      // = <> < <= > >=
    ZAREZ, TACKA, ZVEZDICA,
    KRAJ
}