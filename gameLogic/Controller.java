package gameLogic;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import utils.Input;
import utils.Timer;

public class Controller {
    protected Vector3f position;
    protected Vector3f rotation;

    protected float moveSpeed;
    protected float rotateSpeed;

    public Controller(float moveSpeed, float rotateSpeed){
        this.position=new Vector3f();
        this.rotation=new Vector3f();
        this.moveSpeed=moveSpeed;
        this.rotateSpeed=rotateSpeed;
    }
    
    private float findSignedVolume(Vector3f a, Vector3f b, Vector3f c, Vector3f d) {
    	return (1f/6)*new Vector3f(b).sub(a).cross(new Vector3f(c).sub(a)).dot(new Vector3f(d).sub(a));
    }
    
    private boolean linePasses(Vector3f lineStart, Vector3f lineEnd, Vector3f triangleA, Vector3f triangleB, Vector3f triangleC) {
    	return true;
    }

    public void control(Input input, Timer timer){
        float deltaUpdate=(float)timer.getDeltaUpdate();
        Vector3f translationVector=new Vector3f();
        if(input.isKeyDown(GLFW.GLFW_KEY_W)){
            move(new Vector3f(0,0,-deltaUpdate*moveSpeed),translationVector);
        }else if(input.isKeyDown(GLFW.GLFW_KEY_S)){
            move(new Vector3f(0,0,deltaUpdate*moveSpeed),translationVector);
        }if(input.isKeyDown(GLFW.GLFW_KEY_A)){
            move(new Vector3f(-deltaUpdate*moveSpeed,0,0),translationVector);
        }else if(input.isKeyDown(GLFW.GLFW_KEY_D)){
            move(new Vector3f(deltaUpdate*moveSpeed,0,0),translationVector);
        }if(input.isKeyDown(GLFW.GLFW_KEY_Q)){
            move(new Vector3f(0,deltaUpdate*moveSpeed,0),translationVector);
        }else if(input.isKeyDown(GLFW.GLFW_KEY_E)){
            move(new Vector3f(0,-deltaUpdate*moveSpeed,0),translationVector);
        }
        if(input.isKeyDown(GLFW.GLFW_KEY_LEFT)){
            rotate(new Vector3f(0,-deltaUpdate*rotateSpeed,0));
        }else if(input.isKeyDown(GLFW.GLFW_KEY_RIGHT)){
            rotate(new Vector3f(0,deltaUpdate*rotateSpeed,0));
        }
        if(input.isKeyDown(GLFW.GLFW_KEY_UP)){
            rotate(new Vector3f(-deltaUpdate*rotateSpeed,0,0));
        }else if(input.isKeyDown(GLFW.GLFW_KEY_DOWN)){
            rotate(new Vector3f(deltaUpdate*rotateSpeed,0,0));
        }
        position.add(translationVector); 
    }

    public void move(Vector3f translation, Vector3f translationVector){
        if ( translation.z != 0 ) {
        	translationVector.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * translation.z;
        	translationVector.z += (float)Math.cos(Math.toRadians(rotation.y)) * translation.z;
        }
        if ( translation.x != 0) {
        	translationVector.x += (float)Math.sin(Math.toRadians(rotation.y-90)) * -1.0f * translation.x;
        	translationVector.z += (float)Math.cos(Math.toRadians(rotation.y-90)) * translation.x;
        }
        translationVector.y += translation.y;
    }

    public void rotate(Vector3f rotation){
        this.rotation.add(rotation);
    }

    public void setPosition(Vector3f position){
        this.position=new Vector3f(position);
    }

    public void setRotation(Vector3f rotation){
        this.rotation=new Vector3f(rotation);
    }

    public Vector3f getPosition(){
        return new Vector3f(position);
    }

    public Vector3f getRotation(){
        return new Vector3f(rotation);
    }
}
