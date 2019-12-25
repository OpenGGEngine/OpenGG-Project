package com.opengg.core.world.components.viewmodel;

import com.opengg.core.editor.DataBinding;
import com.opengg.core.editor.ForComponent;
import com.opengg.core.editor.BindingAggregate;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.Component;
import com.opengg.core.world.components.TriggerComponent;
import com.opengg.core.world.components.triggers.Triggerable;

import java.util.Arrays;
import java.util.stream.Collectors;


@ForComponent(TriggerComponent.class)
public class TriggerComponentViewModel<T extends TriggerComponent> extends ComponentViewModel<T> {

    @Override
    public void createMainViewModel() {
        super.createMainViewModel();

        var children = new DataBinding.StringBinding();
        children.autoupdate = false;
        children.name = "Subscribed observers";
        children.internalname = "children";
        children.visible = true;
        children.setValueAccessorFromData(() ->
                model.getSubscribers()
                        .stream().filter(t -> t instanceof Component)
                        .map(t -> (Component) t)
                        .map(Component::getName)
                        .collect(Collectors.joining(";")));
        children.onViewChange(ss -> {
                    model.clearSubscribers();
                    Arrays.stream(ss.split(";"))
                            .flatMap(s -> WorldEngine.findEverywhereByName(s).stream())
                            .peek(s -> System.out.println("Found " + s.toString()))
                            .filter(c -> c instanceof Triggerable)
                            .map(c -> (Triggerable) c)
                            .forEach(model::addListener);
                }
        );

        this.addElement(children);
    }

    @Override
    public BindingAggregate getInitializer(BindingAggregate init) {
        return init;
    }

    @Override
    public T getFromInitializer(BindingAggregate init) {
        return (T) new TriggerComponent();
    }
}