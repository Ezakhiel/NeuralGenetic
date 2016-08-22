package com.me.flappybird;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.SnapshotArray;

import cs.ubb.Genetic.GeneticLearn;

public class Screenplay implements Screen {

	private TextureAtlas Atlas;
	private Stage stage;
	public AiBird bird;
	public static Land land;
	public static Label labelScore;
	private float duraTimepipe;
	private float check; //check when to make map
	Array<Actor> allActors;
	public ArrayList screen;
	double[][] data = new double[config.dataHeigth][config.dataWidth];
	GeneticLearn gLearn;

	public Screenplay(Flappybird game, GeneticLearn gL) {
		stage = new Stage();
		game.manager.load("data/flappy.txt", TextureAtlas.class);
		game.manager.finishLoading();
		Atlas = game.manager.get("data/flappy.txt", TextureAtlas.class);
		TextureRegion[] birdRegions = new TextureRegion[] {
				Atlas.findRegion("bird1"), Atlas.findRegion("bird2"),
				Atlas.findRegion("bird3") };
		gLearn = gL;
	}

	private void clearData(){
		for (double[] row : data)
			Arrays.fill(row, 0);
	}

	private void printMap(){
		for (double[] row : data){
			System.out.println(Arrays.toString(row));
		}
		System.out.println("==========================================================================");
	}

	@Override
	public void render(float delta) { // fps renderer
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//my stuff
		//how often to get data
		if ((((int)bird.score % config.makeMapTimer) == 0) && !bird.isDie){
			clearData();
			allActors = stage.getActors();
			for (Actor actor : allActors){
				switch (actor.getName()){
					case "bird":
						//System.out.println("Bird at:"+ actor.getX() +": " + actor.getY());
						data[getDataYLocation(actor.getY())][getDataXLocation(actor.getX())] = 3;
						break;
						
					case "Pipe2":
						//System.out.println("Pipe1 at:"+ actor.getX() + " " + actor.getY());
						int tmpXLoc = Math.abs(getDataXLocation(actor.getX()));
						int tmpYLoc = Math.abs(getDataYLocation(actor.getY()));
						for (int i=0; i<config.dataHeigth; i++){
							if (i == tmpYLoc+1 || i == tmpYLoc+2)
								data[i][tmpXLoc] = 0;
							else
								data[i][tmpXLoc] = 1;
						}
						//data[getDataYLocation(actor.getY())][getDataXLocation(actor.getX())] = 1;
						break;
				}
			}
			//printMap();
		}
		 //print data
		if (gLearn.decision(0, data)){
			if (bird.isDie) {
				resetGame();
			} else {
				bird.Tapme();
				//Flappybird.Sounds.get(config.SoundsJump).play();
			}
		}
		else{
			System.out.println("Dont Jump");
		}

		if (Gdx.input.justTouched()) {
			if (bird.isDie) {
				resetGame();
			} else {
				bird.Tapme();
				//Flappybird.Sounds.get(config.SoundsJump).play();
			}
		}
		duraTimepipe += delta;
		bird.updateScore((int)Math.ceil(delta)); // FITNESS = time alive

		if (duraTimepipe > config.KtimeAddPipe) {
			if (bird.isTapPipe()) {
				duraTimepipe = 0;
				addPipe();
			}
		}
		stage.act();
		stage.draw();
		if (!bird.TapPipe)
			bird.TapPipe = true;
	}
	
	public List<?> getStage(int key){
		//return map for bird with key
		return null;
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(Flappybird.VIEWPORT.x, Flappybird.VIEWPORT.y);
	}

	@Override
	public void show() {
		resetGame();
	}

	public void resetGame() {
		stage.clear();
		Pipe.setPIPE_HIT(1);
		addBackground();
		addBird();
		addScore();
		addLand();
	}

	public void addBird() {
		TextureRegion[] birdRegions = new TextureRegion[] {
				Atlas.findRegion("bird1"), Atlas.findRegion("bird2"),
				Atlas.findRegion("bird3") };
		bird = new AiBird(birdRegions);
		bird.setName("bird");
		bird.setWidth(config.BirdWidth);
		bird.setHeight(config.BirdHeigth);
		bird.setPosition(config.screenWidth / 2 - bird.getWidth(),
				config.screenHeigth / 2);
		stage.addActor(bird);
	}

	public void addBackground() {
		Image bg = new Image(Atlas.findRegion("background"));
		bg.setName("background");
		bg.setWidth(config.screenWidth);
		stage.addActor(bg);
	}

	public void addScore() {
		LabelStyle style = new LabelStyle();
		style.font = new BitmapFont(Gdx.files.internal("data/flappyfont.fnt"),
				Gdx.files.internal("data/flappyfont.png"), false);
		labelScore = new Label("0", style);
		labelScore.setPosition(
				Flappybird.VIEWPORT.x / 2 - labelScore.getWidth() / 2,
				Flappybird.VIEWPORT.y - labelScore.getHeight());
		labelScore.setName("score");
		stage.addActor(labelScore);
	}

	public void addPipe() {
		int r = config.random(0, 10);
		float dy = r * 10;
		r = config.random(0, 1);
		if (r == 0) {
			dy = -dy;
		}
		Pipe pipe1 = new Pipe(Atlas.findRegion("pipe1"), bird, true);
		pipe1.setZIndex(1);
		float x = Flappybird.VIEWPORT.x;
		float y = (Flappybird.VIEWPORT.y - config.KlandHeight) / 2
				+ config.KlandHeight + config.KholeBetwenPipe / 2;
		pipe1.setPosition(x, y + dy);
		Pipe pipe2 = new Pipe(Atlas.findRegion("pipe2"), bird, false);
		pipe2.setZIndex(1);
		y = (Flappybird.VIEWPORT.y - config.KlandHeight) / 2
				+ config.KlandHeight - pipe2.getHeight()
				- config.KholeBetwenPipe / 2;
		pipe2.setPosition(x, y + dy);
		pipe1.setWidth(config.PipeWidth);
		pipe2.setWidth(config.PipeWidth);
		pipe1.setName("Pipe1");
		pipe2.setName("Pipe2");
		stage.addActor(pipe1);
		stage.addActor(pipe2);
	
		labelScore.setZIndex(pipe1.getZIndex());
		land.setZIndex(pipe2.getZIndex());
		bird.setZIndex(pipe2.getZIndex());
	}

	public void addLand() {
		land = new Land(Atlas.findRegion("land"), bird);
		land.setName("land");
		land.setPosition(0, 0);
		stage.addActor(land);
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	public static Vector2 getStageLocation(Actor actor) {
		return actor.localToStageCoordinates(new Vector2(0, 0));
		
	}
	public double getNormalizedXLocation(double x){
		return (x - config.trueXOrigo);
	}
	public double getNormalizedYLocation(double y){
		return (y - config.trueYOrigo);
	}
	
	public int getDataXLocation(double x){
		return (int)((x - config.trueXOrigo)/config.BirdWidth);
	}
	public int getDataYLocation(double y){
		return (int)((y - config.trueYOrigo)/config.BirdHeigth);
	}

}
