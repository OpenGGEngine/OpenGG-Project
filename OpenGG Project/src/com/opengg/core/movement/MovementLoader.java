

package com.opengg.core.movement;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.lwjgl.LWJGLException;
import static org.lwjgl.Sys.getTime;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

/**
 * Simple lightweight movement processor for 3d LWJGL programs.
 * @author Javier Coindreau
 */
public class MovementLoader {
    private static final int baseSpeed = 30;
     private static int walkingSpeed = 30;
    private static int mouseSpeed = 2;
//    private static Vector3f position = new Vector3f(0, 0, 0);
//    private static Vector3f rotation = new Vector3f(0, 0, 0);
    private static final int maxLookUp = 89;

    private static final int maxLookDown = -89;
    private static final boolean resizable = true;
    private static long lastFrame;
    private static volatile boolean running = true;
    static final Logger main = Logger.getLogger("main");
    private MovementLoader(){        
    }
    private static int getDelta() {
        long currentTime = getTime();
        int delta = (int) (currentTime - lastFrame);
        lastFrame = getTime();
        return delta;
    }
    public static boolean setup(){ 
         try {
             Keyboard.create();
             Mouse.create();             
         } catch (LWJGLException ex) {
             return false;
         }
         return true;
    }
    
    public static Vector3f processRotation(Vector3f rotation){
        if (Mouse.isGrabbed()) {
                float mouseDX = Mouse.getDX() * mouseSpeed * 0.16f;
                float mouseDY = Mouse.getDY() * mouseSpeed * 0.16f;
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
            }
        return rotation;
    }
    
     public static Vector3f processMovement(Vector3f position, Vector3f rotation){
        

            
            
            boolean keyUp = Keyboard.isKeyDown(Keyboard.KEY_UP) || Keyboard.isKeyDown(Keyboard.KEY_W);
            boolean keyDown = Keyboard.isKeyDown(Keyboard.KEY_DOWN) || Keyboard.isKeyDown(Keyboard.KEY_S);
            boolean keyLeft = Keyboard.isKeyDown(Keyboard.KEY_LEFT) || Keyboard.isKeyDown(Keyboard.KEY_A);
            boolean keyRight = Keyboard.isKeyDown(Keyboard.KEY_RIGHT) || Keyboard.isKeyDown(Keyboard.KEY_D);
            boolean flyUp = Keyboard.isKeyDown(Keyboard.KEY_SPACE);
            boolean flyDown = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT);
            boolean moveFaster = Keyboard.isKeyDown(Keyboard.KEY_LCONTROL);
            boolean moveMuchFaster = Keyboard.isKeyDown(Keyboard.KEY_TAB);
            boolean reset = Keyboard.isKeyDown(Keyboard.KEY_C);
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
           
            while (Mouse.next()) {
                if (Mouse.isButtonDown(0)) {
                    Mouse.setGrabbed(true);
                }
                if (Mouse.isButtonDown(1)) {
                    Mouse.setGrabbed(false);
                }
            }
            while (Keyboard.next()) {
                if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
                    position = new Vector3f(0, 0, 0);
                    rotation = new Vector3f(0, 0, 0);
                }
//                if (Keyboard.isKeyDown(Keyboard.KEY_O)) {
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
               
            }
            return position;
    }
     
}
