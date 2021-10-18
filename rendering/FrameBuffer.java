package rendering;

import org.lwjgl.opengl.GL46;

public class FrameBuffer {
	private int fbo;
	
	private Texture2D[] textureAttachments;
	private int[] attachmentTypes;
	
	public FrameBuffer(){
		fbo=GL46.glGenFramebuffers();
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, fbo);
	}
	
	public void attachTextures(Texture2D[] textures, int[] attachments)throws Exception{
		if(textures.length!=attachments.length){
			throw new Exception("Couldn't attach textures: Texture and attachment mismatch");
		}
		for(int i=0;i<textures.length;i++){
			GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, attachments[i], GL46.GL_TEXTURE_2D,
					textures[i].getId(), 0);
		}
		if(GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER)!=GL46.GL_FRAMEBUFFER_COMPLETE){
			throw new Exception("Couldn't attach textures: something went wrong with the framebuffer :/");
		}
	}
	
	
	public void cleanup(){
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
		GL46.glDeleteFramebuffers(fbo);
	}
}
