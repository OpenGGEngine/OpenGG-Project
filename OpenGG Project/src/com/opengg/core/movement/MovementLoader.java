package com.opengg.core.movement;

import java.util.logging.Logger;
import org.lwjgl.glfw.GLFW;
import com.opengg.core.Vector3f;
import com.opengg.core.io.input.KeyBoardHandler;
import com.opengg.core.io.input.MousePosHandler;
import static org.lwjgl.glfw.GLFW.*;
import org.lwjgl.glfw.GLFWKeyCallback;

/**
 * Simple lightweight movement processor for 3d LWJGL programs.
 *
 * @author Javier Coindreau
 */
public class MovementLoader {

    private static float mouseX, mouseY, mouseDX, mouseDY;
    private static long window;
    private static int baseSpeed = 10;
    private static int walkingSpeed = 30;
    private static int mouseSpeed = 2;

    private static final int maxLookUp = 89;
    private static GLFWKeyCallback keyCallback;

    private static final int maxLookDown = -89;
    private static final boolean resizable = true;
    private static double lastFrame;
    private static volatile boolean running = true;
    static final Logger main = Logger.getLogger("main");

    public MovementLoader(long w) {

    }

    public static void setSpeed(int s) {
        baseSpeed = s;
    }

    public static void setup(long w, int s) {
        baseSpeed = s;

        mouseX = mouseY = mouseDX = mouseDY = 0;

        
    }

    private static float getDelta() {
        double currentTime = GLFW.glfwGetTime();
        float delta = (float) (currentTime - lastFrame);
        lastFrame = GLFW.glfwGetTime();
        return delta * 1000;
    }

    public static Vector3f processRotation(float sens, boolean inv) {

        Vector3f r = new Vector3f();
        
        mouseDX += (float) ((float) mouseX - (MousePosHandler.getX()/4 * sens));
        mouseDY += (float) ((float) mouseY - (MousePosHandler.getY()/4 * sens));
             
        
        if(!(mouseY - mouseDY < -89 || mouseY - mouseDY > 89)){
            mouseY = mouseY - mouseDY;
            mouseDY = 0;
        }else if(mouseY - mouseDY < -89){
            mouseY = -89;
        }else if(mouseY - mouseDY > 89){
            mouseY = 89;
        }
        
        mouseX = mouseX - mouseDX;
        mouseDX = 0;
        
        //System.out.println(mouseX + " " + mouseY);
        r.x = mouseY;
        r.y = mouseX;
        r.z = 0;
        
        return r;
    }
    
    /**
     * 
     * Processes Keyboard inputs Relating to Movement
     * @param position Position
     * @param rotation Rotation
     * @return Final Position
     */
    public static Vector3f processMovement(Vector3f position, Vector3f rotation) {

        boolean keyUp = KeyBoardHandler.isKeyDown(GLFW_KEY_W);
        boolean keyDown = KeyBoardHandler.isKeyDown(GLFW_KEY_S);
        boolean keyLeft = KeyBoardHandler.isKeyDown(GLFW_KEY_A);
        boolean keyRight = KeyBoardHandler.isKeyDown(GLFW_KEY_D);
        boolean flyUp = KeyBoardHandler.isKeyDown(GLFW_KEY_SPACE);
        boolean flyDown = KeyBoardHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT);

        boolean moveFaster = KeyBoardHandler.isKeyDown(GLFW_KEY_LEFT_CONTROL);
        boolean moveMuchFaster = KeyBoardHandler.isKeyDown(GLFW_KEY_TAB);
        boolean reset = KeyBoardHandler.isKeyDown(GLFW_KEY_C);
        float delta = getDelta();

        if (moveMuchFaster) {
            walkingSpeed = baseSpeed * 15;
        } else if (moveFaster) {
            walkingSpeed = baseSpeed * 7;
        } else {
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
        if (keyRight && !keyLeft && !keyUp && !keyDown) {
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

    public static void delete() {
        keyCallback.free();
    }

}
