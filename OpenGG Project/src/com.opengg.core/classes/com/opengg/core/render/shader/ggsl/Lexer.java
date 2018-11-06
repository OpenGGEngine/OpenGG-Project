package com.opengg.core.render.shader.ggsl;

import com.opengg.core.math.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class Lexer {
    private String contents;
    private List<Tuple<Token, String>> tokens;

    public Lexer(String contents){
        this.contents = contents;
        tokens = new ArrayList<>();
        var dab = System.nanoTime();
        //System.out.println(contents);

        List<Tuple<Token, Tuple<String,Integer>>> allmatches = new ArrayList<>();
        for(Token token : Token.values()){
            if(token.isSpaced()){
                contents = token.getPattern().matcher(contents).replaceAll(" " + token.getMatch() + " ");
            }
        }

        System.out.println(contents);

        for (Token token : Token.values()){
            var matcher = token.getPattern().matcher(contents);
            while (matcher.find()){
                if(allmatches.stream().filter(s -> s.y.y == matcher.start()).findAny().isPresent()) continue;
                allmatches.add(new Tuple<>(token, new Tuple<>(matcher.group().trim(), matcher.start())));
            }
        }

        while (true){
            if(allmatches.isEmpty()) break;
            Tuple<Token, Tuple<String, Integer>> last = allmatches.get(0);

            for(var match : allmatches){
                if(match.y.y < last.y.y) last = match;
            }

            tokens.add(new Tuple<>(last.x, last.y.x));

            allmatches.remove(last);
            System.out.println(last.x.name() + ": " + last.y.x);
        }

        System.out.println(tokens.stream()
                .map(t -> t.y)
                .map(s -> s.equals(";") || s.equals("}") || s.equals("{") ? s + "\n" : s + " ")
                .collect(Collectors.joining()));

        System.out.println((System.nanoTime()-dab)/1_000_000f);
    }
}
