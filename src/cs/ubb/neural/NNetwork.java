package cs.ubb.neural;

import com.me.flappybird.config;

import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.NeuronProperties;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.List;

import be.abeel.io.Base64.InputStream;

public class NNetwork {
	
	NeuralNetwork<LearningRule> nn;
	public double[] gene;
	public int fitness;
	int layerSize;
	double minWeight;
	double maxWeight;
	
	
	public NNetwork(int dataSize,double minW,double maxW){
		layerSize = dataSize;
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
		Layer percLayer = new Layer(layerSize, new NeuronProperties());
		Neuron[] perceptronArray = percLayer.getNeurons();
		Layer hiddenLayer1 = new Layer(layerSize * 3, new NeuronProperties());
		Layer hiddenLayer2 = new Layer(layerSize / 2, new NeuronProperties());
		Layer outputLayer = new Layer(1, new NeuronProperties());
		Neuron[] outNeuron = outputLayer.getNeurons();
		for(int i=0; i<layerSize; i++) {
			for (int j = 0; j < layerSize * 3; j++) {
				ConnectionFactory.createConnection(perceptronArray[i], hiddenLayer1.getNeuronAt(j), gene[1][i * j]);
			}
		}
		for(int i=0; i<layerSize * 3; i++) {
			for (int j = 0; j < layerSize / 2; j++) {
				ConnectionFactory.createConnection(hiddenLayer1.getNeuronAt(i), hiddenLayer2.getNeuronAt(j), gene[2][i * j]);
			}
		}
		for(int i=0; i<layerSize / 2; i++){
			ConnectionFactory.createConnection(
					hiddenLayer2.getNeuronAt(i), 
					outNeuron[0],
					gene[3][i]);
		}
		nn = new NeuralNetwork<LearningRule>();
		nn.addLayer(0, percLayer);
		nn.addLayer(1, hiddenLayer1);
		nn.addLayer(2, hiddenLayer2);
		nn.setOutputNeurons(outputLayer.getNeurons());
	}
	
	public void init(){
		NeuronProperties np = new NeuronProperties();
		np.setProperty("transferFunction", TransferFunctionType.SIGMOID);
		Layer percLayer = new Layer(layerSize,new NeuronProperties());
		Neuron[] perceptronArray = percLayer.getNeurons();
		Layer hiddenLayer1 = new Layer(layerSize * 3, new NeuronProperties());
		Layer hiddenLayer2 = new Layer(layerSize / 2, new NeuronProperties());
		Layer outputLayer = new Layer(1, np);
		Neuron[] outNeuron = outputLayer.getNeurons();
		MeanInputFunction meanInputFunction = new MeanInputFunction();
		for (Neuron neuron : perceptronArray)
			neuron.setInputFunction(meanInputFunction);
		for (Neuron neuron : hiddenLayer1.getNeurons())
			neuron.setInputFunction((meanInputFunction));
		for (Neuron neuron : hiddenLayer2.getNeurons())
			neuron.setInputFunction(meanInputFunction);
		outNeuron[0].setInputFunction(meanInputFunction);
		
		for(int i=0; i<layerSize; i++) {
			for (int j = 0; j < layerSize * 3; j++) {
				ConnectionFactory.createConnection(perceptronArray[i], hiddenLayer1.getNeuronAt(j));
			}
		}
		for(int i=0; i<layerSize * 3; i++) {
			for (int j = 0; j < layerSize / 2; j++) {
				ConnectionFactory.createConnection(hiddenLayer1.getNeuronAt(i), hiddenLayer2.getNeuronAt(j));
			}
		}
		for(int i=0; i<layerSize / 2; i++){
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
	
	public Double[] getWeigths(){
		List<Double> lastCon = new ArrayList<Double>();
		for (Layer layer : nn.getLayers()){
			for (Neuron neuron : layer.getNeurons()){
				for (Connection conn : neuron.getOutConnections()){
					lastCon.add(conn.getWeight().getValue());
				}
			}
		}
//		for (Neuron neuron : nn.getInputNeurons()){
//			for (Connection conn : neuron.getOutConnections()){
//				lastCon.add(conn.getWeight().getValue());
//			}
//		}
		System.out.println(lastCon.size());
		return nn.getWeights();
	}
	
	public void mutate(double percentage){
		for (Layer layer : nn.getLayers()){
			for (Neuron neuron : layer.getNeurons()){
				for (Connection conn : neuron.getOutConnections()){
					if (config.random()*100 <= percentage)
						conn.setWeight(new Weight(config.random()));
				}
			}
		}
	}
	
	public boolean decision(double[] data){
		Neuron[] inputLayer = nn.getLayerAt(0).getNeurons();
		for (int i=0;i<data.length;i++){
			inputLayer[i].setInput(data[i]);
		}
		nn.calculate();
		double ans = nn.getLayerAt(3).getNeuronAt(0).getOutput();
		if (nn.getLayerAt(3).getNeuronAt(0).getOutput() > 0.4){
			System.out.println("JUMP - " + ans);
			return true;
		}
		else {
			//System.out.println("DONT JUMP - " + ans);
			return false;
		}
	}
	
}
