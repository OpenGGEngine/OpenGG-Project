/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.Configuration;
import com.opengg.core.audio.AudioController;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.console.ConsoleListener;
import com.opengg.core.console.GGConsole;
import com.opengg.core.console.UserCommand;
import static com.opengg.core.engine.OpenGG.*;

import com.opengg.core.model.ModelManager;
import com.opengg.core.physics.PhysicsRenderer;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.render.postprocess.PostProcessController;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.world.WorldEngine;

/**
 *
 * @author Javier
 */
public class OpenGGCommandExtender implements ConsoleListener{
    @Override
    public void onConsoleInput(UserCommand command) {
        switch(command.command){
            case "quit":
                endApplication();
                break;

            case "fquit":
                forceEnd();
                break;

            case "ping":
                GGConsole.log("Pong!");
                break;

            case "volume":
                if(command.argCount == 1){
                    try{
                        float vol = Float.parseFloat(command.args[0]);
                        AudioController.setGlobalGain(vol);
                        GGConsole.log("Set volume to " + vol);
                    }catch(Exception e){
                        GGConsole.error(command.args[0] + " is not a valid volume!");
                    }
                }
                break;

            case "world":
                if(command.argCount == 1){
                    if(command.args[0].equalsIgnoreCase("print-layout")){
                        WorldEngine.getCurrent().printLayout();
                    }
                }
                break;

            case "snd":
                if(command.argCount == 1){
                    if(command.args[0].equalsIgnoreCase("restart")){
                        AudioController.restart();
                    }else if(command.args[0].equalsIgnoreCase("next-track")){
                        SoundtrackHandler.getCurrent().next();
                        GGConsole.log("Playing next track");
                    }
                }
                break;

            case "bind":
                if(command.argCount == 1){
                    if(command.args[0].equalsIgnoreCase("list")){
                        BindController.printBinds();
                    }
                }
                break;

            case "model":
                if(command.argCount == 1){
                    if(command.args[0].equalsIgnoreCase("clear-cache")){
                        ModelManager.getModelList();
                    }
                }if(command.argCount == 2){
                if(command.args[0].equalsIgnoreCase("load")){
                    Resource.getModel(command.args[1]);
                }
            }
                break;

            case "phys":
                if(command.argCount == 2){
                    if(command.args[0].equalsIgnoreCase("render")){
                        try{
                            boolean vol = Boolean.parseBoolean(command.args[1].toLowerCase());
                            PhysicsRenderer.setEnabled(vol);
                        }catch(Exception e){
                            GGConsole.error(command.args[0] + " is not a valid boolean!");
                        }
                    }

                    if(command.args[0].equalsIgnoreCase("parallel")){
                        try{
                            CollisionManager.parallelProcessing = Boolean.parseBoolean(command.args[1].toLowerCase());
                        }catch(Exception e){
                            GGConsole.error(command.args[0] + " is not a valid boolean!");
                        }
                    }
                }
                break;

            case "shader":
                if(command.argCount == 3){
                    if(command.args[0].equalsIgnoreCase("uniformfloat")){
                        try{
                            float val = Float.parseFloat(command.args[2].toLowerCase());
                            ShaderController.setUniform(command.args[1], val);
                        }catch(Exception e){
                            GGConsole.error("Invalid/malformed float");
                        }
                    }
                }
                break;

            case "config":
                if(command.argCount == 2){
                    if(command.args[0].equalsIgnoreCase("get")){
                        var input = command.args[1];
                        try{
                            GGConsole.log("Value of " + input + " is " + Configuration.get(input));
                        }catch(Exception e){
                            GGConsole.warning("Failed to find config named " + input);
                        }
                    }
                }else if(command.argCount == 3){
                    if(command.args[0].equalsIgnoreCase("set")){
                        var config = command.args[1];
                        var newval = command.args[2];
                        var success = Configuration.set(config, newval);
                        if(success) GGConsole.log("Changed value in " + config + " to " + newval);
                        else        GGConsole.warning("Failed to find value named " + config);
                    }
                }
                break;
            case "pp":
                if(command.argCount == 2){
                    if(command.args[0].equalsIgnoreCase("enable")){
                        PostProcessController.getPass(command.args[1]).setEnabled(true);
                        GGConsole.log("Enabled post-process path named " + command.args[1]);
                    }else if(command.args[0].equalsIgnoreCase("disable")){
                        PostProcessController.getPass(command.args[1]).setEnabled(false);
                        GGConsole.log("Disabled post-process path named " + command.args[1]);

                    }

                }
        }
    }
}
