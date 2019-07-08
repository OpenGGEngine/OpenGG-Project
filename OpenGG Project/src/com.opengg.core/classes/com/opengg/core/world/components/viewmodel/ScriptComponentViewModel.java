package com.opengg.core.world.components.viewmodel;

import com.opengg.core.world.components.ScriptComponent;

@ForComponent(ScriptComponent.class)
public class ScriptComponentViewModel extends ViewModel<ScriptComponent>{
    @Override
    public void createMainViewModel() {
        this.addElement(new Element()
                        .name("Script Name")
                        .internalName("script")
                        .type(Element.Type.STRING)
                        .value(""));
    }

    @Override
    public Initializer getInitializer(Initializer init) {
        return init;
    }

    @Override
    public ScriptComponent getFromInitializer(Initializer init) {
        return new ScriptComponent();
    }

    @Override
    public void onChange(Element element) {
        switch (element.name) {
            case "script" -> this.component.setScript((String) element.value);
        }
    }

    @Override
    public void updateView(Element element) {
        switch (element.name) {
            case "script" -> element.value = this.component.getScriptName();
        }
    }
}
