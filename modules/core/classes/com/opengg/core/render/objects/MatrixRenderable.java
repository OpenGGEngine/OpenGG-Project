package com.opengg.core.render.objects;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.render.Renderable;
import com.opengg.core.render.shader.CommonUniforms;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;

/**
 *
 * @author Warren
 */
public class MatrixRenderable implements Renderable {
    private Matrix4f matrix;
    private Renderable object;

    public MatrixRenderable(Renderable renderable, Matrix4f t){
        object = renderable;
        this.matrix = t;
    }

    @Override
    public void render() {
        CommonUniforms.setModel(matrix);
        object.render();
    }

    public void setMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    public Renderable getWrappedRenderable() {
        return object;
    }

    public void setWrappedRenderable(Renderable renderable) {
        this.object = renderable;
    }
}
