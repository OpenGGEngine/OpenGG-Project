

package com.opengg.core.movement;

import java.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import com.opengg.core.Vector3f;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import static org.lwjgl.opengl.GL11.GL_TRUE;

/**
 * Simple lightweight movement processor for 3d LWJGL programs.
 * @author Javier Coindreau
 */
public class MovementLoader {
    private static int mouseX, mouseY, mouseDX, mouseDY;
    private static long window;
    private static final int baseSpeed = 30;
    private static int walkingSpeed = 30;
    private static int mouseSpeed = 2;
    
    
    private static final int maxLookUp = 89;
    private static GLFWErrorCallback errorCallback;
    private static GLFWKeyCallback   keyCallback;
    private static GLFWCursorPosCallback cursorPosCallback;
    
    
    private static final int maxLookDown = -89;
    private static final boolean resizable = true;
    private static long lastFrame;
    private static volatile boolean running = true;
    static final Logger main = Logger.getLogger("main");
    public MovementLoader(long w){   
        window = w;
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
                @Override
                public void invoke(long window, int key, int scancode, int action, int mods) {
                    if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                        glfwSetWindowShouldClose(window, GL_TRUE); // We will detect this in our rendering loop
                }
            });
 
            // Initialize all mouse values as 0
            mouseX = mouseY = mouseDX = mouseDY = 0;
            glfwSetCursorPosCallback(window, cursorPosCallback = new GLFWCursorPosCallback(){
 
                @Override
                public void invoke(long window, double xpos, double ypos) {
                    // Add delta of x and y mouse coordinates
                    mouseDX += (int)xpos - mouseX;
                    mouseDY += (int)xpos - mouseY;
                    // Set new positions of x and y
                    mouseX = (int) xpos;
                    mouseY = (int) ypos;
                }
            });

    }
    private static int getDelta() {
        long currentTime = (long) GLFW.glfwGetTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = (long) GLFW.glfwGetTime();
        return delta;
    }
    
    public int getDX(){
        // Return mouse delta x and set delta x to 0
        return mouseDX | (mouseDX = 0);
    }
 
    public int getDY(){
        // Return mouse delta y and set delta y to 0      
        return mouseDY | (mouseDY = 0);
    }

    
    public static Vector3f processRotation(Vector3f rotation){
            glfwPollEvents ();            
            if (rotation.y + mouseDX >= 360) {
                rotation.y = rotation.y + mouseDX - 360;
            } else if (rotation.y + mouseDX < 0) {
                rotation.y = 360 - rotation.y + mouseDX;
            } else {
                rotation.y += mouseDX;
            }
            if (rotation.x - mouseDY >= maxLookDown && rotation.x - mouseDY <= maxLookUp) {
                rotation.x += -mouseDY;
            } else if (rotation.x - mouseDY < maxLookDown) {
                rotation.x = maxLookDown;
            } else if (rotation.x - mouseDY > maxLookUp) {
                rotation.x = maxLookUp;
            }
            
        return rotation;
    }
    
     public static Vector3f processMovement(Vector3f position, Vector3f rotation){
        

            
            
            boolean keyUp = (glfwGetKey(window, GLFW_KEY_W) ==  GLFW_PRESS );
            boolean keyDown = (glfwGetKey(window, GLFW_KEY_S)==  GLFW_PRESS );
            boolean keyLeft = (glfwGetKey(window, GLFW_KEY_A)==  GLFW_PRESS );
            boolean keyRight = (glfwGetKey(window, GLFW_KEY_D) ==  GLFW_PRESS );
            boolean flyUp = (glfwGetKey(window, GLFW_KEY_SPACE)==  GLFW_PRESS );
            boolean flyDown = (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT)==  GLFW_PRESS );
            
            boolean moveFaster = (glfwGetKey(window, GLFW_KEY_LEFT_CONTROL)==  GLFW_PRESS );
            boolean moveMuchFaster = (glfwGetKey(window, GLFW_KEY_TAB)==  GLFW_PRESS );
            boolean reset = (glfwGetKey(window, GLFW_KEY_C)==  GLFW_PRESS );
            int delta = getDelta();
            
            
            if(moveFaster){
                walkingSpeed = baseSpeed * 4;
            }else{
                walkingSpeed = baseSpeed;
            }


            if (keyUp && keyRight && !keyLeft && !keyDown) {
                float angle = rotation.y + 45;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyUp && keyLeft && !keyRight && !keyDown) {
                float angle = rotation.y - 45;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyUp && !keyLeft && !keyRight && !keyDown) {
                float angle = rotation.y;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && keyLeft && !keyRight && !keyUp) {
                float angle = rotation.y - 135;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && keyRight && !keyLeft && !keyUp) {
                float angle = rotation.y + 135;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyDown && !keyUp && !keyLeft && !keyRight) {
                float angle = rotation.y;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = -(walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyLeft && !keyRight && !keyUp && !keyDown) {
                float angle = rotation.y - 90;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (keyRight && !keyLeft && !keyUp && !keyDown ) {
                float angle = rotation.y + 90;
                Vector3f newPosition = new Vector3f(position);
                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
                newPosition.z += adjacent;
                newPosition.x -= opposite;
                position.z = newPosition.z;
                position.x = newPosition.x;
            }
            if (flyUp && !flyDown) {
                double newPositionY = (walkingSpeed * 0.0002) * delta;
                position.y -= newPositionY;
//                float angle = rotation.z + 90;
//                Vector3f newPosition = new Vector3f(position);
//                float hypotenuse = (walkingSpeed * 0.0002f) * delta;
//                float adjacent = hypotenuse * (float) Math.cos(Math.toRadians(angle));
//                float opposite = (float) (Math.sin(Math.toRadians(angle)) * hypotenuse);
//                newPosition.z += adjacent;
//                newPosition.x -= opposite;
//                position.y = newPosition.z;
//                position.z = newPosition.z;
//                position.x = newPosition.x;
            }
            if (flyDown && !flyUp) {
                double newPositionY = (walkingSpeed * 0.0002) * delta;
                position.y += newPositionY;
            }
                      
            
            
            position = new Vector3f(0, 0, 0);
            rotation = new Vector3f(0, 0, 0);
//                if (window, GLFW_isKeyDown(window, GLFW_KEY_O)) {
//                    mouseSpeed += 1;
//                    main.log(Level.INFO, "Mouse speed changed to {0}.", mouseSpeed);
//                }
//                if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
//                    if (mouseSpeed - 1 > 0) {
//                        mouseSpeed -= 1;
//                        main.log(Level.INFO, "Mouse speed changed to {0}.", mouseSpeed);
//                    }
//                }
//                if (Keyboard.isKeyDown(Keyboard.KEY_Q)) {
//                    main.log(Level.INFO, "Flying speed changed to {0}.", walkingSpeed);
//                    walkingSpeed += 1;
//                }
//                if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
//                    main.log(Level.INFO, "Flying speed changed to {0}.", walkingSpeed);
//                    walkingSpeed -= 1;
//                }
               
            
            return position;
    }
    
     public static void delete(){
         keyCallback.release();
     }
     
}
