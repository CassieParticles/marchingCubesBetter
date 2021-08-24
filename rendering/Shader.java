package rendering;

import org.lwjgl.opengl.GL46;

public class Shader {
    private int shaderType;

    private int shaderId;

    public Shader(String shaderCode, int shaderType) throws Exception {
        shaderId = GL46.glCreateShader(shaderType);

        if (shaderId == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }

        GL46.glShaderSource(shaderId, shaderCode);
        GL46.glCompileShader(shaderId);

        if (GL46.glGetShaderi(shaderId, GL46.GL_COMPILE_STATUS) == 0) {
            System.out.println(shaderCode);
            throw new Exception("Error compiling Shader code: " + GL46.glGetShaderInfoLog(shaderId, 1024));

        }

        this.shaderType = shaderType;
    }

    public int getId() {
        return shaderId;
    }

    public int getShaderType(){
        return shaderType;
    }
}
