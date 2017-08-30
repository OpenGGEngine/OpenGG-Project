/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render;

import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.MaterialDrawnObject;
import com.opengg.core.render.texture.text.GGFont;

/**
 *
 * @author Warren
 */
public class Text{

    private String textString;
    private float fontSize;

    private int textMeshVao;
    private int vertexCount;
    private Vector3f colour = new Vector3f(0f, 0f, 0f);

    private Vector2f position = new Vector2f();
    private float lineMaxSize;
    private int numberOfLines;

    private boolean centerText = false;

    private float distanceFieldWidth;
    private float distanceFieldEdge;
    private float borderWidth;
    private float borderEdge;
    private Vector2f offset = new Vector2f(0f, 0f);
    private Vector3f outlineColor = new Vector3f(0f, 0f, 0f);

    Drawable textDraw;
    
    public Text(String text, Vector2f position, float fontSize, float maxLineLength, boolean centered) {
        this.fontSize = fontSize;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.centerText = centered;
        this.textString = text;
    }
    
    public Text(String text){
        this(text, new Vector2f(0,0), 10, 100, false);
    }
    
    public Text(){
        this("");
    }
    
    public Text copyFormat(String newtext){
        return new Text(newtext, position, fontSize, lineMaxSize, centerText);
    }
    
    public void remove() {
        //TextMaster.removeText(this);
    }
    
    public void setText(String text){
        this.textString = text;
    }
    
    public void setColour(float r, float g, float b) {
        colour = new Vector3f(r, g, b);
    }

    public Vector3f getColour() {
        return colour;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getMesh() {
        return textMeshVao;
    }

    public void setMeshInfo(int vao, int verticesCount) {
        this.textMeshVao = vao;
        this.vertexCount = verticesCount;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public float getFontSize() {
        return fontSize;
    }


    public void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }

    public boolean isCentered() {
        return centerText;
    }

    public float getMaxLineSize() {
        return lineMaxSize;
    }

    public String getTextString() {
        return textString;
    }

    public float getDistanceFieldWidth() {
        return distanceFieldWidth;
    }

    public void setDistanceFieldWidth(float distanceFieldWidth) {
        this.distanceFieldWidth = distanceFieldWidth;
    }

    public float getDistanceFieldEdge() {
        return distanceFieldEdge;
    }

    public void setDistanceFieldEdge(float distanceFieldEdge) {
        this.distanceFieldEdge = distanceFieldEdge;
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
    }

    public float getBorderEdge() {
        return borderEdge;
    }

    public void setBorderEdge(float borderEdge) {
        this.borderEdge = borderEdge;
    }

    public Vector2f getOffset() {
        return offset;
    }

    public void setOffset(float x, float y) {
        offset = new Vector2f(x, y);
    }

    public Vector3f getOutlineColour() {
        return outlineColor;
    }

    public void setOutlineColour(float r, float g, float b) {
        outlineColor = new Vector3f(r, g, b); 
    }

    public MaterialDrawnObject getDrawable(GGFont font){
        return font.loadText(this);
    }
 
}
