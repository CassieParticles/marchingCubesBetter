package main;

import gameLogic.Camera;
import gameLogic.Terrain;
import gameLogic.TerrainModifier;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import rendering.Program;
import rendering.Shader;
import rendering.TerrainChunk;
import utils.FileHandling;
import utils.Input;
import utils.Timer;

public class Main {
	
	private Timer timer;
	private Input input;
	private Camera camera;

	private Program renderProgram;

	private Terrain terrainHandler;
	private TerrainModifier terrainModifier;
	
	private int penSize=10;

	private Window window;

	public static void main(String[] args){
		new Main().gameLoop();
	}
	
	private void gameLoop(){
		try{
			init();
			loop();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			cleanup();
		}
	}
	
	private void init() throws Exception{
		window=new Window(900,900,"gameing");
		input=new Input();
		timer=new Timer(60,60);	//UPS,FPS
		camera=new Camera(30f,270);

		window.init();
		input.init(window);
		camera.calculateProjectionMatrix((float)Math.toRadians(60),0.1f,1000f,window.getAspectRatio());
		terrainHandler=new Terrain();
		terrainModifier=new TerrainModifier(terrainHandler);


		camera.rotate(new Vector3f(-30,90,0));

		renderProgram=new Program();
		renderProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/shaders/rendering/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/rendering/geometry.glsl"),GL46.GL_GEOMETRY_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/rendering/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		renderProgram.link();

		renderProgram.createUniform("projectionMatrix");
		renderProgram.createUniform("viewMatrix");
		renderProgram.createUniform("colour");
		renderProgram.createUniform("cameraPos");
		renderProgram.createUniform("translation");

		terrainHandler.generate();

		GL46.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		
		window.loop();
	}
	
	private void loop(){
		while(!window.shouldClose()){
			timer.update();
			if(timer.getUpdate()){
				update();
			}if(timer.getFrame()){
				render();
			}
		}
	}
	
	private void render(){
		window.loop();
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
		renderProgram.useProgram();
		for(TerrainChunk chunk: terrainHandler.getChunks()){
			chunk.render(renderProgram,camera);
		}
	}
	
	private void update(){

		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}if(input.isKeyDown(GLFW.GLFW_KEY_LEFT_CONTROL)){
			terrainModifier.addCircleArea((int)camera.getPosition().x,(int)camera.getPosition().y,(int)camera.getPosition().z,penSize,3f*(float)timer.getDeltaUpdate());
		}if(input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)){
			terrainModifier.addCircleArea((int)camera.getPosition().x,(int)camera.getPosition().y,(int)camera.getPosition().z,penSize,-3f*(float)timer.getDeltaUpdate());
		}if(input.isKeyPressed(GLFW.GLFW_KEY_PERIOD)){	// >
			penSize++;
		}if(input.isKeyPressed(GLFW.GLFW_KEY_COMMA)){	// <
			if(penSize>1){
				penSize--;
			}
		}
		camera.control(input,timer);
		input.updateInputs();
	}
	
	private void cleanup(){
		window.cleanup();
		for(TerrainChunk chunk: terrainHandler.getChunks()){
			chunk.cleanup();
		}
		renderProgram.cleanup();
	}
}
