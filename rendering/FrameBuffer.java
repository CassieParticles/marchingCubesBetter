package rendering;

import org.lwjgl.opengl.GL46;

public class FrameBuffer {
	private int id;
	
	private Texture2D[] textureAttachments;
	private int[] attachmentTypes;
	
	public FrameBuffer(){
		id=GL46.glGenFramebuffers();
	}
	
	public void attachTextures(Texture2D[] textures, int[] attachments)throws Exception{
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, id);
		if(textures.length!=attachments.length){
			throw new Exception("Couldn't attach textures: Texture and attachment mismatch");
		}
		for(int i=0;i<textures.length;i++){
			GL46.glFramebufferTexture2D(GL46.GL_FRAMEBUFFER, attachments[i], GL46.GL_TEXTURE_2D,
					textures[i].getId(), 0);
		}
		textureAttachments=textures;
		if(GL46.glCheckFramebufferStatus(GL46.GL_FRAMEBUFFER)!=GL46.GL_FRAMEBUFFER_COMPLETE){
			throw new Exception("Couldn't attach textures: something went wrong with the framebuffer :/");
		}
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
	}
	
	public void bindFrameBuffer(){
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,id);
	}

	public void unbindFrameBuffer(){
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER,0);
	}

	public Texture2D getTexture(int id){
		return textureAttachments[id];
	}

	public void cleanup(){
		for(Texture2D texture: textureAttachments){
			texture.cleanup();
		}
		GL46.glBindFramebuffer(GL46.GL_FRAMEBUFFER, 0);
		GL46.glDeleteFramebuffers(id);
	}
}
