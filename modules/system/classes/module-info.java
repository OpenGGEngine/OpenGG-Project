/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
module com.opengg.system {
    requires com.opengg.base;
    requires com.opengg.console;

    requires org.lwjgl;
    requires org.lwjgl.opengl;

    requires org.lwjgl.natives;
    requires org.lwjgl.opengl.natives;

    exports com.opengg.core.system;
}
