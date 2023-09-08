
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.csse3200.game.components.CombatStatsComponent;
import com.csse3200.game.components.HealthBarComponent;
import com.csse3200.game.components.ProximityActivationComponent;
import com.csse3200.game.components.joinable.JoinLayer;
import com.csse3200.game.components.joinable.JoinableComponent;
import com.csse3200.game.components.joinable.JoinableComponentShapes;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.entities.configs.GateConfig;
import com.csse3200.game.entities.configs.WallConfig;
import com.csse3200.game.entities.configs.WallConfigs;
import com.csse3200.game.files.FileLoader;
import com.csse3200.game.physics.PhysicsLayer;
import com.csse3200.game.physics.components.ColliderComponent;
import com.csse3200.game.physics.components.PhysicsComponent;
import com.csse3200.game.rendering.AtlasRenderComponent;
import com.csse3200.game.services.ServiceLocator;

public class Portal extends Entity {
    public Portal(Entity player) {
        addComponent(new ProximityActivationComponent(1.5f, player, this::teleport, null));
        addComponent(new PhysicsComponent().setBodyType(BodyDef.BodyType.StaticBody));
        //addComponent(new ColliderComponent().setLayer(PhysicsLayer.WALL));
    }

    /**
     * Changes the texture to resemble a closed gate and enables the collision.
     *
     * @param player - the player who opened the gate
     */
    public void teleport(Entity player) {
        player.setPosition(5, 5);
    }
}
