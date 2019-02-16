/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.window;

import com.opengg.core.math.Vector2i;


/**
 *
 * @author Javier
 */
public interface WindowResizeListener {
    void onResize(Vector2i size);
}
