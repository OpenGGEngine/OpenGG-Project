/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
module com.opengg.core {
    requires org.lwjgl.glfw;
    requires org.lwjgl.openal;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires org.lwjgl;
    requires org.lwjgl.stb.natives;
    requires org.lwjgl.natives;
    requires org.lwjgl.assimp.natives;
    requires org.lwjgl.assimp;
    requires org.lwjgl.glfw.natives;
    requires org.lwjgl.openal.natives;
    requires org.lwjgl.opengl.natives;
    requires org.lwjgl.lz4.natives;
    requires java.logging;
    requires java.desktop;
    requires com.opengg.math;
    requires com.opengg.base;
    requires com.opengg.system;
    requires com.opengg.console;
    requires org.lwjgl.lz4;
    requires org.lwjgl.openvr;
    requires org.lwjgl.tootle;
    requires org.lwjgl.tootle.natives;
    requires org.lwjgl.meow;
    requires org.lwjgl.meow.natives;
    requires java.compiler;

    exports com.opengg.core.audio;
    exports com.opengg.core.engine;
    exports com.opengg.core.exceptions;
    exports com.opengg.core.extension;
    exports com.opengg.core.gui;
    exports com.opengg.core.io;
    exports com.opengg.core.io.input.keyboard;
    exports com.opengg.core.io.input.mouse;
    exports com.opengg.core.model;
    exports com.opengg.core.network;
    exports com.opengg.core.network.client;
    exports com.opengg.core.network.server;
    exports com.opengg.core.physics;
    exports com.opengg.core.physics.collision;
    exports com.opengg.core.render;
    exports com.opengg.core.render.drawn;
    exports com.opengg.core.render.light;
    exports com.opengg.core.render.objects;
    exports com.opengg.core.render.postprocess;
    exports com.opengg.core.render.shader;
    exports com.opengg.core.render.texture;
    exports com.opengg.core.render.text.impl;
    exports com.opengg.core.render.window;
    exports com.opengg.core.states;
    exports com.opengg.core.util;
    exports com.opengg.core.render.text;
    exports com.opengg.core.world;
    exports com.opengg.core.world.components;
    exports com.opengg.core.world.components.particle;
    exports com.opengg.core.world.components.physics;
    exports com.opengg.core.world.components.triggers;
    exports com.opengg.core.world.components.viewmodel;
    exports com.opengg.core.world.generators;
    exports com.opengg.core.model.io;
    exports com.opengg.core.model.process;
    exports com.opengg.core.animation;
    exports com.opengg.core.script;
}
