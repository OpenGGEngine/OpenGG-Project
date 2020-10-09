package com.opengg.core.render.texture;

import com.opengg.core.console.GGConsole;
import com.opengg.core.system.Allocator;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class TextureGenerator {
    public static TextureData generateFromURI(String data){
        var preData = data.substring(10);
        var type = preData.substring(0, preData.indexOf("?"));
        var args = Arrays.stream(preData.substring(preData.indexOf("?") + 1).split("&"))
                .collect(Collectors.toMap(s -> s.substring(0, s.indexOf('=')), s -> s.substring(s.indexOf('=') + 1)));
        return switch (type){
            case "color" -> ofColor((byte)Integer.parseInt(args.get("red")),
                                    (byte)Integer.parseInt(args.get("green")),
                                    (byte)Integer.parseInt(args.get("blue")),
                                    (byte)Integer.parseInt(args.get("alpha")));
            default -> {
                GGConsole.warning("Attempted to generate unknown texture type: " + data);
                yield TextureManager.getDefault();
            }
        };
    }

    public static TextureData ofColor(Color color, float transparency){
        return ofColor((byte) color.getRed(), (byte) color.getGreen(), (byte) color.getBlue(), (byte) (transparency*255f));
    }

    public static TextureData ofColor(byte r, byte g, byte b, byte a){
        return new TextureData(1,1,4, Allocator.alloc(4)
                .put(r)
                .put(g)
                .put(b)
                .put(a)
                .flip(),
                String.format("generated:color?red=%d&green=%d&blue=%d&alpha=%d", r,g,b,a));
    }
}
