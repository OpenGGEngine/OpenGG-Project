/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.opengg.core.engine;

import com.opengg.core.Configuration;
import com.opengg.core.audio.SoundEngine;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.console.ConsoleListener;
import com.opengg.core.console.GGConsole;
import com.opengg.core.console.UserCommand;
import static com.opengg.core.engine.OpenGG.*;

import com.opengg.core.gui.GUIController;
import com.opengg.core.model.ModelManager;
import com.opengg.core.network.NetworkEngine;
import com.opengg.core.network.common.ChatMessage;
import com.opengg.core.network.common.ConnectionData;
import com.opengg.core.physics.PhysicsRenderer;
import com.opengg.core.physics.collision.CollisionManager;
import com.opengg.core.render.RenderEngine;
import com.opengg.core.render.postprocess.PostProcessController;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.window.WindowController;
import com.opengg.core.world.WorldEngine;
import com.opengg.core.world.components.FreeFlyComponent;

/**
 *
 * @author Javier
 */
public class OpenGGCommandExtender implements ConsoleListener{
    @Override
    public void onConsoleInput(UserCommand command) {
        switch(command.command) {
            case "quit" -> endApplication();
            case "fquit" -> forceEnd();

            case "ping" -> {
                if (NetworkEngine.getClient() != null) {
                    NetworkEngine.getClient().calculateLatency()
                            .thenAccept(l -> GGConsole.log("Latency to " + NetworkEngine.getClient().getServerName() + " is " + l));
                } else {
                    GGConsole.log("Pong!");
                }
            }

            case "volume" -> {
                if (command.argCount == 1) {
                    try {
                        float vol = Float.parseFloat(command.args[0]);
                        SoundEngine.setGlobalGain(vol);
                        GGConsole.log("Set volume to " + vol);
                    } catch (Exception e) {
                        GGConsole.error(command.args[0] + " is not a valid volume!");
                    }
                }
            }

            case "world" -> {
                if (command.argCount == 1) {
                    if (command.args[0].equalsIgnoreCase("print-layout")) {
                        WorldEngine.getCurrent().printLayout();
                    }
                }
            }

            case "snd" -> {
                if (command.argCount == 1) {
                    if (command.args[0].equalsIgnoreCase("restart")) {
                        SoundEngine.restart();
                    } else if (command.args[0].equalsIgnoreCase("next-track")) {
                        SoundtrackHandler.getCurrent().next();
                        GGConsole.log("Playing next track");
                    }
                }
            }

            case "bind" -> {
                if (command.argCount == 1) {
                    if (command.args[0].equalsIgnoreCase("list")) {
                        BindController.printBinds();
                    }
                }
            }


            case "model" -> {
                if (command.argCount == 1) {
                    if (command.args[0].equalsIgnoreCase("clear-cache")) {
                        ModelManager.getModelList();
                    }
                }
                if (command.argCount == 2) {
                    if (command.args[0].equalsIgnoreCase("load")) {
                        Resource.getModel(command.args[1]);
                    }
                }
            }


            case "phys" -> {
                if (command.argCount == 2) {
                    if (command.args[0].equalsIgnoreCase("render")) {
                        try {
                            boolean vol = Boolean.parseBoolean(command.args[1].toLowerCase());
                            PhysicsRenderer.setEnabled(vol);
                        } catch (Exception e) {
                            GGConsole.error(command.args[0] + " is not a valid boolean!");
                        }
                    }

                    if (command.args[0].equalsIgnoreCase("parallel")) {
                        try {
                            CollisionManager.parallelProcessing = Boolean.parseBoolean(command.args[1].toLowerCase());
                        } catch (Exception e) {
                            GGConsole.error(command.args[0] + " is not a valid boolean!");
                        }
                    }
                }
            }


            case "shader" -> {
                if (command.argCount == 3) {
                    if (command.args[0].equalsIgnoreCase("uniformfloat")) {
                        try {
                            float val = Float.parseFloat(command.args[2].toLowerCase());
                            ShaderController.setUniform(command.args[1], val);
                        } catch (Exception e) {
                            GGConsole.error("Invalid/malformed float");
                        }
                    }
                }
            }

            case "noclip" -> {
                WorldEngine.getCurrent().attach(new FreeFlyComponent().setPositionOffset(RenderEngine.getCurrentView().getPosition()));
                WindowController.getWindow().setCursorLock(true);
            }

            case "config" -> {
                if (command.argCount == 2) {
                    if (command.args[0].equalsIgnoreCase("get")) {
                        var input = command.args[1];
                        try {
                            GGConsole.log("Value of " + input + " is " + Configuration.get(input));
                        } catch (Exception e) {
                            GGConsole.warning("Failed to find config named " + input);
                        }
                    }
                } else if (command.argCount == 3) {
                    if (command.args[0].equalsIgnoreCase("set")) {
                        var config = command.args[1];
                        var newval = command.args[2];
                        var success = Configuration.set(config, newval);
                        if (success) GGConsole.log("Changed value in " + config + " to " + newval);
                        else GGConsole.warning("Failed to find value named " + config);
                    }
                }
            }

            case "ui" -> {
                if (command.argCount == 1) {
                    if (command.args[0].equalsIgnoreCase("enable")) {
                        GUIController.setEnabled(true);
                    }
                    if (command.args[0].equalsIgnoreCase("disable")) {
                        GUIController.setEnabled(false);
                    }
                }
            }

            case "pp" -> {
                if (command.argCount == 2) {
                    if (command.args[0].equalsIgnoreCase("enable")) {
                    //    PostProcessController.getPass(command.args[1]).setEnabled(true);
                        GGConsole.log("Enabled post-process path named " + command.args[1]);
                    } else if (command.args[0].equalsIgnoreCase("disable")) {
                    //    PostProcessController.getPass(command.args[1]).setEnabled(false);
                        GGConsole.log("Disabled post-process path named " + command.args[1]);

                    }
                }
            }

            case "say" -> {
                if(NetworkEngine.getServer() == null){
                    GGConsole.warning("Cannot use that command outside of a server context");
                    return;
                }
                if (command.argCount == 1) {
                    NetworkEngine.getServer().getClients().forEach(c -> new ChatMessage("Server", command.args[0]).send(c.getConnection()));
                }else {
                    GGConsole.log("Usage: say {message}");
                }
            }

            case "server-info" -> {
                if(command.argCount == 2){
                    var future = NetworkEngine.pollServer(new ConnectionData(command.args[0], command.args[1]));
                    future.thenAccept(c -> GGConsole.log("Name: " + c.name + ", MOTD: " + c.motd + ",\nUsers: " + c.users + ", Max Users: " + c.maxUsers));
                }else{
                    GGConsole.log("Usage: server-info {ip} {port}");
                }
            }

            case "connect" -> {
                if(command.argCount == 3){
                    if(NetworkEngine.getClient() != null){
                        NetworkEngine.getClient().disconnect();
                    }

                    OpenGG.asyncExec(() -> NetworkEngine.connect(new NetworkEngine.ClientOptions(command.args[2], command.args[0], command.args[1])));
                }else{
                    GGConsole.log("Usage: connect {ip} {port} {username}");
                }
            }

            case "disconnect" -> {
                if(NetworkEngine.getClient() == null){
                    GGConsole.warning("Not currently connected to anything");
                }else{
                    NetworkEngine.close();
                }
            }

            default -> GGConsole.error("Command \"" + command.command + "\" does not exist.");

        }
    }
}
