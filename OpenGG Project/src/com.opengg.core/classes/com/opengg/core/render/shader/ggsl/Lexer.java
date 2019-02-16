package com.opengg.core.render.shader.ggsl;

import com.opengg.core.math.Tuple;

import java.util.*;

public class Lexer {
    private String contents;
    private List<Tuple<Token, String>> tokens;

    public Lexer(String contents) {
        this.contents = contents;
    }

    public void process(){
        tokens = new ArrayList<>();
        contents = " " + contents;
        List<Tuple<Token, Tuple<String,Integer>>> allmatches = new ArrayList<>();
        for(Token token : Token.values()){
            if(token.isSpaced()){
                var matcher = token.getPattern().matcher(contents);
                contents = matcher.replaceAll(" " + token.getMatch() + " ");
            }
        }

        for (Token token : Token.values()){
            var matcher = token.getPattern().matcher(contents);
            while (matcher.find()){
                if(allmatches.stream().anyMatch(s -> s.y.y == matcher.start())) continue;
                allmatches.add(new Tuple<>(token, Tuple.of(matcher.group().trim(), matcher.start())));
            }
        }

        while (!allmatches.isEmpty()) {
            Tuple<Token, Tuple<String, Integer>> last = allmatches.get(0);

            for (var match : allmatches) {
                if (match.y.y < last.y.y) last = match;
            }

            tokens.add(new Tuple<>(last.x, last.y.x));

            allmatches.remove(last);
            //System.out.println(last.x.name() + ": " + last.y.x);
        }
        tokens.add(Tuple.of(Token.EOF, ""));
    }

    public List<Tuple<Token, String>> getContents(){
        return tokens;
    }
}
