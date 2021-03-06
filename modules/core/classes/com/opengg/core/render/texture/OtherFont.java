/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


package com.opengg.core.render.texture;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class OtherFont {

    //Lets define them characters shall we
    private final Map<Integer, String> CHARS = new HashMap<>() {
    };

    //Variables
    private final java.awt.Font font;
    private final FontMetrics fontMetrics;
    private final BufferedImage bufferedImage;

    //Constructors
    public OtherFont(String path, String text, float size) throws Exception {
        this.font = new java.awt.Font("Arial", java.awt.Font.PLAIN, (int) size);
        CHARS.put(0, text);
        //Generate buffered image
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        Graphics2D graphics = gc.createCompatibleImage(1, 1, Transparency.TRANSLUCENT).createGraphics();
        graphics.setFont(font);

        fontMetrics = graphics.getFontMetrics();
        bufferedImage = graphics.getDeviceConfiguration().createCompatibleImage((int) getFontImageWidth(), (int) getFontImageHeight(), Transparency.TRANSLUCENT);

        ByteBuffer buffer = asByteBuffer();
    }

    //Getters
    public float getFontImageWidth() {
        return (float) CHARS.values().stream().mapToDouble(e -> fontMetrics.getStringBounds(e, null).getWidth()).max().getAsDouble();
    }

    public float getFontImageHeight() {
        return CHARS.keySet().size() * (this.getCharHeight());
    }

    public float getCharX(char c) {
        String originStr = CHARS.values().stream().filter(e -> e.contains("" + c)).findFirst().orElse("" + c);
        return (float) fontMetrics.getStringBounds(originStr.substring(0, originStr.indexOf(c)), null).getWidth();
    }

    public float getCharY(char c) {
        float lineId = CHARS.keySet().stream().filter(i -> CHARS.get(i).contains("" + c)).findFirst().orElse(0);
        return this.getCharHeight() * lineId;
    }

    public float getCharWidth(char c) {
        return fontMetrics.charWidth(c);
    }

    public float getCharHeight() {
        //System.out.println(fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
        return (fontMetrics.getMaxAscent() + fontMetrics.getMaxDescent());
    }

    public ByteBuffer asByteBuffer() {

        ByteBuffer byteBuffer;

        //Draw the characters on our image
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
        imageGraphics.setFont(font);
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);

        // draw every CHAR by line...
        imageGraphics.setColor(java.awt.Color.WHITE);
        CHARS.keySet().forEach(i -> imageGraphics.drawString(CHARS.get(i), 0, fontMetrics.getMaxAscent() + (this.getCharHeight() * i)));

        //Generate texture data
        int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());
        byteBuffer = ByteBuffer.allocateDirect((bufferedImage.getWidth() * bufferedImage.getHeight() * 4));

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int pixel = pixels[y * bufferedImage.getWidth() + x];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF));   // Red component
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));    // Green component
                byteBuffer.put((byte) (pixel & 0xFF));           // Blue component
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF));   // Alpha component. Only for RGBA
            }
        }

        byteBuffer.flip();

        return byteBuffer;
    }

    public ByteBuffer displayCertainCharacters(int index) {

        ByteBuffer byteBuffer;

        //Draw the characters on our image
        Graphics2D imageGraphics = (Graphics2D) bufferedImage.getGraphics();
        imageGraphics.setFont(font);
        imageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        imageGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // draw every CHAR by line...
        imageGraphics.setColor(java.awt.Color.WHITE);
        for (int i = 0; i < index; i++) {
            imageGraphics.drawString(CHARS.get(i), 0, fontMetrics.getMaxAscent() + (this.getCharHeight() * i));
        }
        //Generate texture data
        int[] pixels = new int[bufferedImage.getWidth() * bufferedImage.getHeight()];
        bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), pixels, 0, bufferedImage.getWidth());
        byteBuffer = ByteBuffer.allocateDirect((bufferedImage.getWidth() * bufferedImage.getHeight() * 4));

        for (int y = 0; y < bufferedImage.getHeight(); y++) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                int pixel = pixels[y * bufferedImage.getWidth() + x];
                byteBuffer.put((byte) ((pixel >> 16) & 0xFF));   // Red component
                byteBuffer.put((byte) ((pixel >> 8) & 0xFF));    // Green component
                byteBuffer.put((byte) (pixel & 0xFF));           // Blue component
                byteBuffer.put((byte) ((pixel >> 24) & 0xFF));   // Alpha component. Only for RGBA
            }
        }

        byteBuffer.flip();

        return byteBuffer;
    }
}