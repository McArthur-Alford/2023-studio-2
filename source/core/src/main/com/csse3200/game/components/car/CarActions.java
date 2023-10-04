package com.csse3200.game.components.car;

import com.badlogic.gdx.math.Vector2;
import com.csse3200.game.entities.Entity;
import com.badlogic.gdx.physics.box2d.Body;
import com.csse3200.game.components.Component;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.services.ServiceLocator;

public class CarActions extends Component {

    private Vector2 walkDirection = Vector2.Zero.cpy();
    private float Direction = 100;

    private static final Vector2 CAR_SPEED = new Vector2(8f, 8f);

    private static final float TIME_STEP = 1.0f / 60.0f;

    private boolean isMoving = false;

    private Entity player = ServiceLocator.getEntityService().getPlayer();
    private boolean isSilent = false;

    // Constants to represent control modes
    private static final int CONTROL_NORMAL = 0;
    private static final int CONTROL_CUSTOM = 1;
    private int controlMode = CONTROL_NORMAL;

    private PhysicsComponent physicsComponent;

    @Override
    public void create() {
        physicsComponent = entity.getComponent(PhysicsComponent.class);
        entity.getEvents().addListener("walk", this::walk);
        entity.getEvents().addListener("walkStop", this::stopWalking);
    }

    @Override
    public void update() {
        if (!isSilent) {
            if (isMoving) {
                updateSpeed();
            }
        }
    }

    public void setControlMode(int mode) {
        controlMode = mode;
    }

    public boolean isSilent() {
        return isSilent;
    }

    private void updateSpeed() {
        Body body = physicsComponent.getBody();
        Vector2 velocity = body.getLinearVelocity();
        Vector2 desiredVelocity = walkDirection.cpy().scl(CAR_SPEED);
        Vector2 force = desiredVelocity.sub(velocity).scl(body.getMass() / TIME_STEP);
        body.applyForceToCenter(force, true);
    }

    void walk(Vector2 direction) {
        this.walkDirection = direction;
        this.Direction = walkDirection.angleDeg();
        this.isMoving = true;
    }

    public void stopWalking() {
        this.walkDirection = Vector2.Zero.cpy();
        updateSpeed();
        this.isMoving = false;
    }
}