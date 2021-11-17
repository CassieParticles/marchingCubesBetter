package main;

import gameLogic.Camera;
import gameLogic.Terrain;
import gameLogic.TerrainModifier;
import gameLogic.TextureSampler;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL46;
import rendering.*;
import utils.FileHandling;
import utils.Input;
import utils.Timer;

public class Main {
	
	private Timer timer;
	private Input input;
	private Camera camera;
	private Window window;

	private Program renderProgram;
	private Program solidColourProgram;
	private Program screenProgram;

	private FrameBuffer terrainFrameBuffer;
	private Screen screen;

	private TextureSampler textureSampler;

	private Terrain terrainHandler;
	private TerrainModifier terrainModifier;

	private int explode=0;
	
	private int penSize=10;

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
		camera=new Camera(70f,270);

		window.init();
		input.init(window);
		camera.calculateProjectionMatrix((float)Math.toRadians(60),0.1f,1000f,window.getAspectRatio());
		terrainHandler=new Terrain();
		terrainModifier=new TerrainModifier(terrainHandler);
		terrainFrameBuffer =new FrameBuffer();

		textureSampler=new TextureSampler(window);

		camera.rotate(new Vector3f(-30,90,0));
		
		screen=new Screen();

		terrainFrameBuffer.attachTextures(new Texture2D[]{
				new Texture2D(window.getWidth(),window.getHeight(),GL46.GL_RGBA,GL46.GL_RGBA,GL46.GL_FLOAT),
				new Texture2D(window.getWidth(), window.getHeight(),GL46.GL_RGB,GL46.GL_RGB,GL46.GL_FLOAT),
				new Texture2D(window.getWidth(), window.getHeight(),GL46.GL_DEPTH24_STENCIL8,GL46.GL_DEPTH_STENCIL,GL46.GL_UNSIGNED_INT_24_8)
				},new int[]{
						GL46.GL_COLOR_ATTACHMENT0,
						GL46.GL_COLOR_ATTACHMENT1,
						GL46.GL_DEPTH_STENCIL_ATTACHMENT
				});

		renderProgram=new Program();
		renderProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/shaders/rendering/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/rendering/geometry.glsl"),GL46.GL_GEOMETRY_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/rendering/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		renderProgram.link();
		
		solidColourProgram=new Program();
		solidColourProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/shaders/rendering/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/rendering/geometry.glsl"),GL46.GL_GEOMETRY_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/rendering/fragmentSolidColour.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		solidColourProgram.link();
		
		screenProgram =new Program();
		
		screenProgram.attachShaders(new Shader[]{
				new Shader(FileHandling.loadResource("src/shaders/screen/vertex.glsl"),GL46.GL_VERTEX_SHADER),
				new Shader(FileHandling.loadResource("src/shaders/screen/fragment.glsl"),GL46.GL_FRAGMENT_SHADER)
		});
		
		screenProgram.link();

		renderProgram.createUniform("projectionMatrix");
		renderProgram.createUniform("viewMatrix");
		renderProgram.createUniform("cameraPos");
		renderProgram.createUniform("translation");
		renderProgram.createUniform("time");
		renderProgram.createUniform("explodeTrue");
		renderProgram.createUniform("size");

		solidColourProgram.createUniform("projectionMatrix");
		solidColourProgram.createUniform("viewMatrix");
		solidColourProgram.createUniform("translation");
		
		screenProgram.createUniform("screenTexture");

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


		terrainFrameBuffer.bindFrameBuffer();
		GL46.glDrawBuffers(new int[]{GL46.GL_COLOR_ATTACHMENT0,GL46.GL_COLOR_ATTACHMENT1});
		GL46.glClearColor(0.1f, 0.1f, 0.2f, 1.0f);
		GL46.glClear(GL46.GL_DEPTH_BUFFER_BIT |GL46.GL_COLOR_BUFFER_BIT);

		renderProgram.useProgram();
		renderProgram.setUniform("time",(float)timer.getCurrentTime());
		renderProgram.setUniform("explodeTrue",explode);
		renderProgram.setUniform("size",terrainHandler.getSize());

//        GL46.glEnable(GL46.GL_CULL_FACE);
		GL46.glEnable(GL46.GL_DEPTH_TEST);
		for(TerrainChunk chunk: terrainHandler.getChunks()){
			chunk.render(renderProgram,camera);
		}

//        GL46.glDisable(GL46.GL_CULL_FACE);
		GL46.glDisable(GL46.GL_DEPTH_TEST);

		terrainFrameBuffer.unbindFrameBuffer();
		GL46.glClearColor(1f, 1f, 1f, 1f);
		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
		screenProgram.useProgram();

		screen.render(screenProgram, terrainFrameBuffer.getTexture(0));

		screenProgram.unlinkProgram();
	}
	
	private void update(){

		if(input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)){
    		window.close();
    	}if(input.isKeyPressed(GLFW.GLFW_KEY_PERIOD)){	// >
			penSize++;
		}if(input.isKeyPressed(GLFW.GLFW_KEY_COMMA)){	// <
			if(penSize>1){
				penSize--;
			}
		}if(input.isKeyPressed(GLFW.GLFW_KEY_SPACE)){
			explode=1-explode;
		}if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)){
			int[] mousePos=input.getMousePos();
			float[] position=textureSampler.sampleTexture(mousePos[0], window.getHeight()-mousePos[1], 1,1,terrainFrameBuffer.getTexture(1));
			Vector4f centrePos= new Vector4f(position).mul(terrainHandler.getSize());
			terrainModifier.addCircleArea((int)(centrePos.x),(int)centrePos.y,(int)centrePos.z,penSize,-1f*(float)timer.getDeltaUpdate());
		}if(input.isMouseButtonDown(GLFW.GLFW_MOUSE_BUTTON_RIGHT)){
			int[] mousePos=input.getMousePos();
			float[] position=textureSampler.sampleTexture(mousePos[0], window.getHeight()-mousePos[1],1,1,terrainFrameBuffer.getTexture(1));
			Vector4f centrePos= new Vector4f(position).mul(terrainHandler.getSize());
			terrainModifier.addCircleArea((int)(centrePos.x),(int)centrePos.y,(int)centrePos.z,penSize,1f*(float)timer.getDeltaUpdate());
		}
		camera.control(input,timer);
		input.updateInputs();
	}
	
	private void cleanup(){
		window.cleanup();
		terrainHandler.cleanup();
		screen.cleanup();
		
		renderProgram.cleanup();
		screenProgram.cleanup();
		solidColourProgram.cleanup();
		terrainFrameBuffer.cleanup();
		textureSampler.cleanup();
	}
}
