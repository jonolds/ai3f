import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Game {
	Matrix mat;
	Random r;
	String timestamp;
	static final int num_genes = 291, mat_size = 50;
	static final String[] params = new String[] { "Won", "Lost", "Iters", "Fitness", "SurviveLoss"};
	//TOURNEY RULES
	static final int num_tourneys = 80;
	static final double init_survive_loss_rate = .25;
	static final double survive_loss_max = .40;
	static final double survive_loss_min = .10;
	//MUTATION
	static final double mutation_rate = .15;
	static final double ave_mut_dev = .5;
	//BREEDING
	static final int dating_pool_size = 8;
	
	boolean beatReflex = false;
	double[] winningCombo = null;
	int battles = 0;

	void init() throws Throwable {
		timestamp = (new SimpleDateFormat("M-d_H.mm.s")).format(new Date());
		r = new Random();
//		Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(evolveORIG()));

		double[] best_spawn = evolve();
//		printDoubleArray(best_spawn);
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(best_spawn));
	}

	double[] evolve() throws Throwable {
		
		//CREATE A RANDOM INITIAL POPULATION
		mat = new Matrix(mat_size, 291);
		for(int i = 0; i < mat_size; i++)
			for(int j = 0; j < num_genes; j++)
				mat(i)[j] = 0.6 * r.nextGaussian();
		
		//ADD EXTRA PARAM COLUMNS TO TRACK AGENT FITNESS
		addParams();

		//TRAIN POPULATION 
		for(int i = 0; i < 1000000000; i++) {
			if(i%20 == 0) { 
				printTopFitness();
				updateSurvival();
			}	//SCORE FITNESS every 1000 cycles
			if(beatReflex)
				return winningCombo;

			//Get a random double to decide next action
			double d = r.nextDouble();

			if(d < .4)					//MUTATE 40%
				mutate(d);
			else if (d < .7)			//TOURNAMENT 30%
				scrimmage(d);
			else if (d < 1.0)			//TRAINELITE 30%
				trainVsReflex(d);
		}

//		for(int i = 0; i < num_tourneys; i++) {
//			System.out.println(i);
//			checkIfWinning();
//		}

		ArrayList<double[]> bestSet = mat.getBest();
		System.out.println("bestSet size: " + bestSet.size() + "  # wins: " + bestSet.get(0)[291]);
		return getWts(bestSet.get(0));
	}

	void updateFitness(int i) {
		double iters = mat(i)[mat.col("Iters")];
		double wins = mat(i)[mat.col("Won")];
		double fitness = (double)((int)(10000000*wins/iters))/10;
		mat(i)[mat.col("Fitness")] = fitness;
	}

	//MUTATE
	void mutate(double d) {					//d < .4
		d /= .4;							//Convert d to a double between 0.0 and 1.0
		
		if(d < .05)							//CATASTROPHIC 5% of Mutations
			catastrophic(d);
		
		int row_num = r.nextInt(mat_size);
		double mutation_level = ave_mut_dev * r.nextGaussian();
		mat(row_num)[r.nextInt(num_genes)] += mutation_level;
	}
	void catastrophic(double d) {
		
//		double extra_toes = mat(r.nextInt(mat_size));
		
	}
	
	//BATTLE
	void scrimmage(double d) throws Exception {
		for(int i = 0; i < mat.rows(); i++) {
			int a = r.nextInt(mat_size);	//Random Agent from matrix
			int[] outcome = battle(a, i);
			results(a, i, outcome);
		}
	}
	void trainVsReflex(double d) throws Exception {
//		double start = (double)Instant.now().toEpochMilli();
		mat.sort(mat.col("Won"), true);
		for(int i = 0; i < mat_size; i++) {
			int[] outcome = battle(-1, i);
			mat(i)[mat.col("Iters")] += outcome[1];
			if(outcome[0] != 0)
				mat(i)[(outcome[0] == -1) ? mat.col("Won") : mat.col("Lost")]++;
			updateFitness(i);
			if(outcome[0] == -1) {
				System.out.println("Beat Reflex    Iters: " + outcome[1]);
//				beatReflex = true;
				winningCombo = getWts(mat(i));
				break;
			}
				
		}
//		double elapsed = (double)((int)(10*(Instant.now().toEpochMilli() - start))/1000)/10;
//		System.out.println("trainVsReflex: " + elapsed + "s    Total Battles: " + battles);
	}
	void results(int a, int b, int[] outcome) {
		int iters_col = mat.col("Iters");
		if(outcome[0] != 0) {
			double d = r.nextDouble();
			int winner = (outcome[0] == -1) ? a : b;
			int loser = (outcome[0] == -1) ? b : a;
			int lived = (d < mat(loser)[mat.col("SurviveLoss")]) ? b : a;
			if(lived == winner) 
				mat(lived)[mat.col("Won")]++;
			else
				mat(lived)[mat.col("Lost")]++;
			mat(lived)[mat.col("Fitness")] = mat(lived)[mat.col("Won")]/mat(lived)[iters_col];
			mat(lived)[iters_col] += outcome[1];
			killAndBreed((lived == loser) ? winner : loser);
			updateFitness(lived);
		}
		else {
//			mat(a)[iters_col] += outcome[1];
//			mat(b)[iters_col] += outcome[1];
//			updateFitness(a);
//			updateFitness(b);
		}
	}
	void killAndBreed(int kill_num) {
		//Pick 9 random parent candidates. possible_parents[0] is parent a
		int[] possible_parents = new int[dating_pool_size + 1];
		possible_parents = ThreadLocalRandom.current().ints(0, mat_size).distinct().limit(dating_pool_size + 1).toArray();
		double[] a = mat(possible_parents[0]);		//Parent 1
		double[] b = new double[291];					//dating pool

		int b_num = 0, best_score = 0;

		//Compare similarity !!!!!!!---FIX only comparing sign
		for(int i = 1; i < 9; i++) {
			int score = 0;
			for(int k = 0; k < 291; k++) {
				if((a[k] > 0 && mat(i)[k] > 0)||(a[k] < 0 && mat(i)[k] < 0))
					score++;
			}
			if(score >= best_score)
				b_num = possible_parents[i];
		}
		b = mat(b_num);
		for(int i = 0; i < 291; i+=3) {								//REPLACE(kill) genes in sets of 3
			mat(kill_num)[i] = (r.nextBoolean()) ? a[i] : b[i];
			mat(kill_num)[i+1] = (r.nextBoolean()) ? a[i+1] : b[i+1];
			mat(kill_num)[i+2] = (r.nextBoolean()) ? a[i+2] : b[i+2];
		}

		for(int i = 291; i < mat_size + params.length; i++)			//RESET params
			mat(kill_num)[i] = 0.0;
		mat(kill_num)[mat.col("SurviveLoss")] = 0.75;		//WIN_SURVIVE back to default
	}
	int[] battle(int blue, int red) throws Exception {		//SHORTCUT TO Controller.doBattle
		battles++;
		IAgent a = (blue == -1) ? new ReflexAgent() : new NeuralAgent(getWts(blue));
		IAgent b = new NeuralAgent(getWts(red));
		return Controller.doBattleNoGui(a, b);
	}
	
	public boolean checkIfWinning() throws Throwable {
		ArrayList<double[]> bestSet = mat.getBest();
		double[] weights = getWts(bestSet.get(r.nextInt(bestSet.size())));
		Long start = Instant.now().toEpochMilli();
		int[] outcome = Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(weights));
		System.out.println("Outcome vs blue: " + outcome + "   Time:" + Long.toString(Instant.now().toEpochMilli() - start));
		return (outcome[0] == -1);
	}
	
	void updateSurvival() {
		double mean_fit = mat.columnMean(mat.col("Fitness"));
		double max_fit = mat.columnMax(mat.col("Fitness"));
		double min_fit = mat.columnMin(mat.col("Fitness"));
		
		for(int i = 0; i < mat_size; i++) {
			double fitness = mat(i)[mat.col("Fitness")];
//			if(fitness > mean_fit)		
		}
	}
	
	int getMostFitIndex() {
		return mat.columnMaxIndex(mat.col("Fitness"));
	}
	double[] getWts(int rowNum) {
		double[] row = mat(rowNum);
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}
	double[] getWts(double[] row) {
		double[] weights = new double[291];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}

	void printTopFitness() {
		System.out.println(mat.columnMax(mat.col("Fitness")));
	}
	void printAllFitness() {
		mat.sort(mat.col("Fitness"), true);
		mat.printCol(mat.col("Fitness"));
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
	double[] mat(int i) {
		return mat.row(i);
	}
	void addParams() {
		for(int i = 0; i < params.length; i++)
			mat.addCol(params[i]);
		//Initialize everyone to live after win_survive_rate of wins
		for(int i = 0; i < mat.rows(); i++)
			mat(i)[mat.col("SurviveLoss")] = init_survive_loss_rate;
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
	Game() { 
		r = new Random();  
	}
	
}
interface IAgent {
	void reset();
	void update(Model m);
}