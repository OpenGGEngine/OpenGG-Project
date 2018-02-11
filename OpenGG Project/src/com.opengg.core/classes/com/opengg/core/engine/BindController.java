/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.io.Bind;
import com.opengg.core.io.ControlType;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.world.Action;
import com.opengg.core.world.ActionTransmitter;
import com.opengg.core.world.ActionType;
import java.util.ArrayList;
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

    private BindController(){
        KeyboardController.addToPool(this);
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
    public static void addController(ActionTransmitter controller){
        controllers.add(controller);
    }
    
    /**
     * Clears all previous {@link ActionTransmitter ActionTransmitters}, and adds the given one to the list
     * @param controller Transmitter to use as unique transmitter
     */
    public static void setOnlyController(ActionTransmitter controller){
        controllers.clear();
        controllers.add(controller);
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
        for(Bind bind : binds){
            System.out.println(bind.action + ", " + bind.type + ", " + bind.button);
        }
    }
    
    @Override
    public void keyPressed(int key) {
        for(Bind bind : binds){    
            if(bind.button == key && bind.type == ControlType.KEYBOARD){
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
    public void keyReleased(int key) {
        for(Bind bind : binds){
            if(bind.button == key && bind.type == ControlType.KEYBOARD){
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
    public void buttonPressed(int button) {
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
    public void buttonReleased(int button) {
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
}
