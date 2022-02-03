import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Train 
{
	public static void train() throws FileNotFoundException 
	{
		for (int iter = 1; iter <= Data.T; iter++) 
		{
			if (iter == 100 ||iter == 500||iter == 1000) 
			{
				if (Data.fnTestData.length() > 0) 
				{
					System.out.println("Iter:" + Integer.toString(iter) + "| ");
					Test.testRanking(Data.TestData);
				}
			}
			// =================================================================
			for (int iter2 = 0; iter2 < Data.num_train; iter2++) 
			{
				// --- randomly sample a user-item pair, Math.random(): [0.0,1.0)
				int u = -1, i = -1, idx2 = -1;
				while (true) 
				{
					idx2 = (int) Math.floor(Math.random() * Data.num_train);
					u = Data.indexUserTrain[idx2];
					i = Data.indexItemTrain[idx2];
					if(!Data.TrainData.containsKey(u))
					{
						continue;
					}
					if (Data.TrainData.get(u).indexOf(i) >= Data.L)//because of the Markov chain
					{
						break;
					}
				}// **************got the positive sample**********
				FISMrmse(u,i);
			}
			// =================================================================
		}
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public static void FISMrmse(int u, int i) 
	{
		ArrayList<Integer> ItemSet = Data.TrainData.get(u);
		int ItemSetSize = ItemSet.size();
		ArrayList<Integer> pre = new ArrayList<Integer>();
		pre.add(0);
		for (int r = 1; r <= Data.L; ++r) 
		{
			int temp = ItemSet.get(ItemSet.indexOf(i) - r);
			pre.add(temp);     //pre:save the short term markov_chain items [t-1,t-2,t-3...]
		}
		
		// got the negative sample:j
		int j = -1;
		if (true) 
		{
			while (true) 
			{
				j = (int) (Math.floor(Math.random() * Data.m) + 1);  //[1,m]
				if (Data.ItemSetWhole.contains(j)&& (!ItemSet.contains(j))) 
				{
					break;
				}
			}
		}
		// ===================================================
		// ----- normalization
		float normalizationFactor_plus = (float) Math.pow(ItemSetSize - 1f + 0.0001f, Data.alpha);
		float normalizationFactor_plus_negative = (float) Math.pow(ItemSetSize - 0f + 0.0001f, Data.alpha);

		// ===================================================
		// --- $U_{u\cdot}^{-i}$
		float[] U_u_i = new float[Data.d];
		float[] U_u_i_negative = new float[Data.d];

		// long term preferences
		for (int i2 : ItemSet) 
		{
			if (i2 != i) 
			{
				for (int f = 0; f < Data.d; f++) 
				{
					U_u_i[f] += Data.W[i2][f];
				}
			}
		}

		for (int i2 : ItemSet) 
		{
			if (true)
			{
				for (int f = 0; f < Data.d; f++) 
				{
					U_u_i_negative[f] += Data.W[i2][f];
				}
			}
		}
		for (int f = 0; f < Data.d; f++) 
		{
			U_u_i[f] = U_u_i[f] / normalizationFactor_plus;
			U_u_i_negative[f] = U_u_i_negative[f]/ normalizationFactor_plus_negative;
		}

		// short term dynamics
		for (int f = 0; f < Data.d; f++) 
		{
			float temp = 0f;
			for (int l = 1; l <= Data.L; l++) 
			{
				temp += Data.W[pre.get(l)][f]* (Data.eta_u[u][l] + Data.eta[l]);
			}
			
			U_u_i[f] += temp;
			U_u_i_negative[f] += temp;
		}

		// ===================================================
		// Update Parameters
		float[] Vi_before = new float[Data.d];
		for (int f = 0; f < Data.d; f++) 
		{
			Vi_before[f] = Data.V[i][f];
		}

		float[] Vi_negative_before = new float[Data.d];
		for (int f = 0; f < Data.d; f++) 
		{
			Vi_negative_before[f] = Data.V[j][f];
		}

		float[] eta_before = new float[Data.L + 1];
		for (int l = 1; l <= Data.L; l++) 
		{
			eta_before[l] = Data.eta[l];
		}
		float[] eta_u_before = new float[Data.L + 1];
		for (int l = 1; l <= Data.L; l++) 
		{
			eta_u_before[l] = Data.eta_u[u][l];
		}

		// ----- $r_{ui}$
		float r_ui = Data.biasV[i];
		float r_ui_negative = Data.biasV[j];

		for (int f = 0; f < Data.d; f++) 
		{
			r_ui += U_u_i[f] * Data.V[i][f];
			r_ui_negative += U_u_i_negative[f] * Data.V[j][f];
		}

		// ----- $e_{ui}$
		float e_uij = (r_ui - r_ui_negative);
		e_uij = 1f / (1 + (float) Math.pow(Math.E, e_uij));

		
		// ===================================================
		// ----- update $b_i$
		Data.biasV[i] = Data.biasV[i] - Data.gamma* (-e_uij + Data.beta_v * Data.biasV[i]);

		Data.biasV[j] = Data.biasV[j] - Data.gamma* (e_uij + Data.beta_v * Data.biasV[j]);

		// ----- update $V_{i\cdot}$ positive
		for (int f = 0; f < Data.d; f++) 
		{
			Data.V[i][f] =Data.V[i][f]  - Data.gamma*(-e_uij * U_u_i[f] + Data.alpha_v * Data.V[i][f]);
		}

		// ----- update $V_{j\cdot}$ negative
		for (int f = 0; f < Data.d; f++) 
		{
			Data.V[j][f] = Data.V[j][f] - Data.gamma* (e_uij * U_u_i_negative[f] + Data.alpha_v* Data.V[j][f]);
		}
		
		// ----- update $eta and eta_u $
		for (int l = 1; l <= Data.L; l++)
		{
			float term = 0.0f;
			for (int f = 0; f < Data.d; f++) 
			{   //dot product
				term += Data.W[pre.get(l)][f]* (Vi_before[f] - Vi_negative_before[f]);
			}

			Data.eta[l] = Data.eta[l] - Data.gamma* (-e_uij * term + Data.beta_eta * Data.eta[l]);
			Data.eta_u[u][l] = Data.eta_u[u][l] - Data.gamma* (-e_uij * term + Data.beta_eta * Data.eta_u[u][l]);
		}
		
		// ======update $W$=============================================
		// ----- update $W_{i'\cdot}$
		for (int i2 : ItemSet)   //ItemSet(�б�):  Data.TrainData.get(u)
		{
			if (i2 != i && pre.contains(i2) == false) 
			{
				for (int f = 0; f < Data.d; f++) 
				{
					Data.W[i2][f] = Data.W[i2][f]- Data.gamma*(-e_uij* (Vi_before[f] / normalizationFactor_plus 
							- Vi_negative_before[f]/normalizationFactor_plus_negative) 
											+ Data.alpha_w* Data.W[i2][f]);
				}
			}
		}

		// -----
		for (int f = 0; f < Data.d; ++f) 
		{
			Data.W[i][f] = Data.W[i][f]- Data.gamma*
					(e_uij * Vi_negative_before[f]/ normalizationFactor_plus_negative + Data.alpha_w* Data.W[i][f]);
		}

		
		// ----- update $W_{h'\cdot}$ --- short term items
		for (int l = 1; l <= Data.L; l++) 
		{
			int item = pre.get(l);
			for (int f = 0; f < Data.d; ++f) 
			{
				float temp = (Vi_before[f]* (1f / normalizationFactor_plus + eta_before[l] + eta_u_before[l]) 
						     - Vi_negative_before[f]*(1f / normalizationFactor_plus_negative + eta_before[l] +eta_u_before[l]));
				Data.W[item][f] = Data.W[item][f] - Data.gamma* (-e_uij * temp + Data.alpha_w * Data.W[item][f]);
			}
		}
	}
}
