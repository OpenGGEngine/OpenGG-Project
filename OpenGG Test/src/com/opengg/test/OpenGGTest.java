package com.opengg.test;

import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioHandler;
import com.opengg.core.audio.AudioListener;
import com.opengg.core.audio.AudioSource;
import com.opengg.core.engine.WorldManager;
import com.opengg.core.gui.GUI;
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
import static com.opengg.core.render.gl.GLOptions.enable;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Font;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.DisplayMode;
import com.opengg.core.render.window.GLFWWindow;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.util.Time;
import com.opengg.core.world.Camera;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldObject;
import com.opengg.core.world.components.ModelRenderComponent;
import com.opengg.core.world.components.PhysicsComponent;
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
    }

    private VertexArrayObject vao;

    Camera c;
    GUI g = new GUI();
    Texture t1 = new Texture();
    Texture t2 = new Texture();
    Texture ppbf = new Texture();
    InstancedDrawnObject flashbang;
    DrawnObject test5, base2, sky;
    DrawnObjectGroup test6, awp3;

    OBJModel m;
    OBJModel m2;
    private Font f;
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
        MovementLoader.setup(window, 80);

        vao = new VertexArrayObject();
        vao.bind();
        

        AudioHandler.init(1);
        so = AudioHandler.loadSound(OpenGGTest.class.getResource("res/maw.wav"));

        so2 = AudioHandler.loadSound(OpenGGTest.class.getResource("res/mgs.wav"));
        
        so3 = AudioHandler.loadSound(OpenGGTest.class.getResource("res/stal.wav"));

        URL verts = OpenGGTest.class.getResource("res/shaders/shader.vert");
        URL frags = OpenGGTest.class.getResource("res/shaders/shader.frag");
        URL geoms = OpenGGTest.class.getResource("res/shaders/shader.geom");
        
        s.setup(verts, frags, geoms);
        
        c = new Camera(pos, rot);
        c.setPos(pos);
        c.setRot(rot);

        g.setupGUI(new Vector2f(-3, -3), new Vector2f(3, 3));
        
        System.out.println("Shader/VAO Loading and Generation Complete");
        
        t1.setupTexToBuffer(2000,2000);
        ppbf.setupTexToBuffer(win.getWidth(), win.getHeight());
        t3.loadTexture("C:/res/deer.png", true);
        f = new Font("", "thanks dad", 11);

        t2.loadFromBuffer(f.asByteBuffer(), (int) f.getFontImageWidth(), (int) f.getFontImageHeight());
        t2.useTexture(0);
        cb.loadTexture("C:/res/skybox/majestic");
        
        URL path = OpenGGTest.class.getResource("res/models/deer.obj");
        URL path2 = OpenGGTest.class.getResource("res/models/awp3.obj");
        m = new OBJParser().parse(path);
        m2 = new OBJParser().parse(path2);
        
        test = ObjectBuffers.genBuffer(m, 1f, 0.2f, new Vector3f());
        test2 = ObjectBuffers.genBuffer(m2, 1f, 1f, new Vector3f());
        test6 = OBJ.getDrawableModel("C:/res/3DSMusicPark/3DSMusicPark.obj");
        
        FloatBuffer b = BufferUtils.createFloatBuffer(12);
        b.put(20).put(20).put(20).put(20).put(40).put(40)
                .put(40).put(40).put(60).put(60).put(60).put(60);
        b.flip();
        
        flashbang = new InstancedDrawnObject(test2, b);

        test2 = ObjectBuffers.getSquareUI(1, 3, 1, 3, -1, 1f, false);
        //test5 = new DrawnObject(test2, 12);
        ppsht = new DrawnObject(ObjectBuffers.getSquareUI(-1, 1, -1, 1, .6f, 1, false),12);
        test2 = ObjectBuffers.genSkyCube();
        sky = new DrawnObject(test2, 12);
        
        System.out.println("Model and Texture Loading Completed");
        
        w = WorldManager.getDefaultWorld();
        GlobalInfo.curworld = w;
        w.floorLev = -10;
        w.addObject(w1 = new WorldObject(awp3));
        w.addObject(w2 = new WorldObject(flashbang));

        flashbang.removeBuffer();
        ModelRenderComponent m = new ModelRenderComponent(test6);
        ModelRenderComponent l = new ModelRenderComponent(flashbang);

        l.setPosition(new Vector3f(10,30,0));

        terrain = new WorldObject();
        awps = new WorldObject();
        awps.attach(l);
        terrain.attach(m);
        
        bad = new PhysicsComponent(terrain);
        terrain.attach(bad);
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
        System.out.println("Setup Complete");
    }

    public void exit() {
       
        AudioHandler.destroy();
        vao.delete();
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
        
        //glEnable(GL_CULL_FACE);
        c.setPos(new Vector3f(15, -40, -10));
        c.setRot(new Vector3f(60, 50, 0));

        s.setLightPos(new Vector3f(40, 80, 40));
        s.setView(c);
        ppbf.startTexRender();
        
        s.setPerspective(90, ratio, 4, 300f);      
        s.setMode(Mode.POS_ONLY);
        s.setMode(Mode.OBJECT);
        
        c.setPos(pos);
        c.setRot(rot);

        s.setView(c);
        s.setPerspective(90, ratio, 0.3f, 2500f);
        
        terrain.render();
        t3.useTexture(0);
        flashbang.draw();
        s.setMode(Mode.SKYBOX);
        cb.use(2);
        sky.draw();
        
        s.setMode(Mode.GUI);
        g.startGUI();
        ppbf.endTexRender();
        glDisable(GL_CULL_FACE);
        s.setMode(Mode.PP);
        s.setOrtho(-1, 1, -1, 1, -1, 1);
        s.setView(new Camera());
        ppbf.useTexture(0);
        ppbf.useDepthTexture(1);
        ppsht.draw();
    }

    public void update() {
        float delta = t.getDeltaSec();
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
