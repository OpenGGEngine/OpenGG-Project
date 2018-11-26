package com.opengg.core.model.ggmodel.process;

import com.opengg.core.model.ggmodel.GGModel;

public abstract class ModelProcess{
    public int numcompleted;
    public int totaltasks;
    public Runnable run;

    public abstract void process(GGModel model);

    public void broadcast(){
        run.run();
    };

}
