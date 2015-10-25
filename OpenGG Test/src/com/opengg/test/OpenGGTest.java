package com.opengg.test;
import com.opengg.core.Matrix4f;
import com.opengg.core.Vector2f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioHandler;
import com.opengg.core.gui.GUI;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.io.input.KeyboardEventHandler;
import com.opengg.core.io.input.KeyboardListener;
import com.opengg.core.io.objloader.parser.OBJModel;
import com.opengg.core.io.objloader.parser.OBJParser;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.DrawnObject;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.render.buffer.ObjectBuffers;
import com.opengg.core.render.shader.Shader;
import com.opengg.core.render.shader.ShaderProgram;
import com.opengg.core.render.shader.premade.ObjectShader;
import com.opengg.core.render.texture.Texture;
import com.opengg.core.render.window.DisplayMode;
import static com.opengg.core.render.window.RenderUtil.endFrame;
import static com.opengg.core.render.window.RenderUtil.startFrame;
import com.opengg.core.render.window.ViewUtil;
import com.opengg.core.render.window.Window;
import com.opengg.core.world.Camera;
import com.opengg.core.world.Terrain;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import com.opengg.core.render.texture.Font;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
public class OpenGGTest implements KeyboardListener{
 
    static long window;
    Window win = new Window();
    boolean draw = true;
    private Shader vertexTex;
    private Shader fragmentTex;
    private int rotm;
    private float ratio;

    public float xrot;
    public float rot1=0;
    public float xm = 0, ym=0, zm=0;
    int quads;
    Vector3f rot = new Vector3f(0,0,0);
    Vector3f pos = new Vector3f(0,0,0);
    
    public static void main(String[] args) throws IOException, Exception {
        new OpenGGTest();
    }
    
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    
    Shader vertexShader, fragmentShader;
    
    ObjectShader sh;
    private int uniModel, lightpos, div;
    Camera c;
    GUI g = new GUI();
    Texture t1 = new Texture();
    Texture t2 = new Texture();
    Texture blank = new Texture();
    Texture blank2 = new Texture();
    
    DrawnObject test3;
    DrawnObject test4;
    DrawnObject test5;
    DrawnObject base2;
    
    float speed = 0.2f;
    
    OBJModel m;
    OBJModel m2;
    private DrawnObject test6;
    
    public OpenGGTest() throws IOException, Exception{
        Window w = new Window();
        KeyboardEventHandler.addToPool(this);
        
        try {
            window = w.init(1280,1024, "Test", DisplayMode.WINDOWED);
        } catch (Exception ex) {
            Logger.getLogger(OpenGGTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setup();
        while(!win.shouldClose(window)){
            
            startFrame();

            update(1);
            render();
            endFrame(window);
        }
        exit();
    }   
    
    FloatBuffer base;
    FloatBuffer test2;
    FloatBuffer test;
    
    Matrix4f view;
    
    public void setup() throws FileNotFoundException, IOException, Exception{
        String text = "hi";
        MovementLoader.setup(window,80);

        
        sh.setup(win);
        

        vao = new VertexArrayObject();
        vao.bind();
        
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        
        t1.setupTexToBuffer();
        t2.loadTexture("C:/res/trump.png");
        t2.useTexture();   
        
//        AudioHandler.init(1);
//        AudioHandler.setSoundBuffer(OpenGGTest.class.getResource("res/maw.wav"));
//        AudioHandler.shouldLoop(true);
//        AudioHandler.play();
        try {
            URL path = OpenGGTest.class.getResource("res/awp3.obj");
            URL path2 = OpenGGTest.class.getResource("res/flashbang.obj");
            m = new OBJParser().parse(path);
            m2 = new OBJParser().parse(path2);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        InputStream s = OpenGGTest.class.getResource("res/trump.png").openStream();
        
        test = ObjectBuffers.genBuffer(m, 1f, 0.2f);
        test2 = ObjectBuffers.genBuffer(m2, 1f, 1f);
        test3 = new DrawnObject(test,vbo);
        test4 = new DrawnObject(test2,vbo); 
        test2 = ObjectBuffers.getSquareUI(1, 3, 1, 3, -1, 1f);
        test5 = new DrawnObject(test2,vbo);
        test2 = ObjectBuffers.getSquareUI(-3, -1, -3,- 1, -1, 1f);
        test6 = new DrawnObject(test2,vbo);
        test3.removeBuffer();
        test4.removeBuffer();
        Terrain base = new Terrain(0,0,t1);
        base2 = new DrawnObject(base.generateTerrain(s),vbo);
        base.removeBuffer();

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        
        glEnable(GL_TEXTURE_2D);  
        
//        glEnable(GL_CULL_FACE);
//        glCullFace(GL_BACK);
    }
    
    
    
    
    public void exit() {   
        //AudioHandler.destroy();
        vao.delete();
        vbo.delete();
    }
    
    public void render() {
        rot = new Vector3f(0,-xrot,0);      
        pos = MovementLoader.processMovement(pos, rot);
        rot = new Vector3f(-xrot,0,0); 
        
        c.setPos(new Vector3f(5,0,30));
        c.setRot(new Vector3f(170,0,0));
        c.use();
        
        ViewUtil.setOrtho(-10, 10, -10, 10, 0.3f, 50, sh.getProgram());
        
        t1.startTexRender();
        t2.useTexture();
        //program.setUniform(div, 20f);
        test3.draw();
        test4.draw();
        base2.draw();
        t1.endTexRender();
                
        c.setPos(pos);
        c.setRot(rot);
        c.use();
        //program.setUniform(div, 1f);
        ViewUtil.setPerspective(90, ratio, 0.3f, 2000f, sh.getProgram());  
        test3.draw();
        test4.draw();
        base2.draw();
        
        g.startGUI();
        
        t1.useDepthTexture();
        test5.draw();

        
        t2.useTexture();
        test6.draw();

    }
    
    public void update(float delta) {       
        xrot += rot1*5;
    }

    @Override
    public void keyPressed(int key) {
       
        if(key == GLFW_KEY_Q){
            rot1 += 0.3;
            
        }
        if(key == GLFW_KEY_E){
            rot1 -= 0.3;
            
        }

    }

    @Override
    public void keyReleased(int key) {
        
        if(key == GLFW_KEY_Q){
            rot1 -= 0.3;
            
        }
        if(key == GLFW_KEY_E){
            rot1 += 0.3;
            
        }

    }
}
