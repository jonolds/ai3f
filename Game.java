import java.util.ArrayList;
import java.util.Random;

class Game extends Thread {
	static int pop_size = 16;
	static String[] params = new String[] { "Wins", "Losses", "Ties", "Time", "Defender", "Aggressor", "FlagAttacker"};

	public static void main(String[] args) throws Exception {
		double[] random_row = evolveWeights();
		Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(random_row));
		
	}
	
	static void addNewStats(double[] origRow, double[] newRow) {
		for(int i = 291; i < 291 + params.length; i++)
			origRow[i] += newRow[i];
		
	}
	
	static double[] evolveWeights() throws Exception {
		// Create a random initial population
		Random r = new Random();
		Matrix pop = new Matrix(pop_size, 291);
		for(int i = 0; i < pop_size; i++) {
			double[] chromosome = pop.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.6 * r.nextGaussian();
		}
		//Add Params
		addParams(pop);	
		//Divide by random Gaussian
		for(int i = 0; i < pop.rows(); i++) {
			for(int k = 0; k < pop.row(0).length - params.length; k++)
				pop.row(i)[k] /= r.nextGaussian();
		}
		
		int[] rank = new int[100];
		rank = Controller.rankAgents(popToAgents(pop), rank);
		printIntArray(rank);
		
		

		// Return an arbitrary member from the population
		return extractWeights(pop, 0);
	}
	
	static void addParams(Matrix pop) {
		for(int i = 0; i < params.length; i++)
			pop.addCol(params[i]);
	}
	
	static ArrayList<IAgent> popToAgents(Matrix pop) {
		ArrayList<IAgent> agent_pop = new ArrayList<>();
		for(int i = 0; i < pop.rows(); i++)
			agent_pop.add(new NeuralAgent(extractWeights(pop, i)));
		return agent_pop;
	}

	static double[] extractWeights(Matrix pop, int rowNum) {
		double[] row = pop.row(rowNum);
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}
	
	static void printDoubleArray(double[] dArr) {
		for(int i = 0; i < dArr.length; i++)
			System.out.print(dArr[i] + " ");
		System.out.println();
	}
	static void printIntArray(int[] iArr) {
		for(int i = 0; i < iArr.length; i++)
			System.out.print(iArr[i] + " ");
		System.out.println();
	}
}

interface IAgent {
	void reset();
	void update(Model m);
}