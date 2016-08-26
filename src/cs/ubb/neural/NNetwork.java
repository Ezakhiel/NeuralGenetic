package cs.ubb.neural;

import com.me.flappybird.config;

import org.apache.commons.lang3.ArrayUtils;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
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
	
	
	public NNetwork(double minW,double maxW){
		minWeight = minW;
		maxWeight = maxW;
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
	
	public void init(double[][] gene){
		NeuronProperties np = new NeuronProperties();
		np.setProperty("transferFunction", TransferFunctionType.SIGMOID);
		Layer percLayer = new Layer(config.PERCEPTRONSIZE, new NeuronProperties());
		Neuron[] perceptronArray = percLayer.getNeurons();
		Layer hiddenLayer1 = new Layer(config.HIDDENLAYERSIZE1, new NeuronProperties());
		Layer hiddenLayer2 = new Layer(config.HIDDENLAYERSIZE2, new NeuronProperties());
		Layer outputLayer = new Layer(config.OUTPUTLAYERSIZE, np);
		Neuron[] outNeuron = outputLayer.getNeurons();
		for(int i=0; i<config.PERCEPTRONSIZE; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE1; j++) {
				ConnectionFactory.createConnection(perceptronArray[i], hiddenLayer1.getNeuronAt(j), gene[0][i * j]);
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE1; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE2; j++) {
				ConnectionFactory.createConnection(hiddenLayer1.getNeuronAt(i), hiddenLayer2.getNeuronAt(j), gene[1][i * j]);
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE2; i++){
			ConnectionFactory.createConnection(
					hiddenLayer2.getNeuronAt(i), 
					outNeuron[0],
					gene[2][i]);
		}
		nn = new NeuralNetwork<LearningRule>();
		nn.addLayer(0, percLayer);
		nn.addLayer(1, hiddenLayer1);
		nn.addLayer(2, hiddenLayer2);
		nn.addLayer(3, outputLayer);
	}
	
	public void init(double[] gene){
		double[][] setGene = new double[3][];
		setGene[0] = new double[config.FIRSTCONNECTIONCOUNT];
		setGene[1] = new double[config.SECONDCONNECTIONCOUNT];
		setGene[2] = new double[config.THIRDCONNECTIONCOUNT];
		for(int i=0; i<config.PERCEPTRONSIZE; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE1; j++) {
				setGene[0][i*j] = gene[i * j];
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE1; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE2; j++) {
				setGene[1][i*j] = gene[config.FIRSTCONNECTIONCOUNT + i * j];
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE2; i++){
			setGene[1][i] = gene[config.FIRSTCONNECTIONCOUNT + config.SECONDCONNECTIONCOUNT+ i];
		}
		NeuronProperties np = new NeuronProperties();
		np.setProperty("transferFunction", TransferFunctionType.SIGMOID);
		Layer percLayer = new Layer(config.PERCEPTRONSIZE, new NeuronProperties());
		Neuron[] perceptronArray = percLayer.getNeurons();
		Layer hiddenLayer1 = new Layer(config.HIDDENLAYERSIZE1, new NeuronProperties());
		Layer hiddenLayer2 = new Layer(config.HIDDENLAYERSIZE2, new NeuronProperties());
		Layer outputLayer = new Layer(config.OUTPUTLAYERSIZE, np);
		Neuron[] outNeuron = outputLayer.getNeurons();
		for(int i=0; i<config.PERCEPTRONSIZE; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE1; j++) {
				ConnectionFactory.createConnection(perceptronArray[i], hiddenLayer1.getNeuronAt(j), gene[i * j]);
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE1; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE2; j++) {
				ConnectionFactory.createConnection(hiddenLayer1.getNeuronAt(i), hiddenLayer2.getNeuronAt(j), gene[config.FIRSTCONNECTIONCOUNT + i * j]);
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE2; i++){
			ConnectionFactory.createConnection(
					hiddenLayer2.getNeuronAt(i),
					outNeuron[0],
					gene[config.FIRSTCONNECTIONCOUNT + config.SECONDCONNECTIONCOUNT+ i]);
		}
		nn = new NeuralNetwork<LearningRule>();
		nn.addLayer(0, percLayer);
		nn.addLayer(1, hiddenLayer1);
		nn.addLayer(2, hiddenLayer2);
		nn.addLayer(3, outputLayer);
	}
	
	public void init(){
		NeuronProperties np = new NeuronProperties();
		np.setProperty("transferFunction", TransferFunctionType.SIGMOID);
		Layer percLayer = new Layer(config.PERCEPTRONSIZE, new NeuronProperties());
		Neuron[] perceptronArray = percLayer.getNeurons();
		Layer hiddenLayer1 = new Layer(config.HIDDENLAYERSIZE1, new NeuronProperties());
		Layer hiddenLayer2 = new Layer(config.HIDDENLAYERSIZE2, new NeuronProperties());
		Layer outputLayer = new Layer(config.OUTPUTLAYERSIZE, np);
		Neuron[] outNeuron = outputLayer.getNeurons();
		MeanInputFunction meanInputFunction = new MeanInputFunction();
		for (Neuron neuron : perceptronArray)
			neuron.setInputFunction(meanInputFunction);
		for (Neuron neuron : hiddenLayer1.getNeurons())
			neuron.setInputFunction((meanInputFunction));
		for (Neuron neuron : hiddenLayer2.getNeurons())
			neuron.setInputFunction(meanInputFunction);
		outNeuron[0].setInputFunction(meanInputFunction);
		
		for(int i=0; i<config.PERCEPTRONSIZE; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE1; j++) {
				ConnectionFactory.createConnection(perceptronArray[i], hiddenLayer1.getNeuronAt(j));
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE1; i++) {
			for (int j = 0; j < config.HIDDENLAYERSIZE2; j++) {
				ConnectionFactory.createConnection(hiddenLayer1.getNeuronAt(i), hiddenLayer2.getNeuronAt(j));
			}
		}
		for(int i=0; i<config.HIDDENLAYERSIZE2; i++){
			ConnectionFactory.createConnection(
					hiddenLayer2.getNeuronAt(i),
					outNeuron[0]);
		}
		nn = new NeuralNetwork<LearningRule>();
		nn.addLayer(0, percLayer);
		nn.addLayer(1, hiddenLayer1);
		nn.addLayer(2, hiddenLayer2);
		nn.addLayer(3, outputLayer);
	}
	
	public void randomWeights(){
		nn.randomizeWeights(minWeight, maxWeight);
	}
	
	public Double[] getWeights(){
		return nn.getWeights();
	}
	
	public NNetwork cross (NNetwork father){
		NNetwork child =  new NNetwork(0,1);
		Double[] fatherWeights = father.getWeights();
		Double[] motherWeights = this.getWeights();
		Double[] weights = new Double[motherWeights.length];
		for (int i=0; i < motherWeights.length;i++)
			weights[i] = crossMean(fatherWeights[i],motherWeights[i]);
		child.init(ArrayUtils.toPrimitive(weights));
		return child;
	}
	
	public double crossMean(double father, double mother){
		return (father+mother)/2;
	}
	
	public boolean decision(double[] data){
		Neuron[] inputLayer = nn.getLayerAt(0).getNeurons();
		for (int i=0;i<data.length;i++){
			inputLayer[i].setInput(data[i]);
		}
		nn.calculate();
		double ans = nn.getLayerAt(3).getNeuronAt(0).getOutput();
		if (nn.getLayerAt(3).getNeuronAt(0).getOutput() > 0.5){
			//System.out.println("JUMP - " + ans);
			return true;
		}
		else {
			//System.out.println("DONT JUMP - " + ans);
			return false;
		}
	}

}
