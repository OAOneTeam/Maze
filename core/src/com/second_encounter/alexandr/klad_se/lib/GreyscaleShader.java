package com.second_encounter.alexandr.klad_se.lib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class GreyscaleShader extends ShaderProgram {

    public GreyscaleShader() {
        super(SpriteBatch.createDefaultShader().getVertexShaderSource(), Gdx.files.internal("shader/greyscale.fragment.glsl").readString());
        if (!isCompiled())
            throw new IllegalArgumentException("Error compiling 'Greyscale' shader: " + getLog());
    }
}
