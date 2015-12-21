package com.opengg.test;

import com.opengg.core.Matrix4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioHandler;
import com.opengg.core.gui.GUI;
import com.opengg.core.io.input.KeyboardEventHandler;
import com.opengg.core.io.input.KeyboardListener;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.DrawnObject;
import com.opengg.core.render.DrawnObjectGroup;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.buffer.ObjectBuffers;
import static com.opengg.core.render.gl.GLOptions.enable;
import com.opengg.core.render.shader.Mode;
import com.opengg.core.render.shader.ShaderController;
import com.opengg.core.render.texture.Cubemap;
import com.opengg.core.render.texture.Font;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.DisplayMode;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.Window;
import com.opengg.core.util.GlobalInfo;
import com.opengg.core.world.Camera;
import com.opengg.core.world.Terrain;
import com.opengg.core.world.World;
import com.opengg.core.world.WorldManager;
import com.opengg.core.world.WorldObject;
import com.opengg.core.world.physics.MainLoop;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS;

public class OpenGGTest implements KeyboardListener {

    static long window;
    Window win = new Window();
    boolean draw = true;
    private float ratio;

    public float xrot, yrot;
    public float rot1 = 0, rot2 = 0;
    public float xm = 0, ym = 0, zm = 0;
    int quads;
    Vector3f rot = new Vector3f(0, 0, 0);
    Vector3f pos = new Vector3f(0, -10, -30);
    
    World w;
    
    public static void main(String[] args) throws IOException, Exception {
        new OpenGGTest();
    }

    private VertexArrayObject vao;
    private VertexBufferObject vbo;

    Camera c;
    GUI g = new GUI();
    Texture t1 = new Texture();
    Texture t2 = new Texture();

    DrawnObject awp3, flashbang, test5, base2, sky;
    DrawnObjectGroup test6;

    float speed = 0.2f;

    OBJModel m;
    OBJModel m2;
    private Font f;
    private Texture t3 = new Texture();
    private Cubemap cb = new Cubemap();
    private ShaderController s = new ShaderController();
    
    WorldObject w1, w2;
    
    public OpenGGTest() throws IOException, Exception {
        Window w = new Window();
        KeyboardEventHandler.addToPool(this);

        new Thread(() -> {
            MainLoop.process();
        }).start();
 
        try {
            window = w.init(1280, 1024, "Test", DisplayMode.WINDOWED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        setup();
        while (!win.shouldClose(window)) {

            startFrame();

            update(1);
            render();
            endFrame(window);
        }
        
        MainLoop.killProcess();
        exit();
    }

    FloatBuffer base, test2, test;

    public void setup() throws FileNotFoundException, IOException, Exception {
        MovementLoader.setup(window, 80);

        vao = new VertexArrayObject();
        vao.bind();
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        GlobalInfo.b = vbo;

        t1.setupTexToBuffer(2048);
        t3.loadTexture("C:/res/deer.png", true);
        f = new Font("", "thanks dad", 11);

        t2.loadFromBuffer(f.asByteBuffer(), (int) f.getFontImageWidth(), (int) f.getFontImageHeight());
        t2.useTexture(0);
        cb.loadTexture("C:/res/skybox/majestic");

        AudioHandler.init(1);
        AudioHandler.setSoundBuffer(OpenGGTest.class.getResource("res/maw.wav"));
        AudioHandler.shouldLoop(true);
        //AudioHandler.play();

        try {
            URL path = OpenGGTest.class.getResource("res/models/deer.obj");
            URL path2 = OpenGGTest.class.getResource("res/models/flashbang.obj");
            m = new OBJParser().parse(path);
            m2 = new OBJParser().parse(path2);
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        InputStream heightmap = OpenGGTest.class.getResource("res/heightmap.png").openStream();

        URL verts = OpenGGTest.class.getResource("res/shaders/shader.vert");
        URL frags = OpenGGTest.class.getResource("res/shaders/shader.frag");

        
        s.setup(win, verts, frags);
        GlobalInfo.main = s;
        
        c = new Camera(pos, rot);
        c.setPos(pos);
        c.setRot(rot);

        g.setupGUI(new Vector2f(-3, -3), new Vector2f(3, 3));

        test = ObjectBuffers.genBuffer(m, 1f, 0.2f);
        test2 = ObjectBuffers.genBuffer(m2, 1f, 1f);

        awp3 = new DrawnObject(test, 12);
        flashbang = new DrawnObject(test2, 12);

        test2 = ObjectBuffers.getSquareUI(1, 3, 1, 3, -1, 1f, false);
        test5 = new DrawnObject(test2, 12);

        //test6 = new DrawnObjectGroup(OpenGGTest.class.getResource("res/models/Mansion.obj"), 1);

        test2 = ObjectBuffers.genSkyCube();
        sky = new DrawnObject(test2, 12);
        
        w = WorldManager.getDefaultWorld();
        
        w.floorLev = -10;
        w.addObject(w1 = new WorldObject(awp3));
        w.addObject(w2 = new WorldObject(flashbang));
        w1.setPos(new Vector3f(0,30,0));
        w2.setPos(new Vector3f(0,0,0));
        
        awp3.removeBuffer();
        flashbang.removeBuffer();
        Terrain base = new Terrain(0, 0, t1);
        base2 = new DrawnObject(base.generateTerrain(heightmap), 12);
        base.removeBuffer();

        ratio = win.getRatio();
        
        base2.setModel(Matrix4f.translate(-50, 0, -100));


        enable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        enable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);

        enable(GL_TEXTURE_2D);

        enable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
    }

    public void exit() {

        AudioHandler.destroy();
        vao.delete();
        vbo.delete();
    }

    public void render() {
        rot = new Vector3f(-yrot, -xrot, 0);
        pos = MovementLoader.processMovement(pos, rot);

        c.setPos(new Vector3f(15, -40, -10));
        c.setRot(new Vector3f(60, 50, 0));

        s.setLightPos(new Vector3f(30, 40, 50));
        s.setView(c);

        s.setPerspective(90, ratio, 4, 300f); 
        
        s.setMode(Mode.OBJECT);
        t1.startTexRender();
        t3.useTexture(0);
        
        awp3.saveShadowMVP();
        awp3.draw();
        
        flashbang.saveShadowMVP();
        flashbang.draw();

        base2.setModel(Matrix4f.translate(-50, 0, -100));
        base2.saveShadowMVP();
        base2.draw();

        t1.endTexRender();

        t1.useDepthTexture(2);
        c.setPos(pos);
        c.setRot(rot);

        s.setView(c);
        s.setPerspective(90, ratio, 0.3f, 2000f);
        
        s.setMode(Mode.SKYBOX);
        
        cb.use();
        sky.draw();
        
        s.setMode(Mode.OBJECT);
        awp3.drawShaded();

        flashbang.drawShaded();

        t1.useDepthTexture(0);
        base2.drawShaded();

        g.startGUI();

        t1.useDepthTexture(0);
        test5.draw();
    }

    public void update(float delta) {
        xrot += rot1 * 5;
        yrot += rot2 * 5;
        
        awp3.setModel(Matrix4f.translate(w1.getEntity().pos));
        flashbang.setModel(Matrix4f.translate(w2.getEntity().pos));
    }

    @Override
    public void keyPressed(int key) {

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
