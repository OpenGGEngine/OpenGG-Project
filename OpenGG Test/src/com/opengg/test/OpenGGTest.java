package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.objects.ObjectCreator;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.world.Terrain;
import com.opengg.core.world.World;
import com.opengg.core.world.components.TerrainComponent;
import com.opengg.core.world.components.particle.FountainParticleEmitter;
import com.opengg.core.world.generators.SmoothPerlinGenerator;
import java.io.File;
import java.io.IOException;

public class OpenGGTest extends GGApplication{
    private MatDrawnObject base2;
    private GGFont f;
    private Texture t2, t3;
    private Sound so, so2;
    private AudioListener as;
    private Light l;
    TestPlayerComponent player;
    
    public static void main(String[] args) throws IOException, Exception {
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.width = 1280;
        w.height = 1024;
        w.resizable = false;
        w.type = GLFW;
        w.vsync = true;
        OpenGG.initialize(new OpenGGTest(), w);
    }
    private GUIText g;
    private TerrainComponent world;

    @Override
    public  void setup(){
        so = new Sound(OpenGGTest.class.getResource("res/gay.wav"));
        so2 = new Sound(OpenGGTest.class.getResource("res/mgs.wav"));
        
        t3 = Texture.get("C:/res/deer.png");
        t2 = Texture.get("C:/res/test.png");

        f = new GGFont(t2, new File("C:/res/test.fnt"));
        g = new GUIText("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", f, 1f, new Vector2f(), 0.5f, false);

        base2 = f.loadText(g);

        World w = WorldEngine.getCurrent();
        w.setFloor(-10);
        
        //WorldObject terrain = new WorldObject();
        /*
        ModelRenderComponent island = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Island.bmf"));
        ModelRenderComponent water = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Sea.bmf"));
        terrain.setScale(new Vector3f(0.04f,0.04f,0.04f));
        terrain.setPositionOffset(new Vector3f(0, 0, -550f));
        terrain.attach(island);
        terrain.attach(water);*/
       
        world = new TerrainComponent(Terrain.generateProcedural(new SmoothPerlinGenerator(6,0.2,10), 500,500));
        world.setScale(new Vector3f(1000,1,1000));
        
        player = new TestPlayerComponent();
        player.use();

        //EnemySpawnerComponent cc = new EnemySpawnerComponent();
        //w.attach(cc);
        //w.attach(terrain);
        FountainParticleEmitter particle = new FountainParticleEmitter(10,2,5,t3);
        
        w.attach(player);
        w.attach(world);
        //w.attach(particle);
        world.enableRendering();
        world.enableCollider();

        WorldEngine.useWorld(w);
        
        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_Q);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_E);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_R);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_F);
        BindController.addBind(ControlType.KEYBOARD, "fire", KEY_L);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);
        
        l = new Light(new Vector3f(10,200,0), new Vector3f(1,1,1), 4000f, 0);
        
        RenderEngine.addLight(l);
        RenderEngine.setSkybox(ObjectCreator.createCube(1500f), Cubemap.get("C:\\res\\skybox\\majestic"));
        RenderEngine.setCulling(false);  
    }
    
    @Override
    public void render() {
        ShaderController.setPerspective(90, OpenGG.window.getRatio(), 0.2f, 3000f);
        RenderEngine.draw();
    }

    @Override
    public void update() {}
}
