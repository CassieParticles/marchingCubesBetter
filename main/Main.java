package main;

import gameLogic.Camera;
import gameLogic.TempGenerator;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import rendering.Mesh;
import rendering.Program;
import rendering.Shader;
import utils.FileHandling;
import utils.Input;
import utils.Timer;

import java.io.File;

public class Main<TextureMesh> {
	
	private Timer timer;
	private Input input;
	private Camera camera;

	private Program renderProgram;
	private Program scalarGenProgram;
	private Program marchingCubeProgram;

	private TempGenerator generator;

	private Mesh mesh;
	private Vector3f planetCentre;

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
		generator=new TempGenerator();

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
//		renderProgram.createUniform("transformationMatrix");
		renderProgram.createUniform("colour");
		renderProgram.createUniform("cameraPos");

		scalarGenProgram=new Program();
		scalarGenProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/shaders/compute/scalarGeneration.glsl"),GL46.GL_COMPUTE_SHADER)
		});
		scalarGenProgram.link();

		scalarGenProgram.createUniform("scalarTexture");
		scalarGenProgram.createUniform("size");


		//Create code to generate scalar field and test marching cubes algorithm

		float[] vertices=new float[]{
				-1,-1,-1,
				1,-1,-1,
				1,-1,1,
				-1,-1,1,
				-1,1,-1,
				1,1,-1,
				1,1,1,
				-1,1,1
		};

		int[] indices=new int[]{
				0,2,1, 0,3,2,
				0,4,7, 0,7,3,
				0,5,4, 0,1,5,
				1,6,5, 1,2,6,
				3,7,6, 3,6,2,
				4,5,6, 4,6,7
		};
		System.out.println("Starting point generation");
		int radius=120;
		int noiseMagnitude=2;
		float[][][] points=generator.scalarGenerator((radius+2)*2+noiseMagnitude,(radius+2)*2+noiseMagnitude,(radius+2)*2+noiseMagnitude,radius,noiseMagnitude);
		System.out.println("Starting mesh generation");
		mesh=generator.generate(points,0);
		System.out.println("Mesh generated");

		planetCentre=new Vector3f(radius+noiseMagnitude+2);
//		mesh=new Mesh(vertices,indices);

//		generator.testTriTable();

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
		mesh.render(renderProgram,camera);

	}
	
	private void update(){
		input.updateInputs();
		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}

		camera.control(input,timer);
	}
	
	private void cleanup(){
		window.cleanup();
		mesh.cleanup();

		renderProgram.cleanup();
	}
}
