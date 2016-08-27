package com.me.flappybird;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.utils.Array;

import java.io.IOException;
import java.util.Arrays;

import cs.ubb.Genetic.GeneticLearn;

public class Screenplay implements Screen {
	
	private TextureAtlas Atlas;
	private Stage stage;
	public AiBird[] birds;
	public static Land land;
	public static Label labelScore;
	private float duraTimepipe;
	Array<Actor> allActors;
	double[][][] data ;
	GeneticLearn gLearn;
	public int population;
	public static boolean allDead;
	int timesPlayed;

	public Screenplay(Flappybird game, GeneticLearn gL) {
		stage = new Stage();
		game.manager.load("data/flappy.txt", TextureAtlas.class);
		game.manager.finishLoading();
		Atlas = game.manager.get("data/flappy.txt", TextureAtlas.class);
		gLearn = gL;
		population = gL.geneticDTO.population;
		data = new double[population][config.dataHeigth][config.dataWidth];
		birds = new AiBird[population];
		timesPlayed = 1;
	}

	private void clearData(){
		for (double[][] map : data)
			for (double[] row : map)
				Arrays.fill(row, config.NOTHING);
	}

	private void printMap(){
		for (double[][] map : data)
			for (double[] row : map)
			System.out.println(Arrays.toString(row));
	}
	
	public boolean hasAliveBird(){
		for (AiBird aiBird : birds){
			if (!aiBird.isDie)
				return true;
		}
		return false;
	}

	@Override
	public void render(float delta) { // fps renderer
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//my stuff
		if ((( ((int)delta) % config.makeMapTimer) == 0) && hasAliveBird()){
			clearData();
			allActors = stage.getActors();
			for (Actor actor : allActors){
				switch (actor.getName()){
					case "bird":
						//System.out.println("Bird at:"+ actor.getX() +": " + actor.getY());
						data[((AiBird)actor).id]
							[getDataYLocation(actor.getY())]
							[getDataXLocation(actor.getX())] = config.BRDVALUE;
						break;
						
					case "Pipe2":
						//System.out.println("Pipe1 at:"+ actor.getX() + " " + actor.getY());
						int tmpXLoc = Math.abs(getDataXLocation(actor.getX()));
						int tmpYLoc = Math.abs(getDataYLocation(actor.getY()));
						for (int i=0; i<config.dataHeigth; i++){
							if (i == tmpYLoc+1 || i == tmpYLoc+2)
								for (double[][] map : data)
									map[i][tmpXLoc] = config.NOTHING;
							else
								for (double[][] map : data)
									map[i][tmpXLoc] = config.PIPEVALUE;
						}
						//data[getDataYLocation(actor.getY())][getDataXLocation(actor.getX())] = 1;
						break;
				}
			}
			//printMap();
		}
		//how often to get data
		 //print data
		allDead = true;
		duraTimepipe += delta;
		for (AiBird bird : birds){
			if (!bird.isDie){
				allDead = false;
				//System.out.print("NN " + bird.id + ":");
				normalizeInput(bird.id);
				if (gLearn.decision(0, data[bird.id])){
						bird.Tapme();
				}
				// FITNESS = time alive
				bird.updateScore((int)Math.ceil(delta));
			}
			if (!bird.TapPipe)
				 bird.TapPipe = true;
		}
		if (allDead) {
			resetGame();
		}
		/*
		 * Manual Jump */
//		if (Gdx.input.justTouched()) {
//				birds[0].Tapme();
//		}
		if (duraTimepipe > config.KtimeAddPipe) {
			//if (birds.isTapPipe()) {  				??????????
				duraTimepipe = 0;
				addPipe();
			//}
		}
		stage.act();
		stage.draw();

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
		if (timesPlayed > config.ONEGENTIMER){
			gLearn.setFitness(birds);
			try {
				gLearn.save();
			} catch (IOException e) {
				System.out.println("ERROR SAVING POPULATION!");
			}
			return;
		}
		stage.clear();
		timesPlayed++;
		Pipe.setPIPE_HIT(1);
		addBackground();
		for (int i=0;i<population;i++)
			addBird(i);
		addScore();
		addLand();
	}
	public void startNewGame() {
		
	}

	public void addBird(int id) {
		TextureRegion[] birdRegions = new TextureRegion[] {
				Atlas.findRegion("bird1"), Atlas.findRegion("bird2"),
				Atlas.findRegion("bird3") };
		AiBird bird = new AiBird(birdRegions, id);
		bird.setName("bird");
		bird.setWidth(config.BirdWidth);
		bird.setHeight(config.BirdHeigth);
		bird.setPosition(config.screenWidth / 2 - bird.getWidth(),
				config.screenHeigth / 2);
		birds[id] = bird;
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
		Pipe pipe1 = new Pipe(Atlas.findRegion("pipe1"), birds, true);
		pipe1.setZIndex(1);
		float x = Flappybird.VIEWPORT.x;
		float y = (Flappybird.VIEWPORT.y - config.KlandHeight) / 2
				+ config.KlandHeight + config.KholeBetwenPipe / 2;
		pipe1.setPosition(x, y + dy);
		Pipe pipe2 = new Pipe(Atlas.findRegion("pipe2"), birds, false);
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
		for (AiBird bird : birds){
			bird.setZIndex(pipe2.getZIndex());
		}
	}

	public void addLand() {
		land = new Land(Atlas.findRegion("land"), birds);
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
		stage.clear();
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}
	public void exit(){
		
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
	
	private void normalizeInput(int index){
		for (int i=0; i< config.dataWidth; i++){
			for (int j=0; j< config.dataHeigth; j++) {
				data[index][j][i] = (data[index][j][i] - config.PIPEVALUE) / (config.BRDVALUE - config.PIPEVALUE);
			}
		}
	}

}
