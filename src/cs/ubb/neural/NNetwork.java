package cs.ubb.neural;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.Weight;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.NeuronProperties;

import com.me.flappybird.config;

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
		Layer percLayer = new Layer(layerSize);
		Neuron[] perceptronArray = percLayer.getNeurons();
		Layer hiddenLayer1 = new Layer(layerSize);
		Layer hiddenLayer2 = new Layer(layerSize);
		Layer outputLayer = new Layer(1);
		Neuron[] outNeuron = outputLayer.getNeurons();
		for(int i=0; i<layerSize; i++){
			for(int j=0; j<layerSize; j++){
				ConnectionFactory.createConnection(
						perceptronArray[i], 
						hiddenLayer1.getNeuronAt(j),
						gene[1][i*j]);
				ConnectionFactory.createConnection(
						hiddenLayer1.getNeuronAt(i), 
						hiddenLayer2.getNeuronAt(j),
						gene[2][i*j]);
			}
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

		Layer percLayer = new Layer(layerSize,new NeuronProperties());
		Layer hiddenLayer1 = new Layer(layerSize,new NeuronProperties());
		Layer hiddenLayer2 = new Layer(layerSize,new NeuronProperties());
		Layer outputLayer = new Layer(1,new NeuronProperties());
		NeuronProperties np = new NeuronProperties();
		MyInputFunction myInputFunction = new MyInputFunction();
		for(int i=0; i<layerSize; i++){
			for(int j=0; j<layerSize; j++){
				ConnectionFactory.createConnection(
						percLayer.getNeuronAt(i), 
						hiddenLayer1.getNeuronAt(j));
				ConnectionFactory.createConnection(
						hiddenLayer1.getNeuronAt(i), 
						hiddenLayer2.getNeuronAt(j));
			}
			ConnectionFactory.createConnection(
					hiddenLayer2.getNeuronAt(i), 
					outputLayer.getNeuronAt(0));
			hiddenLayer1.getNeuronAt(i).setInputFunction(myInputFunction);
			hiddenLayer2.getNeuronAt(i).setInputFunction(myInputFunction);
		}
		outputLayer.getNeuronAt(0).setInputFunction(myInputFunction);
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
		if (nn.getLayerAt(3).getNeuronAt(0).getOutput() > 0.5)
			return true;
		else
			return false;
	}
	
}
