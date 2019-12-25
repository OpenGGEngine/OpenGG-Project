package com.opengg.core.editor;

import java.util.List;

public abstract class ViewModel<T> extends BindingAggregate{
    protected T model;

    public void createMainViewModel(){ }

    public abstract BindingAggregate getInitializer(BindingAggregate init);

    public abstract T getFromInitializer(BindingAggregate init);

    public abstract void delete();

    public final void setModel(T c){
        this.model = c;
    }

    public final T getModel(){
        return model;
    }
}
