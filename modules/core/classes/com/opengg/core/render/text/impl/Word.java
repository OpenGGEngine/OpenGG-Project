/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.text.impl;

import com.opengg.core.console.GGConsole;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class Word {
    private final List<GGCharacter> characters = new ArrayList<>();

        public List<GGCharacter> getCharacters() {
            return List.copyOf(characters);
        }
        public double width = 0;

        public Word(double fontSize) {
            this.fontSize = fontSize;
        }
        public double fontSize;

        public void addCharacter(GGCharacter character) {
            try{
                characters.add(character);
                width += character.xAdvance
                        * fontSize;
            }catch(NullPointerException e){
                GGConsole.warning("Null Pointer, " + character.sizeX);
            }
        }
}
