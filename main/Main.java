package main;

import gameLogic.Camera;
import gameLogic.Terrain;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import rendering.Program;
import rendering.Shader;
import rendering.TerrainChunk;
import utils.FileHandling;
import utils.Input;
import utils.Timer;

public class Main<TextureMesh> {
	
	private Timer timer;
	private Input input;
	private Camera camera;

	private Program renderProgram;

	private Terrain terrainHandler;

	private int radius=120;
	private int noiseMagnitude=50;
	private float noiseFrequency=0.31f;
	private int chunkSize=20;

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
			terrainHandler.addFloat((int)camera.getPosition().x,(int)camera.getPosition().y,(int)camera.getPosition().z,1f);
			terrainHandler.reCalcMesh(terrainHandler.findChunkIdFromCoord(camera.getPosition()));
		}if(input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)){
		terrainHandler.addFloat((int)camera.getPosition().x,(int)camera.getPosition().y,(int)camera.getPosition().z,-1f);
		terrainHandler.reCalcMesh(terrainHandler.findChunkIdFromCoord(camera.getPosition()));
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
