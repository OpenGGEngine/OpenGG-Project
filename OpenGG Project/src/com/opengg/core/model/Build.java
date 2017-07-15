/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.model;

/**
 *
 * @author Warren
 */
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import java.util.*;
import static java.util.logging.Level.INFO;
import static java.util.logging.Level.SEVERE;
import java.util.logging.Logger;

public class Build implements BuilderInterface {

    private Logger log = Logger.getLogger(Build.class.getName());

    public String objFilename = null;
    // these accumulate each type of vertex as they are parsed, so they can then be referenced via index.
    public ArrayList<Vector3f> verticesG = new ArrayList<>();
    public ArrayList<Vector2f> verticesT = new ArrayList<>();
    public ArrayList<Vector3f> verticesN = new ArrayList<>();
    // we use this map to consolidate redundant face vertices.  Since a face is defined as a list of index 
    // triplets, each index referring to a vertex within ONE of the three arraylists verticesG,  verticesT
    // or verticesN, two faces might end up specifying the same combination.  Clearly (@TODO: really?) this 
    // combination should be shared between both faces. 
    HashMap<String, FaceVertex> faceVerticeMap = new HashMap<>();
    // Each face vertex as it is parsed, minus the redundant face vertices.  @TODO: Not used anywhere yet, maybe get rid of this.
    public ArrayList<FaceVertex> faceVerticeList = new ArrayList<>();
    public ArrayList<BuilderFace> faces = new ArrayList<>();
    public HashMap<Integer, ArrayList<BuilderFace>> smoothingGroups = new HashMap<>();
    private int currentSmoothingGroupNumber = NO_SMOOTHING_GROUP;
    private ArrayList<BuilderFace> currentSmoothingGroup = null;
    public HashMap<String, ArrayList<BuilderFace>> groups = new HashMap<>();
    private ArrayList<String> currentGroups = new ArrayList<>();
    private ArrayList<ArrayList<BuilderFace>> currentGroupFaceLists = new ArrayList<>();
    public String objectName = null;
    private Material currentMaterial = null;
    private Material currentMap = null;
    public HashMap<String, Material> materialLib = new HashMap<>();
    private Material currentMaterialBeingParsed = null;
    public HashMap<String, Material> mapLib = new HashMap<>();
    private Material currentMapBeingParsed = null;
    public int faceTriCount = 0;
    public int faceQuadCount = 0;
    public int facePolyCount = 0;
    public int faceErrorCount = 0;

    public Build() {
    }

    public void setObjFilename(String filename) {
        this.objFilename = filename;
    }

    public void addVertexGeometric(float x, float y, float z) {
        verticesG.add(new Vector3f(x, y, z));
//        log.log(INFO,"Added geometric vertex " + verticesG.size() + " = " + verticesG.get(verticesG.size() - 1));
    }

    public void addVertexTexture(float u, float v) {
        verticesT.add(new Vector2f(u, v));
//        log.log(INFO,"Added texture  vertex " + verticesT.size() + " = " + verticesT.get(verticesT.size() - 1));
    }

    @Override
    public void addVertexNormal(float x, float y, float z) {
        verticesN.add(new Vector3f(x, y, z));
    }

    @Override
    public void addPoints(int[] values) {
        log.log(INFO, "@TODO: Got {0} points in builder, ignoring", values.length);
    }

    @Override
    public void addLine(int[] values) {
        log.log(INFO, "@TODO: Got a line of {0} segments in builder, ignoring", values.length);
    }

    @Override
    public void addFace(int[] vertexIndices) {
        BuilderFace face = new BuilderFace();

        face.material = currentMaterial;
        face.map = currentMap;

        int loopi = 0;
        // @TODO: add better error checking - make sure values is not empty and that it is a multiple of 3
        while (loopi < vertexIndices.length) {
            // >     v is the vertex reference number for a point element. Each point
            // >     element requires one vertex. Positive values indicate absolute
            // >     vertex numbers. Negative values indicate relative vertex numbers.

            FaceVertex fv = new FaceVertex();
//            log.log(INFO,"Adding vertex g=" + vertexIndices[loopi] + " t=" + vertexIndices[loopi + 1] + " n=" + vertexIndices[loopi + 2]);
            int vertexIndex;
            vertexIndex = vertexIndices[loopi++];
            // Note that we can use negative references to denote vertices in manner relative to the current point in the file, i.e.
            // rather than "the 5th vertice in the file" we can say "the 5th vertice before now"
            if (vertexIndex < 0) {
                vertexIndex = vertexIndex + verticesG.size();
            }
            if (((vertexIndex - 1) >= 0) && ((vertexIndex - 1) < verticesG.size())) {
                // Note: vertex indices are 1-indexed, i.e. they start at
                // one, so we offset by -1 for the 0-indexed array lists.
                fv.v = verticesG.get(vertexIndex - 1);
            } else {
                log.log(SEVERE, "Index for geometric vertex=" + vertexIndex + " is out of the current range of geometric vertex values 1 to " + verticesG.size() + ", ignoring");
            }

            vertexIndex = vertexIndices[loopi++];
            if (vertexIndex != EMPTY_VERTEX_VALUE) {
                if (vertexIndex < 0) {
                    // Note that we can use negative references to denote vertices in manner relative to the current point in the file, i.e.
                    // rather than "the 5th vertice in the file" we can say "the 5th vertice before now"
                    vertexIndex = vertexIndex + verticesT.size();
                }
                if (((vertexIndex - 1) >= 0) && ((vertexIndex - 1) < verticesT.size())) {
                    // Note: vertex indices are 1-indexed, i.e. they start at
                    // one, so we offset by -1 for the 0-indexed array lists.
                    fv.t = verticesT.get(vertexIndex - 1);
                } else {
                    log.log(SEVERE, "Index for texture vertex=" + vertexIndex + " is out of the current range of texture vertex values 1 to " + verticesT.size() + ", ignoring");
                }
            }

            vertexIndex = vertexIndices[loopi++];
            if (vertexIndex != EMPTY_VERTEX_VALUE) {
                if (vertexIndex < 0) {
                    // Note that we can use negative references to denote vertices in manner relative to the current point in the file, i.e.
                    // rather than "the 5th vertice in the file" we can say "the 5th vertice before now"
                    vertexIndex = vertexIndex + verticesN.size();
                }
                if (((vertexIndex - 1) >= 0) && ((vertexIndex - 1) < verticesN.size())) {
                    // Note: vertex indices are 1-indexed, i.e. they start at
                    // one, so we offset by -1 for the 0-indexed array lists.
                    fv.n = verticesN.get(vertexIndex - 1);
                } else {
                    log.log(SEVERE, "Index for vertex normal=" + vertexIndex + " is out of the current range of vertex normal values 1 to " + verticesN.size() + ", ignoring");
                }
            }

            if (fv.v == null) {
                log.log(SEVERE, "Can't add vertex to face with missing vertex!  Throwing away face.");
                faceErrorCount++;
                return;
            }

            // Make sure we don't end up with redundant vertice
            // combinations - i.e. any specific combination of g,v and
            // t is only stored once and is reused instead.
            String key = fv.toString();
            FaceVertex fv2 = faceVerticeMap.get(key);
            if (null == fv2) {
                faceVerticeMap.put(key, fv);
                fv.index = faceVerticeList.size();
                faceVerticeList.add(fv);
            } else {
                fv = fv2;
            }

            face.add(fv);
        }
//        log.log(INFO,"Parsed face=" + face);
        if (currentSmoothingGroup != null) {
            currentSmoothingGroup.add(face);
        }

        if (currentGroupFaceLists.size() > 0) {
            for (loopi = 0; loopi < currentGroupFaceLists.size(); loopi++) {
                currentGroupFaceLists.get(loopi).add(face);
            }
        }

        faces.add(face);

        // collect some stats for laughs
        if (face.vertices.size() == 3) {
            faceTriCount++;
        } else if (face.vertices.size() == 4) {
            faceQuadCount++;
        } else {
            facePolyCount++;
        }
    }

    public void setCurrentGroupNames(String[] names) {
        currentGroups.clear();
        currentGroupFaceLists.clear();
        if (null == names) {
            // Set current group to 'none' - so since we've already
            // cleared the currentGroups lists, just return.
            return;
        }
        for (int loopi = 0; loopi < names.length; loopi++) {
            String group = names[loopi].trim();
            currentGroups.add(group);
            if (null == groups.get(group)) {
                groups.put(group, new ArrayList<BuilderFace>());
            }
            currentGroupFaceLists.add(groups.get(group));
        }
    }

    public void addObjectName(String name) {
        this.objectName = name;
    }

    public void setCurrentSmoothingGroup(int groupNumber) {
        currentSmoothingGroupNumber = groupNumber;
        if (currentSmoothingGroupNumber == NO_SMOOTHING_GROUP) {
            return;
        }
        if (null == smoothingGroups.get(currentSmoothingGroupNumber)) {
            currentSmoothingGroup = new ArrayList<BuilderFace>();
            smoothingGroups.put(currentSmoothingGroupNumber, currentSmoothingGroup);
        }
    }

    // @TODO:
    // 
    // > maplib filename1 filename2 . . .
    // > 
    // >     This is a rendering identifier that specifies the map library file
    // >     for the texture map definitions set with the usemap identifier. You
    // >     can specify multiple filenames with maplib. If multiple filenames
    // >     are specified, the first file listed is searched first for the map
    // >     definition, the second file is searched next, and so on.
    // > 
    // >     When you assign a map library using the Model program, Model allows
    // >     only one map library per .obj file. You can assign multiple
    // >     libraries using a text editor.
    // > 
    // >     filename is the name of the library file where the texture maps are
    // >     defined. There is no default.
    public void addMapLib(String[] names) {
        if (null == names) {
            log.log(INFO, "@TODO: ERROR! Got a maplib line with null names array - blank group line? (i.e. \"g\\n\" ?)");
            return;
        }
        if (names.length == 1) {
            log.log(INFO, "@TODO: Got a maplib line with one name=|{0}|", names[0]);
            return;
        }
        for (int loopi = 0; loopi < names.length; loopi++) {
        }
    }

    // @TODO:
    // 
    // > usemap map_name/off
    // > 
    // >     This is a rendering identifier that specifies the texture map name
    // >     for the element following it. To turn off texture mapping, specify
    // >     off instead of the map name.
    // > 
    // >     If you specify texture mapping for a face without texture vertices,
    // >     the texture map will be ignored.
    // > 
    // >     map_name is the name of the texture map.
    // > 
    // >     off turns off texture mapping. The default is off.
    public void setCurrentUseMap(String name) {
        currentMap = mapLib.get(name);
    }

    // > usemtl material_name
    // > 
    // >     Polygonal and free-form geometry statement.
    // > 
    // >     Specifies the material name for the element following it. Once a
    // >     material is assigned, it cannot be turned off; it can only be
    // >     changed.
    // > 
    // >     material_name is the name of the material. If a material name is
    // >     not specified, a white material is used.
    public void setCurrentUseMaterial(String name) {
        currentMaterial = materialLib.get(name);
    }

    // > mtllib filename1 filename2 . . .
    // > 
    // >     Polygonal and free-form geometry statement.
    // > 
    // >     Specifies the material library file for the material definitions
    // >     set with the usemtl statement. You can specify multiple filenames
    // >     with mtllib. If multiple filenames are specified, the first file
    // >     listed is searched first for the material definition, the second
    // >     file is searched next, and so on.
    // > 
    // >     When you assign a material library using the Model program, only
    // >     one map library per .obj file is allowed. You can assign multiple
    // >     libraries using a text editor.
    // > 
    // >     filename is the name of the library file that defines the
    // >     materials.  There is no default.
    // @TODO: I think I need to just delete this... because we now parse material lib files in Parse.java in processMaterialLib()
//    public void addMaterialLib(String[] names) {
//        if (null == names) {
//            log.log(INFO,"@TODO: Got a mtllib line with null names array - blank group line? (i.e. \"g\\n\" ?)");
//            return;
//        }
//        if (names.length == 1) {
//            log.log(INFO,"@TODO: Got a mtllib line with one name=|" + names[0] + "|");
//            return;
//        }
//        log.log(INFO,"@TODO: Got a mtllib line;");
//        for (int loopi = 0; loopi < names.length; loopi++) {
//            log.log(INFO,"        names[" + loopi + "] = |" + names[loopi] + "|");
//        }
//    }
    public void newMtl(String name) {
        currentMaterialBeingParsed = new Material(name);
        materialLib.put(name, currentMaterialBeingParsed);
    }

    public void setXYZ(int type, float x, float y, float z) {
        Vector3f rt = currentMaterialBeingParsed.ka;
        if (type == MTL_KD) {
            rt = currentMaterialBeingParsed.kd;
        } else if (type == MTL_KS) {
            rt = currentMaterialBeingParsed.ks;
        } else if (type == MTL_TF) {
            rt = currentMaterialBeingParsed.tf;
        }

        rt.x = x;
        rt.y = y;
        rt.z = z;
    }

    public void setRGB(int type, float r, float g, float b) {
        Vector3f rt = currentMaterialBeingParsed.ka;
        if (type == MTL_KD) {
            rt = currentMaterialBeingParsed.kd;
        } else if (type == MTL_KS) {
            rt = currentMaterialBeingParsed.ks;
        } else if (type == MTL_TF) {
            rt = currentMaterialBeingParsed.tf;
        }

        rt.x = r;
        rt.y = g;
        rt.z = b;
    }

    public void setIllum(int illumModel) {
        currentMaterialBeingParsed.illumModel = illumModel;
    }

    public void setD(boolean halo, float factor) {
        currentMaterialBeingParsed.dHalo = halo;
        currentMaterialBeingParsed.dFactor = factor;
    }

    public void setNs(float exponent) {
        currentMaterialBeingParsed.nsExponent = exponent;
    }

    public void setSharpness(float value) {
        currentMaterialBeingParsed.sharpnessValue = value;
    }

    public void setNi(float opticalDensity) {
        currentMaterialBeingParsed.niOpticalDensity = opticalDensity;
    }

    public void setMapDecalDispBump(int type, String filename) {
        if (type == MTL_MAP_KA) {
            currentMaterialBeingParsed.mapKaFilename = filename;
        } else if (type == MTL_MAP_KD) {
            currentMaterialBeingParsed.mapKdFilename = filename;
        } else if (type == MTL_MAP_KS) {
            currentMaterialBeingParsed.mapKsFilename = filename;
        } else if (type == MTL_MAP_NS) {
            currentMaterialBeingParsed.mapNsFilename = filename;
        } else if (type == MTL_MAP_D) {
            currentMaterialBeingParsed.mapDFilename = filename;
        } else if (type == MTL_DECAL) {
            currentMaterialBeingParsed.decalFilename = filename;
        } else if (type == MTL_DISP) {
            currentMaterialBeingParsed.dispFilename = filename;
        } else if (type == MTL_BUMP) {
            currentMaterialBeingParsed.bumpFilename = filename;
        }
    }

    public void setRefl(int type, String filename) {
        currentMaterialBeingParsed.reflType = type;
        currentMaterialBeingParsed.reflFilename = filename;
    }

    @Override
    public void doneParsingMaterial() {
        //  if we finish a .mtl file, and then we parse another mtllib (.mtl) file AND that other 
        // file is malformed, and missing the newmtl line, then any statements would quietly 
        // overwrite whatever is being pointed to by currentMaterialBeingParsed.  Hence we set 
        // it to null now.   (Now any such malformed .mtl file will cause an exception but that's 
        // better than quiet bugs.)   This method ( doneParsingMaterial() ) is called by Parse when 
        // it finished parsing a .mtl file.
        //
        // @TODO: We can make this not throw an exception if we simply add a check for a null 
        // currentMaterialBeingParsed at the start of each material setter method in Build... but that
        // still assumes we'll always have a newmtl line FIRST THING for each material, to create the 
        // currentMaterialBeingParsed object.  Is that a reasonable assumption?
        currentMaterialBeingParsed = null;
    }

    @Override
    public void doneParsingObj(String filename) {
        log.log(INFO, "Loaded filename ''{0}'' with {1} verticesG, {2} verticesT, {3} verticesN and {4} faces, of which {5} triangles, {6} quads, and {7} with more than 4 points, and faces with errors {8}", new Object[]{filename, verticesG.size(), verticesT.size(), verticesN.size(), faces.size(), faceTriCount, faceQuadCount, facePolyCount, faceErrorCount});
    }
    
    public String getObjectFileName(){
        String name = objFilename.substring(objFilename.lastIndexOf("\\")+2);
        return name;
    }
    
    public String getObjectName(){
        String fname = getObjectFileName();
        int fp = fname.lastIndexOf(".");
        String name = fname.substring(0, fp);
        return name;
    }
}
