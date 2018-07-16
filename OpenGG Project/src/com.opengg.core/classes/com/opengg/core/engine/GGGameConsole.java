package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.gui.GUI;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardCharacterListener;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.Text;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;

import java.awt.*;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Scanner;

public class GGGameConsole implements KeyboardListener, KeyboardCharacterListener {
    private static String currenttext = "";
    private static String consolevalue = "";
    private static Drawable background;
    private static Text userinput;
    private static Text consoletext;
    private static GGFont font;
    private static boolean enabled = false;
    private static boolean wasInMenu = false;

    public static final int LINE_AMOUNT = 40;

    public static void initialize(){
        GGGameConsole console = new GGGameConsole();
        KeyboardController.addKeyboardCharacterListener(console);
        KeyboardController.addKeyboardListener(console);

        background = new TexturedDrawnObject(ObjectCreator.createSquare(new Vector2f(-1,-1), new Vector2f(1,1), 0.99f),
                Texture.ofColor(Color.gray, 0.9f));

        font = Resource.getFont("test", "test.png");

        consoletext = new Text("", new Vector2f(), 0.5f, 1f, false);
        //consoletext.setNumberOfLines(LINE_AMOUNT);

        userinput = new Text("", new Vector2f(0, 0), 0.7f, 1f, false);
        //j;luserinput.setNumberOfLines(1);
    }

    public static void render(){
        if(!enabled) return;

        consolevalue = "";

        GGConsole.getAllMessages().stream()
                .skip(Math.max(GGConsole.getAllMessages().size()-LINE_AMOUNT, 0))
                .map(m -> m.toString())
                .map(m -> m + "\n")
                .forEach(m -> consolevalue += m);

        userinput.setText(currenttext);
        consoletext.setText(consolevalue);

        RenderEngine.setDepthCheck(false);
        background.render();

        ShaderController.setDistanceField(1);
        var userdrawable = userinput.getDrawable(font);
        userdrawable.setMatrix(Matrix4f.translate(0,-1.9f,0));
        userdrawable.render();
        // .render();
        consoletext.getDrawable(font).render();
        ShaderController.setDistanceField(0);

        RenderEngine.setDepthCheck(true);

    }

    @Override
    public void charPressed(char val) {
        if(enabled && val != '`')
            currenttext += val;
    }

    @Override
    public void keyPressed(int key) {
        if(enabled){
            if(key == Key.KEY_ENTER){
                GGConsole.acceptUserInput(currenttext);
                currenttext = "";
            }
            if(key == Key.KEY_BACKSPACE){
                if(!currenttext.isEmpty()){
                    currenttext = currenttext.substring(0, currenttext.length()-1);
                }
            }
        }

        if(key == Key.KEY_GRAVE_ACCENT){
            enabled = !enabled;
            if(enabled){
                wasInMenu = GGInfo.isMenu();
                GGInfo.setMenu(true);
            }else{
                GGInfo.setMenu(wasInMenu);
            }
        }
    }

    @Override
    public void keyReleased(int key) {

    }
}
