package team089;

import battlecode.common.*;

public class MapAssessment{
	
	public static int[][] coarseMap;
	
	public static void assessMap(){
		coarseMap = new int[DataCache.coarseWidth][DataCache.coarseHeight];
		for(int x=DataCache.coarseWidth*DataCache.bigBoxSize;--x>=0;){
			for(int y=DataCache.coarseHeight*DataCache.bigBoxSize;--y>=0;){
				coarseMap[x/DataCache.bigBoxSize][y/DataCache.bigBoxSize]+=countObstacles(x,y);
			}
		}
	}

	public static int countObstacles(int x, int y){//returns a 0 or a 1
		int terrainOrdinal = DataCache.rc.senseTerrainTile(new MapLocation(x,y)).ordinal();//0 NORMAL, 1 ROAD, 2 VOID, 3 OFF_MAP
		return (terrainOrdinal<2?0:1);
	}
	
	public static void printCoarseMap(){
		System.out.println("Coarse map:");
		for(int x=0;x<coarseMap[0].length;x++){
			for(int y=0;y<coarseMap.length;y++){
				int numberOfObstacles = coarseMap[x][y];
				System.out.print(Math.min(numberOfObstacles, 9));
			}
			System.out.println();
		}
	}
	public static void printBigCoarseMap(RobotController rc){
		System.out.println("Fine map:");
		for(int x=0;x<coarseMap[0].length*DataCache.bigBoxSize;x++){
			for(int y=0;y<coarseMap.length*DataCache.bigBoxSize;y++){
				if(countObstacles(x,y)==0){//there's no obstacle, so print the box's obstacle count
					int numberOfObstacles = coarseMap[x/DataCache.bigBoxSize][y/DataCache.bigBoxSize];
					System.out.print(Math.min(numberOfObstacles, 9));
				}else{//there's an obstacle, so print an X
					System.out.print("X");
				}
				System.out.print(" ");
			}
			System.out.println();
		}
	}
}