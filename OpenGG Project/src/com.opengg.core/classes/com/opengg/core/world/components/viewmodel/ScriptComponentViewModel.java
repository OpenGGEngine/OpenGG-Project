package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.Initializer;
import com.opengg.core.world.components.ScriptComponent;

@ForComponent(ScriptComponent.class)
public class ScriptComponentViewModel extends ViewModel<ScriptComponent>{
    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var name = new DataBinding.StringBinding();
        name.name("Script Name");
        name.internalName("script");
        name.setValueAccessorFromData(component::getScriptName);
        name.onViewChange(component::setScript);
        this.addElement(name);

        var mode = new DataBinding.BooleanBinding();
        mode.name("Enable on trigger");
        mode.internalName("mode");
        mode.setValueAccessorFromData(() -> component.getRunMode() == ScriptComponent.RunMode.ON_TRIGGER);
        mode.onViewChange(b -> component.setRunMode(b ? ScriptComponent.RunMode.ON_TRIGGER : ScriptComponent.RunMode.CONTINUOUS));
        this.addElement(mode);
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public ScriptComponent getFromInitializer(Initializer init) {
        return new ScriptComponent();
    }
}
