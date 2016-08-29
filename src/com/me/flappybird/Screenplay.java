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
	double[][] data ;
	double[][] birdData;
	public GeneticLearn gLearn;
	public int population;
	public static boolean allDead;
	int timesPlayed;
	float timePassed;
	float lastTopPipeLocation;

	public Screenplay(Flappybird game, GeneticLearn gL) {
		stage = new Stage();
		game.manager.load("data/flappy.txt", TextureAtlas.class);
		game.manager.finishLoading();
		Atlas = game.manager.get("data/flappy.txt", TextureAtlas.class);
		initGeneration(gL);
		timePassed = 0f;
		lastTopPipeLocation = config.trueYOrigo + 7 * config.BirdHeigth;
		duraTimepipe = 4;
	}
	
	public void initGeneration(GeneticLearn gL){
		gLearn = gL;
		population = gL.geneticDTO.population;
		data = new double[config.dataHeigth][config.dataWidth-1];
		birdData = new double[population][config.dataHeigth];
		birds = new AiBird[population];
		timesPlayed = 1;
	}

	private void clearData(){
		for (double[] row : data)
			Arrays.fill(row, config.NOTHING);
		for (double[] row : birdData)
			Arrays.fill(row, config.NOTHING);
	}

	private void printMap(){
		for (int i=0; i < data.length ; i++){
			System.out.print(birdData[0][i]);
			System.out.println(Arrays.toString(data[i]));
		}
		System.out.println("========================================================");
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
		draw(delta);
		
	}

	private void draw(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		//my stuff
		timePassed += delta;
		
		if (timePassed >= config.makeDecisionTimer){
			if (hasAliveBird()){
				clearData();
				timePassed = 0;
				allActors = stage.getActors();
				for (Actor actor : allActors){//set map
					switch (actor.getName()){
						case "bird":
							birdData[((AiBird)actor).id]
								[config.dataHeigth-1-getDataYLocation(actor.getY())]
										= config.BRDVALUE;
							break;
							
						case "Pipe2":
							int tmpXLoc = Math.abs(getDataXLocation(actor.getX()));
							int tmpYLoc = Math.abs(getDataYLocation(actor.getY()));
							if (tmpXLoc > 0){
								for (int i=config.dataHeigth-1; i>=0; i--){
									if (i == tmpYLoc-1 || i == tmpYLoc-2 || i == tmpYLoc-3 || i == tmpYLoc-4)
										data[config.dataHeigth-1-i][tmpXLoc] = config.PIPEVALUE;// ha szabad
									else
										data[config.dataHeigth-1-i][tmpXLoc] = config.NOTHING;// ha cso van
								}
							}
							break;
					}
				}//map sett
				//printMap();
			}
		
			duraTimepipe += 1;
			double decision = 0;
			for (AiBird bird : birds){
				if (!bird.isDie){
					//normalizeInput(bird.id);
					decision = gLearn.decision(bird.id, data, birdData[bird.id]);
					if (decision >= 2)
							bird.Tapme();
					// FITNESS = time alive
					bird.updateScore((int)Math.ceil(delta));
				}
				//if (!bird.TapPipe)
				//	 bird.TapPipe = true;
			}
		}
		/*
		if (Gdx.input.justTouched()) {
			birds[0].Tapme();
		}
		*/
		if (duraTimepipe > config.KtimeAddPipe) {
				duraTimepipe = 0;
				addPipe();
		}
		if (!hasAliveBird()) {
			resetGame();
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
				gLearn.evolve();
			} catch (IOException e) {
				System.out.println("ERROR SAVING POPULATION!");
			}
			
			stage.clear();
			//return;
			timesPlayed = 1;
			duraTimepipe = 4;
			birds = new AiBird[population];
			Pipe.setPIPE_HIT(1);
			addBackground();
			for (int i=0;i<population;i++)
				addBird(i);
			addScore(gLearn.generation+"");
			addLand();
		}
		stage.clear();
		duraTimepipe = 4;
		timesPlayed++;
		Pipe.setPIPE_HIT(1);
		addBackground();
		if (birds[0] == null)
			for (int i=0;i<population;i++)
				addBird(i);
		else
			for (int i=0;i<population;i++)
				addBird(birds[i]);
		addScore(gLearn.generation+"");
		addLand();
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
	
	public void addBird(AiBird old) {
		TextureRegion[] birdRegions = new TextureRegion[] {
				Atlas.findRegion("bird1"), Atlas.findRegion("bird2"),
				Atlas.findRegion("bird3") };
		AiBird bird = new AiBird(birdRegions, old.id);
		bird.setName("bird");
		bird.setWidth(config.BirdWidth);
		bird.setHeight(config.BirdHeigth);
		bird.setPosition(config.screenWidth / 2 - bird.getWidth(),
				config.screenHeigth / 2);
		bird.score = old.score;
		birds[old.id] = bird;
		stage.addActor(bird);
	}

	public void addBackground() {
		Image bg = new Image(Atlas.findRegion("background"));
		bg.setName("background");
		bg.setWidth(config.screenWidth);
		stage.addActor(bg);
	}

	public void addScore(String gen) {
		LabelStyle style = new LabelStyle();
		style.font = new BitmapFont(Gdx.files.internal("data/flappyfont.fnt"),
				Gdx.files.internal("data/flappyfont.png"), false);
		labelScore = new Label(gen, style);
		labelScore.setPosition(
				Flappybird.VIEWPORT.x / 2 - labelScore.getWidth() / 2,
				Flappybird.VIEWPORT.y - labelScore.getHeight());
		labelScore.setName("score");
		stage.addActor(labelScore);
	}

	public void addPipe() {
		int r = config.random(-2, 2);
		float x = Flappybird.VIEWPORT.x;
		float y = r * config.BirdHeigth
				+ lastTopPipeLocation;
		
		if (y < config.KlandHeight + config.KholeBetwenPipe)
			y = config.KlandHeight + config.KholeBetwenPipe;
		if (y > Flappybird.VIEWPORT.y)
			y = Flappybird.VIEWPORT.y;
		
		lastTopPipeLocation = y;
		
		Pipe pipe2 = new Pipe(Atlas.findRegion("pipe1"), birds);
		pipe2.setZIndex(1);
		pipe2.setPosition(x, y);
		
		Pipe pipe1 = new Pipe(Atlas.findRegion("pipe2"), birds);
		pipe1.setZIndex(1);
		pipe1.setPosition(x, y - pipe1.getHeight() - config.KholeBetwenPipe );

		
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
		int X = (int) ((x - config.trueXOrigo)/config.BirdWidth);
		return (X > 8) ? 8 : X;
	}
	public int getDataYLocation(double y){
		int Y = (int) ((y - config.trueYOrigo)/config.BirdHeigth);
		return (Y > 9) ? 9: Y;
	}
	/*
	private void normalizeInput(int index){
		for (int i=0; i< config.dataWidth; i++){
			for (int j=0; j< config.dataHeigth; j++) {
				data[index][j][i] = (data[index][j][i] - config.NOTHING) / (config.BRDVALUE - config.NOTHING);
			}
		}
	}
	*/
}
