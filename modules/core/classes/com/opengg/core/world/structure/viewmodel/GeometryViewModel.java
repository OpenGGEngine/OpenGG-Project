package com.opengg.core.world.structure.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ViewModel;
import com.opengg.core.math.Vector3f;
import com.opengg.core.world.structure.WorldGeometry;

public abstract class GeometryViewModel<T extends WorldGeometry> extends ViewModel<T> {
    public void createMainViewModel(){
        DataBinding<Vector3f> pos = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        pos.autoupdate = true;
        pos.name = "Position";
        pos.internalname = "pos";
        pos.setValueAccessorFromData(() -> model.getPosition());
        pos.onViewChange(model::setPosition);

        DataBinding<Vector3f> rot = DataBinding.ofType(DataBinding.Type.VECTOR3F);
        rot.autoupdate = true;
        rot.name = "Rotation";
        rot.internalname = "rot";
        rot.setValueAccessorFromData(() -> model.getRotation().toEuler());
        rot.onViewChange(model::setRotation);

        addElement(pos);
        addElement(rot);

    }

    @Override
    public final void delete(){
        model.delete();
    }
}
