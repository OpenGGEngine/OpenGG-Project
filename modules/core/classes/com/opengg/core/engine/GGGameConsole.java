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
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.internal.opengl.OpenGLRenderer;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.WindowOptions;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GGGameConsole implements KeyboardListener, KeyboardCharacterListener ,MouseScrollListener {
    private static String currentText = "";
    private static String consoleValue = "";
    private static Renderable background;
    private static Text userInputField;
    private static Text consoleText;
    private static Font font;
    private static boolean enabled = false;
    private static boolean wasInMenu = false;
    private static final float FONT_SCALE = 0.08f;

    public static final int LINE_AMOUNT = 27;
    public static int maxMessageSize;
    private static int topBound;
    private static boolean inScroll;

    public static void initialize(){
        GGGameConsole console = new GGGameConsole();
        KeyboardController.addKeyboardCharacterListener(console);
        KeyboardController.addKeyboardListener(console);
        MouseController.addScrollListener(console);

        font = Resource.getTruetypeFont("consolas.ttf");

        background = new TextureRenderable(ObjectCreator.createSquare(new Vector2f(0,0.47f), new Vector2f(1,1), 0.99f),
                Texture.ofColor(Color.gray, 0.9f));

/*
        consoletext = new Text("", new Vector2f(), 0.5f, 1f, false);
        consoletext.setNumberOfLines(LINE_AMOUNT);

        userinput = new Text("", new Vector2f(0, 0), 0.7f, 1f, false);
        //j;luserinput.setNumberOfLines(1);*/
    }

    public static void render(){
        if(!enabled) return;

        List<String> messages = GGConsole.getAllMessages().stream()
                .map(GGMessage::toString)
                .flatMap(m -> Arrays.stream(m.split("\n")))
                .map(m -> m + "\n")
                .collect(Collectors.toList());

        int size = messages.size()-1;
        var reversedList = IntStream.rangeClosed(0,size)
                .mapToObj(i-> messages.get(size-i))
                .skip(topBound)
                .limit(LINE_AMOUNT)
                .collect(Collectors.toList());


        consoleValue = IntStream.rangeClosed(0, LINE_AMOUNT-1)
                .mapToObj(i -> reversedList.get(LINE_AMOUNT-1-i))
                .collect(Collectors.joining());

        //Fixes the Input Field not Rendering
        CommonUniforms.setModel(new Matrix4f());
        ShaderController.useConfiguration("texture");
        if(RenderEngine.getRendererType() == WindowOptions.RendererType.OPENGL) ((OpenGLRenderer) RenderEngine.renderer).setDepthTest(false);
        background.render();

        var consoledrawable = font.createFromText(Text.from(consoleValue)
                .maxLineSize(1f)
                .kerning(true)
                .size(FONT_SCALE));
        CommonUniforms.setModel(Matrix4f.translate(0,1f,0));
        consoledrawable.render();

        var userdrawable = font.createFromText(Text.from(currentText)
                .maxLineSize(1f)
                .kerning(true)
                .size(FONT_SCALE));

        CommonUniforms.setModel(Matrix4f.translate(0,0.5f,0f));
        userdrawable.render();

        if(RenderEngine.getRendererType() == WindowOptions.RendererType.OPENGL) ((OpenGLRenderer) RenderEngine.renderer).setDepthTest(true);
    }

    @Override
    public void charPressed(char val) {
        if(enabled && val != '`')
            currentText += val;
    }

    @Override
    public void keyPressed(int key) {
        if(enabled){
            if(key == Key.KEY_ENTER){
                GGConsole.acceptUserInput(currentText);
                currentText = "";
            }
            if(key == Key.KEY_BACKSPACE){
                if(!currentText.isEmpty()){
                    currentText = currentText.substring(0, currentText.length()-1);
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
