package com.opengg.core.gui;

import com.opengg.core.console.GGConsole;
import com.opengg.core.engine.Resource;
import com.opengg.core.gui.text.UITextLine;
import com.opengg.core.math.Vector2f;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.render.texture.Texture;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Stack;

public class GUILoader {
    private static final XMLInputFactory factory = XMLInputFactory.newInstance();

    public static GUI loadGUI(File file) {
        GUI gui = new GUI();
        try (FileInputStream f = new FileInputStream(file)) {
            final XMLEventReader reader = factory.createXMLEventReader(f);
            //Move past xml decl
            reader.nextEvent();
            XMLEvent rootGUI = reader.nextEvent();
            String name = rootGUI.asStartElement().getAttributes().next().getValue();
            GGConsole.log("Loading GUI " + name);
            Stack<UIGroup> groups = new Stack<>();
            gui.getRoot().setName("root");
            groups.push(gui.getRoot());
            while (reader.hasNext()) {
                XMLEvent event = reader.nextEvent();
                String groupName = "";
                Vector2f position = new Vector2f();
                Vector2f size = new Vector2f();
                Texture tex = Resource.getTexture("default.png");
                Font font = Resource.getTruetypeFont("consolas.ttf");
                float layer = -0.5f;
                if (event.isStartElement()) {
                    Iterator<Attribute> attributes = event.asStartElement().getAttributes();
                    while (attributes.hasNext()) {
                        Attribute a = attributes.next();
                        switch (a.getName().toString()) {
                            case "name":
                                groupName = a.getValue();
                                break;
                            case "pos":
                                position = getVector2f(a);
                                break;
                            case "size":
                                size = getVector2f(a);
                                break;
                            case "tex":
                                tex = Resource.getTexture(a.getValue());
                                break;
                            case "font":
                                font = Resource.getTruetypeFont(a.getValue());
                                break;
                            case "layer":
                                layer = Float.parseFloat(a.getValue());
                                break;
                            default:
                                GGConsole.warning("Unknown parameter: " + a.getName().toString());
                                break;
                        }
                    }
                    switch (event.asStartElement().getName().toString()) {
                        case "group":
                            UIGroup group = new UIGroup();
                            group.setLayer(layer);
                            group.setPositionOffset(position);
                            group.setName(groupName);
                            groups.peek().addItem(groupName, group);
                            groups.push(group);
                            break;
                        case "ggbutton":
                            UIButton button = new UIButton();
                            button.setLayer(layer);
                            button.setPositionOffset(position);
                            button.setName(groupName);
                            groups.peek().addItem(groupName, button);
                            break;
                        case "ggtext":
                            String textData = reader.nextEvent().asCharacters().getData();
                            Text text1 = Text.from(textData).size(size.x).maxLineSize(size.y);
                            UITextLine text = new UITextLine(text1, font);
                            text.setLayer(layer);
                            text.setPositionOffset(position);
                            text.setName(groupName);
                            groups.peek().addItem(groupName, text);
                            break;
                        case "gtex":
                            UITexture texture = new UITexture(tex, size);
                            texture.setLayer(layer);
                            texture.setPositionOffset(position);
                            texture.setName(groupName);
                            groups.peek().addItem(groupName, texture);
                            break;
                            default:
                                GGConsole.warning("Unknown token: " + event.asStartElement().getName().toString());
                                break;
                    }
                } else if (event.isCharacters()) {

                } else if (event.isEndElement()) {
                    if (event.asEndElement().getName().getPrefix().equals("group")) {
                        groups.pop();
                    }
                } else {

                }
            }
        } catch (IOException e) {
            GGConsole.error("Could not find " + file.getAbsolutePath());
        } catch (XMLStreamException e) {
            GGConsole.error("Malformed GUI File");
            e.printStackTrace();
        }
        return gui;
    }


    public static Vector2f getVector2f(Attribute a) {
        String[] tokens = a.getValue().split(",");
        return new Vector2f(Float.parseFloat(tokens[0]), Float.parseFloat(tokens[1]));
    }

}
