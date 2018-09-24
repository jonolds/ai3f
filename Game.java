import java.util.Random;

class Game extends Thread {
	static int pop_size = 80;
	static String[] params = new String[] { "Wins", "Losses", "Ties", "Time"};

	static double[] evolveWeights() {
		// Create a random initial population
		Random r = new Random();
		Matrix pop = new Matrix(pop_size, 291);
		for(int i = 0; i < pop_size; i++) {
			double[] chromosome = pop.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.03 * r.nextGaussian();
		}
		
		System.out.println(pop.row(0)[1]);
		pop.row(0)[1] = 9.9;
		System.out.println(pop.row(0)[1]);
		
		//Add Params
		addParams(pop);	
		
		
		//Divide by random Gaussian
		for(int i = 0; i < pop.rows(); i++) {
			for(int k = 0; k < pop.row(0).length; k++)
				pop.row(i)[k] /= r.nextGaussian();
		}

		// Return an arbitrary member from the population
		return extractWeights(pop, 0);
	}
	
	static void addParams(Matrix pop) {
		for(int i = 0; i < params.length; i++)
			pop.addCol(params[i]);
	}


	public static void main(String[] args) throws Exception {
		double[] w = evolveWeights();
//		Controller.doBattle(new ReflexAgent(), new NeuralAgent(w));
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