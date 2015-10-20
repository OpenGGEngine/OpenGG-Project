/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class World {
    private List<WorldObject> objs = new ArrayList<>();
    private List<Camera> cams = new ArrayList<>();
    private Camera mainCam;
    private float floorLev = -1;
    
    
    public void setFloor(float floor){
        floorLev = floor;
    }
    public void addObject(WorldObject o){
        objs.add(o);
    }
    public void addCamera(Camera c){
        cams.add(c);
    }
    public World(Camera c){
        cams.add(c);
        mainCam = c;
    }
    public void setMainCam(int i){
        mainCam = cams.get(i);
    }
    public void removeObject(int i){
        objs.remove(i);
    }
    public void removeCamera(int i){
        cams.remove(i);
    }
    
}
