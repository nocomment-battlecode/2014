package team089;

import java.util.ArrayList;

import battlecode.common.*;

public class CowBFS {
	
	public static RobotController rc;
	public static MapLocation enemy;
	public static MapLocation myLoc;
	public static int height;
	public static int width;
	public static Direction[][] pathingData;
	public static int[][] distanceData;//integer comparisons are apparently cheaper (2 vs 4 b)
	public static int[][] mapData;
	public static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	public static Direction[] dirs = {Direction.NORTH,Direction.NORTH_EAST,Direction.EAST,Direction.SOUTH_EAST,Direction.SOUTH,Direction.SOUTH_WEST,Direction.WEST,Direction.NORTH_WEST};
	//public static Direction[] dirs = {Direction.NORTH,Direction.EAST,Direction.SOUTH,Direction.WEST};
	public static boolean shortestPathLocated;
	public static RobotController rci;
	
	//pathTo(myLoc,enemy,1000000);
	
	public static void init(RobotController rci, MapLocation myPos, int edgeLength){
		rc = rci;
		MapAssessment.cowAssess(myPos, edgeLength, rci);
		//MapAssessment.printCoarseMap();
		//MapAssessment.printBigCoarseMap(rci);
		//updateInternalMap(rc);
		
	}
	
	private static int getMapData(int x, int y){
		return mapData[x+1][y+1];
	}
	
	public static int getMapData(MapLocation m){
		return getMapData(m.x,m.y);
	}
	
	private static void setMapData(int x, int y, int val){
		mapData[x+1][y+1] = val;
	}
	
	private static void updateInternalMap(RobotController rc){//can take several rounds, but ultimately saves time
		mapData = new int[width+2][height+2];
		for(int x=0;x<width;x++){
			for(int y=0;y<height;y++){
				int val = MapAssessment.coarseMap[x][y];
				if(val==MapAssessment.bigBoxSize*MapAssessment.bigBoxSize){//completely filled with voids
					val=0;//if it's zero, consider it non-traversible
				}else{
					val+=10000;//if it's >= 10000, consider it on-map
				}
				setMapData(x,y,val);//0 off map, >= 10000 on-map, with val-10000 obstacles in the box
			}
		}
	}

}
