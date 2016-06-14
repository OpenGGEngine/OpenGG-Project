/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.util;

import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.window.Window;
import com.opengg.core.world.World;

/**
 *
 * @author Javier
 */
public class GlobalInfo {
    public static Window window;
    public static VertexBufferObject b;
    public static ShaderController main;
    public static World curworld;
}
