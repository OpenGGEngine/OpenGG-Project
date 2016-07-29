/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture.text;

import com.opengg.core.gui.GUIText;
import com.opengg.core.io.newobjloader.Material;
import com.opengg.core.render.drawn.MatDrawnObject;
import java.io.File;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.system.MemoryUtil;

/**
 *
 * @author Warren
 */
public class TextVBOGenerator {
    protected static final double LINE_HEIGHT = 0.03f;
    protected static final int SPACE_ASCII = 32;
    private GGFontFile metaData;

	public TextVBOGenerator(File metaFile) {
		metaData = new GGFontFile(metaFile);
	}

	protected MatDrawnObject createTextData(GUIText text) {
		List<TextLine> lines = createStructure(text);
		FloatBuffer data = createQuadVertices(text, lines);
                MatDrawnObject t = new MatDrawnObject(data, 12);     
                t.setM(Material.defaultmaterial);
                t.setTexture(text.font.texture);
		return t;
	}

	private List<TextLine> createStructure(GUIText text) {
		char[] chars = text.getTextString().toCharArray();
		List<TextLine> lines = new ArrayList<>();
		TextLine currentLine = new TextLine(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
		Word currentWord = new Word(text.getFontSize());
		for (char c : chars) {
                    
                        if(c == '\n'){
                            lines.add(currentLine);
                            currentLine = new TextLine(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
                            continue;
                        }
                    
			int ascii = (int) c;
			if (ascii == SPACE_ASCII) {
				boolean added = currentLine.addWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new TextLine(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
					currentLine.addWord(currentWord);
				}
				currentWord = new Word(text.getFontSize());
				continue;
			}
			GGCharacter character = metaData.getCharacter(ascii);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<TextLine> lines, TextLine currentLine, Word currentWord, GUIText text) {
		boolean added = currentLine.addWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new TextLine(metaData.getSpaceWidth(), text.getFontSize(), text.getMaxLineSize());
			currentLine.addWord(currentWord);
		}
		lines.add(currentLine);
	}

	private FloatBuffer createQuadVertices(GUIText text, List<TextLine> lines) {
		text.setNumberOfLines(lines.size());
		double curserX = 0f;
		double curserY = 0f;
		List<Float> vertices = new ArrayList<>();
		List<Float> textureCoords = new ArrayList<>();
		for (TextLine line : lines) {
			if (text.isCentered()) {
				curserX = (line.maxLength - line.currentLineLength) / 2;
			}
			for (Word word : line.getWords()) {
				for (GGCharacter letter : word.getCharacters()) {
					addVerticesForCharacter(curserX, curserY, letter, text.getFontSize(), vertices);
					addTexCoords(textureCoords, letter.xTextureCoord, letter.yTextureCoord,
							letter.xMaxTextureCoord, letter.yMaxTextureCoord);
					curserX += letter.xAdvance * text.getFontSize();
				}
				curserX += metaData.getSpaceWidth() * text.getFontSize();
			}
			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSize();
		}
                FloatBuffer f = MemoryUtil.memAllocFloat(vertices.size() *12);
                int texpointer = 0;
                for(int i =0;i<vertices.size();i+=3){
                    //vertices
                    f.put(vertices.get(i));
                    f.put(vertices.get(i+1));
                    f.put(vertices.get(i+2));
                    //color
                    f.put(1);
                    f.put(1);
                    f.put(1);
                    f.put(1);
                    
                    //normals
                    f.put(1);
                    f.put(1);
                    f.put(0);
                    
                    //tex coords
                    f.put(textureCoords.get(texpointer));
                    f.put(textureCoords.get(texpointer+1));
                    texpointer +=2;          
                    
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
		double properX = (2 * x) - 1;
		double properY = (-2 * y) + 1;
		double properMaxX = (2 * maxX) - 1;
		double properMaxY = (-2 * maxY) + 1;
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
                
                y = 1-y;
            
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
