/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.ViewModel;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;

/**
 *
 * @author Javier
 */
@ForComponent(Component.class)
public abstract class ComponentViewModel<T extends Component> extends ViewModel<T> {
    public void createMainViewModel(){
        DataBinding<Vector3f> pos = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        pos.autoupdate = true;
        pos.name = "Position";
        pos.internalname = "pos";
        pos.setValueAccessorFromData(() -> model.getPosition());
        pos.onViewChange(model::setPositionOffset);

        DataBinding<Vector3f> rot = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        rot.autoupdate = true;
        rot.name = "Rotation";
        rot.internalname = "rot";
        rot.setValueAccessorFromData(() -> model.getRotation().toEuler());
        rot.onViewChange(model::setRotationOffset);

        DataBinding<Vector3f> scale = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        scale.autoupdate = true;
        scale.name = "Scale";
        scale.internalname = "scale";
        scale.setValueAccessorFromData(() -> model.getScale());
        scale.onViewChange(model::setScaleOffset);

        DataBinding<String> name = DataBinding.ofType(DataBinding.Type.STRING);
        name.autoupdate = true;
        name.name = "Name";
        name.internalname = "name";
        name.setValueAccessorFromData(model::getName);
        name.onViewChange(model::setName);

        DataBinding<Boolean> update = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        update.autoupdate = true;
        update.name = "Enabled";
        update.internalname = "enabled";
        update.setValueAccessorFromData(model::isEnabled);
        update.onViewChange(model::setEnabled);

        DataBinding<Boolean> serialize = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        serialize.autoupdate = true;
        serialize.name = "Should serialize";
        serialize.internalname = "serialize";
        serialize.setValueAccessorFromData(model::shouldSerialize);
        serialize.onViewChange(model::setSerializable);

        DataBinding<Boolean> abs = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        abs.autoupdate = true;
        abs.name = "Is position absolute";
        abs.internalname = "abs";
        abs.setValueAccessorFromData(model::isAbsoluteOffset);
        abs.onViewChange(model::setAbsoluteOffset);

        addElement(pos);
        addElement(rot);
        addElement(scale);
        addElement(name);
        addElement(update);
        addElement(serialize);
        addElement(abs);
    }

    @Override
    public final void delete(){
        this.getModel().delete();
    }
}
