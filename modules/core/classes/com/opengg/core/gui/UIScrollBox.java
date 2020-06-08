package com.opengg.core.gui;

import com.opengg.core.io.input.mouse.MouseScrollListener;
import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector2f;

public class UIScrollBox extends UIGroup implements MouseScrollListener {
    Vector2f dampen = new Vector2f(1);

    public void setXBounds(Vector2f xBounds) {
        this.xBounds = xBounds;
    }

    public void setYBounds(Vector2f yBounds) {
        this.yBounds = yBounds;
    }

    Vector2f xBounds = new Vector2f(Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);
    Vector2f yBounds = new Vector2f(Float.NEGATIVE_INFINITY,Float.POSITIVE_INFINITY);

    public UIScrollBox setDampen(Vector2f dampen){
        this.dampen = dampen;
        return this;
    }

    @Override
    public int onScroll(double x, double y) {
        Vector2f c = getPositionOffset().add(new Vector2f((float)x,(float)y).multiply(dampen));
        c = new Vector2f(FastMath.clamp(c.x,xBounds.x,xBounds.y),FastMath.clamp(c.y,yBounds.x,yBounds.y));
        this.setPositionOffset(c);
        return 0;
    }
}
