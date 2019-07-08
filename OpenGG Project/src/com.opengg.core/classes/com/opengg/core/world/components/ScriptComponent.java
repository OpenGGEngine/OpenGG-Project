package com.opengg.core.world.components;

import com.opengg.core.script.Script;
import com.opengg.core.script.ScriptLoader;
import com.opengg.core.util.GGInputStream;
import com.opengg.core.util.GGOutputStream;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This component is meant to be used as a carrier for a lambda
 */
public class ScriptComponent extends Component{
    private String scriptName = "";
    private Script script;

    private Map<String, Object> scriptValues = new HashMap<>();

    public ScriptComponent setScript(String script){
        this.scriptName = scriptName;
        return this;
    }

    public String getScriptName() {
        return scriptName;
    }

    public Map<String, Object> getScriptStates(){
        return scriptValues;
    }

    public Object getScriptState(String name){
        return scriptValues.get(name);
    }

    public void saveScriptState(String name, Object value){
        scriptValues.put(name,value);
    }

    @Override
    public void update(float delta){
        if(script == null)
            script = ScriptLoader.getScriptByName(scriptName);
        script.accept(this, delta);
    }

    @Override
    public void serialize(GGOutputStream out) throws IOException {
        super.serialize(out);
        out.write(scriptName);
    }

    @Override
    public void deserialize(GGInputStream in) throws IOException{
        super.deserialize(in);
        scriptName = in.readString();
    }
}
