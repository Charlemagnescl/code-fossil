import java.io.IOException;
import java.util.Arrays;

public class ReadConfigurations {
	public static void readConfigurations(String[] args) throws IOException {
		// === Read the configurations
		for (int k = 0; k < args.length; k++) {
			if (args[k].equals("-d"))
				Data.d = Integer.parseInt(args[++k]);
			else if (args[k].equals("-alpha"))
				Data.alpha = Float.parseFloat(args[++k]);
			else if (args[k].equals("-alpha_v")) {
				Data.alpha_v = Float.parseFloat(args[++k]);
				Data.alpha_w = Float.parseFloat(args[k]);
				Data.beta_eta = Float.parseFloat(args[k]);
				Data.beta_v = Float.parseFloat(args[k]);
			}
			else if (args[k].equals("-gamma"))
				Data.gamma = Float.parseFloat(args[++k]);
			else if (args[k].equals("-fnTrainData"))
				Data.fnTrainData = args[++k];
			else if (args[k].equals("-fnTestData"))
				Data.fnTestData = args[++k];
			else if (args[k].equals("-n"))
				Data.n = Integer.parseInt(args[++k]);
			else if (args[k].equals("-m"))
				Data.m = Integer.parseInt(args[++k]);
			else if (args[k].equals("-L"))
				Data.L = Integer.parseInt(args[++k]);
			else if (args[k].equals("-T"))
				Data.T = Integer.parseInt(args[++k]);
			else if (args[k].equals("-topK"))
				Data.topK = Integer.parseInt(args[++k]);
		}

		// === Print the configurations
		System.out.println(Arrays.toString(args));
		System.out.println("fnTrainData: " + Data.fnTrainData);
		System.out.println("fnTestData: " + Data.fnTestData);
		System.out.println("n: " + Integer.toString(Data.n));
		System.out.println("m: " + Integer.toString(Data.m));
		System.out.println("gamma: " + Float.toString(Data.gamma));
		System.out.println("d: " + Integer.toString(Data.d));
		System.out.println("alpha_w: " + Float.toString(Data.alpha_w));
		System.out.println("alpha_v: " + Float.toString(Data.alpha_v));
		System.out.println("beta_eta: " + Float.toString(Data.beta_eta));
		System.out.println("beta_v: " + Float.toString(Data.beta_v));
		
		System.out.println("T: " + Integer.toString(Data.T));
		System.out.println("topK: " + Integer.toString(Data.topK));
		System.out.println("L: " + Integer.toString(Data.L));
		System.out.println("alpha: " + Float.toString(Data.alpha));
		
	}
}
