import java.io.*;
import java.util.*;

//import ID3.DataSet;
//import ID3.Mappings;
//import ID3.NodeGeneration;

// A class to generate whole decision tree using recursion

public class CreateTree 
{
	//A function to decide attribute to be splitted and creating tree using recursion
	
	public NodeGeneration genTree(ArrayList<DataSet> r, NodeGeneration treeroot, ArrayList<Mappings> attributes, ArrayList<Mappings>attr_splitted) 
		{
			double maxGain = 0;
			int bestAtt = -1;
			
			EntropyCalculator newEntropy = new EntropyCalculator();
			treeroot.entropy = newEntropy.calculateEntropy(treeroot.data);

			if (treeroot.entropy == 0)
			{
				return treeroot;
			}
			
			for (int i = 0; i < ID3.Att_size; i++) 
			{
				Mappings Att_new = attributes.get(i);
				if (!attribute_splitted(Att_new, treeroot.att_Splited)) 
				{
					double entropy = 0;
					ArrayList<Double> entropies = new ArrayList<Double>();
					ArrayList<Integer> setSizes = new ArrayList<Integer>();

					for (int j = 0; j < Att_new.maximum; j++) 
					{
						ArrayList<DataSet> set = temp_set(treeroot, i, Att_new.mappingvalues.get(j));
						setSizes.add(set.size());
						if (set.size() != 0) 
						{
							entropy = newEntropy.calculateEntropy(set);
							entropies.add(entropy);
						}
					}
					
					//	Calculating Information gain and deciding which attribute has 
					//		maximum info gain

					double infogain = newEntropy.infoGain(treeroot.entropy, entropies, setSizes, treeroot.data.size());
					
					if (infogain > maxGain) 
					{
						bestAtt = i;
						maxGain = infogain;
					}
				}
			}
			
			if (bestAtt != -1) 
			{
				int c_size = attributes.get(bestAtt).maximum;
				Mappings temp = attributes.get(bestAtt);
				String attributeName = temp.s_name;
				
				treeroot.child = new NodeGeneration[c_size];
				
				treeroot.child_Count = c_size;
				
				ArrayList<Mappings> tempAtt = new ArrayList<Mappings>();
				
				for (int i =0; i < treeroot.att_Splited.size(); i++)
				{
					Mappings tempAttr = treeroot.att_Splited.get(i);
					tempAtt.add(tempAttr);
				}
				
				tempAtt.add(attributes.get(bestAtt));
				
				for (int j = 0; j < c_size; j++) 
				{
					treeroot.child[j] = new NodeGeneration();
					
					treeroot.child[j].parent = treeroot;
					
					treeroot.child[j].data = (temp_set(treeroot, bestAtt, temp.mappingvalues.get(j)));
					treeroot.child[j].attr = temp;
					
					treeroot.child[j].attr_val = temp.mappingvalues.get(j);
					
					treeroot.child[j].att_Splited = tempAtt;
				}
				
				NodeGeneration tempNode;
				
				for (int u = 0; u < c_size; u++) 
				{
					tempNode = genTree(treeroot.child[u].data, treeroot.child[u],attributes, tempAtt);
				}

			}
			else 
			{
				return treeroot;
			}
			return treeroot;
		}
	
	//ArrayList for adding children

		public ArrayList<DataSet> temp_set(NodeGeneration root, int attr, String value) 
		{
			ArrayList<DataSet> subset = new ArrayList<DataSet>();

			for (int i = 0; i < root.data.size(); i++) 
			{
				DataSet ds = root.data.get(i);

				if (ds.attributeValue[attr].equals(value)) 
				{
					subset.add(ds);
				}
			}
			String tempName = ID3.att.get(attr).s_name;
			
			return subset;
		}
		
		//Function to check which attributes to be splitted or not
		
		public boolean attribute_splitted(Mappings attribute,ArrayList<Mappings> usedAttributeList) 
		{
			if (usedAttributeList.contains(attribute)) 
			{
				return true;
			} 
			else 
			{
				return false;
			}
		}
		
		//Function to print whole decision tree as per format of Homework

		public void printingTree(NodeGeneration treeroot, int f) 
		{
			if (treeroot == null) 
			{
				return;
			}
			
			if (!treeroot.attr.s_name.equals("first"))
			{
				System.out.print("\n  ");
				for (int i = 0; i < f; i++) 
				{
					System.out.print("| ");
				}
				System.out.print(treeroot.attr.s_name + " =" + treeroot.attr_val	+ ":");
			}
			
			if (treeroot.child == null) 
			{
				String label = getlabel(treeroot);
				System.out.print(label);
			}

			f++;
			for (int i = 0; i < treeroot.child_Count; i++) 
			{
				printingTree(treeroot.child[i], f);
			}

		}
		
		// A Function to Traversing tree to fine accuracy and maintain counter
		
		public int treeTraversal(NodeGeneration treeroot, ArrayList<DataSet> ds) 
		{
			Queue<NodeGeneration> queue = new LinkedList<NodeGeneration>();
 			int c = 0;
 			queue.add(treeroot);
			
			for (int i = 0; i < ds.size(); i++) 
			{
				DataSet temp4 = ds.get(i);
				NodeGeneration t_Node = treeroot;
				
				while (t_Node.child!= null ) 
				{
					Mappings attr = t_Node.child[0].attr;
					int j = getAttrInd(attr);
					if (j != -1) 
					{
						String attrValue = temp4.attributeValue[j];

						int k = attr.getIndex(attrValue);
						
						t_Node = t_Node.child[k];
					} 
					else if (j == -1) 
					{
						System.out.println("Something wrong while traversing the tree");
						return -1;
					}
				}
				
				if (t_Node.child == null) 
				{
					String leafnode = getlabel(t_Node);
					if (leafnode.equals(temp4.label)) 
					{
						c++;
					}
				}
			}

			return c;
		}
		
		// Function to find accuracy in % using above counter

		public double accuracyCalculator(NodeGeneration root, ArrayList<DataSet> ds) 
		{
			int size = ds.size();
			int c = 0;
			double accuracy = 0.00;
			
			c = treeTraversal(root, ds);
			
			accuracy = ((double) c / (double) size) * 100;
			return accuracy;
		}

		// Function to get Row number using mapping class
		
		public int getAttrInd(Mappings attr) 
		{
			Mappings temp5;
			
			for (int i = 0; i < ID3.att.size(); i++) 
			{
				temp5 = ID3.att.get(i);
				if (temp5.s_name.equals(attr.s_name)) 
				{
					return i;
				}
			}
			
			return -999;
		}
		
		// Function to count mapping of class-0 and class-1 with respect to 
				// given attribute values

		public String getlabel(NodeGeneration treeroot) 
		{
			ArrayList<DataSet> record = treeroot.data;
			DataSet temp6;
			int c_1 = 0;
			int c_0 = 0;
			
			for (int d = 0; d < record.size(); d++) 
			{
				temp6 = record.get(d);
				
				if (temp6.label.equals("0")) 
				{
					c_0++;
				}
				if (temp6.label.equals("1")) 
				{
					c_1++;
				}
			}

			if (c_1 > c_0) 
			{
				return "1";
			} 
			else
				return "0";
		}
	}
