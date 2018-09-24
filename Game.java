import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Game extends Thread {
	Matrix pop;
	Random r;
	static final int num_genes = 291;
	static final String[] params = new String[] { "Wins", "Losses", "Ties", "Time", "Defender", "Aggressor", "FlagAttacker"};
	static final int pop_size = 12;
	
	//TOURNEY RULES
	static final int num_tourneys = 1;
	static final double win_survive_rate = .75;
	//MUTATION
	static final double mutation_rate = .05;
	static final double ave_mut_dev = .5;
	//BREEDING
	static final int dating_pool_size = 8;
	
	void init() throws Throwable {
		double[] best_spawn = evolveWeights();
		Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(best_spawn));
	}
	
	double[] evolveWeights() throws Throwable {
		
		pop = new Matrix(pop_size, 291);			// CREATE A RANDOM INITIAL POPULATION
		for(int i = 0; i < pop_size; i++)
			for(int j = 0; j < num_genes; j++)
				pop.row(i)[j] = 0.6 * r.nextGaussian();
		addParams();									//ADD EXTRA PARAMS TO TRACK AGENT FITNESS

		for(int i = 0; i < 3; i++)
			tourney();
//		
//		//Divide by random Gaussian
//		for(int i = 0; i < pop.rows(); i++) {
//			for(int k = 0; k < num_genes; k++)
//				pop.row(i)[k] /= r.nextGaussian();
//		}
//		
//		int[] rank = new int[100];
//		rank = Controller.rankAgents(popToAgents(), rank);
//		printIntArray(rank);
//
//		// Return an arbitrary member from the population
		return extractWeights(0);
	}
	
	void killAndBreed(int kill_num) {
		System.out.println("	killing " + kill_num);
		//Pick 9 random parent candidates. possible_parents[0] is parent a
		int[] possible_parents = new int[9];
		possible_parents = ThreadLocalRandom.current().ints(0, pop_size).distinct().limit(9).toArray();
		double[] a = pop.row(possible_parents[0]), b = new double[291];

		int b_num = 0, best_score = 0;
		//Compare similarity !!!!!!!---FIX only comparing sign
		for(int i = 1; i < 9; i++) {
			int score = 0;
			for(int k = 0; k < 291; k++) {
				if((a[k] > 0 && pop.row(i)[k] > 0)||(a[k] < 0 && pop.row(i)[k] < 0))
					score++;
			}
			if(score >= best_score)
				b_num = possible_parents[i];
		}
		b = pop.row(b_num);
		for(int i = 0; i < 291; i++)								//REPLACE(kill) genes
			pop.row(kill_num)[i] = (r.nextBoolean()) ? a[i] : b[i];
		for(int i = 291; i < pop_size + params.length; i++)			//RESET params
			pop.row(kill_num)[i] = 0.0;
			
	}
	
	void tourney() throws Throwable {
		int[] wins = new int[pop_size];
		ArrayList<Integer> shuf = new ArrayList<>();
		for(int i = 0; i < pop_size; i++)
			shuf.add(i);
		ArrayList<IAgent> agents = popToAgents();
		
		for(int q = 0; q < pop_size/2; q++) {
			Collections.shuffle(shuf);
			
			for(int i = 0; i < agents.size(); i++) {
				
				int j = shuf.get(i);
				if(j == i) //agent i IS agent j - Skip this cycle
					continue;
				int outcome = Controller.doBattleNoGui(agents.get(i), agents.get(j));
				if(outcome > 0) {		//agent i won
					System.out.println("#" + i + " vs #" + j + ". Winner: #" + i);
					double randoDouble = r.nextDouble();
					System.out.println(randoDouble);
					killAndBreed((randoDouble < win_survive_rate) ? j: i);
				}
				else if(outcome < 0) {	//agent j won
					System.out.println("#" + i + " vs #" + j + ". Winner: #" + j);
					double randoDouble = r.nextDouble();
					System.out.println(randoDouble);
					killAndBreed((randoDouble < win_survive_rate) ? i: j);
				}
//				else
//					System.out.println();
			}
		}
	}
	
	ArrayList<IAgent> popToAgents() {
		ArrayList<IAgent> agent_pop = new ArrayList<>();
		for(int i = 0; i < pop.rows(); i++)
			agent_pop.add(new NeuralAgent(extractWeights(i), i));
		return agent_pop;
	}
	
	void addParams() {
		for(int i = 0; i < params.length; i++)
			pop.addCol(params[i]);
	}

	double[] extractWeights(int rowNum) {
		double[] row = pop.row(rowNum);
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}
	
	void addNewStats(double[] origRow, double[] newRow) {
		for(int i = 291; i < 291 + params.length; i++)
			origRow[i] += newRow[i];
	}
	
	int battle(IAgent blue, IAgent red) throws Exception {	//SHORTCUT TO Controller.doBattle
		return Controller.doBattleNoGui(blue, red);
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

	static double[] evolveORIG() {
		Random r = new Random();
		Matrix population = new Matrix(100, 291);
		for(int i = 0; i < 100; i++) {
			double[] chromosome = population.row(i);
			for(int j = 0; j < chromosome.length; j++)
				chromosome[j] = 0.03 * r.nextGaussian();
		}
		return population.row(0);
	}
	
	public static void main(String[] args) throws Throwable {
		Game g = new Game();
		g.init();
	}
	Game() { r = new Random();  }
}

interface IAgent {
	void reset();
	void update(Model m);
}