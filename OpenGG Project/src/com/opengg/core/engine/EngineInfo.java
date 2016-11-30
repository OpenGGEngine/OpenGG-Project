/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.io.input.keyboard.KeySet;
import com.opengg.core.render.window.Window;
import com.opengg.core.world.World;

/**
 *
 * @author Javier
 */
public class EngineInfo {
    public final static int GLFW = 100;
    public final static int SWT = 101;
    public static Window window;
    public static UpdateEngine engine;
    public static World curworld;
    public static KeySet s;
    public static int windowType;
    public static double runtime;
    
}
