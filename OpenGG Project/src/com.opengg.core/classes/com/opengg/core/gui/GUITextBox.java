package com.opengg.core.gui;

import com.opengg.core.animation.Animation;
import com.opengg.core.animation.AnimationManager;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;

import java.awt.*;

public class GUITextBox extends GUIGroup{
    private String value = "";
    private GUIText text;
    private GUITexture background;
    private float textSize;
    private float speed;

    private Vector2f size;

    private ScrollMode scroll;

    private float counter;
    private int textLength;

    private boolean complete;
    private boolean running = false;

    public GUITextBox(Vector2f pos, Vector2f size){
        super(pos);
        this.size = size;

        this.addItem("background", background = (GUITexture) new GUITexture(Texture.ofColor(Color.BLACK, 0), new Vector2f(), size).setLayer(-0.5f));
        this.addItem("text", text = new GUIText(Resource.getTruetypeFont("consolas.ttf"), new Vector2f(0, size.y - (0.01f + 0))));

        Animation animation = new Animation(0.1f, false);
        animation.addStaticEvent(Animation.AnimationStage.createStaticStage(0,0.1f, d -> Texture.ofColor(Color.BLACK, d.floatValue()*10), t -> background.setTexture(t)));
        animation.setOnCompleteAction(() -> setRunning(true));
        animation.setToRun();
        AnimationManager.register(animation);
    }

    public GUITextBox(Vector2f pos, Vector2f size, String value, float textSize, float speed, float margin, Font font, Texture backgroundTex, ScrollMode scroll){
        this(pos, size);

        this.value = value;
        this.speed = speed;
        this.textSize = textSize;

        this.setMargin(margin);
        this.setFont(font);
        this.setBackground(backgroundTex);
        this.setScrollMode(scroll);
    }

    public boolean isComplete(){
        return complete;
    }

    public ScrollMode getScrollMode() {
        return scroll;
    }

    public GUITextBox setScrollMode(ScrollMode mode){
        this.scroll = mode;
        return this;
    }

    public GUITextBox setText(String value) {
        this.value = value;
        if(textLength > value.length()) textLength = value.length();
        return this;
    }

    public GUITextBox setTextSize(float textSize) {
        this.textSize = textSize;
        return this;
    }

    public GUITextBox setSpeed(float speed) {
        this.speed = speed;
        return this;
    }

    public GUITextBox setMargin(float margin) {
        text.setPositionOffset(new Vector2f(margin, size.y - (0.01f + margin)));
        return this;
    }

    public GUITextBox setFont(Font font) {
        text.setFont(font);
        return this;
    }

    public GUITextBox setBackground(Texture background){
        this.background.setTexture(background);
        return this;
    }

    public void forceComplete(){
        textLength = value.length();
    }

    public void reset(){
        counter = 0;
        textLength = 0;
        complete = false;
    }

    public GUITextBox setRunning(boolean running) {
        this.running = running;
        return this;
    }

    public void update(float delta) {
        super.update(delta);
        if (!enabled) return;
        if(!running) return;

        counter += delta;
        if (counter > speed) {
            counter = 0;
            if (textLength < value.length())
                textLength += 1;
            else
                complete = true;

            var printedString = value.substring(0, textLength);
            text.setText(Text.from(printedString).size(textSize).maxLineSize(size.x));
        }
    }

    public enum ScrollMode{
        NONE, SCROLLING, SKIPPABLE_SCROLL, SPEEDABLE_SCROLL
    }
}
