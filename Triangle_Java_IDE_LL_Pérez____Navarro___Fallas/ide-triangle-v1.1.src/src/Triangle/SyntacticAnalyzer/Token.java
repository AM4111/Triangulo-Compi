/*
 * @(#)Token.java                        2.1 2003/10/07
 *
 * Copyright (C) 1999, 2003 D.A. Watt and D.F. Brown
 * Dept. of Computing Science, University of Glasgow, Glasgow G12 8QQ Scotland
 * and School of Computer and Math Sciences, The Robert Gordon University,
 * St. Andrew Street, Aberdeen AB25 1HG, Scotland.
 * All rights reserved.
 *
 * This software is provided free for educational use only. It may
 * not be used for commercial purposes without the prior written permission
 * of the authors.
 */

package Triangle.SyntacticAnalyzer;


final class Token extends Object {

    protected int kind;
    protected String spelling;
    protected SourcePosition position;

    public Token(int kind, String spelling, SourcePosition position) {

        if (kind == Token.IDENTIFIER) {
            int currentKind = firstReservedWord;
            boolean searching = true;

            while (searching) {
                int comparison = tokenTable[currentKind].compareTo(spelling);
                if (comparison == 0) {
                    this.kind = currentKind;
                    searching = false;
                } else if (comparison > 0 || currentKind == lastReservedWord) {
                    this.kind = Token.IDENTIFIER;
                    searching = false;
                } else {
                    currentKind++;
                }
            }
        } else
            this.kind = kind;

        this.spelling = spelling;
        this.position = position;

    }

    public static String spell(int kind) {
        return tokenTable[kind];
    }

    public String toString() {
        return "Kind=" + kind + ", spelling=" + spelling +
                ", position=" + position;
    }

    // Token classes...
        /*
      CAMBIOS NUEVOS:
      -Eliminación de BEGIN
      -Adición de las palabras reservadas:
        +for
        +from
        +init
        +leave
        +local
        +loop
        +next
        +nil
        +rec
        +return
        +select
        +to
        +until
        +when
       -Adición de el símbolo "|"
       Autor: Joshua
     */
    public static final int
    // literals, identifiers, operators...
            INTLITERAL = 0,
            CHARLITERAL = 1,
            IDENTIFIER = 2,
            OPERATOR = 3,

    // reserved words - must be in alphabetical order...
            ARRAY = 4,

    //BEGIN		= 5,
            CONST = 5,
            DO = 6,
            ELSE = 7,
            END = 8,
            FUNC = 9,
            FOR = 10,
            FROM = 11,
            IF = 12,
            IN = 13,
            INIT = 14,
            LEAVE = 15,
            LET = 16,
            LOCAL = 17,
            LOOP = 18,
            NEXT = 19,
            NIL = 20,
            OF = 21,
            PROC = 22,
            REC = 23,
            RECORD = 24,
            RETURN = 25,
            SELECT = 26,
            THEN = 27,
            TO = 28,
            TYPE = 29,
            UNTIL = 30,
            VAR = 31,
            WHEN = 32,
            WHILE = 33,

    // punctuation...
            DOT = 34,
            COLON = 35,
            SEMICOLON = 36,
            COMMA = 37,
            BECOMES = 38,
            IS = 39,
            BAR = 40,

    // brackets...
            LPAREN = 41,
            RPAREN = 42,
            LBRACKET = 43,
            RBRACKET = 44,
            LCURLY = 45,
            RCURLY = 46,

    // special tokens...
            EOT = 47,
            ERROR = 48;

    private static String[] tokenTable = new String[]{
            "<int>",
            "<char>",
            "<identifier>",
            "<operator>",
            "array",
            "const",
            "do",
            "else",
            "end",
            "func",
            "for",
            "from",
            "if",
            "in",
            "init",
            "leave",
            "let",
            "local",
            "loop",
            "next",
            "nil",
            "of",
            "proc",
            "rec",
            "record",
            "return",
            "select",
            "then",
            "to",
            "type",
            "until",
            "var",
            "when",
            "while",
            ".",
            ":",
            ";",
            ",",
            ":=",
            "~",
            "|",
            "(",
            ")",
            "[",
            "]",
            "{",
            "}",
            "",
            "<error>"
    };

    private final static int firstReservedWord = Token.ARRAY,
            lastReservedWord = Token.WHILE;

}
