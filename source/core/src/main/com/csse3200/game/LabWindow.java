package com.csse3200.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import com.csse3200.game.components.PowerupType;
import com.csse3200.game.entities.Entity;
import com.csse3200.game.input.InputOverrideComponent;
import com.csse3200.game.services.ServiceLocator;

/**
 * This is a window that can be added to a stage to pop up for the extractor Laboratory.
 */
public class LabWindow extends Window {
    private final InputOverrideComponent inputOverrideComponent;
    Table buttonTable;
    Table exit;

    public static LabWindow MakeNewLaboratory() {
        Texture background = new Texture("images/labwindownew.png");
        background.setWrap(Texture.TextureWrap.ClampToEdge, Texture.TextureWrap.ClampToEdge);
        return new LabWindow(background);
    }

    public LabWindow(Texture background) {
        super("", new WindowStyle(new BitmapFont(), Color.BLACK, new TextureRegionDrawable(background)));

        // Here set up the window to be centered on the stage with 80% width and 65% height.
        Stage stage = ServiceLocator.getRenderService().getStage();
        setWidth((float) (stage.getWidth() * 0.8));
        setHeight((float) (stage.getHeight() * 0.65));
        setPosition(stage.getWidth() / 2 - getWidth() / 2 * getScaleX(), stage.getHeight() / 2 - getHeight() / 2 * getScaleY());
        Skin skin = new Skin(Gdx.files.internal("kenney-rpg-expansion/kenneyrpg.json"));
        // Create a Table to hold the buttons and center them within the window
        Table buttonTable = new Table();
        buttonTable.setFillParent(true);
        // Fill the entire LabWindow

        Table exit = new Table();
        Texture deathpotionImage = new Texture("images/Potion3re.png");
        Texture speedpotionImage = new Texture("images/Potion4re.png");
        Texture healthpotionImage = new Texture("images/Potion2re.png");
        Texture invincibilitypotionImage = new Texture("images/Potion1re.png");
        Texture doubledamageImage = new Texture("images/powerups/double_damage.png");
        Image potion1ImageWidget = new Image(deathpotionImage);
        Image potion2ImageWidget = new Image(speedpotionImage);
        Image potion3ImageWidget = new Image(healthpotionImage);
        Image potion4ImageWidget = new Image(invincibilitypotionImage);
        Image potion5ImageWidget = new Image(doubledamageImage);
        TextButton potion1 = new TextButton("Death", skin);
        TextButton potion2 = new TextButton("Speed", skin);
        TextButton potion3 = new TextButton("Health", skin);
        TextButton potion4 = new TextButton("Invincibility", skin);
        TextButton returnToGameButton = new TextButton("Return to Game", skin);
        float buttonWidth = 200f; // Adjust as needed
        float buttonHeight = 200f;
        potion1.setWidth(buttonWidth);
        potion1.setHeight(buttonHeight);

        potion2.setWidth(buttonWidth);
        potion2.setHeight(buttonHeight);

        potion3.setWidth(buttonWidth);
        potion3.setHeight(buttonHeight);

        potion4.setWidth(buttonWidth);
        potion4.setHeight(buttonHeight);

        returnToGameButton.setWidth(buttonWidth);
        returnToGameButton.setHeight(buttonHeight);

        potion1.add(potion1ImageWidget).width(100).height(64);
        potion2.add(potion2ImageWidget).width(100).height(64);
        potion3.add(potion3ImageWidget).width(100).height(64);
        potion4.add(potion4ImageWidget).width(100).height(64);

        buttonTable.top().left();
        buttonTable.add(potion1).padTop(350).padLeft(150);
        buttonTable.add(potion2).padTop(350).padLeft(190);
        buttonTable.add(potion3).padTop(350).padLeft(165);
        buttonTable.add(potion4).padTop(350).padLeft(180);

        buttonTable.row(); //Move to the next row
        exit.add(returnToGameButton).bottom().right().padBottom(150).padLeft(2800);
        addActor(buttonTable);
        addActor(exit);

        Table button2Table = new Table();
        button2Table.setFillParent(true);


        TextButton potion5 = new TextButton("DoubleDamage",skin);

        potion5.setWidth(buttonWidth);
        potion5.setHeight(buttonHeight);
        potion5.add(potion5ImageWidget).width(100).height(64);
        button2Table.add(potion5).padTop(500).padRight(1200);
        button2Table.row(); //Move to the next row
        addActor(button2Table);


        returnToGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                failLaboratory();
            }
        });
        potion1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event,float x, float y){
                ServiceLocator.getEntityService().getCompanion().getEvents().trigger("SpawnPowerup", PowerupType.DOUBLE_DAMAGE);
                failLaboratory();
            }
        });
        potion2.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ServiceLocator.getEntityService().getCompanion().getEvents().trigger("SpawnPowerup",PowerupType.SPEED_BOOST);
                failLaboratory();
            }
        });
        potion3.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ServiceLocator.getEntityService().getCompanion().getEvents().trigger("SpawnPowerup",PowerupType.HEALTH_BOOST);
                failLaboratory();
            }
        });

        potion4.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ServiceLocator.getEntityService().getCompanion().getEvents().trigger("SpawnPowerup",PowerupType.TEMP_IMMUNITY);
                failLaboratory();
            }
        });
        /*potion5.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ServiceLocator.getEntityService().getCompanion().getEvents().trigger("SpawnPotion",PotionType.DOUBLE_DAMAGE);
                failLaboratory();
            }
        });*/
        // Override all normal user input
        inputOverrideComponent = new InputOverrideComponent();
        ServiceLocator.getInputService().register(inputOverrideComponent);
    }

    /**
     * Call this method to exit the Laboratory
     */
    private void failLaboratory() {
        remove();
    }

    /**
     * Call this method to exit the Laboratory and repair the extractor's health.
     */

    @Override
    public boolean remove() {
        // Stop overriding input when exiting the Laboratory
        ServiceLocator.getInputService().unregister(inputOverrideComponent);
        return super.remove();
    }
}