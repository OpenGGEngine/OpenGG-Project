/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

/**
 *
 * @author Javier
 */
public class Action {
    public String name;
    public ActionType type;
    public int source;

    @Override
    public String toString() {
        return "Action{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", triggerSource=" + source +
                '}';
    }
}
