/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.text.impl;

import com.opengg.core.math.Tuple;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.model.Material;
import com.opengg.core.render.drawn.MaterialRenderable;
import com.opengg.core.render.text.Text;
import com.opengg.core.system.Allocator;

import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Warren
 */
public class TextVBOGenerator {
    protected static final double LINE_HEIGHT = 0.03f;
    protected static final int SPACE_ASCII = 32;
    public static boolean kerning=true;
    private GGFontFile metaData;

    public TextVBOGenerator(File metaFile) {
        metaData = new GGFontFile(metaFile);
    }

    protected MaterialRenderable createTextData(Text text, GGFont f) {
        List<TextLine> lines = createStructure(text);
        FloatBuffer data = createQuadVertices(text, lines);
        MaterialRenderable t = new MaterialRenderable(data, RenderEngine.getDefaultFormat());
        t.setMaterial(Material.defaultmaterial);
        t.getMaterial().Kd = f.texture;
        return t;
    }

    private List<TextLine> createStructure(Text text) {
        char[] chars = text.getText().toCharArray();
        List<TextLine> lines = new ArrayList<>();
        TextLine currentLine = new TextLine(metaData.getSpaceWidth(), text.getSize(), text.getMaxLineSize());
        Word currentWord = new Word(text.getSize());
        for (char c : chars) {
            if (c == '\n'){
                lines.add(currentLine);
                currentLine = new TextLine(metaData.getSpaceWidth(), text.getSize(), text.getMaxLineSize());
                continue;
            }

            int ascii = (int) c;
            if (ascii == SPACE_ASCII) {
                boolean added = currentLine.addWord(currentWord);
                if (!added) {
                    lines.add(currentLine);
                    currentLine = new TextLine(metaData.getSpaceWidth(), text.getSize(), text.getMaxLineSize());
                    currentLine.addWord(currentWord);
                }
                currentWord = new Word(text.getSize());
                continue;
            }
            GGCharacter character = metaData.getCharacter(ascii);
            currentWord.addCharacter((character == null? metaData.getCharacter('a'):character));
        }
        completeStructure(lines, currentLine, currentWord, text);
        return lines;
    }

    private void completeStructure(List<TextLine> lines, TextLine currentLine, Word currentWord, Text text) {
        boolean added = currentLine.addWord(currentWord);
        if (!added) {
            lines.add(currentLine);
            currentLine = new TextLine(metaData.getSpaceWidth(), text.getSize(), text.getMaxLineSize()*text.getSize());
            currentLine.addWord(currentWord);
        }
        lines.add(currentLine);
    }

    private FloatBuffer createQuadVertices(Text text, List<TextLine> lines) {
        double curserX = 0f;
        double curserY = 0f;
        List<Float> vertices = new ArrayList<>();
        List<Float> textureCoords = new ArrayList<>();
        for (TextLine line : lines) {
            if (text.isCentered()) {
                curserX = (line.maxLength - line.currentLineLength) / 2;
            }
            for (Word word : line.getWords()) {
                int previous = Integer.MAX_VALUE;
                for (GGCharacter letter : word.getCharacters()) {
                    double kerningAmount;
                    if(!metaData.kernings.containsKey(new Tuple<>(previous,letter.textureid))){
                        kerningAmount = 0;
                    }else{
                        kerningAmount = metaData.kernings.get(new Tuple<>(previous,letter.textureid));
                    }
                    addVerticesForCharacter((kerning?kerningAmount:0)+curserX, curserY, letter, text.getSize(), vertices);
                    addTexCoords(textureCoords, letter.xTextureCoord, letter.yTextureCoord,
                            letter.xMaxTextureCoord, letter.yMaxTextureCoord);
                    curserX += (letter.xAdvance + (kerning?kerningAmount:0))* text.getSize();
                    previous = letter.textureid;
                }
                curserX += metaData.getSpaceWidth() * text.getSize();
            }
            curserX = 0;
            curserY += LINE_HEIGHT * text.getSize() + text.getLinePadding();
        }
        FloatBuffer f = Allocator.allocFloat(vertices.size() * 8);
        int texpointer = 0;
        for (int i = 0; i < vertices.size(); i += 3) {
            //vertices
            f.put(vertices.get(i));
            f.put(vertices.get(i + 1));
            f.put(vertices.get(i + 2));

            //normals
            f.put(1);
            f.put(1);
            f.put(0);

            //tex coords
            f.put(textureCoords.get(texpointer));
            f.put(textureCoords.get(texpointer + 1));
            texpointer += 2;

        }
        f.flip();
        return f;
    }

    private void addVerticesForCharacter(double curserX, double curserY, GGCharacter character, double fontSize,
                                         List<Float> vertices) {
        double x = curserX + (character.xOffset * fontSize);
        double y = curserY + (character.yOffset * fontSize);
        double maxX = x + (character.sizeX * fontSize);
        double maxY = y + (character.sizeY * fontSize);
        double properX = (1 * x);
        double properY = (-1 * y) ;
        double properMaxX = (1 * maxX);
        double properMaxY = (-1 * maxY) ;
        addVertices(vertices, properX, properY, properMaxX, properMaxY);
    }

    private static void addVertices(List<Float> vertices, double x, double y, double maxX, double maxY) {
        vertices.add((float) x);
        vertices.add((float) y);
        vertices.add((float) 1);

        vertices.add((float) x);
        vertices.add((float) maxY);
        vertices.add((float) 1);

        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) 1);

        vertices.add((float) maxX);
        vertices.add((float) maxY);
        vertices.add((float) 1);

        vertices.add((float) maxX);
        vertices.add((float) y);
        vertices.add((float) 1);

        vertices.add((float) x);
        vertices.add((float) y);
        vertices.add((float) 1);
    }

    private static void addTexCoords(List<Float> texCoords, double x, double y, double maxX, double maxY) {

        y = 1 - y;

        maxX += x;
        maxY = y - maxY;

        texCoords.add((float) x);
        texCoords.add((float) y);

        texCoords.add((float) x);
        texCoords.add((float) maxY);

        texCoords.add((float) maxX);
        texCoords.add((float) maxY);

        texCoords.add((float) maxX);
        texCoords.add((float) maxY);

        texCoords.add((float) maxX);
        texCoords.add((float) y);

        texCoords.add((float) x);
        texCoords.add((float) y);
    }

}
