package com.opengg.core.editor;

import com.opengg.core.world.components.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Marks a viewmodel to indicate what {@link com.opengg.core.world.components.Component Component} the viewmodel is supposed to create
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ForComponent{
    /**
     * Class object from the Component that this ViewModel represents
     * @return
     */
    Class<? extends Component> value();
}
