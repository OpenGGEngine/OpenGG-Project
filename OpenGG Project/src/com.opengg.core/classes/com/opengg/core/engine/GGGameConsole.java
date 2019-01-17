package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.console.GGMessage;
import com.opengg.core.io.input.keyboard.Key;
import com.opengg.core.io.input.keyboard.KeyboardCharacterListener;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.*;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.TexturedDrawnObject;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;

import java.awt.*;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GGGameConsole implements KeyboardListener, KeyboardCharacterListener ,MouseScrollListener {
    private static String currenttext = "";
    private static String consolevalue = "";
    private static Drawable background;
    private static Text userinput;
    private static Text consoletext;
    private static Font font;
    private static boolean enabled = false;
    private static boolean wasInMenu = false;
    private static final float FONT_SCALE = 0.034f;

    public static final int LINE_AMOUNT = 27;
    public static int maxMessageSize;
    public static int topBound;
    public static boolean inScroll;

    public static void initialize(){
        GGGameConsole console = new GGGameConsole();
        KeyboardController.addKeyboardCharacterListener(console);
        KeyboardController.addKeyboardListener(console);
        MouseController.addScrollListener(console);

        font = Resource.getTruetypeFont("consolas.ttf");

        background = new TexturedDrawnObject(ObjectCreator.createSquare(new Vector2f(0,0.47f), new Vector2f(1,1), 0.99f),
                Texture.ofColor(Color.gray, 0.9f));

/*
        consoletext = new Text("", new Vector2f(), 0.5f, 1f, false);
        consoletext.setNumberOfLines(LINE_AMOUNT);

        userinput = new Text("", new Vector2f(0, 0), 0.7f, 1f, false);
        //j;luserinput.setNumberOfLines(1);*/
    }

    public static void render(){
        if(!enabled) return;

        //Fixes the Input Field not Rendering
        ShaderController.useConfiguration("texture");

        consolevalue = "";

        List<String> messages = GGConsole.getAllMessages().stream()
                .map(m -> m.toString())
                .flatMap(m -> Arrays.stream(m.split("\n")))
                .map(m -> m + "\n")
                .collect(Collectors.toList());
        maxMessageSize = messages.size()-LINE_AMOUNT;
        if(!inScroll) topBound = Math.max(messages.size() - LINE_AMOUNT, 0);
        messages = messages.subList(topBound,Math.min(topBound + LINE_AMOUNT, messages.size()));
        consolevalue = messages.stream().collect(Collectors.joining());


        RenderEngine.setDepthCheck(false);
        background.render();

        var consoledrawable = font.createFromText(Text.from(consolevalue)
                                                    .maxLineSize(1f)
                                                    .kerning(true)
                                                    .size(FONT_SCALE));
        consoledrawable.setMatrix(Matrix4f.translate(0,0.975f,0));
        consoledrawable.render();

        var userdrawable = font.createFromText(Text.from(currenttext)
                .maxLineSize(1f)
                .kerning(true)
                .size(FONT_SCALE));
        userdrawable.setMatrix(Matrix4f.translate(0,0.48f,0f));
        userdrawable.render();

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


    @Override
    public int onScroll(double x, double y) {
        if(enabled) {
            inScroll = true;
            topBound -= (y) * 8;
            topBound = Math.max(Math.min(topBound, maxMessageSize), 0);
            if (topBound == maxMessageSize) {
                inScroll = false;
            }
        }
        return 0;

    }
}
