package com.zeljko;


import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import com.zeljko.graphics.Camera;
import com.zeljko.graphics.Light;
import com.zeljko.ui.Gui;
import com.zeljko.utils.TextureLoader;
import lombok.Getter;


import javax.swing.*;
import java.awt.*;


import static com.zeljko.utils.Constants.WINDOW_HEIGHT;
import static com.zeljko.utils.Constants.WINDOW_WIDTH;

public class GameActuator implements GLEventListener {

    @Getter
    private GameState gameState;
    private final InputListener inputListener;
    private final Camera camera;
    private Gui gui;
    private final Renderer renderer;
    private final Light light;
    private GLCanvas canvas;
    private FPSAnimator animator;

    public GameActuator(JFrame frame) {
        GLProfile profile = GLProfile.getDefault();
        GLCapabilities caps = new GLCapabilities(profile);
        caps.setAlphaBits(8);
        caps.setDepthBits(24);
        caps.setDoubleBuffered(true);
        caps.setStencilBits(8);

        this.gameState = new GameState();
        this.gameState.setRandomBlueprint();
        this.camera = new Camera(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.inputListener = new InputListener(gameState, camera);
        this.light = new Light();
        this.renderer = new Renderer();


        SwingUtilities.invokeLater(() -> {
            canvas = new GLCanvas(caps);
            canvas.setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
            canvas.addGLEventListener(this);
            canvas.addKeyListener(inputListener);
            canvas.addMouseListener(inputListener);
            canvas.addMouseMotionListener(inputListener);
            canvas.addMouseWheelListener(inputListener);

            animator = new FPSAnimator(canvas, 60);
            gui = new Gui(frame, inputListener, this);
            gui.setBlueprintImage(gameState.getCurrentBlueprint().getBlueprintType());
            GameAction.updateModelConstraints(gui::updateModelCount);
            frame.getContentPane().add(canvas, BorderLayout.CENTER);
            frame.setJMenuBar(gui.getMenuBar());
            frame.add(gui.getPanel(), BorderLayout.SOUTH);
            frame.add(gui.getToolBar(), BorderLayout.NORTH);

            camera.setCenter(0, 0, 0);
            camera.zoom(-2);

            frame.pack();
            frame.setVisible(true);
            animator.start();
        });
    }

    public void stop() {
        animator.stop();
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        GL2 gl = drawable.getGL().getGL2();
        gl.glClearColor(0, 0, 0, 0);

        light.setupLighting(gl);
        TextureLoader.loadAllTextures(gl);
    }

    @Override
    public void display(GLAutoDrawable glAutoDrawable) {
        GL2 gl = glAutoDrawable.getGL().getGL2();
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

        camera.setupViewport(gl);
        camera.setupProjection(gl);
        camera.applyViewTransform(gl);

        float[] zero = {0, 0, 0, 1};
        light.applyLighting(gl, zero);

        // turn on the global ambient light
        if(gui.getGlobalAmbientLight().isSelected()){ // if it's checked
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
                    new float[] {0.2F, 0.2F, 0.2F, 1},
                    0);

        }else{
            gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT,
                    zero,
                    0);
        }

        // add a specular light to make the object shiny
        gl.glMaterialfv(GL2.GL_FRONT_AND_BACK,
                GL2.GL_SPECULAR,
                new float[] {0.2F, 0.2F, 0.2F, 1}, 0);

        if (gameState.getCurrentBlueprint() != null) {
            renderer.renderScene(gl, gameState);
        }
    }

    public void startAutocomplete() {
        GameAction.startAutocomplete(this, gameState);
    }

    public boolean checkAlignment() {
        boolean isAligned = GameAction.checkAlignment(gameState.getCurrentBlueprint(), gameState.getUserModels());
        if (isAligned) {
            gameState.setAlignmentCheck(true);
            gui.showNextLevelButton();
        }

        return isAligned;
    }

    public void newGame() {
        gameState = new GameState();
        gameState.setRandomBlueprint();
        gui.updateModelCount();
        gui.hideNextLevelButton();
        gui.setBlueprintImage(gameState.getCurrentBlueprint().getBlueprintType());
        requestRender();
    }

    public void nextLevel() {
        if (!gameState.getRemainingBlueprints().isEmpty()) {
            gameState.setRandomBlueprint();

            // rest game state
            gameState.getUserModels().clear();
            gameState.setCurrentModelIndex(-1);
            gameState.setAlignmentCheck(false);

            // update gui
            gui.updateModelCount();
            gui.hideNextLevelButton();
            gui.setBlueprintImage(gameState.getCurrentBlueprint().getBlueprintType());
        } else {
            gui.showAllLevelCompleteMessage();
        }
    }

    public void updateLighting() {
        light.setLightOnOff(gui.getLightOnOff().isSelected());
        light.setAmbientLightOn(gui.getAmbientLight().isSelected());
        light.setDiffuseLightOn(gui.getDiffuseLight().isSelected());
        light.setSpecularLightOn(gui.getSpecularLight().isSelected());
        requestRender();
    }

    public void requestRender() {
        if (canvas != null) {
            canvas.display();
        }
    }

    @Override
    public void reshape(GLAutoDrawable glAutoDrawable, int x, int y, int w, int h) {
        WINDOW_HEIGHT = h;
        WINDOW_WIDTH = w;
    }

    @Override
    public void dispose(GLAutoDrawable glAutoDrawable) {
    }
}
