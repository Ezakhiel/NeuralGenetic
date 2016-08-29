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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

	public double decision(int networkIndex, double[][] data, double[] birdData) {
		double[] flatData = new double[data.length * data[1].length];
		int k = 0;
		for (int i = 0; i < data.length; i++) {
			for (int j = 0; j < data[i].length; j++) {
				flatData[k++] = data[i][j];
			}
		}
		return population[networkIndex].decision(flatData,birdData);
	}

	public void play() {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "gameandroiddemo";
		cfg.useGL20 = true;
		cfg.width = config.screenWidth;
		cfg.height = config.screenHeigth;
		cfg.x = 0;
		cfg.y = 0;
		cfg.forceExit = false;
		if (game == null) {
			fl = new Flappybird(this);
			game = new LwjglApplication(fl, cfg);
		} else {
			fl = new Flappybird(this);
			fl.resume();
		}

	}

	public void loadFromFile(int gen) throws FileNotFoundException {
		inFile = new FileInputStream("generation" + gen + ".dat");
		Scanner readIn = new Scanner(inFile);
		double[] gene = new double[config.CONNECTIONCOUNT];
		for (int p = 0; p < geneticDTO.population; p++) {
			for (int i = 0; i < config.CONNECTIONCOUNT; i++) {
				gene[i] = readIn.nextDouble();
			}
			population[p] = new NNetwork(0, 1);
			population[p].initActivatorNetwork(gene);
			readIn.nextInt();
		}
		generation = gen;
	}

	public void save() throws IOException {
		outFile = new FileOutputStream("generation" + generation + ".dat", false);
		PrintWriter printOut = new PrintWriter(outFile);
		for (NNetwork nn : population) {
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

	public void evolve() {
		NNetwork[] newGeneration = new NNetwork[geneticDTO.population];
		for (NNetwork nn : population)
			nn.age++;
		selected = applySelection();
		int target = 0;
		for (int i = target; i < selectCount; i++) {
			newGeneration[i] = selected[i];
		}
		NNetwork[] mutated = getMutated();
		int mutatedIndex = 0;
		target += selectCount;
		for (int i = target; i < target + mutateCount; i++) {
			newGeneration[i] = mutated[mutatedIndex++];
		}
		target += mutateCount;
		NNetwork[] crossed = getNewChildren();
		int crossIndex = 0;
		for (int i = target; i < target + crossCount; i++) {
			newGeneration[i] = crossed[crossIndex++];
		}
		target += crossCount;
		for (int i = target; i < geneticDTO.population; i++) {
			/*
			 * newGeneration[i] = new NNetwork(0,1); newGeneration[i].init();
			 * newGeneration[i].randomWeights();
			 */
			newGeneration[i] = new NNetwork(0, 1);
			newGeneration[i].initIntWeights();
		}
		population = newGeneration;
		generation++;
		System.out.println("Next Generation Loaded!");
	}

	private NNetwork[] getNewChildren() {
		NNetwork[] children = new NNetwork[crossCount];
		int selectIndex = 0;
		for (int i = 0; i < crossCount; i++) {
			if (selectIndex >= selectCount)
				selectIndex = 0;
			children[i] = selected[selectIndex++].cross(selected[config.random(0, selectCount - 1)]);
		}
		return children;
	}

	private NNetwork[] getMutated() {
		NNetwork[] mutated = new NNetwork[mutateCount];
		int selectedForMutation = 0;
		for (int i = 0; i < mutateCount; i++) {
			if (selectedForMutation >= selectCount)
				selectedForMutation = 0;
			mutated[i] = mutate(geneticDTO.mutationVolitality, selectedForMutation++);
		}
		return mutated;
	}

	public NNetwork mutate(double percentage, int parentId) {
		NNetwork mutated = new NNetwork(0, 1);
		Double[] weights = selected[parentId].getWeights();
		/*
		if (selected[parentId].jumpCount > selected[parentId].noJump * 2) {
			for (int i = 0; i < weights.length; i++) {
				if (config.random() * 100 < percentage) {
					weights[i] = config.randDecrease(weights[i], config.MUTATIONLOWERBOUND, config.MUTATIONUPPERBOUND);
				}
			}
			System.out.println("Decrease J");
		} else {
			if (selected[parentId].jumpCount * 2 < selected[parentId].noJump) {
				for (int i = 0; i < weights.length; i++) {
					if (config.random() * 100 < percentage) {
						weights[i] = config.randIncrease(weights[i], config.MUTATIONLOWERBOUND,
								config.MUTATIONUPPERBOUND);
					}
				}
				System.out.println("increase J");
			} else {
			*/
				for (int i = 0; i < weights.length; i++) {
					if (config.random() * 100 < percentage) {
						weights[i] = config.randMutate(weights[i], config.MUTATIONLOWERBOUND,
								config.MUTATIONUPPERBOUND);
					}
				}
			//}
		//}
		mutated.initActivatorNetwork(ArrayUtils.toPrimitive(weights));
		return mutated;
	}

	private NNetwork[] applySelection() {
		NNetwork[] selected = new NNetwork[selectCount];
		int[] best = getBestIndexes();
		int selectedIndex = 0;
		for (int id : best) {
			selected[selectedIndex] = population[id];
			selected[selectedIndex++].fitness = 0;
		}
		return selected;
	}

	private int[] getBestIndexes() {
		List<Integer> best = new ArrayList<Integer>();
		int fitness;
		int id;
		for (int i = 0; i < selectCount; i++) {
			fitness = -1;
			id = -1;
			for (int j = 0; j < geneticDTO.population; j++) {
				if (!best.contains(j)) {
					if (fitness < population[j].fitness && population[j].age < config.GENETICAGEOUT) {
						fitness = population[j].fitness;
						id = j;
					}
				}
			}
			best.add(id);
		}
		return ArrayUtils.toPrimitive(best.toArray(new Integer[selectCount]));
	}

	public void getWeights(int networkIndex) {
		population[networkIndex].getWeights();
	}
/*
	public void randomWeights() {
		generation = 1;
		for (int i = 0; i < geneticDTO.population; i++) {
			population[i] = new NNetwork(0, 1);
			population[i].init();
			population[i].randomWeights();
		}
	}
*/
	public void randomIntWeights() {
		generation = 1;
		for (int i = 0; i < geneticDTO.population; i++) {
			population[i] = new NNetwork(0, 1);
			population[i].initIntWeights();
		}
	}

	public void setFitness(AiBird[] fitness) {
		for (int i = 0; i < fitness.length; i++)
			population[i].fitness = fitness[i].score;
	}
	

}
