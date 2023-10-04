package com.csse3200.game.components.car;

import com.csse3200.game.components.Component;
import com.csse3200.game.rendering.AnimationRenderComponent;


public class CarAnimationController extends Component {
    AnimationRenderComponent animator;

    @Override
    public void create() {
        super.create();
        animator = this.entity.getComponent(AnimationRenderComponent.class);
        entity.getEvents().addListener("stopWalking", this::animateStopMoving);
        entity.getEvents().addListener("startWalking", this::animateMoving);
        entity.getEvents().addListener("startwalkdown", this::animatedown);

        entity.getEvents().addListener("car", this::animateIdle);
        animator.startAnimation("car_up");
    }
    void animateStopMoving() {
        animator.startAnimation("car_up");
    }


    void animateMoving() {

        animator.startAnimation("car_left");
    }

    void animatedown() {

        animator.startAnimation("car_down");
    }

    void animateIdle(String direction) {
        animator.startAnimation(String.format("car_", direction));
    }
}
