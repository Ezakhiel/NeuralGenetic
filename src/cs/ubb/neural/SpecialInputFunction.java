package cs.ubb.neural;

import org.neuroph.core.Connection;
import org.neuroph.core.input.InputFunction;

public class SpecialInputFunction extends InputFunction{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public double getOutput(Connection[] inputConnections) {
		double output = 0d;
		int counter = 0;
	    for (Connection connection : inputConnections) {
	    	if (connection.getWeightedInput() > 0) counter++;
	    	if (counter == 2){
	    		output = 1d;
	    		break;
	    	}
	    }
	    return output;
	}

}
