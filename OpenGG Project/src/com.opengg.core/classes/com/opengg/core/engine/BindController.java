/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.GGInfo;
import com.opengg.core.io.Bind;
import com.opengg.core.io.ControlType;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.ActionType;
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
public class BindController implements KeyboardListener, MouseButtonListener{
    private static BindController bc;
    static List<ActionTransmitter> controllers = new ArrayList<>();
    static List<Bind> binds = new ArrayList<>();
    static HashSet<String> current = new HashSet<>();

    private static boolean enabled = true;

    private BindController(){
        KeyboardController.addKeyboardListener(this);
    }
    
    public static void initialize(){
        bc = new BindController();
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
     * Adds the given {@link com.opengg.core.io.Bind} to the system
     * @param b Premate Bind to be added
     */
    public static void addBind(Bind b){
        binds.add(b);
    }
    
    /**
     * Adds an {@link ActionTransmitter} to the system, which will receive all future commands 
     * @param controller ActionTransmitter to be added
     */
    public static void addController(ActionTransmitter controller) {
        if(controllers.contains(controller)) return;
        for(var key : current){
            Action a = new Action();
            a.name = key;
            a.type = ActionType.PRESS;
            controller.doAction(a);
        }
        controllers.add(controller);

    }

    /**
     * Removes a {@link ActionTransmitter} from the system
     * @param controller ActionTransmitter to be removed
     */
    public static void removeController(ActionTransmitter controller) {
        if(!controllers.contains(controller)) return;
        for(var key : current){
            Action a = new Action();
            a.name = key;
            a.type = ActionType.RELEASE;
            controller.doAction(a);
        }
        controllers.remove(controller);
    }
    
    /**
     * Clears all transmitters from the system;
     */
    public static void clearControllers(){
        controllers.clear();
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

    public static boolean isEnabled() {
        return enabled;
    }

    public static void setEnabled(boolean enabled) {
        BindController.enabled = enabled;
    }

    @Override
    public void keyPressed(int key) {
        if(!act()) return;
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
        if(!act()) return;
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
        if(!act()) return;
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
        if(!act()) return;
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
        return enabled && !GGInfo.isMenu();
    }
}
