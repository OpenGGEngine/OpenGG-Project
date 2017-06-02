package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.audio.Soundtrack;
import com.opengg.core.audio.SoundtrackHandler;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.Resource;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.render.Text;
import com.opengg.core.render.light.Light;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.ArrayTexture;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.WindowInfo;
import com.opengg.core.render.window.WindowOptions;
import static com.opengg.core.render.window.WindowOptions.GLFW;
import com.opengg.core.world.Skybox;
import com.opengg.core.world.Terrain;
import com.opengg.core.world.World;
import com.opengg.core.world.components.FreeFlyComponent;
import com.opengg.core.world.components.TerrainComponent;
import com.opengg.core.world.components.WaterComponent;
import com.opengg.core.world.components.particle.FountainParticleEmitter;
import com.opengg.core.world.generators.DiamondSquare;
import java.io.IOException;

public class OpenGGTest extends GGApplication{
    private GGFont font;
    private Text text;
    private TerrainComponent world;
    private Texture t2;
    private AudioListener as;
    
    public static void main(String[] args) throws IOException, Exception {
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.width = 1280;
        w.height = 1024;
        w.resizable = true;
        w.type = GLFW;
        w.vsync = true;
        OpenGG.initialize(new OpenGGTest(), w);
    }

    @Override
    public  void setup(){
        //Soundtrack track = new Soundtrack();
        //track.addSong("C:\\res\\gun.ogg");
        //track.addSong("C:\\res\\mgs.ogg");
        //track.play();
        //SoundtrackHandler.setSoundtrack(track);
        
        font = Resource.getFont("test", "test.png");
        text = new Text("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n\n"
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + " the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n\n"
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", new Vector2f(), 1f, 0.5f, false);

        World w = WorldEngine.getCurrent();
        w.setFloor(10);
        
        //WorldObject terrain = new WorldObject();
        /*
        ModelRenderComponent island = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Island.bmf"));
        ModelRenderComponent water = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Sea.bmf"));
        terrain.setScale(new Vector3f(0.04f,0.04f,0.04f));
        terrain.setPositionOffset(new Vector3f(0, 0, -550f));
        terrain.attach(island);
        terrain.attach(water);*/

        world = new TerrainComponent(Terrain.generateProcedural(new DiamondSquare(7,20,20,5.5f), 700, 700));
        world.setScale(new Vector3f(800,10,800));
        world.setPositionOffset(new Vector3f(-400, -20,-400));
        world.setGroundArray(ArrayTexture.get(Resource.getTexturePath("grass.png"),
                Resource.getTexturePath("dirt.png"),
                Resource.getTexturePath("flower2.png"),
                Resource.getTexturePath("road.png")));
        world.setBlotmap(Resource.getTexture("blendMap.png"));
        
        //FreeFlyComponent player = new FreeFlyComponent();
        TestPlayerComponent player = new TestPlayerComponent();
        player.use();

        FountainParticleEmitter particle = new FountainParticleEmitter(8,5,1,Resource.getTexture("emak.png"));
        WaterComponent water = new WaterComponent(Resource.getTexture("water.jpg"), 0.1f, 100f);
        water.setPositionOffset(new Vector3f(0,10,0));
        
        w.attach(player);
        w.attach(world);
        w.attach(particle);
        w.attach(new SunComponent(Resource.getTexture("emak.png"), 500, 1f));
        w.attach(water);
       
        world.enableRendering();
        world.enableCollider();
        
        WorldEngine.useWorld(w);
        
        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "down", KEY_LEFT_SHIFT);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_RIGHT);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_LEFT);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_UP);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_DOWN);
        BindController.addBind(ControlType.KEYBOARD, "fire", KEY_L);
        BindController.addBind(ControlType.KEYBOARD, "aim", KEY_K);
        
        RenderEngine.setSkybox(new Skybox(Cubemap.get(Resource.getTexturePath("skybox\\majestic")), 1500f)); 
        GUI.addItem("aids", new GUIText(text, font, new Vector2f(0f,0)));
        
    }
    float wow = 0f;
    
    @Override
    public void render() {
        ShaderController.setPerspective(90, OpenGG.window.getRatio(), 0.2f, 3000f);
    }

    @Override
    public void update() {}
}
