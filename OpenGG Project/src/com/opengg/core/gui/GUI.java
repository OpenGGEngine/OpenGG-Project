/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.gui;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.world.Camera;
import java.util.ArrayList;

/**
 *
 * @author Javier
 */
public class GUI {
    public static GUIGroup root = new GUIGroup(new Vector2f(0,0));

    public static void startGUIPos(){
        ShaderController.setOrtho(-1, 1, -1, 1, -1, 1);
        ShaderController.setView(new Camera().getMatrix());
    }

    public static void render(){
        root.render(root.pos);
    }
}
