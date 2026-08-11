package org.p4j.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import org.p4j.core.Camera;
import org.p4j.core.K;
import org.p4j.core.Voxel;
import org.p4j.core.World;
import org.p4j.gui.Engine;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.system.MemoryUtil.NULL;

@SuppressWarnings("all")
public class Lwjgl3Engine implements Engine {
    private static final Logger log = LoggerFactory.getLogger(Lwjgl3Engine.class);

    private static final String VERTEX_SHADER_SRC = """
        #version 330 core
        layout (location = 0) in vec3 aPos;
        
        uniform mat4 uProjection;
        uniform mat4 uView;
        uniform mat4 uModel;
        
        void main() {
            gl_Position = uProjection * uView * uModel * vec4(aPos, 1.0);
        }
        """;

    private static final String FRAGMENT_SHADER_SRC = """
        #version 330 core
        out vec4 FragColor;
        
        void main() {
            FragColor = vec4(0.2, 0.8, 0.4, 1.0);
        }
        """;

    private final int simWidth;
    private final int simHeight;
    private final int simDepth;

    private long window = NULL;

    private Shaders shaderProgram;
    private Voxel voxelMesh;
    private Camera camera;

    private final Matrix4f projectionMatrix = new Matrix4f();
    private final Matrix4f modelMatrix = new Matrix4f();

    private double lastFrameTime = 0.0;
    private double lastMouseX = 0.0;
    private double lastMouseY = 0.0;
    private boolean firstMouse = true;

    public Lwjgl3Engine(int simWidth, int simHeight, int simDepth, World world) {
        this.simWidth = simWidth;
        this.simHeight = simHeight;
        this.simDepth = simDepth;
    }

    @Override
    public void init(World world) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(K.DEFAULT_WINDOW_WIDTH, K.DEFAULT_WINDOW_HEIGHT,
                K.APP_TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(K.GLFW_VSYNC_INTERVAL);
        glEnable(GL_DEPTH_TEST);
        glClearColor(K.CLEAR_COLOR_RED, K.CLEAR_COLOR_GREEN,
                K.CLEAR_COLOR_BLUE, K.CLEAR_COLOR_ALPHA);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        this.camera = new Camera(new Vector3f(K.CAMERA_START_X, K.CAMERA_START_Y, K.CAMERA_START_Z));
        this.shaderProgram = new Shaders(VERTEX_SHADER_SRC, FRAGMENT_SHADER_SRC);
        this.voxelMesh = new Voxel();

        setupInputCallbacks();

        glfwShowWindow(window);
        this.lastFrameTime = glfwGetTime();

        log.info("OpenGL Context and Camera initialized successfully: {}",
                glGetString(GL_VERSION));
    }

    private void setupInputCallbacks() {
        glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
            if (firstMouse) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                firstMouse = false;
            }

            float xOffset = (float) (xpos - lastMouseX);
            float yOffset = (float) (ypos - lastMouseY);
            lastMouseX = xpos;
            lastMouseY = ypos;
            camera.mouse(xOffset, yOffset);
        });
    }

    private void processInput(float deltaTime) {
        if (glfwGetKey(window, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetWindowShouldClose(window, true);
        }

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.moveForward(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.moveBackward(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.moveLeft(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.moveRight(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.moveUp(deltaTime);
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.moveDown(deltaTime);
        }
    }

    @Override
    public void updatePixels(World world) {
        // Reserved for GPU buffer updates
    }

    @Override
    public void render(World world, KeyboardController keyController,
                       MouseController mouseController, Brush brush) {
        if (shouldClose()) return;

        double currentTime = glfwGetTime();
        float deltaTime = (float) (currentTime - lastFrameTime);
        lastFrameTime = currentTime;

        processInput(deltaTime);

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shaderProgram.bind();

        float aspectRatio = (float) K.DEFAULT_WINDOW_WIDTH / K.DEFAULT_WINDOW_HEIGHT;
        projectionMatrix.identity().perspective(
                (float) Math.toRadians(K.FOV_DEGREES),
                aspectRatio,
                K.Z_NEAR,
                K.Z_FAR
        );

        Matrix4f viewMatrix = camera.getViewMatrix();
        modelMatrix.identity();

        int uProjectionLoc = shaderProgram.getUniformLocation("uProjection");
        int uViewLoc = shaderProgram.getUniformLocation("uView");
        int uModelLoc = shaderProgram.getUniformLocation("uModel");

        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer buffer = stack.mallocFloat(16);

            glUniformMatrix4fv(uProjectionLoc, false, projectionMatrix.get(buffer));
            glUniformMatrix4fv(uViewLoc, false, viewMatrix.get(buffer));
            glUniformMatrix4fv(uModelLoc, false, modelMatrix.get(buffer));
        }

        voxelMesh.render();

        shaderProgram.unbind();

        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    @Override
    public boolean shouldClose() {
        return window != NULL && glfwWindowShouldClose(window);
    }

    @Override
    public void cleanup() {
        if (voxelMesh != null) voxelMesh.cleanup();
        if (shaderProgram != null) shaderProgram.cleanup();
        if (window != NULL) glfwDestroyWindow(window);

        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) callback.free();

        log.info("OpenGL and GLFW resources cleaned up successfully.");
    }

    public int getSimWidth() {
        return simWidth;
    }

    public int getSimHeight() {
        return simHeight;
    }

    public int getSimDepth() {
        return simDepth;
    }
}