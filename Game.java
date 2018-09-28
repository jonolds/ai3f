import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

class Game { // NO_UCD (unused code)
	double[] hard_coded_win = new double[] {3.473418286412789, 2.411982891026087, 0.9072219117105652, 1.5616501183642573, 0.4651128477692462, 2.2545313082101104, -2.794444034481138, 1.157396316469641, 0.626907769388755, -0.38373026564493695, 0.16791595650928817, 1.3711314764319649, -1.537509992815525, -3.8832629632870943, 0.5523650325893353, 0.5835463576028879, 2.077864749203427, 0.7135938903070597, 5.418899613045867, 1.2192338387381974, 1.3996907944048902, -0.34559753846433827, 2.3519541894676435, 8.552711957208027, 0.5716710889783619, 5.478121811918911, 0.9566840535001322, -3.02665803066825, -0.06648792331610842, 0.5649472781911197, -1.718961954085757, 0.8607403819049191, 2.701709618229206, 5.291887302592786, 0.5697712944625329, 0.28079287171483913, 1.0885141653828185, -2.0920075857391343, 1.8502191953701967, 0.5180691353830056, -3.6422939874765055, -0.6888163769711155, -1.731333676258456, -4.430193745724233, 2.7981616231867754, -4.316971158784618, -5.019188249867917, 0.007350216203786231, 0.5704944726009316, -0.47318062141940787, 0.5231494143447603, 0.22117180638465314, 0.472822262618035, 1.548446270283304, 1.620477969046652, -0.9075846108284873, 0.45519436411831116, 0.44870766198941586, 0.6698467111832368, -0.7538557244621511, 1.624127854081805, -1.4070694158550514, -0.4385959868591803, 2.9708758195747205, 2.767909397775801, 1.0791701835973548, -0.7413355005966609, 0.13504389891599347, 0.9748968855790633, -6.470680791221755, -0.5976302601056933, 0.09836660339261016, -0.8581322347386667, -5.415116867136349, -0.01182150620949793, -1.7859151775422557, 2.6520186574049176, -0.011750282030878946, 2.9425043790524295, 0.547671520340916, 0.9375318196848094, 3.4194668294624604, 2.1788554733666423, 0.0482677242310065, -0.0061901379837604395, 1.4274515808398047, 1.7353282618684616, 0.24303996977001896, 0.06690924926135441, 1.4067786525573363, 3.484566719221419, 1.4695322538045548, 1.4520225615380282, 0.6182853606174943, 4.418394746584307, 0.5201594695186669, 0.42772986625325116, 0.4588458743933114, 0.6284884836845297, 2.0509458992503347, -6.723570276389692, 0.5148511841206919, 6.189457949415919, -0.6239299392016971, 3.095258881767896, -5.1936848789930306, -5.947414701037108, 1.0764478563107922, -3.4297810283901766, 1.5334801943779142, -0.5448279979719821, 0.9134957471330805, 0.328347926238543, 1.0437487744858245, -3.3077666580901153, -1.4164754206409598, -1.0397693110809219, 0.13119671941681443, 5.929260338001209, 4.84269434776531, -0.5394065056503059, -2.764395149649752, 0.4065221012678325, 3.5341091646229366, 1.3286301004855035, -0.3381399914703923, 0.8095449852577837, 0.5070398831553086, 0.46699980324377377, 1.630695203859659, 1.7029490191428, 2.0446872295085163, -0.013773784002730341, 2.0478782541932166, 1.2911383384681974, 1.1116906490549818, -1.9516436225351013, 1.3785741470479727, 0.0755743518620593, -0.012390220135501638, 0.020301168809200666, 0.06911516478904929, 0.11324827765305027, 0.5495821749260285, -1.415112619949379, 0.15776189456907347, -1.5346593630893102, 0.3668046793438082, -1.3003954848239943, 1.0691686810395684, 2.2264075228454767, 0.7661209593296652, 1.5964169083138264, -0.5206175436488306, -0.8225935675713355, 5.4757396779116645, 0.8778195171185901, -0.02781825103414131, -0.9296823999547827, 0.9210177573245601, 0.1699112582146872, 2.164641903137326, 3.1391447249833253, -0.06012295142890547, -0.15593494529503782, 0.4545402483204648, -0.7356562331290877, -0.5851170290957837, 0.9200902399121967, -1.4540214058579264, -0.0021643927690705306, 0.020396771463274448, -1.5816858141079726, -0.2681358994007289, -1.344642552261262, 1.4390326078147704, -0.8906283265382773, -2.0178423183650347, 2.1913651639023266, -0.01238345704076092, 1.792862446752254, 1.8914831798955944, 0.5660710260911714, -2.550297862805907, 0.7146251784583251, 0.5334114258901431, 0.26710469249953883, 2.1337653994311108, -0.4428666081195496, 0.021371253268696398, -0.9347200431601851, 2.469006977373655, -3.5287759983756684, 0.574942784851102, 0.047458518468478414, -2.348580148562217, 1.0146602242355167, 0.07933239769210743, -0.15484696175119783, 2.0117097065557705, -0.11218852598983006, 4.047047838778709, 1.3463133549773354, 0.020048207810221513, 0.9972197568459165, 0.6318229344347717, -0.5420650684103246, -1.751658702498817, 0.025357411913670705, 0.038199384535189174, 5.435983387122893, 3.6139949154714266, 0.6751665616041317, 1.5525939537601663, -2.8181608640422637, 1.249538724412913, 0.11780674196089294, 0.6034144272413506, 0.8262729385094351, -0.21285577794874855, -0.15326696301843254, -2.213709581285451, 5.014144532838162, -0.5705832842763519, 3.0140833443270467, 3.1334448673861304, 0.977305773966102, -4.800839424802953, -0.9540123747621131, -1.4511045942529632, -1.492183743959356, 1.9453685371045661, 0.40344674449831924, 0.49631616119706246, -0.291011466282907, 0.2780336570212755, -0.38341459079855666, -1.4358172010680625, 0.05043573728306986, 0.44847660900220093, 4.689687134030712, 0.01072567560476667, 2.701513471122181, -0.5205069330069811, 0.5138669774053979, -2.2706091893469327, 0.5723336064443945, 0.8108396030250131, -0.08161140300829417, 0.4293611425214395, 0.6441622915787141, -0.85954106005446, 1.0137306542146287, -0.5627516748293155, 0.5027829150936142, 3.8548041151618677, 1.7056414493699978, 1.312261563928958, 0.7022472037011236, -0.019867697540051, 1.479279699339549, 2.161100303299752, 2.282999607207612, 0.22833937207317595, 0.9079510015331287, 1.8794902176232813, 1.7691405375862377, 1.4948436681058586, 1.489317610657123, 3.550718893193726, 1.0276178782597551, 8.313121999656156, 1.8095571172566083, 0.42013550125225574, -1.9589145282115743, -2.4343238450935285, 0.9290784450967677, 1.0846384224348493, 3.507190521917425, -3.039957816185434, -1.249572951318129, 3.457540210477551, 0.6360216969196357, 0.4333351099490002, -4.408650646402161, 0.8874875385895553, 2.1861529154360086, 0.47435207103898025, 0.9377091111500634, 0.41680308996332316, 1.179737797905297 };
	//Set at init()
	Matrix pop;
	Random r;
	String timestamp;
	long start_time;
	static final int num_genes = 291, mat_size = 60;
	static final String[] params = new String[] { "Won", "Lost", "Iters", "Fitness", "SurviveLoss", "RefWins", "RefLosses", "RefIters"};
	static final int max_param_len = 12;
	//TOURNEY RULES
	static final int num_tourneys = 80;
	static final double init_survive_loss_rate = .25;
	static final double survive_loss_max = .35;
	static final double survive_loss_min = .15;
	static final int max_iters_to_win = 3500;
	//MUTATION
	static final double mutation_rate = .15;
	static final double ave_mut_dev = .4;
	//BREEDING
	static final int dating_pool_size = 8;
	ArrayList<double[]> winners = new ArrayList<>();
	ArrayList<Integer> tracker = new ArrayList<>();
	int last_fastest_winner_iters = 18000000, num_blue_wins, last_winner_iters = 0;
	boolean beatReflex = false;
	double[] winning_combo = null;
	int battles = 0;
	LinkedList<Integer> recent_iters = new LinkedList<>();

	void init() throws Throwable {
		System.out.println("hard coded win (no Gui): " + Controller.doBattleNoGui(new ReflexAgent(), new NeuralAgent(hard_coded_win))[0]);
		double[] best_spawn = evolve();
		Controller.doBattle(new ReflexAgent(), new NeuralAgent(best_spawn));
	}

	double[] evolve() throws Throwable {
		start_time = time();
		initializePopulation();
		//TRAIN POPULATION 
		for(int i = 1; i < 1000000000; i++) {	//SCORE FITNESS every 1000 cycles
			if (i%10== 0) {
				track(i);
				updateSurvival();
			}
			
			if(beatReflex) {
				System.out.println(time() - start_time);
				return winning_combo;
			}

			//Get a random double to decide next action
			double d = r.nextDouble();
			if(d < .5)								//MUTATE 50%
				mutate(d);
			else if ((d >= .5) && (d < .7))			//SCRIMMAGE 20%
				scrimmage(d);
			else if ((d >= .7) && (d < .8))			//THE PURGE 10%
				bestVsWorst();
			else if ((d >= .8) && (d < 1.0))		//TRAIN VS REFLEX 20%
				trainVsReflex(d);
		}
		saveTracker();
		System.out.println("Elapsed time: " + (time() - start_time)/1000 + "s");
		return winning_combo;
	}
	
	void updateSurvival() {
		int survive_col = pop.col("SurviveLoss");
		double ave_fit = pop.columnMean(pop.col("Fitness"));
		double survival_variance = survive_loss_max - init_survive_loss_rate;
		double survival_boost = r.nextDouble() * survival_variance/4;
		for(int i = 0; i < mat_size; i++) {
			double[] player = pop.row(i);
			if(player[291] + player[292] > 10) {
				if(player[pop.col("Fitness")] > 1.1*ave_fit) {
					player[survive_col] = Math.min(player[survive_col] + survival_boost, survive_loss_max);
				}
				else if(player[pop.col("Fitness")] < .9*ave_fit) {
					player[survive_col] = Math.max(player[survive_col] - survival_boost, survive_loss_min);
				}	
			}
		}
	}

	void updateFitness(int i, int[] outcome) {
		double wins = mat(i)[pop.col("Won")];
		double fitness = (double)((int)(10000000*wins/outcome[1]))/10;
		mat(i)[pop.col("Fitness")] = fitness;
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
//		System.out.println("winner iters: " + iters);
		double iters_range = 15000;		//18000 - my target of < 3000
		iters -= 3000;
		double clone_scale = 1 - iters /iters_range;
		int max_clones = 4;
		int num_clones = (int)((double)max_clones * clone_scale);
//		System.out.println("clone_scale: " + clone_scale + "  num_clones: " + num_clones);
		pop.sort(pop.col("Fitness"), false);
		int lame_guy = mat_size-1;
		for(int i = 0; i < num_clones; i++) {
			for(int k = 0; k < num_genes; k++) 
				mat(lame_guy)[k] = mat(hero)[k];
			for(int k = num_genes; k < num_genes+ params.length;k++ )
				mat(lame_guy)[k] = 0.0;
			mat(lame_guy)[pop.col("SurviveLoss")] = init_survive_loss_rate + .07;
			lame_guy--;
		}
	}
	
	void bestVsWorst() throws Exception {
		pop.sort(pop.col("Fitness"), false);
		for(int i = 0; i < 5; i++) {
			int a = mat_size - i -1;
			int[] outcome = battle(a, i);
			results(a, i, outcome);
		}
	}
	//BATTLE
	void scrimmage(double d) throws Exception {
		for(int i = 0; i < pop.rows()/10; i++) {
			int a = r.nextInt(mat_size);	//Random Agent from matrix
			int b = r.nextInt(mat_size);
			int[] outcome = battle(a, b);
			results(a, b, outcome);
		}
	}
	
	void trainVsReflex(double d) throws Exception {
//		mat.sort(mat.col("Won"), true);
		for(int i = 0; i < mat_size; i++) {
			int[] outcome = battle(-1, i);
			if(outcome[0] != 0) {
				if(outcome[0] == -1) {
					mat(i)[pop.col("Won")]++;
					mat(i)[pop.col("Iters")] += outcome[1];
					mat(i)[pop.col("RefWins")]++;
					mat(i)[pop.col("RefIters")]+= outcome[1];
				}
				else {
					mat(i)[pop.col("RefLosses")]++;
					mat(i)[pop.col("RefIters")]+= outcome[1];
				}
			}
			updateFitness(i, outcome);
			if(outcome[0] == -1 && !recent_iters.contains(outcome[1])) {
				if(recent_iters.size() > 3)
					recent_iters.pop();
				recent_iters.add(outcome[1]);
				if(outcome[1] < 7000)
					mat(i)[pop.col("Fitness")]*=1.20;
				else if(outcome[1] < 9000)
					mat(i)[pop.col("Fitness")]*=1.10;
				else if(outcome[1] < 1100)
					mat(i)[pop.col("Fitness")]*=1.05;
				last_winner_iters = outcome[1];
				System.out.println("Beat Reflex    Iters: " + outcome[1]);
				cloneBlueBeater(i, outcome[1]);
				if(outcome[1] < last_fastest_winner_iters)
					winning_combo = getWts(mat(i));
				num_blue_wins++;
				if(num_blue_wins > 100 || outcome[1] < max_iters_to_win) {
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
			int winner, loser, lived;
			if(outcome[0] == -1) {
				winner = a;	//a if -1, b if 1
				loser = b;
			}
			else {			//outcome[0] = 1
				winner = b;	//b if 1
				loser = a;
			}
			if(d < mat(loser)[pop.col("SurviveLoss")]) {
				lived = loser;
				mat(lived)[pop.col("Lost")]++;
			}
			else {
				lived = winner;
				mat(lived)[pop.col("Won")]++;
			}
			

			mat(lived)[pop.col("Fitness")] = mat(lived)[pop.col("Won")]/mat(lived)[pop.col("Iters")];
			mat(lived)[pop.col("Iters")] += outcome[1];
			killAndBreed((lived == loser) ? winner : loser);
			updateFitness(lived, outcome);
		}
		else {
			int one_punished = (r.nextBoolean())? a : b;
			mat(one_punished)[pop.col("Fitness")] -= ((double)(r.nextInt(4)/100)*mat(one_punished)[pop.col("Fitness")]);
		}
	}
	
	void killAndBreed(int kill_num) {
		//Pick 9 random parent candidates. possible_parents[0] is parent a
		int[] possible_fathers = r.ints(0, mat_size).distinct().limit(dating_pool_size + 1).toArray();
		double[] mother = mat(possible_fathers[0]);		//Parent 1
		double[] father = new double[num_genes];					//dating pool

		int b_num = 0, best_score = 0;

		//Compare similarity !!!!!!!---FIX only comparing sign
		for(int i = 1; i < 9; i++) {
			int score = 0;
			for(int k = 0; k < 291; k++) {
				if((mother[k] > 0 && mat(i)[k] > 0)||(mother[k] < 0 && mat(i)[k] < 0))
					score++;
			}
			if(score >= best_score)
				b_num = possible_fathers[i];
		}
		father = mat(b_num);
		for(int i = 0; i < 291; i+=3) {								//REPLACE(kill) genes in sets of 3
			mat(kill_num)[i] = (r.nextBoolean()) ? mother[i] : father[i];
			mat(kill_num)[i+1] = (r.nextBoolean()) ? mother[i+1] : father[i+1];
			mat(kill_num)[i+2] = (r.nextBoolean()) ? mother[i+2] : father[i+2];
		}

		for(int i = 291; i < mat_size + params.length; i++)			//RESET params
			mat(kill_num)[i] = 0.0;
		mat(kill_num)[pop.col("SurviveLoss")] = init_survive_loss_rate;		//WIN_SURVIVE back to default
	}
	
	int[] battle(int blue, int red) throws Exception {		//SHORTCUT TO Controller.doBattle
		battles++;
		IAgent a = (blue == -1) ? new ReflexAgent() : new NeuralAgent(getWts(mat(blue)));
		IAgent b = new NeuralAgent(getWts(mat(red)));
		return Controller.doBattleNoGui(a, b);
	}

	void track(int i) {
		pop.sort(pop.col("Fitness"), false);
		tracker.add((int)(mat(0)[pop.col("Fitness")]));
		System.out.println(mat(0)[pop.col("Fitness")]);
//		for(int k = 0; k < params.length; k++)
//			System.out.print(toStr(mat(0)[mat.col(params[k])]));
//		System.out.println();
	}
	void printAllFitness() {
		pop.printCol(pop.col("Fitness"));
	}

	void printAllStats() {
		//PRINT COLUMN TITLES
		for(String s: params)
			System.out.print(toStr(s));
		System.out.println();
		
		//PRINT COLUMN DATA
		int startCol = pop.cols() - num_genes;
		for(double[] d: pop.m_data)
			for(int i = startCol; i < num_genes; i++) 
				System.out.print(toStr(d[pop.col(params[i])]));
		System.out.println();
	}
	
	void saveTracker() throws IOException {
		PrintWriter pw = new PrintWriter(new FileWriter("tracker " + timestamp + ".csv"));
		for(int i = 0; i < tracker.size(); i++) pw.println(tracker.get(i));
		pw.close();
	}
	void initializePopulation() {
		//CREATE A RANDOM INITIAL POPULATION
		pop = new Matrix(mat_size, 291);
		for(int i = 0; i < mat_size; i++)
			for(int j = 0; j < num_genes; j++)
				mat(i)[j] = .03 * r.nextGaussian();
		//ADD EXTRA PARAM COLUMNS TO TRACK AGENT FITNESS
		addParams();
	}
	void addParams() {
		for(int i = 0; i < params.length; i++)
			pop.addCol(params[i]);
		//Initialize everyone to live after win_survive_rate of wins
		for(int i = 0; i < pop.rows(); i++)
			mat(i)[pop.col("SurviveLoss")] = init_survive_loss_rate;
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
	
	//Helper methods
	double[] getWts(double[] row) { return Arrays.copyOfRange(row, 0, num_genes); }
	double[] mat(int i) { return pop.row(i); }
	static void printDoubleArray(double[] dArr) { Arrays.stream(dArr).forEach(x->System.out.print(x + ", ")); System.out.println(); }
	static void printIntArray(int[] iArr) { Arrays.stream(iArr).forEach(x->System.out.print(x + " ")); System.out.println(); }
	static String toStr(Object obj) { return String.format("%-12s", String.valueOf(obj)); }//formats data output
	static long time() {return System.currentTimeMillis(); }								//gets time in milli
	static double toSecs(long milli) { return (double)milli/1000; }
	Game() { timestamp = (new SimpleDateFormat("M-d_H.mm.s")).format(new Date()); r = new Random(); }
	public static void main(String[] args) throws Throwable { Game g = new Game(); g.init(); }
}

interface IAgent { void reset(); void update(Model m); }