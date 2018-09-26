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
	static final String[] params = new String[] { "Won", "Lost", "Iters", "Fitness", "SurviveLoss", "RefWins", "RefLosses", "RefIters"};
	static final int max_param_len = 12;
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
	ArrayList<double[]> winners = new ArrayList<>();
	int last_fastest_winner_iters = 18000000, num_blue_wins, last_winner_iters = 0;
	boolean beatReflex = false;
	double[] winning_combo = null;
	int battles = 0;

	void init() throws Throwable {
		timestamp = (new SimpleDateFormat("M-d_H.mm.s")).format(new Date());
		r = new Random();
		double[] best_spawn = evolve();
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(best_spawn));
	}

	double[] evolve() throws Throwable {
		long start_time = time();
		
		for(double k = 0.3; k < .9; k+=.03) {
			//CREATE A RANDOM INITIAL POPULATION
			mat = new Matrix(mat_size, 291);
			for(int i = 0; i < mat_size; i++)
				for(int j = 0; j < num_genes; j++)
					mat(i)[j] = k * r.nextGaussian();
			
			//ADD EXTRA PARAM COLUMNS TO TRACK AGENT FITNESS
			addParams();
			
			
			System.out.println("k: " + k);
			long start = time();
			
			//TRAIN POPULATION 
			for(int i = 1; i < 1000000000; i++) {	//SCORE FITNESS every 1000 cycles
				if (i%50== 0) {
					printTopFitness();
					updateSurvival();
				}
				
				if(beatReflex) {
					System.out.println(time() - start);
					break;
//					return winningCombo;
				}
	
				//Get a random double to decide next action
				double d = r.nextDouble();
				if(d < .5)								//MUTATE 50%
					mutate(d);
				else if ((d >= .5) && (d < .7))			//SCRIMMAGE 20%
					scrimmage(d);
				else if ((d >= .7) && (d < .8))			//THE PURGE 10%
					bestVsWorst();
				else if ((d >= .8) && (d < 1.0))		//TRAINELITE 20%
					trainVsReflex(d);
			}
		}
		System.out.println("Elapsed time: " + (time() - start_time)/1000 + "s");
		return winning_combo;
	}
	
	void updateSurvival() {
		int survive_col = mat.col("SurviveLoss");
		double ave_fit = mat.columnMean(mat.col("Fitness"));
		double survival_variance = survive_loss_max - init_survive_loss_rate;
		double survival_boost = r.nextDouble() * survival_variance/4;
		for(int i = 0; i < mat_size; i++) {
			double[] player = mat.row(i);
			if(player[291] + player[292] > 40) {
				if(player[mat.col("Fitness")] > 1.1*ave_fit) {
					player[survive_col] = Math.min(player[survive_col] + survival_boost, survive_loss_max);
				}
				else if(player[mat.col("Fitness")] < .9*ave_fit) {
					player[survive_col] = Math.max(player[survive_col] - survival_boost, survive_loss_min);
					
				}	
			}
		}
	}

	void updateFitness(int i) {
		double iters = mat(i)[mat.col("Iters")];
		double wins = mat(i)[mat.col("Won")];
		double fitness = (double)((int)(10000000*wins/iters))/10;
		if(mat(i)[mat.col("Won")] >= 20) 
			mat(i)[mat.col("Fitness")] = fitness;
		else
			mat(i)[mat.col("Fitness")] = 0.0;
	}

	//MUTATE
	void mutate(double d) {			//d < .4
		if(d < .1)					//CATASTROPHIC 25% of Mutations
			catastrophic(d);
		else {
			double mutation_level = ave_mut_dev * r.nextGaussian();
			int row_num = r.nextInt(mat_size);
			int num_genes_mutated = r.nextInt(num_genes);
			for(int i = 0; i < num_genes_mutated; i++)
				mat(row_num)[r.nextInt(num_genes)] += mutation_level;
		}
	}
	
	void catastrophic(double d) {
		double[] extra_toes = mat(r.nextInt(mat_size));
		for(int i = 0; i < num_genes; i++) {
			extra_toes[i] += r.nextGaussian()*r.nextInt(5);
		}
	}
	
	void cloneBlueBeater(int hero, int iters) {
		System.out.println("winner iters: " + iters);
		double iters_range = 15000;		//18000 - my target of < 3000
		iters -= 3000;
		double clone_scale = 1 - iters /iters_range;
		int max_clones = 10;
		int num_clones = (int)((double)max_clones * clone_scale);
		System.out.println("clone_scale: " + clone_scale + "  num_clones: " + num_clones);
		mat.sort(mat.col("Fitness"), false);
		int lame_guy = mat_size-1;
		for(int i = 0; i < num_clones; i++) {
			for(int k = 0; k < num_genes; k++) 
				mat(lame_guy)[k] = mat(hero)[k];
			for(int k = num_genes; k < num_genes+ params.length;k++ )
				mat(lame_guy)[k] = 0.0;
			mat(lame_guy)[mat.col("SurviveLoss")] = init_survive_loss_rate + .7;
			lame_guy--;
		}
	}
	
	void bestVsWorst() throws Exception {
		mat.sort(mat.col("Fitness"), false);
		for(int i = 0; i < 10; i++) {
			int a = mat_size - i -1;
			int[] outcome = battle(a, i);
			results(a, i, outcome);
		}
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
//		mat.sort(mat.col("Won"), true);
		for(int i = 0; i < mat_size; i++) {
			int[] outcome = battle(-1, i);
			if(outcome[0] != 0) {
				if(outcome[0] == -1) {
					mat(i)[mat.col("Won")]++;
					mat(i)[mat.col("Iters")] += outcome[1];
					mat(i)[mat.col("RefWins")]++;
					mat(i)[mat.col("RefIters")]+= outcome[1];
				}
				else {
					mat(i)[mat.col("RefLosses")]++;
					mat(i)[mat.col("RefIters")]+= outcome[1];
				}
			}
			updateFitness(i);
			if(outcome[0] == -1 && outcome[1] != last_winner_iters) {
				last_winner_iters = outcome[1];
				System.out.println("Beat Reflex    Iters: " + outcome[1]);
				cloneBlueBeater(i, outcome[1]);
				if(outcome[1] < last_fastest_winner_iters)
					winning_combo = getWts(mat(i));
				num_blue_wins++;
				if(num_blue_wins > 100 || outcome[1] < 2500) {
					beatReflex = true;
					printDoubleArray(mat(i));
				}
				break;
			}
		}
	}
	
	void results(int a, int b, int[] outcome) {
		if(outcome[0] != 0) {
			double d = r.nextDouble();
			int winner = (outcome[0] == -1) ? a : b;
			int loser = (outcome[0] == -1) ? b : a;
			int lived = (d < mat(loser)[mat.col("SurviveLoss")]) ? b : a;
			mat(lived)[mat.col((lived == winner) ? "Won" : "Lost")]++;
			mat(lived)[mat.col("Fitness")] = mat(lived)[mat.col("Won")]/mat(lived)[mat.col("Iters")];
			mat(lived)[mat.col("Iters")] += outcome[1];
			killAndBreed((lived == loser) ? winner : loser);
			updateFitness(lived);
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
		mat(kill_num)[mat.col("SurviveLoss")] = init_survive_loss_rate;		//WIN_SURVIVE back to default
	}
	
	int[] battle(int blue, int red) throws Exception {		//SHORTCUT TO Controller.doBattle
		battles++;
		IAgent a = (blue == -1) ? new ReflexAgent() : new NeuralAgent(getWts(mat(blue)));
		IAgent b = new NeuralAgent(getWts(mat(red)));
		return Controller.doBattleNoGui(a, b);
	}

	double[] getWts(double[] row) {
		double[] weights = new double[num_genes];
		for(int i = 0; i < 291; i++)
			weights[i] = row[i];
		return weights;
	}

	void printTopFitness() {
		mat.sort(mat.col("Fitness"), false);
		
		for(int k = 0; k < params.length; k++)
			System.out.print(toStr(mat(0)[mat.col(params[k])]));
		System.out.println();
	}
	void printAllFitness() {
		mat.printCol(mat.col("Fitness"));
	}

	void printAllStats() {
		//PRINT COLUMN TITLES
		for(String s: params)
			System.out.print(toStr(s));
		System.out.println();
		
		//PRINT COLUMN DATA
		int startCol = mat.cols() - num_genes;
		for(double[] d: mat.m_data)
			for(int i = startCol; i < num_genes; i++) 
				System.out.print(toStr(d[mat.col(params[i])]));
		System.out.println();
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
	String toStr(double d) {
		return String.format("%-12s", d);
	}
	String toStr(String str) {
		return String.format("%-12s", str);
	}
	long time() {return Instant.now().toEpochMilli(); }
}

interface IAgent {
	void reset();
	void update(Model m);
}