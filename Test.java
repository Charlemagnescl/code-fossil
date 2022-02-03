import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
public class Test 
{
	
	public static void testRanking(HashMap<Integer, HashSet<Integer>> TestData) 
	{
		// TestData: user->items
		// ==========================================================
		float[] PrecisionSum = new float[Data.topK + 1];
		float[] RecallSum = new float[Data.topK + 1];
		float[] F1Sum = new float[Data.topK + 1];
		float[] NDCGSum = new float[Data.topK + 1];
		float[] OneCallSum = new float[Data.topK + 1];

		// --- calculate the best DCG, which can be used later
		float[] DCGbest = new float[Data.topK + 1];
		for (int k = 1; k <= Data.topK; k++) 
		{
			DCGbest[k] = DCGbest[k - 1];
			DCGbest[k] += 1 / Math.log(k + 1);
		}

		// --- number of test cases
		int UserNum_TestData = 0;
		for (int u = 1; u <= Data.n; u++)
		{
			// --- check whether the user $u$ is in the test set
			if ((!TestData.containsKey(u)) || (!Data.TrainData.containsKey(u)))
			{
				continue;
			}
			else
			{
				UserNum_TestData++;
			}
			// ---
			float[] U_u = new float[Data.d];
			ArrayList<Integer> ItemSet_u_TrainData = new ArrayList<Integer>();
			if (Data.TrainData.containsKey(u)) 
			{
				ItemSet_u_TrainData = Data.TrainData.get(u);
				// ----- normalization
				float normalizaationFactor_plus = (float) Math.pow(ItemSet_u_TrainData.size(), Data.alpha);
				
				// ---- aggregation
				for (int i2 : ItemSet_u_TrainData) 
				{
					for (int f = 0; f < Data.d; f++) 
					{
						U_u[f] += Data.W[i2][f];
					}
				}
				
				for (int f = 0; f < Data.d; f++) 
				{
					U_u[f] = U_u[f] / normalizaationFactor_plus ;
				}
				
				int len = ItemSet_u_TrainData.size();
				ArrayList<Integer> pre=new ArrayList<Integer>();
				pre.add(0);
				for(int r = 1; r<=Data.L; ++r)
				{	
					if(len-r>=0)
					{
						pre.add(ItemSet_u_TrainData.get(len-r));
					}
				}
				// short term dynamics
				for (int f = 0; f < Data.d; f++) 
				{
					float temp = 0f;
					for (int l = 1; l <= Data.L; l++)
					{
						if(l<pre.size())
						{
							temp += Data.W[pre.get(l)][f] * (Data.eta_u[u][l] + Data.eta[l]);//error
						}
						
					}
					U_u[f] += temp;
				}
			}

			HashSet<Integer> ItemSet_u_TestData = TestData.get(u);
			// --- the number of preferred items of user $u$ in the test data
			int ItemNum_u_TestData = ItemSet_u_TestData.size();
			// --- prediction
			HashMap<Integer, Float> item2Prediction = new HashMap<Integer, Float>();
			item2Prediction.clear();

			for (int i = 1; i <= Data.m; i++) 
			{
				if (!Data.ItemSetWhole.contains(i)|| ItemSet_u_TrainData.contains(i))
				{
					continue;
				}
				// --- prediction via inner product
				float pred =  Data.biasV[i];
				for (int f = 0; f < Data.d; f++) 
				{
					pred += U_u[f] * Data.V[i][f];
				}
				item2Prediction.put(i, pred);
			}
			// --- sort
			List<Map.Entry<Integer, Float>> listY = new ArrayList<Map.Entry<Integer, Float>>(
					item2Prediction.entrySet());
			listY = HeapSort.heapSort(listY, Math.min(listY.size(), Data.topK) );  // using Lei LI's heapsort  
			
			// ===========================================================
			// --- Extract the topK recommended items
			int k = 1;
			int[] TopKResult = new int[Data.topK + 1];
			Iterator<Entry<Integer, Float>> iter = listY.iterator();
			while (iter.hasNext()) 
			{
				if (k > Data.topK)
				{
					break;
				}
				Map.Entry<Integer, Float> entry = (Map.Entry<Integer, Float>) iter.next();
				int itemID = entry.getKey();
				TopKResult[k] = itemID;
				k++;
			}
			// --- TopK evaluation
			int HitSum = 0;
			float[] DCG = new float[Data.topK + 1];
			float[] DCGbest2 = new float[Data.topK + 1];
			for (k = 1; k <= Data.topK; k++)
			{
				// ---
				DCG[k] = DCG[k - 1];
				int itemID = TopKResult[k];
				if (ItemSet_u_TestData.contains(itemID)) 
				{
					HitSum += 1;
					DCG[k] += 1 / Math.log(k + 1);
				}
				// --- precision, recall, F1, 1-call
				float prec = (float) HitSum / k;
				float rec = (float) HitSum / ItemNum_u_TestData;
				float F1 = 0;
				if (prec + rec > 0)
				{
					F1 = 2 * prec * rec / (prec + rec);
				}
				PrecisionSum[k] += prec;
				RecallSum[k] += rec;
				F1Sum[k] += F1;
				// --- in case the the number relevant items is smaller than k
				if (ItemSet_u_TestData.size() >= k)
				{
					DCGbest2[k] = DCGbest[k];
				}
				else
				{
					DCGbest2[k] = DCGbest2[k - 1];
				}
				NDCGSum[k] += DCG[k] / DCGbest2[k];
				OneCallSum[k] += HitSum > 0 ? 1 : 0;
			}
		}
		// =========================================================
		// --- the number of users in the test data
		// --- precision@k
		for (int k = 1; k <= Data.topK; k++) 
		{
			if(k==Data.topK)
			{
				float prec = PrecisionSum[k] / UserNum_TestData;
			    System.out.println("Prec@" + Integer.toString(k) + ":"+ Float.toString(prec));
			}
		}
		// --- recall@k
		for (int k = 1; k <= Data.topK; k++)
		{
			if(k==Data.topK)
			{
				float rec = RecallSum[k] / UserNum_TestData;
			    System.out.println("Rec@" + Integer.toString(k) + ":"+ Float.toString(rec));
			}
		}
		// --- F1@k
		for (int k = 1; k <= Data.topK; k++) 
		{
			if(k==Data.topK)
			{
				float F1 = F1Sum[k] / UserNum_TestData;
			    System.out.println("F1@" + Integer.toString(k) + ":"+ Float.toString(F1));
			}
		}
		// --- NDCG@k
		for (int k = 1; k <= Data.topK; k++) 
		{
			if(k==Data.topK)
			{
				float NDCG = NDCGSum[k] / UserNum_TestData;
			    System.out.println("NDCG@" + Integer.toString(k) + ":"+ Float.toString(NDCG));
			}
		}
		// --- 1-call@k
		for (int k = 1; k <= Data.topK; k++) 
		{
			if(k==Data.topK)
			{
				float OneCall = OneCallSum[k] / UserNum_TestData;
			    System.out.println("1-call@" + Integer.toString(k) + ":"+ Float.toString(OneCall));
			}
		}
	}
}
