package cs.ubb.Genetic;

public class GeneticDTO {
	



	public double crossPercentage;
	public double selectionPercentage;
	public double mutationPercentage;
	public double mutationVolitality;
	public double randomPercentage;
	public int population;
	
	public GeneticDTO(double crossPercentage, double selectionPercentage, double mutationPercentage, int mutationVolitality,
			double randomPercentage, int population) {
		super();
		this.crossPercentage = crossPercentage;
		this.selectionPercentage = selectionPercentage;
		this.mutationPercentage = mutationPercentage;
		this.mutationVolitality = mutationVolitality;
		this.randomPercentage = randomPercentage;
		this.population = population;
	}
	
	@Override
	public String toString() {
		return "GeneticDTO{" + "crossPercentage=" + crossPercentage + ", selectionPercentage=" + selectionPercentage + ", mutationPercentage=" + mutationPercentage + ", randomPercentage=" + randomPercentage + ", population=" + population + '}';
	}
}
