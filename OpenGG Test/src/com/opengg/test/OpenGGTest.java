package com.opengg.test;

import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.Sound;
import com.opengg.core.engine.AudioController;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.RenderEngine;
import com.opengg.core.engine.WorldManager;
import com.opengg.core.gui.GUIItem;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.input.KeyboardEventHandler;
import com.opengg.core.io.input.KeyboardListener;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.model.ModelLoader;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import com.opengg.core.render.drawn.MatDrawnObject;
import com.opengg.core.render.particle.ParticleSystem;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.FramebufferTexture;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.texture.text.GGFont;
import com.opengg.core.render.window.DisplayMode;
import com.opengg.core.render.window.GLFWWindow;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.util.GlobalInfo;
import static com.opengg.core.util.GlobalUtil.print;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldObject;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.ParticleRenderComponent;
import com.opengg.core.world.components.TriggerableAudioComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import com.opengg.core.world.components.triggers.KeyTrigger;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.FloatBuffer;
import static org.lwjgl.glfw.GLFW.*;

public class OpenGGTest implements KeyboardListener {
    GLFWWindow win;
    private float sens = 0.25f;
    public float xrot, yrot;
    public boolean lock = false;
    public float rot1 = 0, rot2 = 0;
    Vector3f rot = new Vector3f(0, 0, 0);
    Vector3f pos = new Vector3f(0, -10, -30);
    WorldObject terrain, drawnobject;
    World w;
    Camera c;
    FramebufferTexture t1 = new FramebufferTexture();
    FramebufferTexture ppbf = new FramebufferTexture();
    InstancedDrawnObject flashbang;
    DrawnObject test5,sky;
    DrawnObjectGroup test6;
    MatDrawnObject awp3, base2;
    GGFont f;
    OBJModel m, m2;
    private Texture t2, t3;
    private Cubemap cb = new Cubemap();
    WorldObject w1, w2;
    private Sound so,so2,so3;
    private AudioListener as;
    private WorldObject awps;
    private PhysicsComponent bad;
    
    public static void main(String[] args) throws IOException, Exception {
        new OpenGGTest();
    }
    
    public OpenGGTest() throws IOException, Exception {
        OpenGG.initializeOpenGG();
        
        KeyboardEventHandler.addToPool(this); 
        try {
            GlobalInfo.window = win = new GLFWWindow(1280, 960, "Test", DisplayMode.WINDOWED);
            
        } catch (Exception ex) {
        }

        setup();
        while (!win.shouldClose()) {
            startFrame();
            update();
            render();
            endFrame(win);
        }
        exit();
    }

    FloatBuffer base, test2, test;

    public void setup() throws FileNotFoundException, IOException, Exception {
        MovementLoader.setup(80);
        
        OpenGG.initializeRenderEngine();
        OpenGG.initializeAudioController();

        c = new Camera(pos, rot);
        c.setPos(pos);
        c.setRot(rot);

        print("Shader/VAO Loading and Generation Complete");
          
        so = new Sound(OpenGGTest.class.getResource("res/maw.wav"));
        so2 = new Sound(OpenGGTest.class.getResource("res/mgs.wav"));
        
        t1.setupTexToBuffer(2000,2000);
        ppbf.setupTexToBuffer(win.getWidth(), win.getHeight());
        t3 = Texture.get("C:/res/deer.png");
        t2 = Texture.get("C:/res/test.png");

        f = new GGFont(t2, new File("C:/res/test.fnt"));
        GUIText g = new GUIText("It is a period of civil war. Rebel spaceships, striking from a hidden base,"
                + " have won their first victory against the evil Galactic Empire. During the battle,"
                + " Rebel spies managed to steal secret plans to the Empires ultimate weapon, the DEATH STAR,"
                + " an armored space station with enough power to destroy an entire planet. \n\n"
                + " Pursued by the Empires sinister agents, Princess Leia races home aboard her starship,"
                + " custodian of the stolen plans that can save her people and restore freedom to the galaxy...", f, 20f, new Vector2f(), 10, false);
        awp3 = f.loadText(g);
        
        g = new GUIText("Turmoil has engulfed the Galactic Republic. The taxation of trade routes to outlying star systems is in dispute. \n" 
                + "\n" 
                + " Hoping to resolve the matter with a blockade of deadly battleships, "
                + "the greedy Trade Federation has stopped all shipping to the small planet of Naboo. \n" 
                + "\n" 
                + " While the congress of the Republic endlessly debates this alarming chain of events,"
                + " the Supreme Chancellor has secretly dispatched two Jedi Knights,"
                + " the guardians of peace and justice in the galaxy, to settle the conflict...", f, 1f, new Vector2f(), 0.5f, false);
        
       base2 = f.loadText(g);
        
        t2.useTexture(0);
        cb.loadTexture("C:/res/skybox/majestic");
        
        test = ObjectBuffers.genBuffer(new OBJParser().parse(OpenGGTest.class.getResource("res/models/deer.obj")), 1f, 0.2f, new Vector3f());
        
        sky = new DrawnObject(ObjectBuffers.genSkyCube(), 12);
        
        w = WorldManager.getDefaultWorld();
        GlobalInfo.curworld = w;
        w.floorLev = -10;

        ModelRenderComponent ep1;
        awps = new WorldObject();
        awps.attach(ep1 = new ModelRenderComponent(awp3));
        awps.setPosition(new Vector3f(5,5,5));
        
        ParticleRenderComponent p = new ParticleRenderComponent();
        p.setPosition(new Vector3f(0,50,0));
        p.addParticleType(new ParticleSystem(0.5f,20f,100f,test, t3));
        
        ModelRenderComponent r = new ModelRenderComponent(ModelLoader.loadModel("C:/res/3DSMusicPark/3DSMusicPark.bmf"));
        print("Model and Texture Loading Completed");

        TriggerableAudioComponent test3 = new TriggerableAudioComponent(so2);
        KeyTrigger t = new KeyTrigger(GLFW_KEY_P);
        t.addSubscriber(test3);
        
        terrain = new WorldObject();
        terrain.attach(bad = new PhysicsComponent());
        terrain.attach(r);
        terrain.attach(p);
        terrain.attach(test3);

        as = new AudioListener();
        AudioController.setListener(as);
        
        
        RenderEngine.setSkybox(sky, cb);
        RenderEngine.addGUIItem(new GUIItem(base2, new Vector2f()));
        RenderEngine.addRenderable(p);
        RenderEngine.addRenderable(r);
        RenderEngine.addRenderable(ep1, true, false);
        
        print("Setup Complete");
    }

    public void exit() {}

    public void render() {
        rot = new Vector3f(yrot, xrot, 0);
        if(lock){
            rot = MovementLoader.processRotation(sens, false);
        }
        pos = MovementLoader.processMovement(pos, rot);
        
        as.setPos(pos);
        as.setRot(rot);
        AudioController.setListener(as);
        
        c.setPos(pos);
        c.setRot(rot);

        RenderEngine.controller.setLightPos(new Vector3f(40, 200, 40));
        RenderEngine.controller.setView(c);
        RenderEngine.controller.setPerspective(90, win.getRatio(), 1, 2500f);
        
        RenderEngine.draw();
    }

    public void update() {
        GlobalInfo.engine.update();
        xrot -= rot1 * 7;
        yrot -= rot2 * 7;

    }

    @Override
    public void keyPressed(int key) {

        if (key == GLFW_KEY_M) {
            win.setCursorLock(lock = !lock);
        }
        if (key == GLFW_KEY_Q) {
            rot1 += 0.3;

        }
        if (key == GLFW_KEY_E) {
            rot1 -= 0.3;

        }
        if (key == GLFW_KEY_R) {
            rot2 += 0.3;

        }
        if (key == GLFW_KEY_F) {
            rot2 -= 0.3;

        }
        if (key == GLFW_KEY_G) {
            bad.velocity = new Vector3f(0,20,0);
        }
        if (key == GLFW_KEY_U){
            RenderEngine.setShadowVolumes(!RenderEngine.getShadowsEnabled());
        }

    }

    @Override
    public void keyReleased(int key) {
        if (key == GLFW_KEY_Q) {
            rot1 -= 0.3;

        }
        if (key == GLFW_KEY_E) {
            rot1 += 0.3;

        }
        if (key == GLFW_KEY_R) {
            rot2 -= 0.3;

        }
        if (key == GLFW_KEY_F) {
            rot2 += 0.3;

        }
    }
}
