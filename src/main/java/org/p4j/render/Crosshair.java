package org.p4j.render;

import org.lwjgl.system.MemoryUtil;
import org.p4j.core.K;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Crosshair {
    private final int vaoId;
    private final int vboId;

    public Crosshair() {
        float sizeX = (K.CROSSHAIR_SIZE_PIXELS / K.DEFAULT_WINDOW_WIDTH) * 2.0f;
        float sizeY = (K.CROSSHAIR_SIZE_PIXELS / K.DEFAULT_WINDOW_HEIGHT) * 2.0f;

        float[] vertices = {
            -sizeX,  0.0f,
             sizeX,  0.0f,
             0.0f, -sizeY,
             0.0f,  sizeY
        };

        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        int attributeIndex = 0;
        int stride = K.CROSSHAIR_COMPONENTS_PER_VERTEX * K.FLOAT_SIZE_BYTES;
        long pointerOffset = 0L;

        glVertexAttribPointer(attributeIndex, K.CROSSHAIR_COMPONENTS_PER_VERTEX,
                GL_FLOAT, false, stride, pointerOffset);

        glEnableVertexAttribArray(attributeIndex);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render() {
        glLineWidth(K.CROSSHAIR_LINE_WIDTH);
        glBindVertexArray(vaoId);
        glDrawArrays(GL_LINES, 0, K.CROSSHAIR_VERTEX_COUNT);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteBuffers(vboId);
        glDeleteVertexArrays(vaoId);
    }
}