package com.opengg.test;
import com.opengg.core.Matrix4f;
import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.input.KeyboardEventHandler;
import com.opengg.core.input.KeyboardListener;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.io.ObjLoader;
import com.opengg.core.objloader.parser.OBJFace;
import com.opengg.core.objloader.parser.OBJModel;
import com.opengg.core.objloader.parser.OBJParser;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.shader.Shader;
import com.opengg.core.shader.ShaderProgram;
import com.opengg.core.texture.Texture;
import com.opengg.core.util.ViewUtil;
import com.opengg.core.window.*;
import static com.opengg.core.window.RenderUtil.endFrame;
import static com.opengg.core.window.RenderUtil.startFrame;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
 
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
    
    {
        vertAmount = squares * 6;
        triangleAmount = vertAmount / 3;
    }
    
    public float x,y,z;
    public float xrot;
    public float rot1 = 0;
    public float xm = 0, ym=0, zm=0;

    
    Vector3f rot = new Vector3f(0,0,0);
    Vector3f pos = new Vector3f(0,0,0);
    
    boolean rotated = false;
    
    boolean backwards = false;
    
    public static void main(String[] args) {
        new OpenGGTest();
    }
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    
    Shader vertexShader, fragmentShader;
    
    private ShaderProgram program;

    private int xrotM, yrotM, zrotM;
    
    private int uniView;
    private int uniModel;

    
    Texture t1 = new Texture();
    Texture t2 = new Texture();
    Texture blank = new Texture();
    
    float speed = 0.2f;
        
    OBJModel m2;
    
    FloatBuffer awpb;
    FloatBuffer vertices;
    FloatBuffer vertices2;
    
    public OpenGGTest(){
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
            render(rot1);
            endFrame(window);
        }
        exit();
    }
    
    IntBuffer ind;
    IntBuffer elements;
    
    Matrix4f view;
    
    public void setup(){

        vao = new VertexArrayObject();
        vao.bind();

        blank.loadTexture("C:/res/blank.png");
        
        Model awpm = new Model();

        try {
            URL path = OpenGGTest.class.getResource("awp.obj");
            m2 = new OBJParser().parse(path);
            awpm = ObjLoader.loadTexturedModel(path);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
        List<Vector3f> awp = m2.getVertices();
        List<Vector3f> awpn = awpm.getNormals();
        List<Model.Face> awpf = awpm.getFaces();
        List<OBJFace> f = m2.getObjects().get(0).getMeshes().get(0).getFaces();
        
        Random random = new Random();
        
        awpb = BufferUtils.createFloatBuffer(awp.size() * 8);
        for (Vector3f awp1 : awp) {
            float colorg = random.nextFloat() % 10;
            float colorr = random.nextFloat() % 10;
            float colorb = random.nextFloat() % 10;    
            awpb.put(awp1.x ).put(awp1.y).put(awp1.z + 60).put(colorr).put(colorg).put(colorb).put(0f).put(0f);           
        }
        awpb.flip();
     
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        
        elements = BufferUtils.createIntBuffer(awpf.size()*3);

        for (OBJFace fa : f){
            int x = fa.getReferences().get(0).vertexIndex;
            int y = fa.getReferences().get(1).vertexIndex;
            int z = fa.getReferences().get(2).vertexIndex;
            elements.put(x).put(y).put(z);
        }
        
        elements.flip();
        
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
    
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

         /* Load shaders */
        vertexShader= new Shader(GL_VERTEX_SHADER, Shaders.vertexSource); 
        fragmentShader = new Shader(GL_FRAGMENT_SHADER, Shaders.fragmentSource); 
        vertexTex= new Shader(GL_VERTEX_SHADER, FileStringLoader.loadStringSequence("C:/res/sh1.vert")); 
        fragmentTex = new Shader(GL_FRAGMENT_SHADER, FileStringLoader.loadStringSequence("C:/res/sh1.frag")); 

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
        

        xrotM = program.getUniformLocation("xrot");
        program.setUniform(xrotM, 0);
        
        yrotM = program.getUniformLocation("yrot");
        program.setUniform(yrotM, 0);
        
        zrotM = program.getUniformLocation("zrot");
        program.setUniform(zrotM, 0);
        
        float ratio = win.getRatio();
        
        program.use();
        ViewUtil.setPerspective(100, ratio, 0.3f, 300f, program);

        vao.bind();      
        
        glEnable(GL_DEPTH_TEST);

        glDepthFunc(GL_LESS);
    }
    
    
    private void specifyVertexAttributes(ShaderProgram programv, boolean textured) {
        int buffersize;
        if(textured){
            buffersize = 8; 
        }else{
            buffersize = 6;
        }
        
        programv.use();
        int posAttrib = programv.getAttributeLocation("position");
        programv.enableVertexAttribute(posAttrib);
        programv.pointVertexAttribute(posAttrib, 3, 8 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = programv.getAttributeLocation("color");
        programv.enableVertexAttribute(colAttrib);
        programv.pointVertexAttribute(colAttrib, 3, 8 * Float.BYTES, 3 * Float.BYTES);
        if(textured){
            int texAttrib = programv.getAttributeLocation("texcoord");
            programv.enableVertexAttribute(texAttrib);
            programv.pointVertexAttribute(texAttrib, 2, 8 * Float.BYTES, 6 * Float.BYTES);
        }
    }
    
    public void exit() {
        vao.delete();
        vbo.delete();
        vertexShader.delete();
        program.delete();
        
    }
    
    public void render(double alpha) {

        Matrix4f move = Matrix4f.translate(x,y,z);       
        
        //program.use();
        program.setUniform(uniModel, move);
        
        program.setUniform(yrotM, xrot);
        blank.useTexture();
        
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        
        vbo.uploadData(GL_ARRAY_BUFFER, awpb, GL_STATIC_DRAW);      
        glDrawElements(GL_TRIANGLES, awpb.capacity(), GL_UNSIGNED_INT, 0);   
    }
    
    public void update(float delta) {       
        x += xm;
        y += ym;
        z += zm;
        xrot += rot1/10;
        
    }

    @Override
    public void keyPressed(int key) {
        if(key == GLFW_KEY_A){
            xm += speed;

        }
        if(key == GLFW_KEY_D){
            xm -= speed;

        }
        if(key == GLFW_KEY_W){
 
            zm += speed;
        }
        if(key == GLFW_KEY_S){

            zm -= speed;

        }
        if(key == GLFW.GLFW_KEY_LEFT_SHIFT){

            ym -= speed;
        }
        if(key == GLFW_KEY_LEFT_CONTROL){

            ym += speed;

        }
        if(key == GLFW_KEY_Q){
            rot1 += 0.3;
            
        }
        if(key == GLFW_KEY_E){
            rot1 -= 0.3;
            
        }

    }

    @Override
    public void keyReleased(int key) {
        if(key == GLFW_KEY_A){

            xm -= speed;
        }
        if(key == GLFW_KEY_D){

            xm += speed;

        }
        if(key == GLFW_KEY_W){
 
            zm -= speed;
        }
        if(key == GLFW_KEY_S){

            zm += speed;

        }
        if(key == GLFW.GLFW_KEY_LEFT_SHIFT){

            ym += speed;
        }
        if(key == GLFW_KEY_LEFT_CONTROL){

            ym -= speed;

        }
        if(key == GLFW_KEY_Q){
            rot1 -= 0.3;
            
        }
        if(key == GLFW_KEY_E){
            rot1 += 0.3;
            
        }

    }
}
