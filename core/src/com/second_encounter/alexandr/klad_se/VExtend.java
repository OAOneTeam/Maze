package com.second_encounter.alexandr.klad_se;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

public class VExtend extends ExtendViewport {

    public OrthographicCamera camera;

    public VExtend(float width, float height) {
        super(width, height);
        camera = new OrthographicCamera(width, height);
        camera.setToOrtho(false, width, height);
        camera.zoom = 1f;
        setCamera(camera);
    }
}
