package com.opengg.core.render;

import com.opengg.core.math.Matrix4f;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector3f;

public interface View {
    Matrix4f getMatrix();
    Vector3f getPosition();
    Quaternionf getRotation();
}
