package cs.ubb.Genetic;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.me.flappybird.Flappybird;
import com.me.flappybird.config;

import cs.ubb.neural.NNetwork;

public class GeneticLearn {

	public GeneticLearn(GeneticDTO geneticDTO) {
		this.geneticDTO = geneticDTO;
	}

	private NNetwork[] population;
	public GeneticDTO geneticDTO;
	
	public boolean decision(int networkIndex,double[][] data){
		double[] flatData = new double[data.length * data[1].length];
		int k = 0;
		for (int i = 0; i < data.length; i++) {
		    for (int j = 0; j < data[i].length; j++) {
		        flatData[k++] = data[i][j];
		    }
		}
		
		return population[networkIndex].decision(flatData);
	}

	
	public void learn(){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gameandroiddemo";
		cfg.useGL20 = true;
		cfg.width = config.screenWidth;
		cfg.height = config.screenHeigth;
		
		cfg.x = 0;
		cfg.y = 0;
		new LwjglApplication(new Flappybird(this), cfg);
	}
	
	public void loadFromFile(){
		
	}
	
	public void save(){
		
	}
	
	public void getWeights(int networkIndex){
		population[networkIndex].getWeigths();
	}
	
	public void randomWeights(){
		population = new NNetwork[geneticDTO.population];
		for (int i=0; i<geneticDTO.population; i++){
			population[i] = new NNetwork(config.dataHeigth * config.dataWidth,0,2.5);
			population[i].init();
			population[i].randomWeights();
		}
	}
}
