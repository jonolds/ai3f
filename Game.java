import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Game extends Thread {
	Thread T1;
	Matrix pop;
	Random r;
	String timestamp;
	static final int num_genes = 291, pop_size = 50;
	static final String[] params = new String[] { "Wins", "Iters", "Fitness", "SurviveWin"};
	//TOURNEY RULES
	static final int num_tourneys = 80;
	static final double win_survive_rate = .75;
	//MUTATION
	static final double mutation_rate = .5;
	static final double ave_mut_dev = .5;
	//BREEDING
	static final int dating_pool_size = 8;
	
	void init() throws Throwable {
		timestamp = (new SimpleDateFormat("M-d_H.mm.s")).format(new Date());
		r = new Random();
		Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(evolveORIG()));
		
		double[] best_spawn = evolveWeights();
		printDoubleArray(best_spawn);
		System.out.println(Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(best_spawn)));
	}
	
	double[] evolveWeights() throws Throwable {
		pop = new Matrix(pop_size, 291);			// CREATE A RANDOM INITIAL POPULATION
		for(int i = 0; i < pop_size; i++)
			for(int j = 0; j < num_genes; j++)
				pop.row(i)[j] = 0.6 * r.nextGaussian();
		addParams();								//ADD EXTRA PARAMS TO TRACK AGENT FITNESS
		
		for(int i = 0; i < 1000000000; i++) {
			if(i%1000 == 0) { fitnessTest(); }		//SCORE FITNESS every 1000 cycles
			
			double d = r.nextDouble();
			
			if(d < .3)
				mutate(d);
			else if (d < .6)
				tournament(d);
			else if (d < .7)
				trainElite(d);
		}
		
		ArrayList<double[]> bestSet = pop.getBest();
		System.out.println("bestSet size: " + bestSet.size() + "  # wins: " + bestSet.get(0)[291]);
		return cleanWts(bestSet.get(0));
	}
	
	void trainElite(double d) throws Exception {
		pop.sort(pop.colNamed("Wins"), true);					//Sort the Matrix to pick the best
		double[] startingFitness = new double[pop_size/5];		
		
		for(int i = 0; i < pop_size/5; i++) {					//Get a baseline from all 10
			int[] outcome = battle(new ReflexAgent(), new NeuralAgent(cleanWts(pop.row(i))));
			pop.row(i)[pop.colNamed("Wins")] += (outcome[0] == -1) ? 1.0: 0.0;
			pop.row(i)[pop.colNamed("Iters")] += outcome[1];
			startingFitness[i] = outcome[1];					//Only off iters. A win ends the game.
		}
			
	}
	
	void fitnessTest() throws Exception {
		for(int i = 0; i < pop_size; i++) {
			int[] result = battle(new ReflexAgent(), new NeuralAgent(cleanWts(pop.row(i))));
			
		}
	}
	
	void mutate(double d) {
		int row_num = r.nextInt(pop_size);
		double mutation_level = ave_mut_dev * r.nextGaussian();
		pop.row(row_num)[r.nextInt(num_genes)] += mutation_level;
	}
	
	void tournament(double d) throws Exception {
		for(int b = 0; b < pop_size; b++) {				//cycle through from row 0 to row pop_zero -1
			for(int a = 0; a < pop_size; a+=2) {		//Split the process in half to add balance
				if(a == b)
					continue;
				int[] outcome = battle(new NeuralAgent(cleanWts(pop.row(a))), new NeuralAgent(cleanWts(pop.row(b))));
				results(a, b, outcome);					//send to results() to see who lives/dies
			}
		}
		for(int b = 0; b < pop_size; b++) {				//Second verse
			for(int a = 1; a < pop_size; a+=2) {		
				if(a == b)
					continue;
				int[] outcome = battle(new NeuralAgent(cleanWts(pop.row(a))), new NeuralAgent(cleanWts(pop.row(b))));
				results(a, b, outcome);					//send to results() to see who lives/dies
			}
		}
	}
	
	void results(int a, int b, int[] outcome) {
		if(outcome[0] == -1) {		//a won
			double winner_lives = pop.row(a)[pop.colNamed("WinSurvive")];
			double d = r.nextDouble();
			killAndBreed((d < winner_lives) ? b : a);
		}
		else if(outcome[0] == 1) {	//b won
			double winner_lives = pop.row(b)[pop.colNamed("WinSurvive")];
			double d = r.nextDouble();
			killAndBreed((d < winner_lives) ? a : b);
		}
		else {} //Tie
	}
	
	void killAndBreed(int kill_num) {
		//Pick 9 random parent candidates. possible_parents[0] is parent a
		int[] possible_parents = new int[dating_pool_size + 1];
		possible_parents = ThreadLocalRandom.current().ints(0, pop_size).distinct().limit(dating_pool_size + 1).toArray();
		double[] a = pop.row(possible_parents[0]);		//Parent 1
		double[] b = new double[291];					//dating pool

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
		for(int i = 0; i < 291; i+=3) {								//REPLACE(kill) genes in sets of 3
			pop.row(kill_num)[i] = (r.nextBoolean()) ? a[i] : b[i];
			pop.row(kill_num)[i+1] = (r.nextBoolean()) ? a[i+1] : b[i+1];
			pop.row(kill_num)[i+2] = (r.nextBoolean()) ? a[i+2] : b[i+2];
		}
		
			
		for(int i = 291; i < pop_size + params.length; i++)			//RESET params
			pop.row(kill_num)[i] = 0.0;					
		pop.row(kill_num)[pop.colNamed("WinSurvive")] = 0.75;		//WIN_SURVIVE back to default
		
//		pop.printRow(kill_num);
			
	}
	
	
	int[] battle(IAgent blue, IAgent red) throws Exception {		//SHORTCUT TO Controller.doBattle
		return Controller.doBattleNoGui(blue, red);
	}
	public boolean checkIfWinning() throws Throwable {
		ArrayList<double[]> bestSet = pop.getBest();
		double[] weights = cleanWts(bestSet.get(r.nextInt(bestSet.size())));
		Long start = Instant.now().toEpochMilli();
		int[] outcome = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(weights));
		System.out.println("Outcome vs blue: " + outcome + "   Time:" + Long.toString(Instant.now().toEpochMilli() - start));
		return (outcome[0] == -1);
	}
	ArrayList<IAgent> popToAgents() {
		ArrayList<IAgent> agent_pop = new ArrayList<>();
		for(int i = 0; i < pop.rows(); i++)
			agent_pop.add(new NeuralAgent(cleanWts(i), i));
		return agent_pop;
	}
	ArrayList<IAgent> popToAgents(ArrayList<Integer> intArr) {
		ArrayList<IAgent> agent_pop = new ArrayList<>();
		for(int i = 0; i < intArr.size(); i++)
			agent_pop.add(new NeuralAgent(cleanWts(intArr.get(i)), intArr.get(i)));
		return agent_pop;
	}
	void addParams() {
		for(int i = 0; i < params.length; i++)
			pop.addCol(params[i]);
		
		//Initialize everyone to live after win_survive_rate of wins
		for(int i = 0; i < pop.rows(); i++)
			pop.row(i)[pop.colNamed("SurviveWin")] = win_survive_rate;
	}

	double[] cleanWts(int rowNum) {
		double[] row = pop.row(rowNum);
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}
	double[] cleanWts(double[] row) {
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}
	
	void addNewStats(double[] origRow, double[] newRow) {
		for(int i = 291; i < 291 + params.length; i++)
			origRow[i] += newRow[i];
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