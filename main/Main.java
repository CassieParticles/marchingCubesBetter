package main;

import gameLogic.Camera;
import gameLogic.Generator;
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

	private Generator generator;

	private TerrainChunk[] chunks;

	private int radius=120;
	private int noiseMagnitude=15;
	private float noiseFrequency=0.83f;
	private int chunkSize=10;

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
		generator =new Generator();

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

		chunks= generator.generateTerrain((radius+2)*2+noiseMagnitude,chunkSize,radius,noiseFrequency,noiseMagnitude);

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
		for(TerrainChunk chunk:chunks){
			chunk.render(renderProgram,camera);
		}
	}
	
	private void update(){

		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}if(input.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
			System.out.println("Gameing");
		}
		camera.control(input,timer);
		input.updateInputs();
	}
	
	private void cleanup(){
		window.cleanup();
		for(TerrainChunk chunk:chunks){
			chunk.cleanup();
		}
		renderProgram.cleanup();
	}
}
