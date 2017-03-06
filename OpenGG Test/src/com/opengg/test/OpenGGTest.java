package com.opengg.test;

import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.AudioController;
import com.opengg.core.engine.BindController;
import com.opengg.core.engine.GGApplication;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.WorldEngine;
import com.opengg.core.gui.GUIText;
import com.opengg.core.gui.GUITexture;
import com.opengg.core.io.ControlType;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.keyboard.KeyboardListener;
import com.opengg.core.io.input.mouse.MouseButtonListener;
import com.opengg.core.math.Quaternionf;
import com.opengg.core.math.Vector2f;
import com.opengg.core.math.Vector3f;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.render.drawn.DrawnObject;
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
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.PlayerComponent;
import com.opengg.core.world.components.RenderComponent;
import com.opengg.core.world.components.WorldObject;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenGGTest extends GGApplication implements MouseButtonListener, KeyboardListener {
    private MatDrawnObject base2;
    private GGFont f;
    private Texture t2, t3;
    private Sound so, so2;
    private AudioListener as;
    private WorldObject awps;
    private PhysicsComponent bad;
    private Light l;
    PlayerComponent player;
    int worldh = 500;
    boolean ortho = false;
    
    public static void main(String[] args) throws IOException, Exception {
        WindowInfo w = new WindowInfo();
        w.displaymode = WindowOptions.WINDOWED;
        w.height = 1024;
        w.width = 1024;
        w.resizable = false;
        w.type = GLFW;
        w.vsync = true;
        OpenGG.initialize(new OpenGGTest(), w);
    }
    private GUIText g;

    @Override
    public  void setup(){
        Terrain t = new Terrain(10,10, t3);
        KeyboardController.addToPool(this);
        try {
            t.generateTerrain(new FileInputStream("C:/res/javi.png"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(OpenGGTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OpenGGTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        as = new AudioListener();
        AudioController.setListener(as);

        so = new Sound(OpenGGTest.class.getResource("res/maw.wav"));
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

        World w = OpenGG.curworld;
        w.setFloor(worldh);
        
        WorldObject terrain = new WorldObject();
        ModelRenderComponent island = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Island.bmf"));
        ModelRenderComponent water = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\island\\Sea.bmf"));
        RenderComponent mesh = new RenderComponent(new DrawnObject(t.elementals,t.indices));
        mesh.setPositionOffset(new Vector3f(0,0,0));
        mesh.setScale(new Vector3f(100,100,100));
        island.setScale(new Vector3f(0.2f,0.2f,0.2f));
        island.setPositionOffset(new Vector3f(0, 0, -550f));
        water.setScale(new Vector3f(0.2f,0.2f,0.2f));
        water.setPositionOffset(new Vector3f(0, 0, -550f));
        terrain.attach(island);
        terrain.attach(water);
        terrain.attach(mesh);
        
        player = new PlayerComponent();
        player.setPositionOffset(new Vector3f(0,5,0));
        
        ModelRenderComponent beretta = new ModelRenderComponent(ModelLoader.loadModel("C:\\res\\beretta\\Beretta_M9.bmf"));
        beretta.setScale(new Vector3f(0.2f,0.2f,0.2f));
        beretta.setRotationOffset(new Quaternionf(new Vector3f(0,0,0)));
        beretta.setPositionOffset(new Vector3f(1,-1,-1));
        //player.attach(beretta);
        WorldEngine.getCurrent().attach(player);
        
        player.use();
   player.currot.x = -180;
       
        BindController.addBind(ControlType.KEYBOARD, "forward", KEY_W);
        BindController.addBind(ControlType.KEYBOARD, "backward", KEY_S);
        BindController.addBind(ControlType.KEYBOARD, "left", KEY_A);
        BindController.addBind(ControlType.KEYBOARD, "right", KEY_D);
        BindController.addBind(ControlType.KEYBOARD, "up", KEY_SPACE);
        BindController.addBind(ControlType.KEYBOARD, "lookright", KEY_Q);
        BindController.addBind(ControlType.KEYBOARD, "lookleft", KEY_E);
        BindController.addBind(ControlType.KEYBOARD, "lookup", KEY_R);
        BindController.addBind(ControlType.KEYBOARD, "lookdown", KEY_F);
        
        w.attach(terrain);
        w.attach(player);
        
        w.useRenderables();
        
        l = new Light(new Vector3f(10,20,190), new Vector3f(1,1,1), 4000f, 0);
        
        RenderEngine.addLight(l);
        RenderEngine.setSkybox(ObjectCreator.createCube(1500f), Cubemap.get("C:/res/skybox/majestic"));
        RenderEngine.addGUIItem(new GUITexture(t3, new Vector2f(),new Vector2f(0.01f,0.01f)));
        RenderEngine.setCulling(false);    
    }
    
    @Override
    public void render() {
        AudioController.setListener(as);
        if(ortho){
            ShaderController.setOrtho(-400, 400, -400, 400, 1f, 4000f);
        }else{
            ShaderController.setPerspective(90, OpenGG.window.getRatio(), 0.2f, 3000f);
        }
        //
        
        t3.useTexture(0);
        RenderEngine.draw();
    }

    @Override
    public void update() {
        if(ortho){
            System.out.println(player.rot.toEuler());
        }
       
    }

    @Override
    public void buttonPressed(int button) {
        bad.velocity = new Vector3f(0,20,0);
        worldh++;
        
    }

    @Override
    public void buttonReleased(int button) {}

    @Override
    public void keyPressed(int key) {
        if(key == KEY_M){
            ortho = !ortho;
        }
    }

    @Override
    public void keyReleased(int key) {

    }
}
