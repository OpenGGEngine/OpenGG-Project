/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture.text;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.WindowController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Warren
 */
public class GGFontFile {

    private static final int PAD_TOP = 0;
    private static final int PAD_LEFT = 1;
    private static final int PAD_BOTTOM = 2;
    private static final int PAD_RIGHT = 3;

    private static final int DESIRED_PADDING = 3; // 3

    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";

    private double aspectRatio;

    private double verticalPerPixelSize;
    private double horizontalPerPixelSize;
    private double spaceWidth;
    private int[] padding;
    private int paddingWidth;
    private int paddingHeight;

    private Map<Integer, GGCharacter> metaData = new HashMap<>();

    private BufferedReader reader;
    private Map<String, String> values = new HashMap<>();

    /**
     * Opens a font file in preparation for reading.
     *
     * @param file - the font file.
     */
    protected GGFontFile(File file) {
        openFile(file);
        loadPaddingData();
        loadLineSizes();
        int imageWidth = getValueOfVariable("scaleW");
        loadCharacterData(imageWidth);
        close();
    }

    protected double getSpaceWidth() {
        return spaceWidth;
    }

    protected GGCharacter getCharacter(int ascii) {
        return metaData.get(ascii);
        
    }

    /**
     * Read in the next line and store the variable values.
     *
     * @return {@code true} if the end of the file hasn't been reached.
     */
    private boolean processNextLine() {
        values.clear();
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException eequalsmcsquared) {
        }
        if (line == null) {
            return false;
        }
        for (String part : line.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                if(valuePairs[1] != null){
                    values.put(valuePairs[0], valuePairs[1]);
                }
            }
        }
//        for (String key : values.keySet()) {
//           System.out.println(key + " " + values.get(key));
//      }

        return true;
    }

    private int getValueOfVariable(String variable) {
        
        return Integer.parseInt(values.get(variable));
    }

    private int[] getValuesOfVariable(String variable) {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for (int i = 0; i < actualValues.length; i++) {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    private void close() {
        try {
            reader.close();
        } catch (IOException e) {
        }
    }

    private void openFile(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            GGConsole.warning("Couldn't load the font file!");
        }
    }
    private void loadPaddingData() {
        processNextLine();
        this.padding = getValuesOfVariable("padding");
        this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
        this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
    }
    private void loadLineSizes() {
        processNextLine();
        int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
        verticalPerPixelSize = TextVBOGenerator.LINE_HEIGHT / (double) lineHeightPixels;
        horizontalPerPixelSize = verticalPerPixelSize / WindowController.getWindow().getRatio();
    }

    private void loadCharacterData(int imageWidth) {
        processNextLine();
        processNextLine();
        while (processNextLine()) {
            GGCharacter c = loadCharacter(imageWidth);
            if (c != null) {
                
                metaData.put(c.textureid, c);
            }
        }
    }
    private GGCharacter loadCharacter(int imageSize) {
        int id = getValueOfVariable("id");
        if (id == TextVBOGenerator.SPACE_ASCII) {
            this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
            return null;
        }
        double xTex = ((double) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING)) / imageSize;
        double yTex = ((double) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING)) / imageSize;
        int width = getValueOfVariable("width") - (paddingWidth - (2 * DESIRED_PADDING));
        int height = getValueOfVariable("height") - ((paddingHeight) - (2 * DESIRED_PADDING));
        double quadWidth = width * horizontalPerPixelSize;
        double quadHeight = height * verticalPerPixelSize;
        double xTexSize = (double) width / imageSize;
        double yTexSize = (double) height / imageSize;
        double xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING) * horizontalPerPixelSize;
        double yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING)) * verticalPerPixelSize;
        double xAdvance = (getValueOfVariable("xadvance") - paddingWidth) * horizontalPerPixelSize;
        return new GGCharacter(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff, quadWidth, quadHeight, xAdvance);
    }
}
