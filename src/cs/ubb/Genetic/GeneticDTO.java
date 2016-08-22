package cs.ubb.Genetic;

public class GeneticDTO {
	



	public double crossPercentage;
	public double selectionPercentage;
	public double mutationPercentage;
	public double randomPercentage;
	public int population;
	
	public GeneticDTO(double crossPercentage, double selectionPercentage, double mutationPercentage,
			double randomPercentage, int population) {
		super();
		this.crossPercentage = crossPercentage;
		this.selectionPercentage = selectionPercentage;
		this.mutationPercentage = mutationPercentage;
		this.randomPercentage = randomPercentage;
		this.population = population;
	}
}
