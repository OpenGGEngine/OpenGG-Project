package com.opengg.test;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioHandler;
import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.AudioSource;
import com.opengg.core.engine.OpenGG;
import com.opengg.core.engine.WorldManager;
import com.opengg.core.gui.GUI;
import com.opengg.core.gui.GUIText;
import com.opengg.core.io.input.KeyboardEventHandler;
import com.opengg.core.io.input.KeyboardListener;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.model.OBJ;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;
import com.opengg.core.render.drawn.InstancedDrawnObject;
import com.opengg.core.render.drawn.MatDrawnObject;
import static com.opengg.core.render.gl.GLOptions.enable;
import com.opengg.core.render.particle.ParticleSystem;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.render.shader.ShaderController;
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
import com.opengg.core.util.Time;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldObject;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.ParticleRenderComponent;
import com.opengg.core.world.components.physics.PhysicsComponent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

public class OpenGGTest implements KeyboardListener {

    static long window;
    GLFWWindow win;
    private float ratio;
    private float sens = 0.25f;
    private float sav;
    public float xrot, yrot;
    public boolean lock = false;
    public float rot1 = 0, rot2 = 0;
    Vector3f rot = new Vector3f(0, 0, 0);
    Vector3f pos = new Vector3f(0, -10, -30);
    WorldObject terrain;
    WorldObject drawnobject;
    
    World w;
    
    public static void main(String[] args) throws IOException, Exception {
        new OpenGGTest();
        OpenGG.initializeOpenGG();
    }

    private VertexArrayObject vao;

    Camera c;
    FramebufferTexture t1 = new FramebufferTexture();
    Texture t2 = new Texture();
    FramebufferTexture ppbf = new FramebufferTexture();
    InstancedDrawnObject flashbang;
    DrawnObject test5,sky;
    DrawnObjectGroup test6;
    MatDrawnObject awp3, base2;
    GGFont f;

    OBJModel m;
    OBJModel m2;
    private Texture t3 = new Texture();
    private Cubemap cb = new Cubemap();
    private ShaderController s = new ShaderController();
    private Time t;
    WorldObject w1, w2;
    private AudioSource so,so2,so3;
    private AudioListener as;
    private DrawnObject ppsht;
    private WorldObject awps;
    private PhysicsComponent bad;
    
    public OpenGGTest() throws IOException, Exception {
        KeyboardEventHandler.addToPool(this);

   
        try {
            win = new GLFWWindow(1280, 960, "Test", DisplayMode.WINDOWED);
            
        } catch (Exception ex) {
            ex.printStackTrace();
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
        
        vao = new VertexArrayObject();
        vao.bind();
        
        URL verts = OpenGGTest.class.getResource("res/shaders/shader.vert");
        URL frags = OpenGGTest.class.getResource("res/shaders/shader.frag");
        URL geoms = OpenGGTest.class.getResource("res/shaders/shader.geom");
        
        s.setup(verts, frags, geoms);
        
        c = new Camera(pos, rot);
        c.setPos(pos);
        c.setRot(rot);

        print("Shader/VAO Loading and Generation Complete");
        
        AudioHandler.init(1);
        so = AudioHandler.loadSound(OpenGGTest.class.getResource("res/maw.wav"));

        so2 = AudioHandler.loadSound(OpenGGTest.class.getResource("res/mgs.wav"));
        
        so3 = AudioHandler.loadSound(OpenGGTest.class.getResource("res/mgs.wav"));

        
        t1.setupTexToBuffer(2000,2000);
        ppbf.setupTexToBuffer(win.getWidth(), win.getHeight());
        t3.loadTexture("C:/res/deer.png", true);
        t2.loadTexture("C:/res/test.png", true);
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
        base2.setMatrix(Matrix4f.translate(0,0,0).multiply(Matrix4f.scale(1f, 1f, 1)));
        
        t2.useTexture(0);
        cb.loadTexture("C:/res/skybox/majestic");
        
        URL path = OpenGGTest.class.getResource("res/models/deer.obj");
        m = new OBJParser().parse(path);
        test = ObjectBuffers.genBuffer(m, 1f, 0.2f, new Vector3f());
        
        //test6 = OBJ.getDrawableModel("C:/res/3DSMusicPark/3DSMusicPark.obj");
        
        FloatBuffer b = BufferUtils.createFloatBuffer(12);
        b.put(0).put(50).put(0).put(0).put(0).put(0)
                .put(0).put(0).put(0).put(0).put(0).put(0);
        b.flip();
        
        flashbang = new InstancedDrawnObject(test, b);
        ParticleRenderComponent p = new ParticleRenderComponent();
        p.setPosition(new Vector3f(0,50,0));
        p.addParticleType(new ParticleSystem(0.5f,20f,-0.27f,100f,test));
        
        ppsht = new DrawnObject(ObjectBuffers.getSquareUI(-1, 1, -1, 1, 1f, 1, false),12);
        sky = new DrawnObject(ObjectBuffers.genSkyCube(), 12);
        
        print("Model and Texture Loading Completed");
        
        w = WorldManager.getDefaultWorld();
        GlobalInfo.curworld = w;
        w.floorLev = -10;

        terrain = new WorldObject();
        bad = new PhysicsComponent();
         
        awps = new WorldObject();
        awps.attach(new ModelRenderComponent(flashbang));
        awps.setPosition(new Vector3f(5,5,5));
        
        //terrain.attach(new ModelRenderComponent(test6));
        terrain.attach(bad);
        terrain.attach(p);
        
        ratio = win.getRatio();
        
        t = new Time();
        
        as = new AudioListener();
        AudioHandler.setListener(as);
        
        enable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        enable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        enable(GL_TEXTURE_2D);

        enable(GL_TEXTURE_CUBE_MAP_SEAMLESS);

        glPolygonMode( GL_FRONT_AND_BACK, GL_FILL );
        print("Setup Complete");
    }

    public void exit() {
       
        AudioHandler.destroy();
        vao.delete();
        terrain.update(1);
    }

    public void render() {
        rot = new Vector3f(yrot, xrot, 0);
        if(lock){
            rot = MovementLoader.processRotation(sens, false);
        }
        pos = MovementLoader.processMovement(pos, rot);
        
        as.setPos(pos);
        as.setRot(rot);
        AudioHandler.setListener(as);
        
        c.setPos(new Vector3f(15, -40, -10));
        c.setRot(new Vector3f(60, 50, 0));

        s.setLightPos(new Vector3f(40, 80, 40));
        s.setView(c);
        ppbf.startTexRender();
        
        s.setPerspective(90, ratio, 4, 300f);      
        s.setMode(Mode.POS_ONLY);

        c.setPos(pos);
        c.setRot(rot);

        s.setView(c);
        s.setPerspective(90, ratio, 0.3f, 2500f);
        
        s.setMode(Mode.SKYBOX);
        cb.use(2);
        sky.draw();
        
        s.setMode(Mode.OBJECT);
        
        terrain.render();
        t3.useTexture(0);
        flashbang.draw();
        
        s.setDistanceField(true);
        awp3.draw();
        
        ppbf.endTexRender();
        glDisable(GL_CULL_FACE);
        GUI.startGUIPos();
        s.setMode(Mode.PP);
        ppbf.useTexture(0);
        ppbf.useDepthTexture(1);
        
        ppsht.draw();
        GUI.enableGUI();
        base2.draw();
        s.setDistanceField(false);
    }
    
    float i = 0;
    boolean flipsd = false;
    public void update() {
        float delta = t.getDeltaSec();
        i += delta;
        s.setTimeMod(i);
        terrain.update(delta);
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
        if (key == GLFW_KEY_P) {
            if(so3.isPaused()){
                so3.play();
            }else{
                so3.pause();
            }
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
