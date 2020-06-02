package com.opengg.core.render.shader.ggsl;

import com.opengg.core.math.util.Tuple;

import java.util.*;

public class Lexer {
    private String contents;
    private List<Token> tokens;

    public Lexer(String contents) {
        this.contents = contents;
    }

    public void process(){
        tokens = new ArrayList<>();
        contents = " " + contents;
        List<Tuple<TokenType, Tuple<String,Integer>>> allmatches = new ArrayList<>();
        for(TokenType tokenType : TokenType.values()){
            if(tokenType.isSpaced()){
                var matcher = tokenType.getPattern().matcher(contents);
                contents = matcher.replaceAll(" " + tokenType.getMatch() + " ");
            }
        }

        for (TokenType tokenType : TokenType.values()){
            var matcher = tokenType.getPattern().matcher(contents);
            while (matcher.find()){
                if(allmatches.stream().anyMatch(s -> s.y().y() == matcher.start())) continue;
                allmatches.add(Tuple.of(tokenType, Tuple.of(matcher.group().trim(), matcher.start())));
            }
        }

        while (!allmatches.isEmpty()) {
            Tuple<TokenType, Tuple<String, Integer>> last = allmatches.get(0);

            for (var match : allmatches) {
                if (match.y().y() < last.y().y()) last = match;
            }

            tokens.add(new Token(last.x(), last.y().x()));

            allmatches.remove(last);
            //System.out.println(last.x.name() + ": " + last.y.x);
        }
        tokens.add(new Token(TokenType.EOF, " "));
    }

    public List<Token> getContents(){
        return tokens;
    }

    public record Token(TokenType type, String contents) {}
}
