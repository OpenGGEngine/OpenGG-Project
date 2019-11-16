package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.components.ScriptComponent;

@ForComponent(ScriptComponent.class)
public class ScriptComponentViewModel extends ComponentViewModel<ScriptComponent> {
    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var name = new DataBinding.StringBinding();
        name.name("Script Name");
        name.internalName("script");
        name.setValueAccessorFromData(model::getScriptName);
        name.onViewChange(model::setScript);
        this.addElement(name);

        var mode = new DataBinding.BooleanBinding();
        mode.name("Enable on trigger");
        mode.internalName("mode");
        mode.setValueAccessorFromData(() -> model.getRunMode() == ScriptComponent.RunMode.ON_TRIGGER);
        mode.onViewChange(b -> model.setRunMode(b ? ScriptComponent.RunMode.ON_TRIGGER : ScriptComponent.RunMode.CONTINUOUS));
        this.addElement(mode);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public ScriptComponent getFromInitializer(BindingAggregate init) {
        return new ScriptComponent();
    }
}
