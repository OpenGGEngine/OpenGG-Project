/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.world;

import com.opengg.core.console.GGConsole;
import com.opengg.core.util.GGOutputStream;
import com.opengg.core.world.components.Component;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Javier
 */
public class Serializer {
    public static byte[] serializeSingleComponent(Component component) throws IOException {
        var stream = new GGOutputStream();
        stream.write(component.getClass().getName());
        serializeComponent(stream, component);
        return stream.asByteArray();
    }

    public static byte[] serializeComponentTree(Component head) throws IOException {
        return serializeComponentTree(head, null);
    }

    public static byte[] serializeComponentTree(Component head, Map<String, Integer> names) throws IOException {
        var out = new GGOutputStream();

        if(names == null)
            out.write(head.getClass().getName());
        else
            out.write(names.get(head.getClass().getName()));

        serializeComponent(out, head);

        out.write(getAllSerializable(head.getChildren()));
        for(Component component : head.getChildren()){
            if(component.shouldSerialize()){
                out.write(serializeComponentTree(component, names));
            }
        }

        return out.asByteArray();
    }

    public static byte[] serializeWorld(World world) throws IOException {
        var allSerializableComponentTypes = Stream.concat(world.getAllDescendants().stream(), List.of(world).stream())
                .filter(Component::shouldSerialize)
                .map(comp -> comp.getClass().getName())
                .distinct()
                .collect(Collectors.toList());

        var classnames = new HashMap<String, Integer>();
        for(int i = 0; i < allSerializableComponentTypes.size(); i++){
            classnames.put(allSerializableComponentTypes.get(i), i);
        }

        var stream = new GGOutputStream();

        stream.write(classnames.size());

        for(var entry : classnames.entrySet()){
            stream.write(entry.getValue());
            stream.write(entry.getKey());
        }

        stream.write(serializeComponentTree(world, classnames));
        return stream.asByteArray();
    }

    private static void serializeComponent(GGOutputStream stream, Component component) throws IOException {
        stream.write(component.getGUID());
        if(component.getParent() == null)
            stream.write((long)-1);
        else
            stream.write(component.getParent().getGUID());

        var substream = new GGOutputStream();
        component.serialize(substream);

        stream.write(substream.asByteArray().length);
        stream.write(substream.asByteArray());
    }

    private static int getAllSerializable(List<Component> components){
        int i = 0;
        for(Component c : components)
            if(c.shouldSerialize())
                i++;
        return i;
    }
}
