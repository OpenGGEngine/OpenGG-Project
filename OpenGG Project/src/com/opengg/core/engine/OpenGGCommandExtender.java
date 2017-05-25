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
public class OpenGGCommandExtender  implements ConsoleListener{
    @Override
    public void onConsoleInput(UserCommand command) {
        if(command.command.equalsIgnoreCase("quit")){
            endApplication();
        }
        if(command.command.equalsIgnoreCase("fquit")){
            forceEnd();
        }
    }
}
