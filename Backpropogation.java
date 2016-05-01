import java.util.*;
import java.io.*;
import java.text.*;
class Backpropogation {
	private int numberOfInput;
	private int numberOfHiddenLayer;
	private int numberOfHiddenNode;
	private int numberOfOutput;
	private double learningrate;
	private int numberofepoch;
	private int numberoflayer; //input layer + hidden layer + output layer
	private String [] column;
	private int columnLength;
	private int numberOfInstances;
	private double weightZchange;
	double yInput[];
	double yExp[];
	double zInput[];
	double zExp[];
	
	private double [][] inputNode;
	private double [][] hiddenNode;
	private double [] outputNode;
	private double [][] weightY;
	private double [][] weightZ;
	private double [] error;
	
	public Backpropogation() {
		numberOfInput = 0;
		numberOfHiddenLayer = 0;
		numberOfHiddenNode = 0;
		numberOfOutput = 0;
		learningrate = 0.0;
		numberofepoch = 0;
		numberoflayer = 0; //input layer + hidden layer + output layer
		columnLength = 0;
		numberOfInstances = 0;
		inputNode = new double[200][200];
		hiddenNode = new double[100][100];
		outputNode = new double[100];
		weightY = new double[200][200];
		weightZ = new double[200][200];
		error = new double[200];
	}
	
	void inputByFile(String fileName, double learningRate, int numberOfEpoch) {
		String line = "";
		
		this.learningrate = learningRate;
		this.numberofepoch = numberOfEpoch;
		
		try {
			FileReader fr = new FileReader(fileName);
			BufferedReader br = new BufferedReader(fr);
			
			line = br.readLine();
			
			column = line.split(",");
			columnLength = column.length;
			numberOfInput = column.length-1;
			numberOfOutput = 1;
			numberOfHiddenNode = numberOfInput/2;
			
			inputNode = new double[200][column.length-1];
			outputNode = new double[200];
			
			int x=0;
			
			while((line = br.readLine())!= null) {
				String splits[] = line.split(",");
				for(int y=0;y<splits.length-1;y++) {
					inputNode[x][y] = Double.parseDouble(splits[y]);
				}
				outputNode[x] = Double.parseDouble(splits[column.length-1]);
				x++;
			} 
			numberOfInstances = x;
			weightY = new double[numberOfHiddenNode][numberOfInput];
			weightZ = new double[numberOfOutput][numberOfHiddenNode];
			
			File file = new File("first weight.txt");
			if(file.exists()) {
				br = new BufferedReader(new FileReader(file));
				line = "";
				int numberofnode = numberOfHiddenNode + numberOfOutput;
				int count = 0;
				while((line = br.readLine())!= null) {
					String splits[] = line.split("\t");
					if(count < (numberofnode - numberOfOutput)) {
						for(int a=0;a<splits.length;a++) {
							weightY[count][a] = Double.parseDouble(splits[a]);
						}
					}
					else {
						for(int a=0;a<splits.length;a++) {
							weightZ[0][a] = Double.parseDouble(splits[a]);
						}
					}
					count++;
				}
			}
			else {
				file.createNewFile();
				FileWriter fw = new FileWriter(file, true);
				PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
				
				//first layer y
				for(x=0;x<numberOfHiddenNode;x++) {
					for(int y=0;y<numberOfInput;y++) {
						weightY[x][y] = Math.random();
						pw.print(weightY[x][y]+"\t");
					}
					pw.println();
				}
				
				//second layer z
				for(x=0;x<numberOfOutput;x++) {
					for(int y=0;y<numberOfHiddenNode;y++) {
						weightZ[x][y] = Math.random();
						pw.print(weightZ[x][y]+"\t");
					}
					pw.println();
				}
				pw.close();
			}
			
			br.close();
		}
		catch(FileNotFoundException e) {
			System.out.println(e);    
		}
		catch(IOException ioe) {
			System.out.println(ioe);    
		}
	}
	
	
	void feedforward(int v) {
		yInput = new double[numberOfHiddenNode];
		yExp = new double[numberOfHiddenNode];
		
		//Initialize yInput zInput
		for(int x=0;x<numberOfHiddenNode;x++) {
			yInput[x] = 0.0;
			yExp[x] = 0.0;
		}
		
		zInput = new double[numberOfOutput];
		zExp = new double[numberOfOutput];
		for(int x=0;x<numberOfOutput;x++) {
			zInput[x] = 0.0;
			zExp[x] = 0.0;
		}      
		
		//Calculate yInput
		for(int x=0;x<numberOfHiddenNode;x++) {
			for(int y=0;y<numberOfInput;y++) {
				yInput[x] += inputNode[v][y] * weightY[x][y];
			}
		}
		//Calculate yExp using sigmoid function
		for(int x=0;x<numberOfHiddenNode;x++) {
			yExp[x] = 1/(1+Math.exp(-yInput[x]));
		}
		
		//Calculate zInput
		for(int x=0;x<numberOfOutput;x++) {
			for(int y=0;y<numberOfHiddenNode;y++) {
				zInput[x] += yExp[y] * weightZ[x][y];
			}
		}
		//Calculate zExp using sigmoid function
		for(int x=0;x<numberOfOutput;x++) {
			zExp[x] = 1/(1+Math.exp(-zInput[x]));
		}
		
		//Calculate error
		error[v] = (outputNode[v] - zExp[0])*(zExp[0])*(1-zExp[0]);
		//System.out.println(error[v]);
	}
	
	void updateError(int v) {
		double weightZchange[][] = new double[numberOfOutput][numberOfHiddenNode];
		double weightYchange[][] = new double[numberOfHiddenNode][numberOfInput];
		double hiddenNodeChangein[] = new double[numberOfHiddenNode];
		double hiddenNodeChange[] = new double[numberOfHiddenNode];
		
		//update weight error between hidden layer and output layer
		for(int w=0;w<numberOfOutput;w++) {
			for(int x=0;x<numberOfHiddenNode;x++) {
				weightZchange[w][x] = learningrate * error[v] * yExp[x];
				weightZ[w][x] += weightZchange[w][x]; 
			}
		}
		
		//update hidden node
		for(int w=0;w<numberOfOutput;w++) {
			for(int x=0;x<numberOfHiddenNode;x++) {
				hiddenNodeChangein[x] = error[v]*weightZ[w][x];
				hiddenNodeChange[x] = hiddenNodeChangein[x]*yExp[x]*(1-yExp[x]);
			}
		}
		
		//update weight error between input layer and hidden layer
		for(int x=0;x<numberOfHiddenNode;x++) {
			for(int y=0;y<numberOfInput;y++) {
				weightYchange[x][y] = learningrate*hiddenNodeChange[x]*inputNode[v][y];
				weightY[x][y] += weightYchange[x][y];
			}
		}
	}
	
	void bpnntraining() {
		for(int w=0;w<numberofepoch;w++) {
			for(int x=0;x<numberOfInstances;x++) {
				feedforward(x);
				updateError(x);
			}
		}
		try {
			FileWriter fw = new FileWriter("weightraining.txt", true);
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			
			//first layer y
			for(int x=0;x<numberOfHiddenNode;x++) {
				for(int y=0;y<numberOfInput;y++) {
					pw.print(weightY[x][y]+"\t");
				}
				pw.println();
			}
			
			//second layer z
			for(int x=0;x<numberOfOutput;x++) {
				for(int y=0;y<numberOfHiddenNode;y++) {
					pw.print(weightZ[x][y]+"\t");
				}
				pw.println();
			}
			pw.close();   
		}
		catch(FileNotFoundException fnfe) {
			System.out.println(fnfe.getMessage());
		}
		catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}   
	}
	
	void bpnntesting(String filetesting, String fileweight, double numberOfClasses) {
		double inputNode[][] = new double[200][200];
		double outputNode[] = new double[200];
		double benchmark = (1.0/numberOfClasses)/2.0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filetesting));
			String line = "";
			line = br.readLine();
			column = line.split(",");
			columnLength = column.length;
			numberOfInput = column.length-1;
			numberOfOutput = 1;
			numberOfHiddenNode = numberOfInput/2;
			int x=0;
			while((line = br.readLine())!= null) {
				String splits[] = line.split(",");
				for(int y=0;y<splits.length-1;y++) {
					inputNode[x][y] = Double.parseDouble(splits[y]);
				}
				outputNode[x] = Double.parseDouble(splits[column.length-1]);
				x++;
			}
			numberOfInstances = x;
			br = new BufferedReader(new FileReader(fileweight));
			line = "";
			int numberofnode = numberOfHiddenNode + numberOfOutput;
			
			int count = 0;
			while((line = br.readLine())!= null) {
				String splits[] = line.split("\t");
				if(count < (numberofnode - numberOfOutput)) {
					for(int a=0;a<splits.length;a++) {
						weightY[count][a] = Double.parseDouble(splits[a]);
					}
				}
				else {
					for(int a=0;a<splits.length;a++) {
						weightZ[0][a] = Double.parseDouble(splits[a]);
					}
				}
				count++;
			}
			/*
				-- Iris Setosa
				-- Iris Versicolour
				-- Iris Virginica
			*/
			int correctnessIrisSetosa = 0;
			int correctnessIrisVersicolour = 0;
			int correctnessIrisVirginica = 0;
			int incorrectnessIrisSetosa = 0;
			int incorrectnessIrisVersicolour = 0;
			int incorrectnessIrisVirginica = 0;

			for(int v=0;v<numberOfInstances;v++) {
				yInput = new double[numberOfHiddenNode];
				yExp = new double[numberOfHiddenNode];
				
				//Initialize yInput zInput
				for(x=0;x<numberOfHiddenNode;x++) {
					yInput[x] = 0.0;
					yExp[x] = 0.0;
				}
				
				zInput = new double[numberOfOutput];
				zExp = new double[numberOfOutput];
				for(x=0;x<numberOfOutput;x++) {
					zInput[x] = 0.0;
					zExp[x] = 0.0;
				}      
				
				//Calculate yInput
				for(x=0;x<numberOfHiddenNode;x++) {
					for(int y=0;y<numberOfInput;y++) {
						yInput[x] += inputNode[v][y] * weightY[x][y];
					}
				}
				//Calculate yExp using sigmoid function
				for(x=0;x<numberOfHiddenNode;x++) {
					yExp[x] = 1/(1+Math.exp(-yInput[x]));
				}
				
				//Calculate zInput
				for(x=0;x<numberOfOutput;x++) {
					for(int y=0;y<numberOfHiddenNode;y++) {
						zInput[x] += yExp[y] * weightZ[x][y];
					}
				}
					//Calculate zExp using sigmoid function
				for(x=0;x<numberOfOutput;x++) {
					zExp[x] = 1/(1+Math.exp(-zInput[x]));
					if(Math.abs(0.33-zExp[x]) <= benchmark) {
						//System.out.println("error : "+ Math.abs(outputNode[v]-zExp[x]));
						System.out.println("Testing output : 0.33");
						if(0.33 == outputNode[v]) {
							//System.out.println("Correctness : Correct");
							correctnessIrisSetosa++;
						}
						else {
							//System.out.println("Correctness : Not Correct");
							incorrectnessIrisSetosa++;
						}
						//System.out.println("Expected output : "+outputNode[v]);
					}
					else if(Math.abs(0.67-zExp[x]) <= benchmark) {
						//System.out.println("error : "+ Math.abs(outputNode[v]-zExp[x]));
						System.out.println("Testing output : 0.67");
						if(0.67 == outputNode[v]) {
							//System.out.println("Correctness : Correct");
							correctnessIrisVersicolour++;
						}
						else {
							//System.out.println("Correctness : Not Correct");
							incorrectnessIrisVersicolour++;
						}
						//System.out.println("Expected output : "+outputNode[v]);
					}
					else if(Math.abs(1.0-zExp[x]) <= benchmark) {
						//System.out.println("error : "+ Math.abs(outputNode[v]-zExp[x]));
						System.out.println("Testing output : 1.0");
						if(1.0 == outputNode[v]) {
							//System.out.println("Correctness : Correct");
							correctnessIrisVirginica++;
						}
						else {
							//System.out.println("Correctness : Not Correct");
							incorrectnessIrisVirginica++;
						}
						//System.out.println("Expected output : "+outputNode[v]);
					}
				}       
			}
			double correctness = correctnessIrisVirginica + correctnessIrisVersicolour + correctnessIrisSetosa;
			double CorrectnessPercent = (correctness/(double)numberOfInstances)*100;
			double NotCorrectnessPercent = 100 - CorrectnessPercent;
			DecimalFormat df = new DecimalFormat("0.##");
			System.out.println("Correctness percentage : "+df.format(CorrectnessPercent));
			System.out.println("Incorrectness percentage : "+df.format(NotCorrectnessPercent));
		}
		catch(FileNotFoundException fnfe) {
			System.out.println(fnfe.getMessage());
		}
		catch(IOException ioe) {
			System.out.println(ioe.getMessage());
		}
	}
}
