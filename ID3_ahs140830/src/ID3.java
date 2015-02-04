import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

	public class ID3 
	{

		public static int Att_size = 0;
		
		static ArrayList<DataSet> testDataset = new ArrayList<DataSet>();
		static ArrayList<DataSet> trainingDataset = new ArrayList<DataSet>();
		
		
		public static ID3 object = new ID3();
		
		//ArrayList structure to store data of file 
		
		static ArrayList<Mappings> att = new ArrayList<Mappings>();
		
		//Main Function
		public static void main(String[] args) 
		{
			
			
			NodeGeneration root = new NodeGeneration(trainingDataset);
			
			CreateTree tree = new CreateTree();
			//ID3 i = new ID3();
		
			//Boundary condition to check if there are 2 arguments
			
			ArrayList<Mappings> attUsed = new ArrayList<Mappings>();
			
			Mappings m = new Mappings();
			
			
			if (args.length != 2) 
			{
				System.out.println("Please Enter 1 Training File first and then 1 Test File...");
				System.exit(1);
			}
			System.out.println("\n\n");
			
			String data_Test = args[1];
			String data_Training = args[0];
			
			boolean flag = false;
			boolean r_train = true;
			boolean r_test = false;
			
			flag = m.ReadFile(data_Training,trainingDataset,r_train);
			
			if (flag == false) 
			{
				System.out.println("File can not be read...");
				System.exit(1);
			}

			flag = m.ReadFile(data_Test,testDataset,r_test);
			if (flag == false) 
			{
				System.out.println("File can not be read...");
				System.exit(1);
			}
			
			Mappings column;
			for (int i = 0; i < att.size(); i++) 
				column = att.get(i);
				
			DataSet filedata;
			
			for (int x = 0; x < att.size(); x++) 
			{
				String t = att.get(x).s_name;
			}
			
			Mappings temp1 = new Mappings();
			
			temp1.s_name = "first";
			root.attr = temp1;
			root = tree.genTree(trainingDataset, root, att, attUsed);
			
			System.out.println("The Decision Tree is following");
			tree.printingTree(root, 0);
			
			//DecimalFormat class to print accuracy up to 2 decimal places
			
			DecimalFormat df = new DecimalFormat("#.##");
			
			//Printing Trained data Accuracy
			
			double accuracyTrainedData = tree.accuracyCalculator(root, trainingDataset);
			System.out.println();
			System.out.println("******************************************************");
			System.out.println("\nAccuracy of trained Data is : "+ df.format(accuracyTrainedData) + "%");
			
			//Printing Test Data Accuracy
			
			double accuracyTestData = tree.accuracyCalculator(root, testDataset);
			System.out.println();
			System.out.println("******************************************************");
			System.out.println("\nAccuracy of test  Data is :  " +df.format(accuracyTestData)+"%");
		}

}
