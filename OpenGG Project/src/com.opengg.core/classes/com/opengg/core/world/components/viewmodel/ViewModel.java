/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.components.Component;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
@ForComponent(Component.class)
public abstract class ViewModel<T extends Component> {

    public T component;
    private final List<DataBinding> dataBindings = new ArrayList<>();
    
    public ViewModel(){

    }

    public void createMainViewModel(){
        DataBinding<Vector3f> pos = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        pos.autoupdate = true;
        pos.name = "Position";
        pos.internalname = "pos";
        pos.setValueAccessorFromData(() -> component.getPosition());
        pos.onViewChange(component::setPositionOffset);

        DataBinding<Vector3f> rot = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        rot.autoupdate = true;
        rot.name = "Rotation";
        rot.internalname = "rot";
        rot.setValueAccessorFromData(() -> component.getRotation().toEuler());
        rot.onViewChange(component::setRotationOffset);

        DataBinding<Vector3f> scale = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        scale.autoupdate = true;
        scale.name = "Scale";
        scale.internalname = "scale";
        scale.setValueAccessorFromData(() -> component.getScale());
        scale.onViewChange(component::setScaleOffset);

        DataBinding<String> name = DataBinding.ofType(DataBinding.Type.STRING);
        name.autoupdate = true;
        name.name = "Name";
        name.internalname = "name";
        name.setValueAccessorFromData(component::getName);
        name.onViewChange(component::setName);

        DataBinding<Boolean> update = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        update.autoupdate = true;
        update.name = "Enabled";
        update.internalname = "enabled";
        update.setValueAccessorFromData(component::isEnabled);
        update.onViewChange(component::setEnabled);

        DataBinding<Boolean> serialize = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        serialize.autoupdate = true;
        serialize.name = "Should serialize";
        serialize.internalname = "serialize";
        serialize.setValueAccessorFromData(component::shouldSerialize);
        serialize.onViewChange(component::setSerializable);

        DataBinding<Boolean> abs = DataBinding.ofType(DataBinding.Type.BOOLEAN);
        abs.autoupdate = true;
        abs.name = "Is position absolute";
        abs.internalname = "abs";
        abs.setValueAccessorFromData(component::isAbsoluteOffset);
        abs.onViewChange(component::setAbsoluteOffset);

        addElement(pos);
        addElement(rot);
        addElement(scale);
        addElement(name);
        addElement(update);
        addElement(serialize);
        addElement(abs);
    }

    public abstract Initializer getInitializer(Initializer init);
    
    public abstract T getFromInitializer(Initializer init);
    
    public final void setComponent(T c){
        this.component = c;
    }
    
    public final T getComponent(){
        return component;
    }
    
    public final List<DataBinding> getDataBindings(){
        return dataBindings;
    }

    public final void addElement(DataBinding dataBinding){
        getDataBindings().add(dataBinding);
    }

    public final DataBinding getByName(String name){
        for(DataBinding e : dataBindings){
            if(e.internalname.equalsIgnoreCase(name))
                return e;
        }
        return null;
    }

    private Boolean get() {
        return component.isAbsoluteOffset();
    }
}
