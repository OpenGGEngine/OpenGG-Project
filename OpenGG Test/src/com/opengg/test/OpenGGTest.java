package com.opengg.test;
import com.opengg.core.Matrix4f;
import com.opengg.core.Model;
import com.opengg.core.Vector3f;
import com.opengg.core.texture.Texture;
import com.opengg.core.input.KeyboardEventHandler;
import com.opengg.core.input.KeyboardListener;
import com.opengg.core.io.ObjLoader;
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
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
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
    private int uniView2;
    private int uniModel2;
    
    {
        vertAmount = squares * 6;
        triangleAmount = vertAmount / 3;
    }
    int uniView;
    public float x,y,z;
    public float xm = 0, ym=0, zm=0;
    private float angle = 0f;
    private float anglePerSecond = 10f;

    double rot1 = 0;
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
            delta = Time.getDelta();
            update(delta / 1000);
            render(rot1);
            endFrame(window);
        }
        exit();
    }
    public void setup(){
        /* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();
        
        t1.loadTexture("C:/res/tex1.png");
        t2.loadTexture("C:/res/tex2.png");
        blank.loadTexture("C:/res/blank.png");
        
        Model awpm = ObjLoader.loadModel("C:/res/awp.obj");
        List<Vector3f> awp = awpm.getVertices();
        
        awpb = BufferUtils.createFloatBuffer(awp.size() * 8);
        
        for (Vector3f awp1 : awp) {
            awpb.put(awp1.x/2).put(awp1.y/2).put(awp1.z/2 + 20).put(0.6f).put(0.6f).put(0.6f).put(0f).put(0f);
        }
        
        awpb.flip();
     
        int ebo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
        
        IntBuffer elements = BufferUtils.createIntBuffer(awp.size());

        for(int i = 0; i < awp.size()/6; i++){
            elements.put(0 + (i*4)).put(1 + (i*4)).put(2 + (i*4)).put(2 + (i*4)).put(3 + (i*4)).put(0 + (i*4));
        }
        elements.flip();
        
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);
        

        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);

         /* Load shaders */
        vertexShader= new Shader(GL_VERTEX_SHADER, Shaders.vertexSource); 
        fragmentShader = new Shader(GL_FRAGMENT_SHADER, Shaders.fragmentSource); 
        vertexTex= new Shader(GL_VERTEX_SHADER, Shaders.vertexTex); 
        fragmentTex = new Shader(GL_FRAGMENT_SHADER, Shaders.fragmentTex); 

        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexTex);   
        program.attachShader(fragmentTex);  
        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
        program.use();
        program.checkStatus();
        
        program2 = new ShaderProgram();
        program2.attachShader(vertexShader);   
        program2.attachShader(fragmentShader);  
        program2.bindFragmentDataLocation(0, "fragColor");
        program2.link();
        
        program2.checkStatus();
        
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
        
        
//        program2.use();
//        uniView2 = program2.getUniformLocation("view");
//        
//        program2.setUniform(uniView, view);
//        uniModel2 = program2.getUniformLocation("model");
        
        

        float ratio = win.getRatio();
        
        program.use();
        ViewUtil.setPerspective(100, ratio, 0.3f, 300f, program);
        
//        program2.use();
//        ViewUtil.setPerspective(100, ratio, 0.3f, 300f, program2);

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
        

        float lerpAngle;
        if(rot1 != 0){
            lerpAngle = (float) ((1f - alpha) * previousAngle + alpha * angle);
        }else{
            lerpAngle = 0;
        }
        if(backwards){
            lerpAngle = -lerpAngle;
        }

        //vao.bind();
        //program.use();

        Matrix4f model = Matrix4f.rotate(lerpAngle, 0f, 1f, 0f);
        Matrix4f move = Matrix4f.translate(x, y, z);
        //Matrix4f scale = Matrix4f.scale(0.4f, 0.4f, 0.4f);
        Matrix4f end = move.add(model);
        
        program.use();
        program.setUniform(uniModel, move);
        
        blank.useTexture();
        
        vbo.uploadData(GL_ARRAY_BUFFER, awpb, GL_STATIC_DRAW);      
        glDrawElements(GL_TRIANGLES, awpb.capacity(), GL_UNSIGNED_INT, 0);
        
    }
    
    public void update(float delta) {
        previousAngle = angle;
        angle += delta * anglePerSecond;
        x += xm;
        y += ym;
        z += zm;
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
            rot1 += 0.6;
            
        }
        if(key == GLFW_KEY_E){
            rot1 -= 0.6;
            
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
