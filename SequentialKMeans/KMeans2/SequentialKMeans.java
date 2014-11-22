package SequentialKMeans.KMeans2;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;




public class SequentialKMeans {
	
	public static Date timeDate1;
	public static Date timeDate2;
	

	public class Constant{
	
		//Defining number of clusters
		public static final int NClusters = 4;	
	}
	
	
	//Class coordinate composed of 2 integer, and presents the sensor 2D coordinates
	public static class Coordinates {
		public long x=0;
		public long y=0;
		
		//1st Constructor by two integer
		public Coordinates (long a, long b){
			x=a;
			y=b;
		}
		
		//2nd constructor by string
		public Coordinates (String pointString){
			
			 String[] resultSplit = pointString.split(",");			
			 x = Integer.parseInt(resultSplit[0]);
			 y=Integer.parseInt(resultSplit[1]);	
		}
		
	}
	
	public static double EuclideanDistance(Coordinates cluster, Coordinates point){
		
		double Sum = 0.0;
        
        Sum =  Math.pow((cluster.x-point.x),2.0) + Math.pow((cluster.y-point.y),2.0);
        
        return Math.sqrt(Sum);
	}
	

	//Class to store the sums of the x and y of the points belonging 
	//to the same cluster to calculate the new centroid
	public static class CoordinatesSums{
		public long X ; //sum of all the x
		public long Y; //sum of all the y
		public long F; //frequency of the coordinates
		
		//constructor by 3 integer
		public CoordinatesSums(long x, long y, long f){
			X=x;
			Y=y;
			F=f;
		}
		
		//2nd constructor by string
		public CoordinatesSums (String sumString){
			
			 String[] resultSplit = sumString.split(",");			
			 X = Integer.parseInt(resultSplit[0]);
			 Y=Integer.parseInt(resultSplit[1]);
			 F=Integer.parseInt(resultSplit[2]);
		}
	}
	
	public static class ArrayCoordinatesSums{
		CoordinatesSums[] clusterSums = new CoordinatesSums[Constant.NClusters];
		
		public ArrayCoordinatesSums(){
			for(int n=0; n<Constant.NClusters; n++)
				clusterSums[n] =new CoordinatesSums(0,0,0);	
			
		}
		
		//Initialize the array to zeros 
		public void SetToZeroClusterSums(){
			for(int n=0; n<Constant.NClusters; n++)
				clusterSums[n] =new CoordinatesSums(0,0,0);	
		}
	}
	
	//Class to save the calculated Euclidean distances to each cluster for a certain point
	public static class Distance{
		
	public double[] dist = new double[Constant.NClusters];	
	
	public Distance(){
		
		for(int n=0; n<Constant.NClusters; n++)
			dist[n] =0.0;	
	}
	
	//Method to set all the elements dist of Distance instance to zero
	public void SetToZero(){
		for(int n=0; n<Constant.NClusters; n++)
			dist[n] =0.0;	
	}
	
	//Method that gives the index of the nearest cluster
	public int CalculClusterIndex(){
			 int indexMin=0;
			 double min =dist[0];
			 
			for(int i=1; i<Constant.NClusters; i++){
			
				if(min>dist[i])
					min=dist[i];
			}
			
			for(int l=0; l<Constant.NClusters; l++){
				if(dist[l] ==min){
					indexMin=l; break;
				}
					
			}
			
			return indexMin;			
	
		}
				
	}
	
	public static void main(String args[]){
		
		//Declaration and Initialization of the initial clusters 
		Coordinates[] cluster = new Coordinates[Constant.NClusters];
		
		//Extraction of the initial centroid from the InitialCentroids.txt
		//location of the sensor coordinates file. It has to be in the executing program 
	try {
		String sCurrentCentroid = null;
		BufferedReader brInitialCentroids = new BufferedReader(new FileReader("C:\\Users\\dabid\\Dropbox\\DistributedSystems\\Project\\Code\\InitialCentroids.txt"));
		int n=0;
		
			while ((sCurrentCentroid = brInitialCentroids.readLine()) != null && n<Constant.NClusters) {
				cluster[n] = new Coordinates(sCurrentCentroid);
				n++;
			}
			
			if (brInitialCentroids != null)
					brInitialCentroids.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		
		
		Coordinates var =null; //to manipulate the coordinates of the SensorCoordinates.txt file
		
		//distance is a variable issued from the Distance class to store 
		//the calculated distances between var and each centroid 
		Distance distance = new Distance();
				
		CoordinatesSums[] cSums = new CoordinatesSums[Constant.NClusters];
		
		//centroid index: each centroid coordinate assigned to an index
		int index;
		
		//BufferReader to read the sensor coordinates file
		BufferedReader br = null;
		
		
		//***Start Calculating the new centroid***//
		
		Coordinates[] clusterOld = new Coordinates[Constant.NClusters]; //Old clusters
		int i=0, thresoldLoop =80; //Counter and threshold for the main while loop
		double difference=100, thresholdDifference = 1; //to measure the difference between the new and the old cluster
		
		//Initialize the old cluster to zero coordinates
		for(int d=0; d<Constant.NClusters; d++)
			clusterOld[d]= new Coordinates(0,0);
		timeDate1 = new Date();
		System.out.println("Started at "+ timeDate1);
		
		//Looping through the file to calculate the new centroids
		while((i < thresoldLoop) && (difference > thresholdDifference)){
		
			//initialization of the CoordinatesSum variables and Distance variable
			//to start the calculate the new centroid
		 	for(int k=0; k<Constant.NClusters; k++){
					distance.dist[k] =0.0;	
					cSums[k] = new CoordinatesSums(0,0,0);
				}
		 	
		
				try{
				//current line of the file which is the point coordinates having the format x,y
				String sCurrentLine;
	 
				//location of the sensor coordinates file. It has to be in the executing program 
				//br = new BufferedReader(new FileReader( System.getProperty("user.dir") + "\\SensorCoordinates.txt"));
				br = new BufferedReader(new FileReader("C:\\Users\\dabid\\Dropbox\\DistributedSystems\\Project\\Code\\SensorCoordinates500M.txt"));
					
				
				//Looping through the sensor coordinates file
				while ((sCurrentLine = br.readLine()) != null) {
					
					//while ((sCurrentLine = br.readLine().trim()) != null) {
					
					var = new Coordinates(sCurrentLine);
					
					//calculate the Euclidean distance between the point and every centroid, 
					//and store the result in the distance variable
					for(int p=0; p<Constant.NClusters; p++){
						distance.dist[p]= EuclideanDistance(cluster[p], var);
					}
					
					//Calculate the nearest centroid for the point, and store its index in the index variable 
					index=distance.CalculClusterIndex();
					
					//Summing this coordinates to the set of the other points belonging to the same cluster
					cSums[index].X +=var.x; 
					cSums[index].Y +=var.y;
					cSums[index].F ++;
					
					//initialize again to 0.0 the distance variable class
					for(int k=0; k<Constant.NClusters; k++){
						distance.dist[k] =0.0;
					}
				}
	 
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					if (br != null)br.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
			//calculating the mean of each cluster: the new centroid
			for(int v=0; v<Constant.NClusters; v++){
				if (cSums[v].F != 0){
				cluster[v].x= cSums[v].X/cSums[v].F;
				cluster[v].y= cSums[v].Y/cSums[v].F;
				}
			}
			
			//Calculate the difference between the new and the old centroid
			difference=0.0;
			for(int r=0; r<Constant.NClusters; r++)
			difference+= EuclideanDistance(clusterOld[r], cluster[r]);	
			
			//Save the new centroid in the clusterOld to calculate the difference
			for(int o=0; o<Constant.NClusters;o++)
			clusterOld[o] = new Coordinates(cluster[o].x, cluster[o].y);
			
			
			i++; //Increment the counter i for the main loop while
		}
		
		
		
		 //Write the final new centroid to the NewCentroid file
		 try {
			 PrintWriter newCentoidFile= new PrintWriter("C:\\Users\\dabid\\Dropbox\\DistributedSystems\\Project\\Code\\NewCentroids.txt" , "UTF-8");
			for(int f=0; f<Constant.NClusters; f++){
			 newCentoidFile.println(cluster[f].x + "," +  cluster[f].y);
			 System.out.println(cluster[f].x + "," +  cluster[f].y);
			}
			if (newCentoidFile != null)newCentoidFile.close();
			
			timeDate2 = new Date();
			System.out.println("finished at "+ timeDate2);
			System.out.println("Total spent time " + (timeDate2.getTime()-timeDate1.getTime()) + " en milliseconde");
		 
		 } catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
		 
		
		
	}
	
	
		
	
}
