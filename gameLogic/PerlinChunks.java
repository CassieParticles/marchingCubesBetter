package gameLogic;

import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PerlinChunks {
	
	private Map<Vector3f, Vector3f> chunkValues=new HashMap<>();
	private Random rand;
	
	private Vector3f genRandVector(Random rand){
		return new Vector3f((rand.nextFloat()*2)-1,(rand.nextFloat()*2)-1,(rand.nextFloat()*2)-1).normalize();
	}
	
	public PerlinChunks(){
		this.rand= ThreadLocalRandom.current();
	}
	
	public Vector3f getChunkCornerValue(int x, int y, int z){
		if(!chunkValues.containsKey(new Vector3f(x,y,z))){
			chunkValues.put(new Vector3f(x,y,z),genRandVector(rand));
		}
		return chunkValues.get(new Vector3f(x,y,z));
	}
	
	
}
