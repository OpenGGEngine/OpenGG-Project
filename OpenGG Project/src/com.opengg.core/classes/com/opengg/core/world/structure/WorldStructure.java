/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.world.structure;

import com.opengg.core.util.GGOutputStream;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class WorldStructure {
    private List<WorldGeometry> allgeometry = new ArrayList();

    public void addGeometry(WorldGeometry geometry){
        allgeometry.add(geometry);
    }

    public void serialize(GGOutputStream out){

    }
}
