package com.opengg.core.render.text;

import com.opengg.core.math.Vector3f;

public class Text {
    private String text;
    private boolean kern = true;
    private boolean newline = true;
    private float size = 0.5f;
    private float linePadding = 0;
    private float maxLineSize = -1f;
    private Vector3f color = new Vector3f(1,1,1);
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
        this.maxLineSize = maxlinesize;
        return this;
    }

    public Text center(boolean centered) {
        this.centered = centered;
        return this;
    }

    public Text linePadding(float linePadding){
        this.linePadding = linePadding;
        return this;
    }

    public Text color(Vector3f color) {
        this.color = color;
        return this;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCentered() {
        return centered;
    }

    public float getMaxLineSize() {
        return maxLineSize;
    }

    public float getLinePadding() {
        return linePadding;
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

    public Vector3f getColor() {
        return color;
    }

    public float getSize() {
        return size;
    }
}
