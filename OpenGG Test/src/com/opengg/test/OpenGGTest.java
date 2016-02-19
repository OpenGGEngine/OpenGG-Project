package com.opengg.test;

import com.opengg.core.Matrix4f;
import com.opengg.core.Quaternion4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioHandler;
import com.opengg.core.components.ModelRenderComponent;
import com.opengg.core.gui.GUI;
import com.opengg.core.io.input.KeyboardEventHandler;
import com.opengg.core.io.input.KeyboardListener;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.drawn.DrawnObject;
import com.opengg.core.render.drawn.DrawnObjectGroup;
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
import static com.opengg.core.util.GlobalUtil.print;
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
    Texture blank = new Texture();
    Texture t1 = new Texture();
    Texture t2 = new Texture();
    
    DrawnObject test5, base2;
    
    float speed = 0.2f;
    
    OBJModel m;
    OBJModel m2;
    private Font f;
    private Texture t3 = new Texture();
    private Cubemap cb = new Cubemap();
    private ShaderController s = new ShaderController();
    double lastTime = System.nanoTime() / 1000000;
    int nbFrames = 0;
    DrawnObject testsky;
    WorldObject awp3, flashbang, drawnobjectgroup, sky;
    
    public OpenGGTest() throws IOException, Exception {
        
        KeyboardEventHandler.addToPool(this);
        
        new Thread(() -> {
            MainLoop.process();
        }).start();
        
        try {
            window = win.init(960, 640, "Test", DisplayMode.WINDOWED);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        setup();
        while (!win.shouldClose(window)) {
            
            startFrame();
            
            update(1);
            render();
            endFrame(win);
        }
        
        exit();
    }
    
    public void setup() throws FileNotFoundException, IOException, Exception {
        MovementLoader.setup(window, 80);
        blank.loadTexture("C:/res/blank.png", true);
        Texture.blank = blank;
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
        cb.loadTexture("C:/res/skybox/better");
        
        //AudioHandler.init(1);
        //AudioHandler.setSoundBuffer(OpenGGTest.class.getResource("res/maw.wav"));
        //AudioHandler.shouldLoop(true);
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
        
        s.setup(verts, frags);
        GlobalInfo.main = s;
        
        c = new Camera(pos, rot);
        c.setPos(pos);
        c.setRot(rot);
        
        g.setupGUI(new Vector2f(-3, -3), new Vector2f(3, 3));
        
        test5 = new DrawnObject(ObjectBuffers.getSquareUI(1, 3, 1, 3, -1, 1f, false), 12);
        
        w = WorldManager.getDefaultWorld();
        
        w.floorLev = -10;
        DrawnObject d = new DrawnObject(ObjectBuffers.genBuffer(m, 1f, 0.2f, new Vector3f()), 12);
        d.setTexture(t3);
        awp3 = new WorldObject();
        flashbang = new WorldObject();
        drawnobjectgroup = new WorldObject();
        ModelRenderComponent a,b,e;
        a = new ModelRenderComponent(d);
        d.destroy();
        
        awp3.attach(a);
        w.addObject(awp3);
        DrawnObject s = new DrawnObject(ObjectBuffers.genBuffer(m2, 1f, 1f, new Vector3f()), 1);
        s.setTexture(t3);
        b = new ModelRenderComponent(s);
       
        flashbang.attach(b);
       
        w.addObject(flashbang);
        e = new ModelRenderComponent(new DrawnObjectGroup(OpenGGTest.class.getResource("res/models/moon.obj"), 0.1f));
        drawnobjectgroup.attach(e);
        w.addObject(drawnobjectgroup);
        drawnobjectgroup.setRot(new Vector3f(0,100,100));
        drawnobjectgroup.setPos(new Vector3f(0,1600,1));
      
        testsky = new DrawnObject(ObjectBuffers.genSkyCube(), 12);
       // w.setSkybox(sky = new WorldObject(testsky));
        awp3.setPos(new Vector3f(0, 30, 0));
        
        flashbang.setPos(new Vector3f(0, 0, 0));
        drawnobjectgroup.setPos(new Vector3f(0, 0, 0));
        
        Terrain base = new Terrain(0, 0, t1);
        base.generateTerrain(heightmap);
        base2 = new DrawnObject(base.elementals, vbo, base.indices);
        base2.setTexture(t3);
        
        base.removeBuffer();
        
        ratio = win.getRatio();
        
        enable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        
        enable(GL_DEPTH_TEST);
        glDepthFunc(GL_LEQUAL);
        
        enable(GL_TEXTURE_2D);
        
        enable(GL_TEXTURE_CUBE_MAP_SEAMLESS);
    }
    
    public void exit() {
        MainLoop.killProcess();
//        AudioHandler.destroy();
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
        
        
        
        awp3.render();
        
        flashbang.render();
        
       
        flashbang.render();
        base2.saveShadowMVP();
       
        base2.draw();
        
        t1.endTexRender();
        
        t1.useDepthTexture(2);
        c.setPos(pos);
        c.setRot(rot);
        
        s.setView(c);
        s.setPerspective(90, ratio, 0.3f, 2500f);
        
        s.setMode(Mode.SKYBOX);
        c.setPos(new Vector3f(0,0,0));
        s.setView(c);
        cb.use();
        c.setPos(pos);
        
        testsky.draw();
        
        s.setMode(Mode.OBJECT);
        s.setView(c);
        awp3.render();
        
        flashbang.render();
        drawnobjectgroup.render();
        
        base2.drawShaded();
        
        g.startGUI();
        
        t1.useDepthTexture(0);
        test5.draw();
      
    }
    
    public void update(float delta) {
         
       
       if ( (System.nanoTime() / 1000000) - lastTime >= 1000 ){ // If last prinf() was more than 1 sec ago
          // printf and reset timer
          glfwSetWindowTitle(window,Double.toString(nbFrames)+" fps");
          nbFrames = 0;
          lastTime += 1000;
      }
      nbFrames++;  

        //sky.setRot(new Quaternion4f(80, 0, 0, 100));
        xrot += rot1 * 5;
        yrot += rot2 * 5;
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