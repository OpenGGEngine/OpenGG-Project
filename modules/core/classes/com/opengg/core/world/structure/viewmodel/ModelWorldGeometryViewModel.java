package com.opengg.core.world.structure.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.Model;
import com.opengg.core.model.ModelManager;
import com.opengg.core.world.structure.ModelWorldGeometry;
import com.opengg.core.world.structure.WorldGeometryBuilder;


public class ModelWorldGeometryViewModel extends GeometryViewModel<ModelWorldGeometry> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        DataBinding<Vector3f> scale = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        scale.autoupdate = true;
        scale.name = "Scale";
        scale.internalname = "scale";
        scale.setValueAccessorFromData(() -> model.getScale());
        scale.onViewChange(model::setScale);

        this.addElement(scale);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        init.addElement(new DataBinding.ModelBinding()
                .name("Model")
                .internalName("model")
                .setValueFromData(ModelManager.getDefaultModel()));

        init.addElement(new DataBinding.BooleanBinding()
                .name("Generate Collider")
                .internalName("collider")
                .setValueFromData(false));
        return init;
    }

    @Override
    public ModelWorldGeometry getFromInitializer(BindingAggregate init) {
        return WorldGeometryBuilder.fromModel(
                (Model)init.get("model").getValue(), new Vector3f(), new Quaternionf(), new Vector3f(),
                true,
                (Boolean) init.get("collider").getValue());
    }
}