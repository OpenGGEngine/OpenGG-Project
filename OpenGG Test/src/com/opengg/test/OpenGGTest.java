package com.opengg.test;
import com.opengg.core.Matrix4f;
import com.opengg.core.Vector3f;
import com.opengg.core.audio.AudioHandler;
import com.opengg.core.buffer.ObjectBuffers;
import com.opengg.core.input.KeyboardEventHandler;
import com.opengg.core.input.KeyboardListener;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.objloader.parser.OBJModel;
import com.opengg.core.objloader.parser.OBJNormal;
import com.opengg.core.objloader.parser.OBJParser;
import com.opengg.core.render.DrawnObject;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.shader.Shader;
import com.opengg.core.shader.ShaderProgram;
import com.opengg.core.texture.Texture;
import com.opengg.core.util.ViewUtil;
import com.opengg.core.window.*;
import static com.opengg.core.window.RenderUtil.endFrame;
import static com.opengg.core.window.RenderUtil.startFrame;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
public class OpenGGTest implements KeyboardListener{
 
    // We need to strongly reference callback instances.
    static long window;
    Window win = new Window();
    boolean draw = true;
    int vertAmount;
    int triangleAmount;
    int squares = 4;
    private Shader vertexTex;
    private Shader fragmentTex;
    private int rotm;
    
    {
        vertAmount = squares * 6;
        triangleAmount = vertAmount / 3;
    }
    
    public float xrot;
    public float rot1=0;
    public float xm = 0, ym=0, zm=0;
    
    Vector3f rot = new Vector3f(0,0,0);
    Vector3f pos = new Vector3f(0,0,0);
    
    public static void main(String[] args) throws IOException {
        new OpenGGTest();
    }
    
    int ebo;
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    
    Shader vertexShader, fragmentShader;
    
    private ShaderProgram program;

    int lightpos;
    private int uniView;
    private int uniModel;
    
    Texture t1 = new Texture();
    Texture t2 = new Texture();
    Texture blank = new Texture();
    Texture blank2 = new Texture();
    
    DrawnObject test3;
    DrawnObject test4;
    DrawnObject base2;
    
    float speed = 0.2f;
    
    OBJModel m;
    OBJModel m2;
    
    public OpenGGTest() throws IOException{
        Window w = new Window();
        long window = 1;
        KeyboardEventHandler.addToPool(this);
        
        try {
            window = w.init(1280,960, "Test", DisplayMode.WINDOWED);
        } catch (Exception ex) {
            Logger.getLogger(OpenGGTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //e.enter();
        float delta;
        setup();
        while(!win.shouldClose(window)){
            
            startFrame();

            update(1);
            render();
            endFrame(window);
        }
        exit();
    }   
    int i = 1;
    
    FloatBuffer base;
    FloatBuffer test2;
    FloatBuffer test;
    
    Matrix4f view;
    
    public void setup() throws FileNotFoundException, IOException{
        MovementLoader.setup(window,60);
        
        vao = new VertexArrayObject();
        
        vao.bind();
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        
        t1.loadTexture("C:/res/tex2.png");
        t1.useTexture();        
        AudioHandler.init((int)window);
        AudioHandler.setSoundBuffer(OpenGGTest.class.getResource("res/maw.wav"));
        AudioHandler.shouldLoop(true);
        AudioHandler.play();
        try {
            URL path = OpenGGTest.class.getResource("res/awp3.obj");
            URL path2 = OpenGGTest.class.getResource("res/cessna.obj");
            m = new OBJParser().parse(path);
            m2 = new OBJParser().parse(path2);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        URL verts = OpenGGTest.class.getResource("res/sh1.vert");
        URL frags = OpenGGTest.class.getResource("res/sh1.frag");
       
        base = ObjectBuffers.getSquare(1000,1000, -1000, -4, -1000, 1f);
        test = ObjectBuffers.genBuffer(m, 1f, 0.2f);
        test2 = ObjectBuffers.genBuffer(m2, 1f, 1f);
        test3 = new DrawnObject(test,vbo);
        test4 = new DrawnObject(test2,vbo);        
        base2 = new DrawnObject(base,vbo);

        vertexTex= new Shader(GL_VERTEX_SHADER, FileStringLoader.loadStringSequence(URLDecoder.decode(verts.getFile(), "UTF-8"))); 
        fragmentTex = new Shader(GL_FRAGMENT_SHADER, FileStringLoader.loadStringSequence(URLDecoder.decode(frags.getFile(), "UTF-8"))); 

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexTex);   
        program.attachShader(fragmentTex);  
        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
        program.use();
        program.checkStatus();
  
        //specifyVertexAttributes(program2, false);
        specifyVertexAttributes(program, true);

        /* Set shader variables */
        view = new Matrix4f();
        
        program.use();
        uniModel = program.getUniformLocation("model");
        
        uniView = program.getUniformLocation("view");
        program.setUniform(uniView, view);
        
        int uniTex = program.getUniformLocation("texImage");
        program.setUniform(uniTex, 0);
        
        rotm = program.getUniformLocation("rot");
        program.setUniform(rotm, new Vector3f(0,0,0));
        
        lightpos = program.getUniformLocation("lightpos");
        program.setUniform(lightpos, new Vector3f(-100,50,-10));
        
        float ratio = win.getRatio();
        
        program.use();
        ViewUtil.setPerspective(90, ratio, 0.3f, 2000f, program);     

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
    }
    
    
    private void specifyVertexAttributes(ShaderProgram programv, boolean textured) {
        programv.use();
        int posAttrib = programv.getAttributeLocation("position");
        programv.enableVertexAttribute(posAttrib);
        programv.pointVertexAttribute(posAttrib, 3, 12 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = programv.getAttributeLocation("color");
        programv.enableVertexAttribute(colAttrib);
        programv.pointVertexAttribute(colAttrib, 4, 12 * Float.BYTES, 3 * Float.BYTES);
        
        int normAttrib = programv.getAttributeLocation("normal"); 
        programv.enableVertexAttribute(normAttrib);
        programv.pointVertexAttribute(normAttrib, 3, 12 * Float.BYTES, 7 * Float.BYTES);
        
        int texAttrib = programv.getAttributeLocation("texcoord"); 
        programv.enableVertexAttribute(texAttrib);
        programv.pointVertexAttribute(texAttrib, 2, 12 * Float.BYTES, 10 * Float.BYTES);

    }
    
    public void exit() {   
        program.delete();
        AudioHandler.destroy();
        vao.delete();
        vbo.delete();
    }
    
    public void render() {
        vao.bind();
        rot = new Vector3f(0,-xrot,0);
        
        pos = MovementLoader.processMovement(pos, rot);
        
        Matrix4f cameraR = Matrix4f.translate(pos.x,pos.y,pos.z);       
        
        Matrix4f cameraM = Matrix4f.rotate(-xrot,0,1,0);

        program.setUniform(uniView, cameraM.multiply(cameraR));      

        program.checkStatus();
        
        program.setUniform(uniModel, Matrix4f.translate(30, 0, 0));
        
         test3.draw();
        program.setUniform(uniModel, Matrix4f.translate(0, 0, 0));
        test4.draw(); 
        base2.draw();
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
