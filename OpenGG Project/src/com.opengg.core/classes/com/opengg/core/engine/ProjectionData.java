/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.render.shader.ShaderController;

/**
 *
 * @author Javier
 */
public class ProjectionData {
    public static final int PERSPECTIVE = 0,
            ORTHO = 1;
    
    int type = 0;
    
    float minz;
    float maxz;
    
    float fov;
    float ratio;
    
    float miny;
    float maxy;
    float minx;
    float maxx;
    
    private ProjectionData(){}
    
    public static ProjectionData getPerspective(float fov, float minz, float maxz){
        ProjectionData data = new ProjectionData();
        
        data.type = PERSPECTIVE;
        data.fov = fov;
        data.ratio = OpenGG.getWindow().getRatio();
        data.minz = minz;
        data.maxz = maxz;
        
        return data;
    }
    
    public void use(){
        if(type == PERSPECTIVE)
            ShaderController.setPerspective(fov, ratio, minz, maxz);
    }
}
