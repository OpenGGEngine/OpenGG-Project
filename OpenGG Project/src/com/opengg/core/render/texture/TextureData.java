/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.render.texture;

import java.nio.Buffer;

/**
 *
 * @author Javier
 */
public class TextureData {

    public TextureData(int width, int height, Buffer buffer) {
        this.width = width;
        this.height = height;
        this.buffer = buffer;
    }
    int width;
    int height;
    Buffer buffer;
}
