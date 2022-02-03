import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Data {
	public static int d = 10;
	// $\alpha$
	public static float alpha = 0.5f;
	// tradeoff $\alpha_w$
	public static float alpha_w = 0.001f;
	public static float alpha_v = 0.001f;
	public static float beta_eta = 0.001f;
	public static float beta_v = 0.001f;
	// learning rate $\gamma$
	public static float gamma = 0.001f;
    //the order of Markov Chains
	public static int L = 1;
	
	// === Input data files
	public static String fnTrainData = "";
	public static String fnTestData = "";
	//
	public static int n = 0; // number of users
	public static int m = 0; // number of items
	public static int num_train = 0; // number of the total (user, item) pairs in training data
	// scan number over the whole data
	public static int T = 0; 
	// === Evaluation
	public static int topK = 5; // top k in evaluation
	//
	// === training data
	public static HashMap<Integer, ArrayList<Integer>> TrainData = new HashMap<Integer, ArrayList<Integer>>();

	// === training data used for uniformly random sampling
	public static int[] indexUserTrain; // start from index "0", used to uniformly sample (u, i) pair
	public static int[] indexItemTrain; // start from index "0", used to uniformly sample (u, i) pair

	// === test data
	public static HashMap<Integer, HashSet<Integer>> TestData = new HashMap<Integer, HashSet<Integer>>();
	// === whole data (items)
	public static HashSet<Integer> ItemSetWhole = new HashSet<Integer>();
	
	//public static HashSet<Integer> TrainItemSetWhole = new HashSet<Integer>();
	
	// === some statistics, start from index "1"
	public static int[] userRatingNumTrain;
	public static int[] itemRatingNumTrain;

	// === model parameters to learn, start from index "1"
	public static float[][] W;
	public static float[][] V;
	public static float[][] eta_u; // l(chain) start from index "1"
	public static float[] eta; // global parameters, l(chain) start from index "1"
	public static float[] biasV; // bias of item
}
