/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.states;

import com.opengg.core.render.window.GLFWWindow;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class StateMachine {
    //public List<State> states = new ArrayList<>();
    State defaultstate;
    State currentstate = defaultstate;
    public void setState(State s){
        currentstate.leave(null);
        currentstate = s;
        currentstate.enter(null);
    }
    public void loop(){
        //currentstate.update(new GLFWWindow(), 2);
        //currentstate.render(new GLFWWindow(), 2);
        
        
    }
    public void initState() throws IOException{
        currentstate.init(null);
        
    }
    
    
    
}
