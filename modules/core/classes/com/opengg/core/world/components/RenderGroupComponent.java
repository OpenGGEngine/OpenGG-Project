package com.opengg.core.world.components;

import com.opengg.core.engine.OpenGG;
import com.opengg.core.render.SceneRenderUnit;
import com.opengg.core.render.Renderable;

import java.util.ArrayList;
import java.util.List;

public class RenderGroupComponent extends Component{
    protected List<SceneRenderUnit> objects = new ArrayList<>();

    public void attachRenderUnit(Renderable renderable, SceneRenderUnit.UnitProperties unitProperties){
        objects.add(new SceneRenderUnit(renderable, unitProperties));
        OpenGG.asyncExec(this::reAddRenderUnits);
    }

    public void removeRenderUnit(Renderable renderable, SceneRenderUnit.UnitProperties unitProperties){
        objects.remove(new SceneRenderUnit(renderable, unitProperties));
    }

    private void reAddRenderUnits(){
        objects.forEach(obj -> getWorld().getRenderEnvironment().addRenderUnit(obj));
    }

    @Override
    public void onComponentAdded() {
        objects.forEach(obj -> getWorld().getRenderEnvironment().addRenderUnit(obj));
    }

    @Override
    public void onComponentRemoved() {
        objects.forEach(obj -> getWorld().getRenderEnvironment().removeRenderUnit(obj));
    }
}
