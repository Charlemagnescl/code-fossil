import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException 
	{	
		// 1. read configurations		
		ReadConfigurations.readConfigurations(args);

		// 2. read training data and test data
        ReadData.readData();
               
		// 3. initialization
		Initialization.initialization();
		 
		// 4. training
		long startTime = System.currentTimeMillis();
		Train.train();
		long endTime = System.currentTimeMillis();
		System.out.println("Time consuming: " + (endTime - startTime)/1000 + "s");

		// 5. test
		//Test.test();		
    }
}
