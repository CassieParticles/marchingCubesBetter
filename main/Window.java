package main;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.awt.Dimension;
import java.awt.Toolkit;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;

public class Window {
    private long windowHandle;

    private int width;
    private int height;
    private float aspectRatio;
    
    private final String title;

    private boolean resized;


    public Window(int width, int height, String title){
    	Dimension size=Toolkit.getDefaultToolkit().getScreenSize();
    	this.width=Math.min(width,(int)size.getWidth());
    	this.height=Math.min(height, (int)size.getHeight()-32);
    	if(width==-1){
    		this.width=(int)size.getWidth();
    	}if(height==-1){
    		this.height=(int)size.getHeight()-32;
    	}
        this.aspectRatio=(float)width/height;
        this.title=title;
        this.resized=false;
    }

    public void init(){
        GLFWErrorCallback.createPrint(System.err).set();

        if(!glfwInit()){
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable
        glfwWindowHint(GLFW_OPENGL_DEBUG_CONTEXT,GLFW_TRUE);
        
        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL);

        if(windowHandle==NULL){
            throw new RuntimeException("Failed to create the GLFW window");
        }

        org.lwjgl.glfw.GLFW.glfwSetFramebufferSizeCallback(windowHandle, (window, width, height) -> {
            this.width = width;
            this.height = height;
            this.aspectRatio=(float)width/height;
            this.setResized(true);
        });
        


        // Make the OpenGL context current
        glfwMakeContextCurrent(windowHandle);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(windowHandle);

        GL.createCapabilities();
        
        GL46.glEnable(GL46.GL_DEPTH_TEST);
//        GL46.glEnable(GL_CULL_FACE);

        // Set the clear color
        glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
    }

    public void loop(){
        glfwSwapBuffers(windowHandle);
        glfwPollEvents();
    }


    public void cleanup(){
        System.out.println("Cleaning up");
        // Free the window callbacks and destroy the window

        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public boolean shouldClose(){
        return glfwWindowShouldClose(windowHandle);
    }
    
    public void close(){
    	glfwSetWindowShouldClose(windowHandle, true); // We will detect this in the rendering loop
    }

    public long getWindowHandle(){
        return windowHandle;
    }

    public void setResized(boolean resized){
        this.resized=resized;
    }

    public boolean getResized(){
        return resized;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight(){
        return height;
    }
    
    public float getAspectRatio(){
    	return aspectRatio;
    }


}

