package com.opengg.core.gui.text;

import com.opengg.core.GGInfo;
import com.opengg.core.io.input.keyboard.*;
import com.opengg.core.render.text.Font;
import com.opengg.core.render.text.Text;
import com.opengg.core.world.Action;
import com.opengg.core.world.Actionable;

import java.util.function.Consumer;

public class UITextField extends UITextLine implements KeyboardCharacterListener, KeyboardListener, Actionable {
    private boolean inFocus = false;
    private boolean clearOnFocus = true;
    private int focusKey = -1;
    private int exitFocusKey = -1;
    private int submitKey = -1;

    private Consumer<String> onEdit = t -> {};
    private Consumer<String> onSubmit = t -> {};

    public UITextField(Font font) {
        this(Text.from("").size(0.08f), font);
    }

    public UITextField(Text text, Font font) {
        super(text, font);
        KeyboardController.addKeyboardListener(this);
        KeyboardController.addKeyboardCharacterListener(this);
    }

    public void setExitFocusKey(int exitFocusKey) {
        this.exitFocusKey = exitFocusKey;
    }

    public void setFocusKey(int focusKey) {
        this.focusKey = focusKey;
    }

    public void setSubmitKey(int submitKey) {
        this.submitKey = submitKey;
    }

    public void setOnEdit(Consumer<String> onEdit) {
        this.onEdit = onEdit;
    }

    public void setOnSubmit(Consumer<String> onSubmit) {
        this.onSubmit = onSubmit;
    }

    public void setClearOnFocus(boolean clear){
        this.clearOnFocus = clear;
    }

    public void setInFocus(boolean inFocus) {
        this.inFocus = inFocus;
        if(inFocus){
            GGInfo.setMenu(true);
            if(clearOnFocus)
                this.text.setText("");
        }else {
            GGInfo.setMenu(false);
        }
    }

    @Override
    public void charPressed(char val) {
        if(!this.isEnabled()) return;
        if(inFocus) {
            this.setText(this.getContents() + val);
            onEdit.accept(this.getContents());
        }
    }

    @Override
    public void keyPressed(int key) {
        if(!this.isEnabled()) return;
        if(inFocus){
            if(key == submitKey){
                onSubmit.accept(this.getContents());
            }
            if(key == Key.KEY_BACKSPACE){
                if(!this.getContents().isEmpty()){
                    this.setText(this.getContents().substring(0, this.getContents().length()-1));
                }
                onEdit.accept(this.getContents());
            }
            if(key == exitFocusKey){
                setInFocus(false);
            }
        }
        if(key == focusKey){
            //if(!GGInfo.isMenu())
                setInFocus(true);
        }
    }

    @Override
    public void keyReleased(int key) {

    }


    @Override
    public void onAction(Action action) {
    }
}
