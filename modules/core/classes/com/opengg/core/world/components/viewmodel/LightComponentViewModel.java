/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.light.Light;
import com.opengg.core.world.components.LightComponent;

/**
 *
 * @author Javier
 */
@ForComponent(LightComponent.class)
public class LightComponentViewModel extends ComponentViewModel<LightComponent> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var color = new DataBinding.Vector3fBinding();
        color.internalname = "color";
        color.name = "Light Color";
        color.setValueAccessorFromData(() -> model.getLight().getColor());
        color.onViewChange(c -> model.getLight().setColor(c));
        this.addElement(color);


        if(this.model.getLight().getType() == Light.POINT){
            var distance = new DataBinding.FloatBinding();
            distance.internalname = "distance";
            distance.name = "Light Distance";
            distance.setValueAccessorFromData(() -> model.getLight().getDistance());
            distance.onViewChange(d -> model.getLight().setDistance(d));
            this.addElement(distance);
        }
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        DataBinding<Boolean> shadow = new DataBinding.BooleanBinding();
        shadow.name("Cast shadows");
        shadow.internalName("shadow");
        shadow.setValueFromData(false);
        init.addElement(shadow);

        DataBinding<Integer> resolution = new DataBinding.IntBinding();
        resolution.name("Resolution (if casts shadow)");
        resolution.internalName("resolution");
        resolution.setValueFromData(512);
        init.addElement(resolution);

        DataBinding<Boolean> directional = new DataBinding.BooleanBinding();
        directional.name("Is directional");
        directional.internalName("directional");
        directional.setValueFromData(false);
        init.addElement(directional);
        return init;
    }

    @Override
    public LightComponent getFromInitializer(BindingAggregate init) {
        Light light;
        if ((Boolean)init.get("shadow").getValue()){
            int res = (Integer)init.get("resolution").getValue();
            if((Boolean)init.get("directional").getValue()){
                light = Light.createDirectionalShadow(new Quaternionf(), new Vector3f(1), new Vector3f(),
                        Matrix4f.orthographic(-100, 100, -100, 100, -100, 100),res,res);
            }else{
                light = Light.createPointShadow(new Vector3f(), new Vector3f(1), 100,res,res);
            }
        }else{
            if((Boolean)init.get("directional").getValue()){
                light = Light.createDirectional(new Quaternionf(), new Vector3f(1));
            }else{
                light = Light.createPoint(new Vector3f(), new Vector3f(1), 100);
            }
        }
        return new LightComponent(light);
    }
}
