package com.csse3200.game.components.car;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.files.UserSettings;
import com.csse3200.game.input.InputComponent;
import com.csse3200.game.utils.math.Vector2Utils;

public class KeyboardCarInputComponent extends InputComponent implements InputProcessor {
    private final Vector2 movementDirection = new Vector2();
    private com.csse3200.game.components.car.CarActions carActions;
    private UserSettings walkDirection;



    @Override
    public boolean keyDown(int keycode) {
        if (!carActions.isSilent()) {
            Vector2 direction = getKeyDirection(keycode);
            if (direction != null) {
                movementDirection.add(direction);
                triggerWalkEvent();
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        if (!carActions.isSilent()) {
            Vector2 direction = getKeyDirection(keycode);
            if (direction != null) {
                movementDirection.sub(direction);
                triggerWalkEvent();
                return true;
            }
        }
        return false;
    }

    private Vector2 getKeyDirection(int keycode) {
        switch (keycode) {
            case Input.Keys.G:
                return Vector2Utils.UP;
            case Input.Keys.C:
                return Vector2Utils.LEFT;
            case Input.Keys.V:
                return Vector2Utils.DOWN;
            case Input.Keys.B:
                return Vector2Utils.RIGHT;
            default:
                return null;
        }
    }

    private void triggerWalkEvent() {
        if (movementDirection.isZero()) {
            entity.getEvents().trigger("walkStop");
        } else {
            entity.getEvents().trigger("walk", movementDirection);
        }
    }


    public void setActions(com.csse3200.game.components.car.CarActions actions) {
        this.carActions = actions;
    }

}