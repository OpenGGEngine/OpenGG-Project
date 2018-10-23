package com.opengg.core.model.ggmodel.process;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ggmodel.GGVertex;
import com.opengg.core.physics.collision.ConvexHull;

import java.util.ArrayList;

public class ConvexHullUtil {

    //Badly implements QuickHull

    private Vector3f[] genInitial(ArrayList<GGVertex> vertices){
        //Find furthest apart point pair
        Vector3f point1 = new Vector3f(0), point2= new Vector3f(0);
        float distance = -10; int p1=0,p2=0;
        for(int i=0;i<vertices.size();i++){
            for(int i2=1;i2<vertices.size();i2++){
                float calDis = Vector3f.distance(vertices.get(i).position,vertices.get(i2).position);
                if(calDis>distance){
                    distance = calDis;
                    point1 = vertices.get(i).position;
                    point2 = vertices.get(i2).position;
                    p1 = i;
                    p2 = i2;
                }
            }
        }
        //Find farthest point from line
        distance = -10;
        Vector3f point3 = new Vector3f();
        for(int i=0;i<vertices.size();i++){
            if(i == p1 || i== p2) continue;
            float calDis = vertices.get(i).position.distanceTo(FastMath.closestPointTo(point1, point2, vertices.get(i).position, false));
            if(calDis > distance){
                distance = calDis;
                point3 = vertices.get(i).position;
            }
        }
        return null;
    }
    public static ConvexHull generateCH(ArrayList<GGVertex> vertices){
        return null;
    }
}
