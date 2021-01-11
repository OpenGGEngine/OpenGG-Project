package com.opengg.core.render.shader.ggsl;

import java.util.regex.Pattern;

public enum TokenType {
    OPEN_BRACE (Pattern.quote("{"), true, "{"),
    CLOSE_BRACE (Pattern.quote("}"), true, "}"),
    OPEN_PARENTHESIS (Pattern.quote("("), true, "("),
    CLOSE_PARENTHESIS (Pattern.quote(")"), true, ")"),
    SEMICOLON (Pattern.quote(";"), true, ";"),
    PLUS ("(?<!\\+)\\+(?![+=])", true, "+"),
    PLUSPLUS (Pattern.quote("++"), true, "++"),
    MINUS ("(?<=[a-zA-Z\\d)]\\s*)-(?=\\s*[a-zA-Z\\d(])", true, "-"),
    NEGATE ("(?<![a-zA-Z\\d)]\\s*)-(?=\\`?\\s*[a-zA-Z\\d])", true, "-`"),
    MINUSMINUS (Pattern.quote("--"), true, "--"),
    MULTIPLY ("\\*(?!=)", true, "*"),
    DIVIDE ("/(?!=)", true, "/"),
    BIT_NEGATE (Pattern.quote("~"), true, "~"),
    BOOL_AND (Pattern.quote("&&"), true, "&&"),
    BIT_AND ("(?<!\\&)\\&(?!\\&)", true, "&"),
    BOOL_OR (Pattern.quote("||"), true, "||"),
    BIT_OR ("(?<!\\|)\\|(?!\\|)", true, "|"),
    EQUALS (Pattern.quote("=="), true, "=="),
    NOT_EQUALS (Pattern.quote("!="), true, "!="),
    LESS ("(?<![=<])<(?![=<])", true, "<"),
    GREATER ("(?<![=>])>(?![=>])", true, ">"),
    LEQUAL (Pattern.quote("<="), true, "<="),
    GEQUAL (Pattern.quote(">="), true, ">="),
    LEFT_SHIFT (Pattern.quote("<<"), true, "<<"),
    RIGHT_SHIFT (Pattern.quote(">>"), true, ">>"),
    ASSIGNMENT ("(?<![!=*-+/])=(?!=)", true, "="),
    ADD_ASSIGNMENT ("(?<![!=*-/])\\+=(?!=)", true, "+="),
    MULT_ASSIGNMENT ("(?<![!=\\-+/])\\*=(?!=)", true, "*="),
    SUB_ASSIGNMENT ("(?<![!=*+/])-=(?!=)", true, "-="),
    DIV_ASSIGNMENT ("(?<![!=*\\-+])/=(?!=)", true, "/="),
    TERNARY_IF (Pattern.quote("?"), true, "?"),
    TERNARY_OR (Pattern.quote(":"), true, ":"),
    COMMA (Pattern.quote(","), true, ","),
    IF ("\\s(?<![.a-zA-Z])if(?![.a-zA-Z])\\s", false),
    ELSE ("\\s(?<![.a-zA-Z])else(?![.a-zA-Z])\\s", false),
    FOR ("\\s(?<![.a-zA-Z])for(?![.a-zA-Z])\\s", false),
    WHILE ("\\s(?<![.a-zA-Z])while(?![.a-zA-Z])\\s", false),
    DO ("\\s(?<![.a-zA-Z])do(?![.a-zA-Z])\\s", false),
    BREAK ("\\s(?<![.a-zA-Z])break(?![.a-zA-Z])\\s", false),
    CONTINUE ("\\s(?<![.a-zA-Z])continue(?![.a-zA-Z])\\s", false),
    RETURN ("\\s(?<![.a-zA-Z])return(?![.a-zA-Z])\\s", false),
    LAYOUT ("\\s(?<![.a-zA-Z])layout(?![.a-zA-Z])\\s", false),
    UNIFORM ("\\s(?<![.a-zA-Z])uniform(?![.a-zA-Z])\\s", false),
    IN ("\\s(?<![.a-zA-Z])in(?![.a-zA-Z])\\s", false),
    OUT ("\\s(?<![.a-zA-Z])out(?![.a-zA-Z])\\s", false),
    INOUT ("\\s(?<![.a-zA-Z])inout(?![.a-zA-Z])\\s", false),
    STRUCT ("\\s(?<![.a-zA-Z])struct(?![.a-zA-Z])\\s", false),
    IDENTIFIER ("\\s[a-zA-Z]+[a-zA-Z0-9_.\\[\\]]*", false),
    RETURN_ACCESSOR ("\\s\\.[a-zA-Z]+[a-zA-Z0-9_.\\[\\]]*", false),
    FLOAT_LITERAL ("\\s(?<![.a-zA-Z])(\\d+f)|(\\d+\\.\\d*f?)\\s", false),
    INT_LITERAL ("\\s(?<![.a-zA-Z])\\d+(?![.a-zA-Z])\\s", false),
    EOF ("a^", false);

    TokenType(String pattern, boolean space){
        this(Pattern.compile(pattern, Pattern.MULTILINE), space, "");
    }

    TokenType(String pattern, boolean space, String match){
        this(Pattern.compile(pattern, Pattern.MULTILINE), space, match);
    }

    TokenType(Pattern pattern, boolean space){
        this(pattern, space, "");
    }

    TokenType(Pattern pattern, boolean space, String match){
        this.pattern = pattern;
        this.spaced = space;
        this.match = match;
    }

    private final Pattern pattern;
    private final boolean spaced;
    private final String match;

    public Pattern getPattern() {
        return pattern;
    }

    public boolean isSpaced() {
        return spaced;
    }

    public String getMatch(){
        return match;
    }
}
