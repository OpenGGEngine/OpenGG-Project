/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.opengg.core.render.text.impl;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Warren
 */
public class TextLine {
        public double maxLength;
	public double spaceSize;

	private List<Word> words = new ArrayList<>();

    public List<Word> getWords() {
        return List.copyOf(words);
    }
	public double currentLineLength = 0;


	protected TextLine(double spaceWidth, double fontSize, double maxLength) {
		this.spaceSize = spaceWidth * fontSize;
		this.maxLength = maxLength;
	}

	public boolean addWord(Word word) {
		double additionalLength = word.width;
		additionalLength += !words.isEmpty() ? spaceSize : 0;
		if (currentLineLength + additionalLength <= maxLength) {
			words.add(word);
			currentLineLength += additionalLength;
			return true;
		} else {
			return false;
		}
	}
}
