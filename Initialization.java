import java.util.ArrayList;

public class Initialization {
	
	public static void initialization() 
	{
		Data.indexUserTrain = new int[Data.num_train];
		Data.indexItemTrain = new int[Data.num_train];
		// === Locate memory for the data structure of the model parameters
		Data.W = new float[Data.m + 1][Data.d];
		Data.V = new float[Data.m + 1][Data.d];
		Data.eta_u = new float[Data.n + 1][Data.L+1];
		Data.eta = new float[Data.L+1]; // 
		Data.biasV = new float[Data.m + 1]; // bias of item
		
		int idx = 0;
		for (int u = 1; u <= Data.n; u++) 
		{
			// --- check whether the user $u$ is in the training data
			if (!Data.TrainData.containsKey(u))
			{
				continue;
			}
			// --- get a copy of the data in indexUserTrain and indexItemTrain
			ArrayList<Integer> ItemSet = new ArrayList<Integer>();
			if (Data.TrainData.containsKey(u))
			{
				ItemSet = Data.TrainData.get(u);
			}
			for (int i : ItemSet)
			{
				Data.indexUserTrain[idx] = u;
				Data.indexItemTrain[idx] = i;
				idx += 1;
			}
		}

		// --- initialization of W and V  eta_u eta
		for (int i = 1; i < Data.m + 1; i++) 
		{
			for (int f = 0; f < Data.d; f++)
			{
				Data.W[i][f] = (float) ((Math.random() - 0.5) * 0.01);
				Data.V[i][f] = (float) ((Math.random() - 0.5) * 0.01);
			}
		}
		
		// --- eta_u[u][l]  initialized to [0,1]
		for (int u=1; u<= Data.n; u++)
		{
			for (int l=1; l < Data.L+1; l++)
			{
				Data.eta_u[u][l]= (float) ((Math.random() - 0.5) * 0.01);
			}
		}
		for (int l=1; l <= Data.L; l++)
		{
			Data.eta[l]= (float) ((Math.random() - 0.5) * 0.01);
		}
		// ======================================================
		// --- initialization of biasV
		float g_avg = 0;
		// int maxItemRatingNumTrain = 0;
		for (int i = 1; i < Data.m + 1; i++) 
		{
			g_avg += Data.itemRatingNumTrain[i];
		}
		g_avg = g_avg / Data.n / Data.m;
		System.out.println("The global average rating:" + Float.toString(g_avg));

		// --- biasV[i] represents the popularity of the item i, which is
		// initialized to [0,1]
		for (int i = 1; i < Data.m + 1; i++) 
		{
			Data.biasV[i] = (float) Data.itemRatingNumTrain[i] / Data.n - g_avg;
		}

	}
}
