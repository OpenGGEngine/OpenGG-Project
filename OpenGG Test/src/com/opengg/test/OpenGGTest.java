package com.opengg.test;
import com.opengg.core.Matrix4f;
import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.texture.Texture;
import com.opengg.core.input.KeyboardEventHandler;
import com.opengg.core.input.KeyboardListener;
import com.opengg.core.io.FileStringLoader;
import com.opengg.core.io.ObjLoader;
import com.opengg.core.movement.MovementLoader;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.shader.Shader;
import com.opengg.core.shader.ShaderProgram;
import com.opengg.core.util.Time;
import com.opengg.core.util.ViewUtil;
 
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import com.opengg.core.window.*;
import static com.opengg.core.window.RenderUtil.endFrame;
import static com.opengg.core.window.RenderUtil.startFrame;
import static com.opengg.test.Shaders.vertices;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
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
    int uniView;
    public float x,y,z;
    public float xrot;
    public float rot1 = 0;
    public float xm = 0, ym=0, zm=0;
    private float angle = 0f;
    private float anglePerSecond = 10f;
    
    Vector3f rot = new Vector3f(0,0,0);
    Vector3f pos = new Vector3f(0,0,0);
    
    
    boolean backwards = false;
    
    public static void main(String[] args) {
        new OpenGGTest();
    }
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    
    Shader vertexShader, fragmentShader;
    
    private ShaderProgram program;
    private ShaderProgram program2;
    
    private int uniModel;
    private float previousAngle;
    
    Texture t1 = new Texture();
    Texture t2 = new Texture();
    Texture blank = new Texture();
    
    float speed = 0.2f;
        
    FloatBuffer awpb;
    FloatBuffer vertices;
    FloatBuffer vertices2;
    
    public OpenGGTest(){
        Window w = new Window();
        long window = 1;
        
        KeyboardEventHandler.addToPool(this);
        
        try {
            window = w.init(640,480, "Test", DisplayMode.WINDOWED);
        } catch (Exception ex) {
            Logger.getLogger(OpenGGTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //e.enter();
        float delta;
        setup();
        while(!win.shouldClose(window)){
            
            startFrame();
            //delta = Time.getDelta();
            update(1);
            render(rot1);
            endFrame(window);
        }
        exit();
    }
    
    IntBuffer ind;
    IntBuffer elements;

    
    public void setup(){
        //MovementLoader.setup(window);
        /* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();
        
//        t1.loadTexture("C:/res/tex1.png");
//        t2.loadTexture("C:/res/tex2.png");
        blank.loadTexture("C:/res/blank.png");
        
        Model awpm = new Model();
        try {
            awpm = ObjLoader.loadTexturedModel("C:/res/engineblock.obj");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        List<Vector3f> awp = awpm.getVertices();
        List<Vector3f> awpn = awpm.getNormals();
        List<Model.Face> awpf = awpm.getFaces();

        Random random = new Random();
        
        awpb = BufferUtils.createFloatBuffer(awp.size() * 8);
        for (Vector3f awp1 : awp) {
            float colorg = random.nextFloat() % 10;
            float colorr = random.nextFloat() % 10;
            float colorb = random.nextFloat() % 10;
            //float color = 0.6f;        
            awpb.put(awp1.x).put(awp1.y).put(awp1.z).put(colorr).put(colorg).put(colorb).put(0f).put(0f);

        }
        awpb.flip();
     
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        
        elements = BufferUtils.createIntBuffer(awpf.size()*3);
        for (Model.Face awpf1 : awpf) {
            int[] ind = awpf1.getVertexIndices();
            elements.put(ind[0]).put(ind[1]).put(ind[2]);
        }
        elements.flip();
        
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        
        vertices = BufferUtils.createFloatBuffer(4 * 8);
        
        vertices.put(-2).put(-2).put(-1f).put(0f).put(1f).put(0f).put(0f).put(0f); 
        vertices.put(2).put(-2).put(-1f).put(0f).put(0f).put(1f).put(1f).put(0f);
        vertices.put(2).put(2).put(-1f).put(1f).put(0f).put(0f).put(1f).put(1f);
        vertices.put(-2).put(2).put(-1f).put(0f).put(0f).put(0f).put(0f).put(1f);
        
        vertices.flip();

        ind = BufferUtils.createIntBuffer(6);
        
        ind.put(0).put(1).put(2).put(2).put(3).put(0);
        
        ind.flip();
        
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
        Matrix4f view = new Matrix4f();
        
        program.use();
        uniModel = program.getUniformLocation("model");
        
        uniView = program.getUniformLocation("view");
        program.setUniform(uniView, view);
        
        int uniTex = program.getUniformLocation("texImage");
        program.setUniform(uniTex, 0);
        
        

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
        


     
        Matrix4f model = Matrix4f.rotate(xrot, 0f, 1f, 0f);
        Matrix4f move = Matrix4f.translate(x,y,z);
     
        //program.use();
        program.setUniform(uniModel, move.add(model));
        
        blank.useTexture();
        
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        
        vbo.uploadData(GL_ARRAY_BUFFER, awpb, GL_STATIC_DRAW);      
        glDrawElements(GL_TRIANGLES, awpb.capacity(), GL_UNSIGNED_INT, 0);
        //glDrawArrays(GL_TRIANGLES, 0, awpb.capacity());
        
        //glBufferData(GL_ELEMENT_ARRAY_BUFFER, ind, GL_STATIC_DRAW);
        //vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        //glDrawElements(GL_TRIANGLES, 4, GL_UNSIGNED_INT, 0);
    }
    
    public void update(float delta) {       
        x += xm;
        y += ym;
        z += zm;
        xrot += rot1;
        
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
            rot1 += 3;
            
        }
        if(key == GLFW_KEY_E){
            rot1 -= 3;
            
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
            rot1 -= 0.6;
            
        }
        if(key == GLFW_KEY_E){
            rot1 += 0.6;
            
        }
    }
}
