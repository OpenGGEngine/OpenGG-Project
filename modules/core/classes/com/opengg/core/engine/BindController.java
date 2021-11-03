/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.console.GGConsole;
import com.opengg.core.io.Bind;
import com.opengg.core.io.ControlType;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.*;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.ActionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Controller for the bind system<br>chr
 * The bind system converts the specific commands from a keyboard, mouse, or other input devices to
 * {@link Action Actions}, which is sent to {@link ActionTransmitter ActionTransmitters} that are added to this controller.
 * This allows for configurations to be easily saved, by simply connecting a key to a command purely through Strings
 * @author Javier
 */
public class BindController implements KeyboardListener, MouseButtonListener, MouseScrollChangeListener {
    private static BindController bc;
    static List<ActionTransmitter> controllers = new ArrayList<>();
    static List<Bind> binds = new ArrayList<>();
    static HashSet<String> current = new HashSet<>();

    private static boolean enabled = true;

    private BindController(){
        MouseController.onButtonPress(this);
        MouseController.addScrollChangeListener(this);
        KeyboardController.addKeyboardListener(this);
    }
    
    public static void initialize(){
        bc = new BindController();
        try {
            var newline = System.getProperty("line.separator");
            Files.lines(Resource.getAbsoluteFromLocal("config/controls.cfg"))
                    .forEach(s -> {
                        if(!s.equals(newline)) {
                            var arr = s.split("\\|");
                            addBind(ControlType.valueOf(arr[0]), arr[1], Integer.parseInt(arr[2]));
                        }
                    });
        } catch (IOException e) {
            GGConsole.error("Failed to load controls from file");
        }
    }
    
    /**
     * Adds a {@link com.opengg.core.io.Bind} to the system, using the given information
     * @param type Type of control to use, which can be either KEYBOARD, MOUSE, and JOYSTICK
     * @param action Case sensitive name of action to be linked to key
     * @param key Key ID of the key to link, which depends on the input type
     */
    public static void addBind(ControlType type, String action, int key){
        binds.add(new Bind(type, action, key));
    }
    
    /**
     * Adds the given {@link com.opengg.core.io.Bind} to the system or updates the existing bind if action already exists
     * @param b Premate Bind to be added
     */
    public static void addBind(Bind b){
        for (Bind bind: binds) {
            if (bind.action.equals(b.action)) {
                bind.button = b.button;
                bind.type = b.type;
                return;
            }
        }
        binds.add(b);
    }
    
    /**
     * Adds an {@link ActionTransmitter} to the system, which will receive all future commands 
     * @param controller ActionTransmitter to be added
     */
    public static void addTransmitter(ActionTransmitter controller) {
        if(controllers.contains(controller)) return;
        startAllActionsFor(controller);
        controllers.add(controller);
    }

    /**
     * Removes a {@link ActionTransmitter} from the system
     * @param controller ActionTransmitter to be removed
     */
    public static void removeController(ActionTransmitter controller) {
        if(!controllers.contains(controller)) return;
        endAllActionsFor(controller);
        controllers.remove(controller);
    }

    public static void startAllActionsFor(ActionTransmitter controller){
        for(var key : current){
            Action a = new Action();
            a.name = key;
            a.type = ActionType.PRESS;
            controller.doAction(a);
        }
    }

    public static void endAllActionsFor(ActionTransmitter controller){
        for(var key : current){
            Action a = new Action();
            a.name = key;
            a.type = ActionType.RELEASE;
            controller.doAction(a);
        }
    }

    public static boolean contains(ActionTransmitter controller){
        return controllers.contains(controller);
    }
    
    /**
     * Clears all transmitters from the system;
     */
    public static void clearControllers(){
        controllers.clear();
    }

    /**
     * Clears all binds from the system;
     */
    public static void clearBinds(){
        binds.clear();
    }

    /**
     * Gets all {@link ActionTransmitter ActionTransmit}
     * @return 
     */
    public static List<ActionTransmitter> getBindControllers(){
        return controllers;
    }
    
    /**
     * Prints all current binds to the default PrintStream
     */
    public static void printBinds(){

    }

    public static void saveBinds(){
        var path = Resource.getAbsoluteFromLocal("config/controls.cfg");
        try (var writer = Files.newBufferedWriter(path)){
            binds.stream()
                    .forEach(bind -> {
                        try {
                            writer.write(bind.type.name() + "|" + bind.action + "|" + bind.button);
                            writer.newLine();
                        } catch (IOException e) {
                            GGConsole.error("Failed to write to controls file");
                        }
                    });

        } catch (IOException e) {
            GGConsole.error("Failed to open writer for controls file");
        }
    }

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        BindController.enabled = enabled;
    }

    @Override
    public void keyPressed(int key) {
        if(act()) return;
        for(Bind bind : binds){    
            if(bind.button == key && bind.type == ControlType.KEYBOARD){
                current.add(bind.action);
                for(ActionTransmitter c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.PRESS;
                    OpenGG.asyncExec(() -> c.doAction(a));
                    //c.doAction(a);
                }
            }
        }
    }

    @Override
    public void keyReleased(int key) {
        if(act()) return;
        for(Bind bind : binds){
            if(bind.button == key && bind.type == ControlType.KEYBOARD){
                current.remove(bind.action);
                for(ActionTransmitter c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.RELEASE;
                    c.doAction(a);
                }
            }
        }
    }

    @Override
    public void onButtonPress(int button){
        if(act()) return;
        for(Bind bind : binds){
            if(bind.button == button && bind.type == ControlType.MOUSEBUTTON){
                for(ActionTransmitter c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.PRESS;
                    c.doAction(a);
                }
            }
        }
    }

    @Override
    public void onButtonRelease(int button){
        if(act()) return;
        for(Bind bind : binds){
            if(bind.button == button && bind.type == ControlType.MOUSEBUTTON){
                for(ActionTransmitter c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.RELEASE;
                    c.doAction(a);
                }
            }
        }
    }

    private boolean act(){
        return !enabled || GGInfo.isMenu();
    }

    @Override
    public void onScrollUp() {
        if(act()) return;
        for(Bind bind : binds){
            if(bind.button == MouseButton.SCROLL_UP && bind.type == ControlType.SCROLLWHEEL){
                for(ActionTransmitter c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.PRESS;
                    c.doAction(a);
                }
            }
        }
    }

    @Override
    public void onScrollDown() {
        if(act()) return;
        for(Bind bind : binds){
            if(bind.button == MouseButton.SCROLL_DOWN && bind.type == ControlType.SCROLLWHEEL){
                for(ActionTransmitter c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.PRESS;
                    c.doAction(a);
                }
            }
        }
    }
}
