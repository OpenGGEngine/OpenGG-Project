/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world.components.physics.collision;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObjectGroup;

/**
 *
 * @author Javier
 */
public class ColliderRenderer implements Renderable {
    Drawable drawn;
    CollisionComponent collider;
    
    public ColliderRenderer(CollisionComponent collider){
        this.collider = collider;
        DrawnObjectGroup d = new DrawnObjectGroup();
        collider.boxes.stream().forEach((c) -> {
            //Vector3f[] vertices = c.getAABBVertices();
            //d.add(ObjectCreator.createQuadPrism(vertices[0], vertices[1]));
        });
        drawn = d;
    }
    
    
    @Override
    public void render() {
        Matrix4f posm = new Matrix4f().translate(collider.getPosition());
        drawn.setMatrix(posm);
        drawn.render();
    }
}
