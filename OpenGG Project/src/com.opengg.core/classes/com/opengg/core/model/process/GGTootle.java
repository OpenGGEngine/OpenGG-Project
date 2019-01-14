package com.opengg.core.model.process;

import com.opengg.core.console.GGConsole;
import com.opengg.core.model.Mesh;
import com.opengg.core.model.Model;
import com.opengg.core.system.Allocator;
import org.lwjgl.system.Platform;

import java.nio.IntBuffer;

import static org.lwjgl.util.tootle.Tootle.*;

public class GGTootle extends ModelProcess {
    public static String name = "AMD Tootle Mesh Optimization";
    private static int vcacheMethod = Platform.get() == Platform.WINDOWS ? 2 : 0;
    private static int vcacheSize = TOOTLE_DEFAULT_VCACHE_SIZE;
    private static boolean optimizeVertexMemory = true;
    private static boolean init =false;
    public static void init(){
        TootleInit();
        init = true;
    }
    @Override
    public void process(Model model){
        this.totaltasks = model.meshes.size();
        this.numcompleted = 0;
        GGConsole.log("Started Tootle Processing");
        if(!init) init();
        int index=0;
        for(Mesh mesh: model.meshes) {
            IntBuffer revised = Allocator.allocInt(mesh.ibo.capacity());
            switch (TootleOptimize(mesh.vbo, mesh.ibo, 11 * Float.BYTES,
                    vcacheSize,
                    null,
                    TOOTLE_CCW,
                    revised,
                    null,
                    TOOTLE_VCACHE_AUTO,
                    TOOTLE_OVERDRAW_AUTO)) {
                case TOOTLE_OK:
                    mesh.ibo = revised;
                    break;
                case TOOTLE_OUT_OF_MEMORY:
                    GGConsole.error("Tootle Out of Memory: Mesh "+index);
                    break;
                case TOOTLE_INVALID_ARGS:
                    GGConsole.error("Tootle Invalid Arguments: Mesh "+index);
                    break;
            }
            index++;
            numcompleted+=1;
            if(this.run != null) broadcast();
        }
        GGConsole.log("Tootle Optimization Finished");
    }
}
