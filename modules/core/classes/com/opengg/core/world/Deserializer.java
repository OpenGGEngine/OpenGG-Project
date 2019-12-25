/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.exceptions.ClassInstantiationException;
import com.opengg.core.util.ClassUtil;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.world.components.Component;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

/**
 *
 * @author Javier
 */
public class Deserializer {

    public static SerialHolder deserializeSingleComponent(GGInputStream in) throws IOException {
        String classname = in.readString();

        return deserializeComponentWithParent(in, classname);
    }

    public static Component deserializeComponentTree(GGInputStream in) throws IOException {
        return deserializeComponentTree(in, null);
    }

    public static Component deserializeComponentTree(GGInputStream in, Map<Integer, String> names) throws IOException {
        String name;
        if (names == null)
            name = in.readString();
        else
            name = names.get(in.readInt());

        var comp = deserializeComponent(in, name);

        var children = in.readInt();
        for(int i = 0; i < children; i++){
            comp.attach(deserializeComponentTree(in, names));
        }

        return comp;
    }

    public static World deserializeWorld(ByteBuffer data){
        try {
            var in = new GGInputStream(data);
            var classnames = new HashMap<Integer, String>();

            int namecount = in.readInt();
            for(int i = 0; i < namecount; i++){
                var id = in.readInt();
                var name = in.readString();
                classnames.put(id, name);
            }

            var world = deserializeComponentTree(in, classnames);

            world.getAllDescendants().forEach(Component::onWorldLoad);

            return (World) world;
        } catch (IOException ex) {
            GGConsole.error("IOException thrown during deserialization of world!");
            throw new RuntimeException(ex);
        }
    }

    private static Component deserializeComponent(GGInputStream in, String classname){
        return deserializeComponentWithParent(in, classname).comp;
    }

    private static SerialHolder deserializeComponentWithParent(GGInputStream in, String classname){
        try {
            long id = in.readLong();
            long parentId = in.readLong();
            Component comp =  (Component)ClassUtil.createByName(classname);
            comp.setGUID(id);
            for(int i = comp.getChildren().size(); i > 0; i--){
                comp.getChildren().get(0).delete();
            }

            int len = in.readInt();
            var serializedData = in.readByteArray(len);
            comp.deserialize(new GGInputStream(serializedData));

            return new SerialHolder(comp, parentId);
        }  catch (ClassInstantiationException | IOException ex) {
            GGConsole.error("Failed to instantiate class " + classname + ": " + ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

    public static class SerialHolder {
        public Component comp;
        public long parent;

        public SerialHolder(Component comp, long parent) {
            this.comp = comp;
            this.parent = parent;
        }
    }
}
