package com.opengg.core.model.process;

import com.opengg.core.model.Model;

public abstract class ModelProcess{
    public int numcompleted;
    public int totaltasks;
    public Runnable run;

    public abstract void process(Model model);

    public void broadcast(){
        if(run != null){ run.run();}
    }

}
