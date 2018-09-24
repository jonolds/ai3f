import java.util.Random;

class Game extends Thread {
	static int pop_size = 80;
	static String[] params = new String[] { "Wins", "Losses", "Ties", "Time", "Defender", "Aggressor", "FlagAttacker"};

	public static void main(String[] args) throws Exception {
		double[] random_row = evolveWeights();
		NeuralAgent agent = new NeuralAgent(random_row);
		Controller.doBattleNoGui(new ReflexAgent(), agent);
		addNewStats(random_row, agent.weightsPlusParams);
		
		
		
	}
	static void addNewStats(double[] origRow, double[] newRow) {
		for(int i = 291; i < 291 + params.length; i++)
			origRow[i] += newRow[i];
		
	}
	
	static double[] evolveWeights() {
		// Create a random initial population
		Random r = new Random();
		Matrix pop = new Matrix(pop_size, 291);
		for(int i = 0; i < pop_size; i++) {
			double[] chromosome = pop.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.03 * r.nextGaussian();
		}

		//Add Params
		addParams(pop);	

		//Divide by random Gaussian
		for(int i = 0; i < pop.rows(); i++) {
			for(int k = 0; k < pop.row(0).length - params.length; k++)
				pop.row(i)[k] /= r.nextGaussian();
		}
		
		pop.printRow(0);

		// Return an arbitrary member from the population
		return pop.row(0);
	}
	
	static void addParams(Matrix pop) {
		for(int i = 0; i < params.length; i++)
			pop.addCol(params[i]);
	}

	static double[] extractWeights(Matrix pop, int rowNum) {
		double[] row = pop.row(rowNum);
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}
}

interface IAgent {
	void reset();
	void update(Model m);
}