package com.me.flappybird;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.forever;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.moveBy;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class Land extends Image {

	private AiBird[] birds;
	private boolean hit;

	public Land(TextureRegion region, AiBird[] bird) {
		super(region);
		this.birds = bird;
		addAction(forever(moveBy(-config.KlandWidth, 0, config.KmoveLeftDura)));
		hit = true;
	}

	@Override
	public void act(float delta) {
		super.act(delta);
		if (getX() <= -config.KlandWidth) {
			setX(0);
		}
		boolean allDead = true;
		for (AiBird bird : birds){
			if (checkcolistion(bird)) {
				bird.hitLand();
			}else{
				allDead = false;
			}
		}
		if (allDead){
			clearActions();
			if (hit && Pipe.getPIPE_HIT() == 1) {
				hit = false;
				//DEAD
				//Flappybird.Sounds.get(config.SoundsHit).play();
			}
		}
	}

	public boolean checkcolistion(AiBird bird) {
		if (bird.getY() <= config.KlandHeight) {
			return true;
		} else {
			return false;
		}
	}

}
