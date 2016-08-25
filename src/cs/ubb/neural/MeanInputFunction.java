package cs.ubb.neural;

import org.neuroph.core.Connection;
import org.neuroph.core.input.InputFunction;

public class MeanInputFunction extends InputFunction{

	@Override
	public double getOutput(Connection[] inputConnections) {
		double output = 0d;
	    for (Connection connection : inputConnections) {
	    	output += connection.getWeightedInput();
	    	}
	    return output / inputConnections.length;
	}

}
