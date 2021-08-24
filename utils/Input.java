package utils;

import main.Window;
import org.lwjgl.glfw.GLFW;

public class Input {
    private boolean[] keys=new boolean[GLFW.GLFW_KEY_LAST];
    private boolean[] keysAllreadyPressed=new boolean[GLFW.GLFW_KEY_LAST];
    private boolean[] mouseButtons=new boolean[GLFW.GLFW_MOUSE_BUTTON_LAST];
    
    private int mouseX;
    private int mouseY;

    public Input(){

    }
    
    public void init(Window windowObject){
    	
    	GLFW.glfwSetKeyCallback(windowObject.getWindowHandle(), (window, key, scancode, action, mods) -> {
            keys[key]=(action == GLFW.GLFW_PRESS||action==GLFW.GLFW_REPEAT);
        });
        GLFW.glfwSetMouseButtonCallback(windowObject.getWindowHandle(), (window, button, action, mods) -> {
            mouseButtons[button]=(action== GLFW.GLFW_PRESS||action==GLFW.GLFW_REPEAT);
        });
        GLFW.glfwSetCursorPosCallback(windowObject.getWindowHandle(), (window,xPos,yPos) -> {
            mouseX=(int)xPos;
            mouseY=(int)yPos;
        });
    }
    
    public void updateInputs(){
        System.arraycopy(keys, 0, keysAllreadyPressed, 0, keys.length);
    }

    public boolean isKeyDown(int key){
        return keys[key];
    }	
    
    public boolean isKeyPressed(int key){
    	return keys[key]&&!keysAllreadyPressed[key];
    }
    
    public boolean isMouseButtonDown(int mouseButton){
        return mouseButtons[mouseButton];
    }
    
    public int[] getMousePos(){
        return new int[]{mouseX,mouseY};
    }
}

