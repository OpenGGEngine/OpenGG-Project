/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.animation;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.shader.ShaderController;
import java.util.Map;
import java.util.Optional;
import static org.lwjgl.opengl.GL11.glGetError;

/**
 *
 * @author Warren
 */
public class AnimatedDrawnObject implements Drawable {

    private Map<String, Animation> animations;

    private Animation currentAnimation;

    public AnimatedDrawnObject(MatDrawnObject d, Map<String, Animation> animations) {
        this.d = d;
        this.animations = animations;
        Optional<Map.Entry<String, Animation>> entry = this.animations.entrySet().stream().findFirst();
        
        currentAnimation = entry.isPresent() ? entry.get().getValue() : null;
        for(String s:animations.keySet()){
            System.out.println(s +": "+animations.get(s));
        }
    }

    public Animation getAnimation(String name) {
        return animations.get(name);
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        this.currentAnimation = currentAnimation;
    }

    MatDrawnObject d;

    @Override
    public void setMatrix(Matrix4f m) {
        d.setMatrix(m);
    }

    @Override
    public Matrix4f getMatrix() {
        return d.getMatrix();
    }

    @Override
    public void destroy() {
        d.destroy();
    }

    @Override
    public boolean hasAdjacency() {
        return d.hasAdjacency();
    }

    @Override
    public void render() {
        AnimatedFrame frame = this.getCurrentAnimation().getCurrentFrame();
       
        ShaderController.setUniform("jointsMatrix", frame.getJointMatrices());
        d.render();
        this.getCurrentAnimation().nextFrame();
    }

}
