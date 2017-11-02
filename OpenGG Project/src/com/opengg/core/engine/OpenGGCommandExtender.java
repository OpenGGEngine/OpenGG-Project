/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import static com.opengg.core.engine.OpenGG.*;

/**
 *
 * @author Javier
 */
public class OpenGGCommandExtender implements ConsoleListener{
    @Override
    public void onConsoleInput(UserCommand command) {
        if(command.command.equalsIgnoreCase("quit")){
            endApplication();
        }
        
        if(command.command.equalsIgnoreCase("fquit")){
            forceEnd();
        }
        
        if(command.command.equalsIgnoreCase("vol") || command.command.equalsIgnoreCase("volume")){
            if(command.argCount == 1){
                try{
                    float vol = Float.parseFloat(command.args[0]);
                    AudioController.setGlobalGain(vol);
                }catch(Exception e){
                    GGConsole.error(command.args[0] + " is not a valid volume!");
                }
            }
        }
        
        if(command.command.equalsIgnoreCase("world")){
            if(command.argCount == 1){
                if(command.args[0].equalsIgnoreCase("print_layout")){
                    WorldEngine.getCurrent().printLayout();
                }
            }
        }
        
        if(command.command.equalsIgnoreCase("snd") || command.command.equalsIgnoreCase("sound")){
            if(command.argCount == 1){
                if(command.args[0].equalsIgnoreCase("restart")){
                    AudioController.restart();
                }
            }
        }
        
        if(command.command.equalsIgnoreCase("bind")){
            if(command.argCount == 1){
                if(command.args[0].equalsIgnoreCase("list")){
                    BindController.printBinds();
                }
            }
        }
    }
}
