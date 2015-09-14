package com.opengg.test;
import com.opengg.core.Matrix4f;
import com.opengg.core.input.KeyboardEventHandler;
import com.opengg.core.input.KeyboardListener;
import com.opengg.core.input.MousePosHandler;
import com.opengg.core.render.VertexArrayObject;
import com.opengg.core.render.VertexBufferObject;
import com.opengg.core.shader.Shader;
import com.opengg.core.shader.ShaderProgram;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;
 
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import com.opengg.core.window.*;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
public class OpenGGTest implements KeyboardListener{
 
    // We need to strongly reference callback instances.
    static long window;
    Window win = new Window();

    int uniView;
    public float x,y,z;
    
    private float angle = 0f;
    private float anglePerSecond = 10f;
    
    private final CharSequence vertexSource
            = "#version 150 core\n"
            + "\n"
            + "in vec3 position;\n"
            + "in vec3 color;\n"
            + "\n"
            + "out vec3 vertexColor;\n"
            + "\n"
            + "uniform mat4 model;\n"
            + "uniform mat4 view;\n"
            + "uniform mat4 projection;\n"
            + "\n"
            + "void main() {\n"
            + "    vertexColor = color;\n"
            + "    mat4 mvp = projection * view * model;\n"
            + "    gl_Position = mvp * vec4(position, 1.0);\n"
            + "}";
    private final CharSequence fragmentSource
            = "#version 150 core\n"
            + "\n"
            + "in vec3 vertexColor;\n"
            + "\n"
            + "out vec4 fragColor;\n"
            + "\n"
            + "void main() {\n"
            + "    fragColor = vec4(vertexColor, 1.0);\n"
            + "}";
    
    /**
     * @param args the command line arguments
     */
    double rot1 = 0;
    boolean backwards = false;
    
    public static void main(String[] args) {
        new OpenGGTest();
    }
    private VertexArrayObject vao;
    private VertexBufferObject vbo;
    Shader vertexShader, fragmentShader;
    private ShaderProgram program;
    private int uniModel;
    private float previousAngle;
    
    public OpenGGTest(){
        Window w = new Window();
        long window = 1;
        KeyboardEventHandler.addToPool(this);
        
        try {
            window = w.init(640,480, "Test", DisplayMode.WINDOWED);
        } catch (Exception ex) {
            Logger.getLogger(OpenGGTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        GL.setCurrent(GLContext.createFromCurrent());
        //e.enter();
        setup();
        while(GLFW.glfwWindowShouldClose(window) == GL_FALSE){
            //e.render(1);
            update(1);
            render(rot1);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
        exit();
    }
    public void setup(){
        /* Generate Vertex Array Object */
        vao = new VertexArrayObject();
        vao.bind();

        /* Vertex data */
        FloatBuffer vertices = BufferUtils.createFloatBuffer(3 * 6);
        vertices.put(-0.6f).put(-0.4f).put(0f).put(1f).put(0f).put(0f);
        vertices.put(0.6f).put(-0.4f).put(0f).put(0f).put(1f).put(0f);
        vertices.put(0f).put(0.4f).put(0f).put(0f).put(1f).put(1f);
        vertices.flip();

        /* Generate Vertex Buffer Object */
        vbo = new VertexBufferObject();
        vbo.bind(GL_ARRAY_BUFFER);
        vbo.uploadData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        
         /* Load shaders */
        vertexShader= new Shader(GL_VERTEX_SHADER, vertexSource); 
        fragmentShader = new Shader(GL_FRAGMENT_SHADER, fragmentSource); 


        /* Create shader program */
        program = new ShaderProgram();
        program.attachShader(vertexShader);   
        program.attachShader(fragmentShader);  
        program.bindFragmentDataLocation(0, "fragColor");
        program.link();
        program.use();
        program.checkStatus();
        specifyVertexAttributes();

        /* Get uniform location for the model matrix */
        uniModel = program.getUniformLocation("model");

        /* Set view matrix to identity matrix */
        Matrix4f view = new Matrix4f();
        uniView = program.getUniformLocation("view");
        program.setUniform(uniView, view);

        /* Get width and height for calculating the ratio */
        long window = GLFW.glfwGetCurrentContext();
        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetFramebufferSize(window, width, height);
        float ratio = width.get() / (float) height.get();

        /* Set projection matrix to an orthographic projection */
        Matrix4f projection = Matrix4f.orthographic(-ratio, ratio, -1f, 1f, -1f, 1f);
        int uniProjection = program.getUniformLocation("projection");
        program.setUniform(uniProjection, projection);
        vao.bind();
        program.use();

    }
    
    
    private void specifyVertexAttributes() {
        /* Specify Vertex Pointer */
        int posAttrib = program.getAttributeLocation("position");
        program.enableVertexAttribute(posAttrib);
        program.pointVertexAttribute(posAttrib, 3, 6 * Float.BYTES, 0);

        /* Specify Color Pointer */
        int colAttrib = program.getAttributeLocation("color");
        program.enableVertexAttribute(colAttrib);
        program.pointVertexAttribute(colAttrib, 3, 6 * Float.BYTES, 3 * Float.BYTES);
    }
    
    public void exit() {
        vao.delete();
        vbo.delete();
        vertexShader.delete();
        program.delete();
        
    }
    
    public void render(double alpha) {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float lerpAngle;
        if(rot1 != 0){
            lerpAngle = (float) ((1f - alpha) * previousAngle + alpha * angle);
        }else{
            lerpAngle = 0;
        }
        if(backwards){
            lerpAngle = -lerpAngle;
        }
        
       
        vao.bind();
        program.use();

        Matrix4f model = Matrix4f.rotate(lerpAngle, 0f, 0f, 1f);
        
        //Matrix4f c = new Matrix4f(model.m00, model.m01, model.m02, m2.m03, model.m10, model.m11, model.m12, m2.m13, model.m20, model.m21, model.m22, m2.m23, model.m00, model.m00, model.m00, model.m00);
        
        program.setUniform(uniModel, model);
        
        
        glDrawArrays(GL_TRIANGLES, 0, 3);
        
    }
    
    public void update(float delta) {
        previousAngle = angle;
        angle += delta * anglePerSecond;
    }

    @Override
    public void keyPressed(int key) {
        if(key == GLFW_KEY_A){
            rot1 += 0.2;
        }
        if(key == GLFW_KEY_D){
            rot1 += 0.2;
            backwards = true;
        }
    }

    @Override
    public void keyReleased(int key) {
        if(key == GLFW_KEY_A){
            rot1 -= 0.2;
            backwards = false;
        }
        if(key == GLFW_KEY_D){
            rot1 -= 0.2;
            backwards = false;
        }
    }
}
