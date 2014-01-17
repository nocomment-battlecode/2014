package team089;

import java.util.ArrayList;

import battlecode.common.*;

public class VectorFunctions
{
	public static MapLocation findClosest(MapLocation[] locList, MapLocation currentLoc){
		int closestDist = 10000000;
		MapLocation closestLoc = null;
		for(MapLocation tryLoc: locList){
			if (tryLoc.x != DataCache.enemyHQLoc.x || tryLoc.y != DataCache.enemyHQLoc.y){
				int tryDist = currentLoc.distanceSquaredTo(tryLoc);
				if (tryDist < closestDist){
					closestDist = tryDist;
					closestLoc = tryLoc;
				}
			}
		}
		return closestLoc;
	}
	
	public static int findClosest(ArrayList<MapLocation> manyLocs, MapLocation point){
		int closestDist = 10000000;
		int challengerDist = closestDist;
		int closestLoc = 0;
		for(int i=manyLocs.size();--i>=0;){
			MapLocation m = manyLocs.get(i);
			challengerDist = point.distanceSquaredTo(m);
			if(challengerDist<closestDist){
				closestDist = challengerDist;
				closestLoc = i;
			}
		}
		return closestLoc;
	}
	
	public static MapLocation mladd(MapLocation m1, MapLocation m2)
	{
		return new MapLocation(m1.x+m2.x, m1.y+m2.y);
	}
	
	public static MapLocation mlsubtract(MapLocation m1, MapLocation m2)
	{
		return new MapLocation(m1.x-m2.x,m1.y-m2.y);
	}
	
	public static MapLocation mlmultiply(MapLocation bigM, int factor)
	{
		return new MapLocation(bigM.x*factor, bigM.y*factor);
	}
	
	public static MapLocation mldivide(MapLocation bigM, int divisor)
	{
		return new MapLocation(bigM.x/divisor, bigM.y/divisor);
	}
	
	public static int locToInt(MapLocation m)
	{
		return (m.x*DataCache.mapBase + m.y);
	}
	
	public static MapLocation intToLoc(int i)
	{
		return new MapLocation(i/DataCache.mapBase,i%DataCache.mapBase);
	}
	
	public static void printPath(ArrayList<MapLocation> path, int bigBoxSize)
	{
		for(MapLocation m:path)
		{
			MapLocation actualLoc = bigBoxCenter(m,bigBoxSize);
			System.out.println("("+actualLoc.x+","+actualLoc.y+")");
		}
	}
	
	public static MapLocation bigBoxCenter(MapLocation bigBoxLoc, int bigBoxSize){
		return mladd(mlmultiply(bigBoxLoc,bigBoxSize),new MapLocation(bigBoxSize/2,bigBoxSize/2));
	}
	
	public static MapLocation[] robotsToLocations(Robot[] robotList, boolean ignoreHQ) throws GameActionException{
		boolean hasHQ = false;
		if(robotList.length==0)
			return new MapLocation[]{};
		RobotInfo[] enemyInfos = new RobotInfo[robotList.length];
		for(int i=robotList.length;--i>=0;){
			Robot anEnemy = robotList[i];
			enemyInfos[i] = DataCache.rc.senseRobotInfo(anEnemy);
			if(enemyInfos[i].type==RobotType.HQ && ignoreHQ)
				hasHQ = true;
		}
		int robotLocsLength = hasHQ?(robotList.length-1):robotList.length;
		MapLocation[] robotLocs = new MapLocation[robotLocsLength];
		int i = 0;
		for(int j=robotList.length;--j>=0;){
			if(enemyInfos[j].type!=RobotType.HQ || !ignoreHQ){
				robotLocs[i] = enemyInfos[j].location;
				i++;
			}
		}
		return robotLocs;
	}
	
	public static MapLocation meanLocation(MapLocation[] manyLocs){
		if(manyLocs.length==0)
			return null;
		MapLocation runningTotal = new MapLocation(0,0);
		for(MapLocation m:manyLocs){
			runningTotal = mladd(runningTotal,m);
		}
		return mldivide(runningTotal,manyLocs.length);
	}
	/*
	public static MapLocation[] robotsToLocations(Robot[] robotList,RobotController rc) throws GameActionException{
		MapLocation[] robotLocations = new MapLocation[robotList.length];
		for(int i=0;i<robotList.length;i++){
			Robot anEnemy = robotList[i];
			RobotInfo anEnemyInfo = rc.senseRobotInfo(anEnemy);
			robotLocations[i] = anEnemyInfo.location;
		}
		return robotLocations;
	}*/
}