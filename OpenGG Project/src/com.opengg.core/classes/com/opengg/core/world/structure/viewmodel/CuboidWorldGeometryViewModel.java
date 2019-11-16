package com.opengg.core.world.structure.viewmodel;


import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.texture.TextureData;
import com.opengg.core.render.texture.TextureManager;
import com.opengg.core.world.structure.CuboidWorldGeometry;
import com.opengg.core.world.structure.WorldGeometryBuilder;


public class CuboidWorldGeometryViewModel extends GeometryViewModel<CuboidWorldGeometry> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        DataBinding<Vector3f> size = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        size.autoupdate = true;
        size.name = "Size";
        size.internalname = "size";
        size.setValueAccessorFromData(() -> model.getLWH());
        size.onViewChange(l -> {
            if (model.getTexture() != null){
                model.deleteParts();
                model.initialize(model.getPosition(), model.getRotation(), l, model.getTexture(), model.getCollider() != null);
            }else{
                model.deleteParts();
                model.initialize(model.getPosition(), model.getRotation(), l);
            }
        });

        addElement(size);

        if(model.getTexture() != null){
            DataBinding<TextureData> tex = DataBinding.ofType(DataBinding.Type.TEXTURE);
            tex.autoupdate = true;
            tex.name = "Texture";
            tex.internalname = "tex";
            tex.setValueAccessorFromData(() -> model.getTexture());
            tex.onViewChange(t -> {
                model.deleteParts();
                model.initialize(model.getPosition(),model.getRotation(),model.getLWH(),t,model.getCollider() != null);
            });
            addElement(tex);
        }

        DataBinding<Boolean> texScale = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        texScale.autoupdate = true;
        texScale.name = "Should scale texture";
        texScale.internalname = "texScale";
        texScale.setValueAccessorFromData(this.model::isTextureScaling);
        texScale.onViewChange(this.model::setTextureScaling);

        addElement(texScale);

        DataBinding<Float> scaleFactor = DataBinding.ofType(DataBinding.Type.FLOAT);
        scaleFactor.autoupdate = true;
        scaleFactor.name = "Texture scaling factor";
        scaleFactor.internalname = "texScale";
        scaleFactor.setValueAccessorFromData(this.model::getTextureScale);
        scaleFactor.onViewChange(this.model::setTextureScale);

        addElement(scaleFactor);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        init.addElement(new DataBinding.Vector3fBinding()
            .name("Size")
            .internalName("size")
            .setValueFromData(new Vector3f()));

        init.addElement(new DataBinding.TextureBinding()
                .name("Texture")
                .internalName("texture")
                .setValueFromData(TextureManager.getDefault()));

        init.addElement(new DataBinding.BooleanBinding()
                .name("Generate Collider")
                .internalName("collider")
                .setValueFromData(false));
        return init;
    }

    @Override
    public CuboidWorldGeometry getFromInitializer(BindingAggregate init) {
        return WorldGeometryBuilder.fromCuboid(new Vector3f(), new Quaternionf(),
                (Vector3f)init.get("size").getValue(),
                (TextureData) init.get("texture").getValue(),
                (Boolean) init.get("collider").getValue());
    }
}