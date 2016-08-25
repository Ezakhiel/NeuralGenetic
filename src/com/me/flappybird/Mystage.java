package com.me.flappybird;

import com.badlogic.gdx.scenes.scene2d.Stage;

public class Mystage extends Stage {

	Screenplay screenplay;

	public Mystage(float width, float height, boolean keepAspectRatio) {
		super(width, height, keepAspectRatio);
		// TODO Auto-generated constructor stub
	}

	public Mystage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setScreenplay(Screenplay screenplay) {
		this.screenplay = screenplay;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if (screenplay != null) {
			/* For android manual input
			if (screenplay.birds.isDie) {
				screenplay.resetGame();
			} else {
				screenplay.birds.Tapme();
			}
			*/
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

}
