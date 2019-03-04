package com.opengg.core.model.process;

import com.opengg.core.math.FastMath;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.GGVertex;
import com.opengg.core.model.Mesh;
import com.opengg.core.model.Model;
import com.opengg.core.model.process.quickhull3d.Point3d;
import com.opengg.core.model.process.quickhull3d.QuickHull3D;
import com.opengg.core.physics.collision.ConvexHull;

import java.util.ArrayList;

public class ConvexHullUtil extends ModelProcess{

    public static ConvexHull generateCH(ArrayList<GGVertex> vertices){
        Point3d[] points = new Point3d[vertices.size()];
        for(int i=0;i<vertices.size();i++){
            Vector3f point = vertices.get(i).position;
            points[i] = new Point3d(point.x,point.y,point.z);
        }
        QuickHull3D hull = new QuickHull3D();
        hull.build(points);
        ArrayList<Vector3f> hullpoints = new ArrayList<>();
        for(int i=0;i< hull.getVertices().length; i++){
            Point3d point = hull.getVertices()[i];
            hullpoints.add(new Vector3f((float)point.x,(float)point.y,(float)point.z));
        }
        return new ConvexHull(hullpoints);
    }

    @Override
    public void process(Model model) {
        totaltasks = model.getMeshes().size();
        for(Mesh mesh : model.getMeshes()){
            numcompleted++;
            mesh.convexHull = generateCH(mesh.getVertices());
            if(this.run != null) broadcast();
        }

    }
}
