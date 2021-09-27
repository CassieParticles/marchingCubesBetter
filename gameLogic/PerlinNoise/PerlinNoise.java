package gameLogic.PerlinNoise;


import org.joml.Vector3f;

public class PerlinNoise {
	
	private final PerlinChunks chunks;
	private final int chunkSize;

	public PerlinNoise( int chunkSize){
		this.chunkSize=chunkSize;
		
		chunks=new PerlinChunks();
	}
	
	private float lerp(float a, float b, float f){	//Linear interpolation
		return (b-a)*f+a;
	}

	private float interpolate(float a0, float a1, float w) {
		return (float)((a1 - a0) * (3.0 - w * 2.0) * w * w + a0);
	}

	private float smoothStep(float t){
		return t*t*t*(t*(t*6-15)+10);
	}

	private Vector3f normalize(Vector3f vec){
		double mag=Math.sqrt(vec.x*vec.x+vec.y*vec.y+vec.z*vec.z);
		if (mag != 0) {
			vec.x /= mag;
			vec.y /= mag;
			vec.z /= mag;
		}
		return vec;
	}
	
	public float genPoint(float x, float y, float z){				//Generate points based on various chunk info
		
		int xChunk=(int)Math.floor(x/chunkSize);
		int yChunk=(int)Math.floor(y/chunkSize);
		int zChunk=(int)Math.floor(z/chunkSize);
		
		int xChunkPos=(xChunk)*chunkSize;	//This refers to the chunk at the lower of the 3 co-ords
		int yChunkPos=(yChunk)*chunkSize;
		int zChunkPos=zChunk*chunkSize;
		
		Vector3f[] cornerValues=new Vector3f[8];
		
		cornerValues[0]=chunks.getChunkCornerValue((xChunk), (yChunk), (zChunk));
		cornerValues[1]=chunks.getChunkCornerValue((xChunk), (yChunk), (zChunk)+1);
		cornerValues[2]=chunks.getChunkCornerValue((xChunk), (yChunk)+1, (zChunk));
		cornerValues[3]=chunks.getChunkCornerValue((xChunk), (yChunk)+1, (zChunk)+1);
		cornerValues[4]=chunks.getChunkCornerValue((xChunk)+1, (yChunk), (zChunk));
		cornerValues[5]=chunks.getChunkCornerValue((xChunk)+1, (yChunk), (zChunk)+1);
		cornerValues[6]=chunks.getChunkCornerValue((xChunk)+1, (yChunk)+1, (zChunk));
		cornerValues[7]=chunks.getChunkCornerValue((xChunk)+1, (yChunk)+1, (zChunk)+1);
		
		float xOld=((x-xChunkPos)/(float)chunkSize)+1.0f/(chunkSize*2);
		float yOld=((y-yChunkPos)/(float)chunkSize)+1.0f/(chunkSize*2);
		float zOld=((z-zChunkPos)/(float)chunkSize)+1.0f/(chunkSize*2);

		float xInternChunkPos= smoothStep(xOld);
		float yInternChunkPos= smoothStep(yOld);
		float zInternChunkPos= smoothStep(zOld);
		
		Vector3f[] deltaCornerValues=new Vector3f[8];
		
		float deltaNX=x-(xChunkPos);
		float deltaPX=x-(xChunkPos+chunkSize);

		float deltaNY=y-(yChunkPos);
		float deltaPY=y-(yChunkPos+chunkSize);

		float deltaNZ=z-(zChunkPos);
		float deltaPZ=z-(zChunkPos+chunkSize);
		
		
		deltaCornerValues[0]=new Vector3f(deltaNX,deltaNY,deltaNZ).normalize();
		deltaCornerValues[1]=new Vector3f(deltaNX,deltaNY,deltaPZ).normalize();
		deltaCornerValues[2]=new Vector3f(deltaNX,deltaPY,deltaNZ).normalize();
		deltaCornerValues[3]=new Vector3f(deltaNX,deltaPY,deltaPZ).normalize();
		deltaCornerValues[4]=new Vector3f(deltaPX,deltaNY,deltaNZ).normalize();
		deltaCornerValues[5]=new Vector3f(deltaPX,deltaNY,deltaPZ).normalize();
		deltaCornerValues[6]=new Vector3f(deltaPX,deltaPY,deltaNZ).normalize();
		deltaCornerValues[7]=new Vector3f(deltaPX,deltaPY,deltaPZ).normalize();
		
		float n0=deltaCornerValues[0].dot(cornerValues[0]);
		float n1=deltaCornerValues[1].dot(cornerValues[1]);
		
		float valZ0=interpolate(n0, n1, zInternChunkPos);
		
		float n2=deltaCornerValues[2].dot(cornerValues[2]);
		float n3=deltaCornerValues[3].dot(cornerValues[3]);
		
		float valZ1=interpolate(n2,n3,zInternChunkPos);
		float valY0=interpolate(valZ0,valZ1,yInternChunkPos);
		
		float n4=deltaCornerValues[4].dot(cornerValues[4]);
		float n5=deltaCornerValues[5].dot(cornerValues[5]);
		
		float valZ2=interpolate(n4, n5, zInternChunkPos);
		
		float n6=deltaCornerValues[6].dot(cornerValues[6]);
		float n7=deltaCornerValues[7].dot(cornerValues[7]);
		
		float valZ3=interpolate(n6,n7,zInternChunkPos);
		float valY1=interpolate(valZ2,valZ3,yInternChunkPos);

		return interpolate(valY0,valY1,xInternChunkPos);
	}
}
