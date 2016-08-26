package cs.ubb.Genetic;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.me.flappybird.AiBird;
import com.me.flappybird.Flappybird;
import com.me.flappybird.Screenplay;
import com.me.flappybird.config;

import org.apache.commons.lang3.ArrayUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import cs.ubb.neural.NNetwork;

public class GeneticLearn {
	
    public int generation;
	private NNetwork[] population;
    private NNetwork[] selected;
	public GeneticDTO geneticDTO;
	public int selectCount;
	public int mutateCount;
	public int crossCount;
	public int randomCount;
    InputStream inFile;
    OutputStream outFile;
    public LwjglApplication game;
    public Flappybird fl;
    public Screenplay screenplay;
	
	
	public GeneticLearn(GeneticDTO geneticDTO) {
		this.geneticDTO = geneticDTO;
		selectCount = (int) ((geneticDTO.population * geneticDTO.crossPercentage) / 100);
		mutateCount = (int) ((geneticDTO.population * geneticDTO.mutationPercentage) / 100);
		crossCount = (int) ((geneticDTO.population * geneticDTO.crossPercentage) / 100);
		randomCount = geneticDTO.population - selectCount - mutateCount - crossCount;
		population = new NNetwork[geneticDTO.population];
	}


	
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
	
	public void play(){
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gameandroiddemo";
        cfg.useGL20 = true;
		cfg.width = config.screenWidth;
		cfg.height = config.screenHeigth;
		cfg.x = 0;
		cfg.y = 0;
		cfg.forceExit= false;
        if (game == null) {
            fl = new Flappybird(this,screenplay);
            game = new LwjglApplication(fl, cfg);
        }else{
            fl.resume();
        }

            
	}
	
	public void loadFromFile(int gen) throws FileNotFoundException {
		inFile = new FileInputStream("generation" + gen + ".dat");
        Scanner readIn = new Scanner(inFile);
        double[][] gene = new double[3][];
        gene[0] = new double[config.FIRSTCONNECTIONCOUNT];
        gene[1] = new double[config.SECONDCONNECTIONCOUNT];
        gene[2] = new double[config.THIRDCONNECTIONCOUNT];
		for (int p=0; p<geneticDTO.population; p++){
        	for (int i = 0; i< config.PERCEPTRONSIZE * config.HIDDENLAYERSIZE1;i++){
				gene[0][i] = readIn.nextDouble();
			}
			for (int i = 0; i< config.HIDDENLAYERSIZE1 * config.HIDDENLAYERSIZE2;i++){
				gene[1][i] = readIn.nextDouble();
			}
			for (int i = 0; i< config.HIDDENLAYERSIZE2;i++){
				gene[2][i] = readIn.nextDouble();
			}
			population[p] =  new NNetwork(0,1);
			population[p].init(gene);
			readIn.nextInt();
        }
	}
	
	public void save() throws IOException {
		outFile = new FileOutputStream("generation"+ generation+ ".dat",false);
        PrintWriter printOut = new PrintWriter(outFile);
        for (NNetwork nn : population){
            for (Double weigth : nn.getWeights()) {
                printOut.print(weigth);
                printOut.print(' ');
            }
            printOut.println();
			printOut.println(nn.fitness);
        }
        printOut.flush();
        printOut.close();
        fl.pause();
	}
	
	public void evolve(){
		NNetwork[] newGeneration = new NNetwork[geneticDTO.population];
		selected = applySelection();
		int target = 0;
		for (int i=target; i< selectCount; i++){
			newGeneration[i] = selected[i];
		}
		NNetwork[] mutated = getMutated();
		int mutatedIndex = 0;
		target += selectCount;
		for (int i = target; i< target + mutateCount; i++){
			newGeneration[i] = mutated[mutatedIndex++];
		}
		target += mutateCount;
		NNetwork[] crossed = getNewChildren();
        int crossIndex = 0;
		for (int i=target; i< target + crossCount; i++){
			newGeneration[i] = crossed[crossIndex++];
		}
		target += crossCount;
		for (int i=target; i<geneticDTO.population; i++){
			newGeneration[i] = new NNetwork(0,1);
			newGeneration[i].init();
			newGeneration[i].randomWeights();
		}
		population = newGeneration;
		generation++;
		System.out.println("Next Genereation Loaded!");
	}
	
	private NNetwork[] getNewChildren(){
		NNetwork[] children = new NNetwork[crossCount];
		for (int i = 0; i<crossCount; i++){
			children[i] = selected[i].cross(selected[config.random(0,selectCount-1)]);
		}
		return children;
	}
	
	private NNetwork[] getMutated(){
		NNetwork[] mutated = new NNetwork[mutateCount];
		int selectedForMutation = 0;
		for (int i=0; i< mutateCount; i++){
			mutated[i] = mutate(geneticDTO.mutationVolitality,i);
			selectedForMutation = selectedForMutation < selected.length ? selectedForMutation+1 : 0;
		}
		return mutated;
	}
    
    public NNetwork mutate(double percentage,int index){
        NNetwork mutated = new NNetwork(0,1);
        Double[] weights = selected[index].getWeights();
        for (int i= 0; i < weights.length; i++){
            if (config.random()*100 <= percentage){
                weights[i] = config.random();
            }
        }
        
        mutated.init(ArrayUtils.toPrimitive(weights));
        return mutated;
    }
	
	private NNetwork[] applySelection() {
		NNetwork[] selected = new NNetwork[selectCount];
		for (int i=0; i< selectCount; i++){
			NNetwork max = new NNetwork(0,1);
			max.fitness = -1;
			for (NNetwork nn : population)
				if (max.fitness < nn.fitness) max = nn;
			selected[i] = max;
		}
		return selected;
	}
	
	public void getWeights(int networkIndex){
		population[networkIndex].getWeights();
	}
	
	public void randomWeights(){
        generation = 1;
		for (int i=0; i<geneticDTO.population; i++){
			population[i] = new NNetwork(0,1);
			population[i].init();
			population[i].randomWeights();
		}
	}
	
	public void setFitness(AiBird[] fitness) {
		for (int i=0; i<fitness.length ; i++)
			population[i].fitness = fitness[i].score;
	}
}
