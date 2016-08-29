package cs.ubb.neural;

import com.me.flappybird.config;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.input.And;
import org.neuroph.core.input.InputFunction;
import org.neuroph.core.input.Max;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.NeuronProperties;
import org.neuroph.util.TransferFunctionType;

import be.abeel.io.Base64.InputStream;

public class NNetwork {
	
	NeuralNetwork<LearningRule> nn;
	public double[] gene;
	public int fitness;
	double minWeight;
	double maxWeight;
	public int jumpCount;
	public int noJump;
	public int age;
	NeuronProperties np;
	
	
	public NNetwork(double minW,double maxW){
		minWeight = minW;
		maxWeight = maxW;
		jumpCount = 0;
		noJump = 0;
		age = 1;
		np = new NeuronProperties();
		np.setProperty("inputFunction", SpecialInputFunction.class);
		//np.setProperty("transferFunction", TransferFunctionType.SIGMOID);
	}
	public boolean saveNetwork(String fileName){
		try{
			nn.save(fileName);
		}catch (Exception e){
			return false;
		}
		return true;
	}
	public void init(InputStream inStream){
		nn.load(inStream);
	}
	
	public void initActivatorNetwork(double[] gene){
		Layer inLayerPipes = new Layer(config.PERCEPTRONSIZEPIPE, new NeuronProperties());
		Neuron[] inArrayPipes = inLayerPipes.getNeurons();
		
		Layer inLayerBird = new Layer(config.PERCEPTRONSIZEBIRD, new NeuronProperties());
		Neuron[] inArrayBird = inLayerBird.getNeurons();
		
		Layer percLayerPipes = new Layer(config.PERCEPTRONSIZEPIPE, np);
		Neuron[] perceptronArrayPipes = percLayerPipes.getNeurons();
		
		Layer percLayerBird = new Layer(config.PERCEPTRONSIZEBIRD, new NeuronProperties());
		Neuron[] perceptronArrayBird = percLayerBird.getNeurons();
		
		int genecount=0;
		//input transfer to hidden layer
		for(int i=0; i<config.PERCEPTRONSIZEBIRD; i++) {
			ConnectionFactory.createConnection(inArrayBird[i], perceptronArrayBird[i],1);
		}
		for(int i=0; i<config.PERCEPTRONSIZEPIPE; i++) {
			ConnectionFactory.createConnection(inArrayPipes[i], perceptronArrayPipes[i],1);
		}
		//hidden layer connections
		for(int i=0; i<config.PERCEPTRONSIZEBIRD; i++) {
			for(int j=0; j<config.PERCEPTRONSIZEPIPE; j++) {
				ConnectionFactory.createConnection(perceptronArrayBird[i], perceptronArrayPipes[j]
						,gene[genecount++]);
			}
		}
		
		Layer outputLayer = new Layer(config.OUTPUTLAYERSIZE, new NeuronProperties());
		Neuron[] outNeuron = outputLayer.getNeurons();
		//hidden to output
		for(int i=0; i<config.PERCEPTRONSIZEPIPE; i++) {
			ConnectionFactory.createConnection(perceptronArrayPipes[i], outNeuron[0],gene[genecount++]);
		}
		nn = new NeuralNetwork<LearningRule>();
		
		nn.addLayer(0, inLayerPipes);
		nn.addLayer(1, inLayerBird);
		nn.addLayer(2, percLayerBird);
		nn.addLayer(3, percLayerPipes);
		nn.addLayer(4, outputLayer);
	}
	
	public void randomWeights(){
		nn.randomizeWeights(minWeight, maxWeight);
	}
	
	public void initIntWeights(){
		double[] gene = new double[config.CONNECTIONCOUNT];
		double x;
		for (int i=0; i< config.CONNECTIONCOUNT ; i++){
			gene[i] = config.random(0, 1);
		}
		initActivatorNetwork(gene);
	}
	
	public Double[] getWeights(){
		Double[] weights = new Double[config.CONNECTIONCOUNT];
		int weightIterator= 0;
		for (Neuron n : nn.getLayerAt(2).getNeurons()){
			for(Connection c : n.getOutConnections())
				weights[weightIterator++] = c.getWeight().getValue();
		}
		for (Neuron n : nn.getLayerAt(3).getNeurons()){
			for(Connection c : n.getOutConnections())
				weights[weightIterator++] = c.getWeight().getValue();
		}
		return weights;
	}
	
	public NNetwork cross (NNetwork father){
		NNetwork child =  new NNetwork(0,1);
		Double[] fatherWeights = father.getWeights();
		Double[] motherWeights = this.getWeights();
		Double[] weights = new Double[motherWeights.length];
		for (int i=0; i < motherWeights.length;i++)
			weights[i] = crossMean(fatherWeights[i],motherWeights[i]);
		child.initActivatorNetwork(ArrayUtils.toPrimitive(weights));
		return child;
	}
	
	public double crossMean(double father, double mother){
		return (father+mother)/2;
	}
	
	public double decision(double[] data,double[] birdData){
		Neuron[] birdInputLayer = nn.getLayerAt(1).getNeurons();
		Neuron[] pipeInputLayer = nn.getLayerAt(0).getNeurons();
	
		for (int i=0;i<data.length;i++){
			pipeInputLayer[i].setInput(data[i]);
		}
		for (int i=0;i<birdData.length;i++){
			birdInputLayer[i].setInput(birdData[i]);
		}
		
		
		nn.calculate();
		/*
		for (int i=0; i < nn.getLayersCount() ; i++)
			for (int j=0; j < nn.getLayerAt(i).getNeuronsCount() ; j++)
				System.out.println("Layer "+i+"- neuron "+j+":"+nn.getLayerAt(i).getNeuronAt(j).getOutput());
		System.out.println("==============================");
		*/
		double ans = nn.getLayerAt(nn.getLayersCount()-1).getNeuronAt(0).getOutput();
		//System.out.println(ans);
		if (ans >= 2){
			jumpCount++;
		}
		else {
			noJump++;
		}
		return ans;
	}

}
