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

    public void control(Input input, Timer timer){
        float deltaUpdate=(float)timer.getDeltaUpdate();
        if(input.isKeyDown(GLFW.GLFW_KEY_W)){
            move(new Vector3f(0,0,-deltaUpdate*moveSpeed));
        }else if(input.isKeyDown(GLFW.GLFW_KEY_S)){
            move(new Vector3f(0,0,deltaUpdate*moveSpeed));
        }if(input.isKeyDown(GLFW.GLFW_KEY_A)){
            move(new Vector3f(-deltaUpdate*moveSpeed,0,0));
        }else if(input.isKeyDown(GLFW.GLFW_KEY_D)){
            move(new Vector3f(deltaUpdate*moveSpeed,0,0));
        }if(input.isKeyDown(GLFW.GLFW_KEY_Q)){
            move(new Vector3f(0,deltaUpdate*moveSpeed,0));
        }else if(input.isKeyDown(GLFW.GLFW_KEY_E)){
            move(new Vector3f(0,-deltaUpdate*moveSpeed,0));
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
    }

    public void move(Vector3f translation){
        if ( translation.z != 0 ) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * translation.z;
            position.z += (float)Math.cos(Math.toRadians(rotation.y)) * translation.z;
        }
        if ( translation.x != 0) {
            position.x += (float)Math.sin(Math.toRadians(rotation.y-90)) * -1.0f * translation.x;
            position.z += (float)Math.cos(Math.toRadians(rotation.y-90)) * translation.x;
        }
        position.y += translation.y;
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
