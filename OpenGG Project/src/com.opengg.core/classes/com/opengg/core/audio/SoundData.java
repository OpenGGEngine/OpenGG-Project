/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.audio;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

/**
 *
 * @author Javier
 */
public class SoundData {
    ShortBuffer data;
    String origin;
    int channels;
    int format;
    int samplerate;
}
