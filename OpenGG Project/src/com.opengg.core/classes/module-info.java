/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
module com.opengg.core {
    requires org.lwjgl.assimp;
    requires org.lwjgl.egl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.jawt;
    requires org.lwjgl.nfd;
    requires org.lwjgl.openal;
    requires org.lwjgl.opencl;
    requires org.lwjgl.opengl;
    requires org.lwjgl.stb;
    requires org.lwjgl.vulkan;
    requires org.lwjgl.xxhash;
    requires org.lwjgl;
    requires java.logging;
    requires java.desktop;
    requires com.opengg.math;
    requires com.opengg.base;
    requires com.opengg.system;
    requires com.opengg.console;
    exports com.opengg.core.audio;
    exports com.opengg.core.engine;
    exports com.opengg.core.exceptions;
    exports com.opengg.core.extension;
    exports com.opengg.core.gui;
    exports com.opengg.core.io;
    exports com.opengg.core.io.input.keyboard;
    exports com.opengg.core.io.input.mouse;
    exports com.opengg.core.model;
    exports com.opengg.core.online;
    exports com.opengg.core.online.client;
    exports com.opengg.core.online.server;
    exports com.opengg.core.physics;
    exports com.opengg.core.physics.collision;
    exports com.opengg.core.render;
    exports com.opengg.core.render.drawn;
    exports com.opengg.core.render.light;
    exports com.opengg.core.render.objects;
    exports com.opengg.core.render.postprocess;
    exports com.opengg.core.render.shader;
    exports com.opengg.core.render.texture;
    exports com.opengg.core.render.texture.text;
    exports com.opengg.core.render.window;
    exports com.opengg.core.states;
    exports com.opengg.core.thread;
    exports com.opengg.core.util;
    exports com.opengg.core.world;
    exports com.opengg.core.world.components;
    exports com.opengg.core.world.components.particle;
    exports com.opengg.core.world.components.physics;
    exports com.opengg.core.world.components.triggers;
    exports com.opengg.core.world.components.viewmodel;
    exports com.opengg.core.world.entities;
    exports com.opengg.core.world.entities.physics;
    exports com.opengg.core.world.entities.resources;
    exports com.opengg.core.world.generators;
}
