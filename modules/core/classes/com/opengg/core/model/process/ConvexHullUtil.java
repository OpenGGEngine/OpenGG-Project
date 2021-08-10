package com.opengg.core.model.process;

import com.opengg.core.math.Vector3f;
import com.opengg.core.model.GGVertex;
import com.opengg.core.model.Mesh;
import com.opengg.core.model.Model;
import com.opengg.core.model.process.quickhull3d.Point3d;
import com.opengg.core.model.process.quickhull3d.QuickHull3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ConvexHullUtil extends ModelProcess{

    public static List<Vector3f> generateConvexHull(List<GGVertex> vertices){
        Point3d[] points = new Point3d[vertices.size()];
        for(int i=0;i<vertices.size();i++){
            Vector3f point = vertices.get(i).position;
            points[i] = new Point3d(point.x,point.y,point.z);
        }
        QuickHull3D hull = new QuickHull3D();
        hull.build(points);
        List<Vector3f> hullpoints = Arrays.stream(points)
                .map(h -> new Vector3f((float)h.x,(float)h.y,(float)h.z))
                .collect(Collectors.toList());

        return hullpoints;
    }

    @Override
    public void process(Model model) {
        totaltasks = model.getMeshes().size();
        for(Mesh mesh : model.getMeshes()){
            numcompleted++;
            mesh.setConvexHull(generateConvexHull(mesh.getVertices()));
            if(this.run != null) broadcast();
        }

    }
}
