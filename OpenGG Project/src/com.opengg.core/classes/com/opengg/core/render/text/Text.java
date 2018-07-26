package com.opengg.core.render.text;

public class Text {
    private String text;
    private boolean kern = true;
    private boolean newline = true;
    private float size = 12;
    private float maxlinesize = -1f;
    private boolean centered = false;

    private Text(String text){
        this.text = text;
    }

    public static Text from(String data){
        return new Text(data);
    }

    public Text kerning(boolean kern) {
        this.kern = kern;
        return this;
    }

    public Text hasNewline(boolean newline) {
        this.newline = newline;
        return this;
    }

    public Text size(float size) {
        this.size = size;
        return this;
    }

    public Text maxLineSize(float maxlinesize) {
        this.maxlinesize = maxlinesize;
        return this;
    }

    public Text center(boolean centered) {
        this.centered = centered;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCentered() {
        return centered;
    }

    public float getMaxLineSize() {
        return maxlinesize;
    }

    public String getText() {
        return text;
    }

    public boolean isKerningEnabled() {
        return kern;
    }

    public boolean hasNewlines() {
        return newline;
    }

    public float getSize() {
        return size;
    }
}
