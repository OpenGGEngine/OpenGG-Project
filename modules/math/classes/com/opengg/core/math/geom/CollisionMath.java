package com.opengg.core.math.geom;

import com.opengg.core.math.*;
import com.opengg.core.math.util.Tuple;

import java.util.*;

public class CollisionMath {
    public static Vector3f barycentric(Vector3f p, Vector3f a, Vector3f b, Vector3f c) {
        Vector3f v0 = b.subtract(a), v1 = c.subtract(a), v2 = p.subtract(a);
        float d00 = v0.dot(v0);
        float d01 = v0.dot(v1);
        float d11 = v1.dot(v1);
        float d20 = v2.dot(v0);
        float d21 = v2.dot(v1);
        float denom = d00 * d11 - d01 * d01;
        float bx = (d11 * d20 - d01 * d21) / denom;
        float by = (d00 * d21 - d01 * d20) / denom;
        return new Vector3f(bx, by, 1.0f - bx - by);
    }

    //where is the student center
    private static MinkowskiSet getSupport(Vector3f dir, List<Vector3f> v1, List<Vector3f> v2) {
        var p1 = FastMath.getFarthestInDirection(dir, v1);
        var p2 = FastMath.getFarthestInDirection(dir.inverse(), v2);

        return new MinkowskiSet(p1, p2, p1.subtract(p2));
    }

    public static GJKResult runGJK(List<Vector3f> v1, List<Vector3f> v2) {
        final int MAX_ITER = 50;
        var simplex = new Simplex();
        simplex.searchDir = new Vector3f(1, 0, 0);

        for (int i = 0; i < MAX_ITER; i++) {
            var newPoint = getSupport(simplex.searchDir, v1, v2);

            if(simplex.count == 3){
                if(newPoint.equals(simplex.a)) return new GJKResult(simplex);
                if(newPoint.equals(simplex.b)) return new GJKResult(simplex);
                if(newPoint.equals(simplex.c)) return new GJKResult(simplex);
                if(newPoint.equals(simplex.d)) return new GJKResult(simplex);
            }

            switch (simplex.count){
                case 0 -> simplex.a = newPoint;
                case 1 -> simplex.b = newPoint;
                case 2 -> simplex.c = newPoint;
                case 3 -> simplex.d = newPoint;
            }

            if (stepGJK(simplex)){
                return new GJKResult(simplex);
            }
        }

        return new GJKResult(simplex);
    }

    private static boolean stepGJK(Simplex simplex) {
        if (simplex.count == 0) {
            simplex.distance = Float.MAX_VALUE;
        }
        simplex.count++;

        simplex.bary[simplex.count - 1] = 1.0f;

        /* IV.) Find closest simplex point */
        switch (simplex.count) {
            case 1: break;
            case 2: {
                /* -------------------- Line ----------------------- */
                var a = simplex.a.vec;
                var b = simplex.b.vec;

                /* compute barycentric coordinates */
                var ab = a.subtract(b);
                var ba = b.subtract(a);

                float u = b.dot(ba);
                float v = a.dot(ab);
                if (v <= 0.0f) {
                    /* region A */
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                if (u <= 0.0f) {
                    /* region B */
                    simplex.a = simplex.b;
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                /* region AB */
                simplex.bary[0] = u;
                simplex.bary[1] = v;
                simplex.count = 2;
            } break;
            case 3: {
                /* -------------------- Triangle ----------------------- */
                var a = simplex.a.vec;
                var b = simplex.b.vec;
                var c = simplex.c.vec;

                var ab = a.subtract(b);
                var ba = b.subtract(a);
                var bc = b.subtract(c);
                var cb = c.subtract(b);
                var ca = c.subtract(a);
                var ac = a.subtract(c);

                /* compute barycentric coordinates */
                float u_ab = b.dot(ba);
                float v_ab = a.dot(ab);

                float u_bc = c.dot(cb);
                float v_bc = b.dot(bc);

                float u_ca = a.dot(ac);
                float v_ca = c.dot(ca);

                if (v_ab <= 0.0f && u_ca <= 0.0f) {
                    /* region A */
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                if (u_ab <= 0.0f && v_bc <= 0.0f) {
                    /* region B */
                    simplex.a = simplex.b;
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                if (u_bc <= 0.0f && v_ca <= 0.0f) {
                    /* region C */
                    simplex.a = simplex.c;
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                /* calculate fractional area */
                var n = ba.cross(ca);
                var n1 = b.cross(c);
                var n2 = c.cross(a);
                var n3 = a.cross(b);

                float u_abc = n1.dot(n);
                float v_abc = n2.dot(n);
                float w_abc = n3.dot(n);

                if (u_ab > 0.0f && v_ab > 0.0f && w_abc <= 0.0f) {
                    /* region AB */
                    simplex.bary[0] = u_ab;
                    simplex.bary[1] = v_ab;
                    simplex.count = 2;
                    break;
                }
                if (u_bc > 0.0f && v_bc > 0.0f && u_abc <= 0.0f) {
                    /* region BC */
                    simplex.a = simplex.b;
                    simplex.b = simplex.c;
                    simplex.bary[0] = u_bc;
                    simplex.bary[1] = v_bc;
                    simplex.count = 2;
                    break;
                }
                if (u_ca > 0.0f && v_ca > 0.0f && v_abc <= 0.0f) {
                    /* region CA */
                    simplex.b = simplex.a;
                    simplex.a = simplex.c;
                    simplex.bary[0] = u_ca;
                    simplex.bary[1] = v_ca;
                    simplex.count = 2;
                    break;
                }
                /* region ABC */
                assert(u_abc > 0.0f && v_abc > 0.0f && w_abc > 0.0f);
                simplex.bary[0] = u_abc;
                simplex.bary[1] = v_abc;
                simplex.bary[2] = w_abc;
                simplex.count = 3;
            } break;
            case 4: {
                /* -------------------- Tetrahedron ----------------------- */
                var a = simplex.a.vec;
                var b = simplex.b.vec;
                var c = simplex.c.vec;
                var d = simplex.d.vec;

                var ab = a.subtract(b);
                var ba = b.subtract(a);
                var bc = b.subtract(c);
                var cb = c.subtract(b);
                var ca = c.subtract(a);
                var ac = a.subtract(c);

                var db = d.subtract(b);
                var bd = b.subtract(d);
                var dc = d.subtract(c);
                var cd = c.subtract(d);
                var da = d.subtract(a);
                var ad = a.subtract(d);

                /* compute barycentric coordinates */
                float u_ab = b.dot(ba);
                float v_ab = a.dot(ab);

                float u_bc = c.dot(cb);
                float v_bc = b.dot(bc);

                float u_ca = a.dot(ac);
                float v_ca = c.dot(ca);

                float u_bd = d.dot(db);
                float v_bd = b.dot(bd);

                float u_dc = c.dot(cd);
                float v_dc = d.dot(dc);

                float u_ad = d.dot(da);
                float v_ad = a.dot(ad);

                /* check verticies for closest point */
                if (v_ab <= 0.0f && u_ca <= 0.0f && v_ad <= 0.0f) {
                    /* region A */
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                if (u_ab <= 0.0f && v_bc <= 0.0f && v_bd <= 0.0f) {
                    /* region B */
                    simplex.a = simplex.b;
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                if (u_bc <= 0.0f && v_ca <= 0.0f && u_dc <= 0.0f) {
                    /* region C */
                    simplex.a = simplex.c;
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                if (u_bd <= 0.0f && v_dc <= 0.0f && u_ad <= 0.0f) {
                    /* region D */
                    simplex.a = simplex.d;
                    simplex.bary[0] = 1.0f;
                    simplex.count = 1;
                    break;
                }
                /* calculate fractional area */
                var n = da.cross(ba);
                var n1 = d.cross(b);
                var n2 = b.cross(a);
                var n3 = a.cross(d);

                float u_adb = n1.dot(n);
                float v_adb = n2.dot(n);
                float w_adb = n3.dot(n);

                n = ca.cross(da);
                n1 = c.cross(d);
                n2 = d.cross(a);
                n3 = a.cross(c);

                float u_acd = n1.dot(n);
                float v_acd = n2.dot(n);
                float w_acd = n3.dot(n);

                n = bc.cross(dc);
                n1 = b.cross(d);
                n2 = d.cross(c);
                n3 = c.cross(b);

                float u_cbd = n1.dot(n);
                float v_cbd = n2.dot(n);
                float w_cbd = n3.dot(n);

                n = ba.cross(ca);
                n1 = b.cross(c);
                n2 = c.cross(a);
                n3 = a.cross(b);

                float u_abc = n1.dot(n);
                float v_abc = n2.dot(n);
                float w_abc = n3.dot(n);

                /* check edges for closest point */
                if (w_abc <= 0.0f && v_adb <= 0.0f && u_ab > 0.0f && v_ab > 0.0f) {
                    /* region AB */
                    simplex.bary[0] = u_ab;
                    simplex.bary[1] = v_ab;
                    simplex.count = 2;
                    break;
                }
                if (u_abc <= 0.0f && w_cbd <= 0.0f && u_bc > 0.0f && v_bc > 0.0f) {
                    /* region BC */
                    simplex.a = simplex.b;
                    simplex.b = simplex.c;
                    simplex.bary[0] = u_bc;
                    simplex.bary[1] = v_bc;
                    simplex.count = 2;
                    break;
                }
                if (v_abc <= 0.0f && w_acd <= 0.0f && u_ca > 0.0f && v_ca > 0.0f) {
                    /* region CA */
                    simplex.b = simplex.a;
                    simplex.a = simplex.c;
                    simplex.bary[0] = u_ca;
                    simplex.bary[1] = v_ca;
                    simplex.count = 2;
                    break;
                }
                if (v_cbd <= 0.0f && u_acd <= 0.0f && u_dc > 0.0f && v_dc > 0.0f) {
                    /* region DC */
                    simplex.a = simplex.d;
                    simplex.b = simplex.c;
                    simplex.bary[0] = u_dc;
                    simplex.bary[1] = v_dc;
                    simplex.count = 2;
                    break;
                }
                if (v_acd <= 0.0f && w_adb <= 0.0f && u_ad > 0.0f && v_ad > 0.0f) {
                    /* region AD */
                    simplex.b = simplex.d;
                    simplex.bary[0] = u_ad;
                    simplex.bary[1] = v_ad;
                    simplex.count = 2;
                    break;
                }
                if (u_cbd <= 0.0f && u_adb <= 0.0f && u_bd > 0.0f && v_bd > 0.0f) {
                    /* region BD */
                    simplex.a = simplex.b;
                    simplex.b = simplex.d;
                    simplex.bary[0] = u_bd;
                    simplex.bary[1] = v_bd;
                    simplex.count = 2;
                    break;
                }
                /* calculate fractional volume (volume can be negative!) */
                float denom = box(cb, ab, db);
                float volume = (denom == 0) ? 1.0f : 1.0f/denom;
                float u_abcd = box(c, d, b) * volume;
                float v_abcd = box(c, a, d) * volume;
                float w_abcd = box(d, a, b) * volume;
                float x_abcd = box(b, a, c) * volume;

                /* check faces for closest point */
                if (x_abcd <= 0.0f && u_abc > 0.0f && v_abc > 0.0f && w_abc > 0.0f) {
                    /* region ABC */
                    simplex.bary[0] = u_abc;
                    simplex.bary[1] = v_abc;
                    simplex.bary[2] = w_abc;
                    simplex.count = 3;
                    break;
                }
                if (u_abcd <= 0.0f && u_cbd > 0.0f && v_cbd > 0.0f && w_cbd > 0.0f) {
                    /* region CBD */
                    simplex.a = simplex.c;
                    simplex.c = simplex.d;
                    simplex.bary[0] = u_cbd;
                    simplex.bary[1] = v_cbd;
                    simplex.bary[2] = w_cbd;
                    simplex.count = 3;
                    break;
                }
                if (v_abcd <= 0.0f && u_acd > 0.0f && v_acd > 0.0f && w_acd > 0.0f) {
                    /* region ACD */
                    simplex.b = simplex.c;
                    simplex.c = simplex.d;
                    simplex.bary[0] = u_acd;
                    simplex.bary[1] = v_acd;
                    simplex.bary[2] = w_acd;
                    simplex.count = 3;
                    break;
                }
                if (w_abcd <= 0.0f && u_adb > 0.0f && v_adb > 0.0f && w_adb > 0.0f) {
                    /* region ADB */
                    simplex.c = simplex.b;
                    simplex.b = simplex.d;
                    simplex.bary[0] = u_adb;
                    simplex.bary[1] = v_adb;
                    simplex.bary[2] = w_adb;
                    simplex.count = 3;
                    break;
                }
                /* region ABCD */
                assert(u_abcd > 0.0f && v_abcd > 0.0f && w_abcd > 0.0f && x_abcd > 0.0f);
                simplex.bary[0] = u_abcd;
                simplex.bary[1] = v_abcd;
                simplex.bary[2] = w_abcd;
                simplex.bary[3] = x_abcd;
                simplex.count = 4;
            } break;
        }

        /* V.) Check if origin is enclosed by tetrahedron */
        if (simplex.count == 4) {
            simplex.contact = true;
            return true;
        }
        /* VI.) Ensure closing in on origin to prevent multi-step cycling */
        Vector3f pnt = new Vector3f();
        float denom = 0;
        for (int i = 0; i < simplex.count; ++i)
            denom += simplex.bary[i];
        denom = 1.0f / denom;

        switch (simplex.count) {
            case 1 -> pnt = simplex.a.vec;
            case 2 -> {
                /* --------- Line -------- */
                var a = simplex.a.vec.multiply(denom * simplex.bary[0]);
                var b = simplex.b.vec.multiply(denom * simplex.bary[1]);
                pnt = a.add(b);
            }
            case 3 -> {
                /* ------- Triangle ------ */
                var a = simplex.a.vec.multiply(denom * simplex.bary[0]);
                var b = simplex.b.vec.multiply(denom * simplex.bary[1]);
                var c = simplex.c.vec.multiply(denom * simplex.bary[2]);

                pnt = a.add(b).add(c);
            }
            case 4 -> {
                /* ----- Tetrahedron ----- */
                var a = simplex.a.vec.multiply(denom * simplex.bary[0]);
                var b = simplex.b.vec.multiply(denom * simplex.bary[1]);
                var c = simplex.c.vec.multiply(denom * simplex.bary[2]);
                var d = simplex.d.vec.multiply(denom * simplex.bary[3]);

                pnt = a.add(b).add(c).add(d);
            }
        }
        if (pnt.lengthSquared() >= simplex.distance){
            //return true;
        }
        simplex.distance = pnt.lengthSquared();

        /* VII.) New search direction */
        switch (simplex.count) {
            case 1: {
                /* --------- Point -------- */
                simplex.searchDir = simplex.a.vec.inverse();
            } break;
            case 2: {
                /* ------ Line segment ---- */
                var ba = simplex.b.vec.subtract(simplex.a.vec);
                var b0 = simplex.b.vec.inverse();
                simplex.searchDir = ba.cross(b0).cross(ba);
            } break;
            case 3: {
                /* ------- Triangle ------- */
                var ab = simplex.b.vec.subtract(simplex.a.vec);
                var ac = simplex.c.vec.subtract(simplex.a.vec);
                var n = ab.cross(ac);
                if (n.dot(simplex.a.vec) <= 0.0f)
                    simplex.searchDir = n;
                else simplex.searchDir = n.inverse();
            }
        }
        if (simplex.searchDir.lengthSquared() < 0.000001f) {
            return true;
        }
        return false;
    }

    public static Tuple.OrderedTuple<Vector3f, Vector3f> getClosestPoints(Simplex s){
        Vector3f p1 = new Vector3f(), p2 = new Vector3f();
        
        /* calculate normalization denominator */
        float denom = 0;
        for (int i = 0; i < s.count; ++i)
            denom += s.bary[i];
        denom = 1.0f / denom;

        /* compute closest points */
        switch (s.count) {
            case 1: {
                /* Point */
                p1 = s.a.a;
                p2 = s.a.b;
            } break;
            case 2: {
                /* Line */
                float as = denom * s.bary[0];
                float bs = denom * s.bary[1];

                var a = s.a.a.multiply(as);
                var b = s.b.a.multiply(bs);
                var c = s.a.b.multiply(as);
                var d = s.b.b.multiply(bs);

                p1 = a.add(b);
                p2 = c.add(d);
            } break;
            case 3: {
                /* Triangle */
                float as = denom * s.bary[0];
                float bs = denom * s.bary[1];
                float cs = denom * s.bary[2];

                var a = s.a.a.multiply(as);
                var b = s.b.a.multiply(bs);
                var c = s.c.a.multiply(cs);

                var d = s.a.b.multiply(as);
                var e = s.b.b.multiply(bs);
                var f = s.c.b.multiply(cs);

                p1 = a.add(b).add(c);
                p2 = d.add(e).add(f);
            } break;
            case 4: {
                /* Tetrahedron */
                var a = s.a.a.multiply(denom * s.bary[0]);
                var b = s.b.a.multiply(denom * s.bary[1]);
                var c = s.c.a.multiply(denom * s.bary[2]);
                var d = s.d.a.multiply(denom * s.bary[3]);

                p1 = a.add(b).add(c).add(d);
                p2 = p1;
            } break;
        }
        return Tuple.of(p1,p2);
    }

    private static float box(Vector3f a, Vector3f b, Vector3f c) {
        var n = a.cross(b);
        return n.dot(c);
    }

    public static Optional<EPAResult> runEPA(List<Vector3f> v1, List<Vector3f> v2, Simplex simplex) {
        final float EXIT_THRESHOLD = 0.001f;
        final int EXIT_ITERATION_LIMIT = 50;
        List<MinkowskiTriangle> faces = new LinkedList<>();

        faces.add(new MinkowskiTriangle(simplex.a, simplex.b, simplex.c));
        faces.add(new MinkowskiTriangle(simplex.a, simplex.c, simplex.d));
        faces.add(new MinkowskiTriangle(simplex.a, simplex.d, simplex.b));
        faces.add(new MinkowskiTriangle(simplex.b, simplex.d, simplex.c));

        for(int currentIter = 0; currentIter < EXIT_ITERATION_LIMIT; currentIter++) {
            // find closest triangle to origin
            var face = findClosestFace(faces);
            var support = getSupport(face.y().n, v1, v2);
            var dist = support.vec.dot(face.y().n);

            if(face.y().n.dot(support.vec) - face.x() < 0.001) {
                return Optional.of(new EPAResult(dist, face.y()));
            }

            reconstruct(faces, support);
        }
        return Optional.empty();
    }

    private static void addEdge(MinkowskiSet a, MinkowskiSet b, List<MinkowskiEdge> edges) {
        for (MinkowskiEdge edge : edges) {
            if (edge.a.vec.equals(b.vec) && edge.b.vec.equals(a.vec)) {
                edges.remove(edge);
                return;
            }
        }
        edges.add(new MinkowskiEdge(a, b));
    }

    private static void reconstruct(List<MinkowskiTriangle> simplexFaces, MinkowskiSet support){
        List<MinkowskiEdge> edges = new LinkedList<>();

        Iterator<MinkowskiTriangle> iterator = simplexFaces.iterator();
        while (iterator.hasNext()) {
            MinkowskiTriangle t = iterator.next();
            if (t.n.dot(support.vec.subtract(t.a.vec)) > 0) {
                addEdge(t.a, t.b, edges);
                addEdge(t.b, t.c, edges);
                addEdge(t.c, t.a, edges);
                iterator.remove();
            }
        }

        // create new triangles from the edges in the edge list
        for (MinkowskiEdge edge : edges) {
            simplexFaces.add(new MinkowskiTriangle(support, edge.a, edge.b));
        }
    }

    private static Tuple.OrderedTuple<Float, MinkowskiTriangle> findClosestFace(List<MinkowskiTriangle> simplexFaces){
        float closest = Float.MAX_VALUE;
        MinkowskiTriangle closestTri = new MinkowskiTriangle(new MinkowskiSet(), new MinkowskiSet(), new MinkowskiSet());

        for(var face : simplexFaces) {
            var newDist = face.a.vec.dot(face.n);
            if(newDist < closest) {
                closest = newDist;
                closestTri = face;
            }
        }

        return Tuple.of(closest, closestTri);
    }

    private static class MinkowskiEdge {
        MinkowskiSet a;
        MinkowskiSet b;

        MinkowskiEdge(MinkowskiSet a, MinkowskiSet b) {
            this.a = a;
            this.b = b;
        }
    }

    public static class GJKResult{
        public final Simplex simplex;

        public GJKResult(Simplex simplex) {
            this.simplex = simplex;
        }
    }

    public static class EPAResult{
        public final float depth;
        public final MinkowskiTriangle contact;

        public EPAResult(float depth, MinkowskiTriangle contact) {
            this.depth = depth;
            this.contact = contact;
        }
    }

}
