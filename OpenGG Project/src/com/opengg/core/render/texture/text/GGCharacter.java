/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.texture.text;

/**
 *
 * @author Warren
 */
public class GGCharacter {
    public GGCharacter(int textureid, double xTextureCoord, double yTextureCoord, double xMaxTextureCoord, double yMaxTextureCoord, double xOffset, double yOffset, double sizeX, double sizeY, double xAdvance) {
            this.textureid = textureid;
            this.xTextureCoord = xTextureCoord;
            this.yTextureCoord = yTextureCoord;
            this.xMaxTextureCoord = xMaxTextureCoord;
            this.yMaxTextureCoord = yMaxTextureCoord;
            this.xOffset = xOffset;
            this.yOffset = yOffset;
            this.sizeX = sizeX;
            this.sizeY = sizeY;
            this.xAdvance = xAdvance;
        }

        public int textureid;
        public double xTextureCoord;
        public double yTextureCoord;
        public double xMaxTextureCoord;
        public double yMaxTextureCoord;
        public double xOffset;
        public double yOffset;
        public double sizeX;
        public double sizeY;
        public double xAdvance;
}
