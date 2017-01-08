package TwitterAnalysis;

public class Locations {

	
	
	double[][]locationLondon = {{-0.56, 51.23}, {0.25, 51.72}};
	
	double[][]locationCalifornia={{-122.75, 36.8}, {-121.75, 37.8}}; 
	double[][]locationNewYork	={{-74.0, 40.0}, {-73.0, 41.0}};
	//double [][]locationSeattle={ {-94.65,38.06},{-93.65, 39.06}};
	double [][]locationUtah={ {-114.05301, 36.99795},{-109.04107, 42.00162}};
	
	//double [][]locationSeattle= {{-74.0, 40.0}, {-73.0, 41.0}, {-74.3, 40.3}, {-73.3, 41.3}};
	
	
//	double [][]locationSeattle={{-105.478142, 40.094551},
//			{-104.478142, 41.964069},
//									{-105.301758, 39.964069},
//									{-104.301758, 40.094551}
//									};
	double[][] getArrayLocation(String str)
	{
		if(str.equalsIgnoreCase("Utah"))
			return locationUtah;
		
		else if(str.equalsIgnoreCase("California"))
			return locationCalifornia;
		
		
		else if(str.equalsIgnoreCase("New York"))
			return locationNewYork;
		
		return null;
		
	}
	

}
