package com.opengg.core.world.structure;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.Resource;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.physics.collision.colliders.BoundingBox;
import com.opengg.core.physics.RigidBody;
import com.opengg.core.physics.collision.colliders.ConvexHull;
import com.opengg.core.render.objects.TextureRenderable;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.List;

public class CuboidWorldGeometry extends WorldGeometry{
    private Vector3f lwh;
    private TextureData texture;

    private boolean textureScaling = true;
    private float textureScale = 1;

    public void initialize(Vector3f position, Quaternionf rotation, Vector3f LWH, TextureData texture, boolean collide){
        super.initalize(position, rotation, new Vector3f(1));
        this.lwh = LWH;
        this.texture = texture;

        OpenGG.asyncExec(() ->{
            var drawable = ObjectCreator.createQuadPrism(lwh.divide(2).inverse(), lwh.divide(2));
            this.setRenderable(new TextureRenderable(drawable, Texture.create(Texture.config(), texture)));
            getParent().remakeRenderGroups();
        });
        if(collide){
            createCollider();
        }
    }

    public void initialize(Vector3f position, Quaternionf rotation, Vector3f LWH){
        super.initalize(position, rotation, new Vector3f(1));
        this.lwh = LWH;

        createCollider();
    }

    public void createCollider(){
        this.setCollider(new RigidBody(new BoundingBox(lwh), new ConvexHull(
                List.of(lwh.divide(2).multiply(new Vector3f(-1,-1,1)),
                        lwh.divide(2).multiply(new Vector3f(1,-1,1)),
                        lwh.divide(2).multiply(new Vector3f(-1,-1,-1)),
                        lwh.divide(2).multiply(new Vector3f(1,-1,-1)),
                        lwh.divide(2).multiply(new Vector3f(-1,1,1)),
                        lwh.divide(2).multiply(new Vector3f(1,1,1)),
                        lwh.divide(2).multiply(new Vector3f(-1,1,-1)),
                        lwh.divide(2).multiply(new Vector3f(1,1,-1)))
        )));
        this.getCollider().setPosition(this.getPosition());
        this.getCollider().setRotation(this.getRotation());
    }

    public Vector3f getLWH() {
        return lwh;
    }

    public TextureData getTexture(){
        return texture;
    }

    public boolean isTextureScaling() {
        return textureScaling;
    }

    public void setTextureScaling(boolean textureScaling) {
        this.textureScaling = textureScaling;
    }

    @Override
    public void render(){
        if(textureScaling){
            ShaderController.setUniform("cuboidscaling", 1f);
            ShaderController.setUniform("cuboidscale", this.lwh.multiply(textureScale));

        }
        super.render();
        if(textureScaling)
            ShaderController.setUniform("cuboidscaling", 0f);

    }

    public float getTextureScale() {
        return textureScale;
    }

    public void setTextureScale(float textureScale) {
        this.textureScale = textureScale;
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        out.write(lwh);
        out.write(texture != null);
        if(texture != null)
            out.write(texture.source);

        out.write(textureScaling);
        if(textureScaling)
            out.write(textureScale);
    }

    @Override
    public void deserialize(GGInputStream in, boolean draw, boolean collide) throws IOException {
        var LWH = in.readVector3f();
        var hasTex = in.readBoolean();
        if(hasTex)
            initialize(getPosition(), getRotation(), LWH, Resource.getTextureData(in.readString()), collide);
        else
            initialize(getPosition(), getRotation(), LWH);

        var scaling = in.readBoolean();
        if(scaling){
            setTextureScaling(true);
            setTextureScale(in.readFloat());
        }
    }
}
