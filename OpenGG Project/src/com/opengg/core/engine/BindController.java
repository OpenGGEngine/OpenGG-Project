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
import com.opengg.core.world.ActionType;
import com.opengg.core.world.components.UserControlComponent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Javier
 */
public class BindController implements KeyboardListener, MouseButtonListener{
    private static BindController bc;
    static List<UserControlComponent> controllers = new ArrayList<>();
    static List<Bind> binds = new ArrayList<>();

    private BindController(){
        KeyboardController.addToPool(this);
    }
    
    public static void initialize(){
        bc = new BindController();
    }
    
    public static void addBind(ControlType type, String action, int key){
        binds.add(new Bind(type, action, key));
    }
    
    public static void addBind(Bind b){
        binds.add(b);
    }
    
    public static void addController(UserControlComponent controller){
        controllers.add(controller);
    }
    
    @Override
    public void keyPressed(int key) {
        for(Bind bind : binds){    
            if(bind.button == key && bind.type == ControlType.KEYBOARD){
                for(UserControlComponent c : controllers){
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
                for(UserControlComponent c : controllers){
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
                for(UserControlComponent c : controllers){
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
                for(UserControlComponent c : controllers){
                    Action a = new Action();
                    a.name = bind.action;
                    a.type = ActionType.RELEASE;
                    c.doAction(a);
                }
            }
        }
    }
}
