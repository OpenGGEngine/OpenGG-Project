/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.drawn.Drawable;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.MaterialDrawnObject;
import com.opengg.core.render.shader.ShaderController;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Warren
 */
public class ModelDrawnObject implements Drawable {
    public Model model;
    private Animation currentAnimation;
    List<Drawable> drawables = new ArrayList<>();
    float animcounter = 0;

    public ModelDrawnObject(Model model) {
        this.model = model;
        
        for(Mesh mesh : model.getMeshes()){
            MaterialDrawnObject mdo = new MaterialDrawnObject(new DrawnObject(mesh.vbodata, mesh.inddata), mesh.material);
            drawables.add(mdo);
        }

        if(model.isanimated){
            Optional<Map.Entry<String, Animation>> entry = model.animations.entrySet().stream().findFirst();
            Animation intro = entry.isPresent() ? entry.get().getValue() : null;
            if(intro != null) setCurrentAnimation(intro);
        }
    }

    public Animation getAnimation(String name) {
        return model.animations.get(name);
    }

    public Animation getCurrentAnimation() {
        return currentAnimation;
    }

    public void setCurrentAnimation(Animation currentAnimation) {
        Animator.removeAnimation(this.currentAnimation);
        this.currentAnimation = currentAnimation;
        Animator.registerAnimation(this.currentAnimation);
    }

    @Override
    public void setMatrix(Matrix4f m) {
        for(Drawable drawable : drawables) drawable.setMatrix(m);
    }

    @Override
    public void destroy() {
        for(Drawable drawable : drawables) drawable.destroy();
    }

    @Override
    public void render() {
        if(currentAnimation != null){
            AnimatedFrame frame = this.getCurrentAnimation().getCurrentFrame();
            ShaderController.setUniform("jointsMatrix", frame.getJointMatrices());
        }else{
            ShaderController.setUniform("jointsMatrix", new Matrix4f[4]);
        }
        for(Drawable drawable : drawables) drawable.render();
    }
}
