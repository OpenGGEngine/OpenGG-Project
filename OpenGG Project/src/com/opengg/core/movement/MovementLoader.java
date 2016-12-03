package com.opengg.core.movement;

import com.opengg.core.Vector3f;
import static com.opengg.core.io.input.keyboard.Key.*;
import com.opengg.core.io.input.keyboard.KeyboardController;
import com.opengg.core.io.input.mouse.MouseController;
import com.opengg.core.util.Time;
import java.util.logging.Logger;
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
    private static final int mouseSpeed = 2;

    private static final int maxLookUp = 89;
    private static GLFWKeyCallback keyCallback;

    private static final int maxLookPressed = -89;
    private static final boolean resizable = true;
    private static double lastFrame;
    private static volatile boolean running = true;
    static final Logger main = Logger.getLogger("main");
    private static final Time t;
    private MovementLoader(long w) {}

    static{
        t = new Time();
    }
    
    public static void setSpeed(int s) {
        baseSpeed = s;
    }

    public static void setup(int s) {
        baseSpeed = s;

        mouseX = mouseY = mouseDX = mouseDY = 0;
    }

    public static Vector3f processRotation(float sens, boolean inv) {

        Vector3f r = new Vector3f();
        
        mouseDX += (float) ((float) mouseX - (MouseController.getX()/4 * sens));
        mouseDY += (float) ((float) mouseY - (MouseController.getY()/4 * sens));
             
        
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

        boolean keyUp = KeyboardController.isKeyPressed(KEY_W);
        boolean keyPressed = KeyboardController.isKeyPressed(KEY_S);
        boolean keyLeft = KeyboardController.isKeyPressed(KEY_A);
        boolean keyRight = KeyboardController.isKeyPressed(KEY_D);
        boolean flyUp = KeyboardController.isKeyPressed(KEY_SPACE);
        boolean flyPressed = KeyboardController.isKeyPressed(KEY_LEFT_SHIFT);

        boolean moveFaster = KeyboardController.isKeyPressed(KEY_LEFT_CONTROL);
        boolean moveMuchFaster = KeyboardController.isKeyPressed(KEY_TAB);
        boolean reset = KeyboardController.isKeyPressed(KEY_C);
        float delta = t.getDeltaMs();

        if (moveMuchFaster) {
            walkingSpeed = baseSpeed * 15;
        } else if (moveFaster) {
            walkingSpeed = baseSpeed * 7;
        } else {
            walkingSpeed = baseSpeed;
        }

        if (keyUp && keyRight && !keyLeft && !keyPressed) {
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
        if (keyUp && keyLeft && !keyRight && !keyPressed) {
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
        if (keyUp && !keyLeft && !keyRight && !keyPressed) {
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
        if (keyPressed && keyLeft && !keyRight && !keyUp) {
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
        if (keyPressed && keyRight && !keyLeft && !keyUp) {
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
        if (keyPressed && !keyUp && !keyLeft && !keyRight) {
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
        if (keyLeft && !keyRight && !keyUp && !keyPressed) {
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
        if (keyRight && !keyLeft && !keyUp && !keyPressed) {
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
        if (flyUp && !flyPressed) {
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
        if (flyPressed && !flyUp) {
            double newPositionY = (walkingSpeed * 0.0002) * delta;
            position.y += newPositionY;
        }

//                if (window, isKeyPressed(window, KEY_O)) {
//                    mouseSpeed += 1;
//                    main.log(Level.INFO, "Mouse speed changed to {0}.", mouseSpeed);
//                }
//                if (Keyboard.isKeyPressed(Keyboard.KEY_L)) {
//                    if (mouseSpeed - 1 > 0) {
//                        mouseSpeed -= 1;
//                        main.log(Level.INFO, "Mouse speed changed to {0}.", mouseSpeed);
//                    }
//                }
//                if (Keyboard.isKeyPressed(Keyboard.KEY_Q)) {
//                    main.log(Level.INFO, "Flying speed changed to {0}.", walkingSpeed);
//                    walkingSpeed += 1;
//                }
//                if (Keyboard.isKeyPressed(Keyboard.KEY_Z)) {
//                    main.log(Level.INFO, "Flying speed changed to {0}.", walkingSpeed);
//                    walkingSpeed -= 1;
//                }
        return position;
    }

    public static void delete() {
        keyCallback.free();
    }

}
