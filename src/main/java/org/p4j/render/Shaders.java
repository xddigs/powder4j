package org.p4j.render;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL20.*;

public class Shaders {
    private static final Logger log = LoggerFactory.getLogger(Shaders.class);
    private final int programId;

    public Shaders(String vertexCode, String fragmentCode) {
        int vertexShaderId = compileShader(vertexCode, GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(fragmentCode, GL_FRAGMENT_SHADER);

        this.programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            String infoLog = glGetProgramInfoLog(programId);
            log.error("Failed to link shader program: {}", infoLog);
            throw new RuntimeException("Shader link error: " + infoLog);
        }

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
    }

    private int compileShader(String code, int type) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, code);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String infoLog = glGetShaderInfoLog(shaderId);
            log.error("Failed to compile shader (type {}): {}", type, infoLog);
            throw new RuntimeException("Shader compilation error: " + infoLog);
        }
        return shaderId;
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getUniformLocation(String name) {
        return glGetUniformLocation(programId, name);
    }

    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}