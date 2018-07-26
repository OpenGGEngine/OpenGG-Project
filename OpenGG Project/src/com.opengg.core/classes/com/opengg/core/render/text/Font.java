package com.opengg.core.render.text;

import com.opengg.core.render.drawn.Drawable;

public interface Font {
    static Font load(String path){
        if(path.endsWith(".ttf")){
            //return new TTFBaked();
        }
        throw new RuntimeException();
    }

    Drawable createFromText(String text);
    Drawable createFromText(Text text);
}
