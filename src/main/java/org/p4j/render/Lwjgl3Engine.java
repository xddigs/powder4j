package org.p4j.render;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.p4j.core.K;
import org.p4j.core.World;
import org.p4j.gui.Engine;
import org.p4j.input.Brush;
import org.p4j.input.KeyboardController;
import org.p4j.input.MouseController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Lwjgl3Engine implements Engine {
    private static final Logger log = LoggerFactory.getLogger(Lwjgl3Engine.class);

    private final int simWidth;
    private final int simHeight;
    private final int simDepth;
    private long window = NULL;
    private boolean isGlInitialized = false;

    public Lwjgl3Engine(int simWidth, int simHeight, int simDepth, World world) {
        this.simWidth = simWidth;
        this.simHeight = simHeight;
        this.simDepth = simDepth;
    }

    @Override
    public void init(World world) {
        GLFWErrorCallback.createPrint(System.err).set();

        if (!glfwInit()) {
            throw new IllegalStateException("GLFW couldn't initialize");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        window = glfwCreateWindow(1280, 720, K.APP_TITLE, NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("GLFW error, couldn't create a window");
        }

        glfwShowWindow(window);
        glfwMakeContextCurrent(NULL);
    }

    private void initGLContext() {
        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        glfwSwapInterval(1);
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1f, 0.1f, 0.12f, 1.0f);
        log.info("Lwjgl3Engine ran OpenGL successfully: {}", glGetString(GL_VERSION));
        isGlInitialized = true;
    }

    @Override
    public void updatePixels(World world) {

    }

    @Override
    public void render(World world, KeyboardController keyController,
                       MouseController mouseController, Brush brush) {
        if (!isGlInitialized) {
            initGLContext();
        }

        if (shouldClose()) return;

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(window);
        glfwPollEvents();
    }

    @Override
    public boolean shouldClose() {
        return window != NULL && glfwWindowShouldClose(window);
    }

    @Override
    public void cleanup() {
        if (window != NULL) {
            glfwDestroyWindow(window);
        }
        glfwTerminate();
        GLFWErrorCallback callback = glfwSetErrorCallback(null);
        if (callback != null) {
            callback.free();
        }
        log.info("Resources freed");
    }
}