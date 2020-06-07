package com.opengg.core.render.text;

import com.opengg.core.math.Vector3fm;
import com.opengg.core.render.Renderable;

public interface Font {
    static Font load(String path){
        if(path.endsWith(".ttf")){
            //return new TTFBaked();
        }
        throw new RuntimeException();
    }

    Renderable createFromText(String text);
    Renderable createFromText(Text text);
    Renderable createFromTextWithSize(Text wholetext, Vector3fm endSize);
}
