package com.me.flappybird;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;

import java.util.HashMap;

import cs.ubb.Genetic.GeneticLearn;

public class Flappybird extends Game {
	
	public static final Vector2 VIEWPORT = new Vector2(config.screenWidth, config.screenHeigth);
	public AssetManager manager = new AssetManager();
	public static HashMap<String, Sound> Sounds = new HashMap<String, Sound>();
	private GeneticLearn gL;
	public Screenplay sP;

	public Flappybird(GeneticLearn gLearn) {
		// TODO Auto-generated constructor stub
		gL = gLearn;
	}
	@Override
	public void create() {
//		Sounds.put(config.SoundsHit, Gdx.audio.newSound(Gdx.files.internal("data/sounds/sfx_hit.mp3")));
//		Sounds.put(config.SoundsScore, Gdx.audio.newSound(Gdx.files.internal("data/sounds/sfx_point.mp3")));
//		Sounds.put(config.SoundsJump, Gdx.audio.newSound(Gdx.files.internal("data/sounds/sfx_wing.mp3")));
		sP = new Screenplay(this,gL);
		setScreen(sP);
	}

	@Override
	public void dispose() {
		for (String key : Sounds.keySet()) {
			Sounds.get(key).dispose();
		}
		manager.dispose();
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		super.resize(width, height);
	}
	
	@Override
	public void pause(){
		super.pause();
	}
	
	@Override
	public void resume(){
		super.resume();
	}

}
