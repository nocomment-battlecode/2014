package team089;

//import java.util.ArrayList;

import battlecode.common.*;

public class BasicPathing{
	
	//static ArrayList<MapLocation> snailTrail = new ArrayList<MapLocation>();
	static MapLocation[] snailTrail = new MapLocation[DataCache.trailLength];
	static int currentIndex = 0;
	static boolean filledTrail = false;
	
	public static boolean canMove(Direction dir, boolean selfAvoiding, boolean avoidEnemyHQ){
		//include both rc.canMove and the snail Trail requirements
		MapLocation resultingLocation = DataCache.selfLoc.add(dir);
		if(selfAvoiding){
			for(int i=DataCache.trailLength;--i>=0;){
				//MapLocation m = snailTrail.get(i);
				MapLocation m = snailTrail[i];
				if(!m.equals(DataCache.selfLoc)){
					if(resultingLocation.isAdjacentTo(m)||resultingLocation.equals(m)){
						return false;
					}
				}
			}
		}
		if(avoidEnemyHQ)
			if(closeToEnemyHQ(resultingLocation))
				return false;
		//if you get through the loop, then dir is not adjacent to the icky snail trail
		return DataCache.rc.canMove(dir);
	}
	
	public static boolean closeToEnemyHQ(MapLocation loc){
		return DataCache.enemyHQLoc.distanceSquaredTo(loc)<=RobotType.HQ.attackRadiusMaxSquared;
	}
	
	public static void tryToMove(Direction chosenDirection,boolean selfAvoiding,boolean avoidEnemyHQ, boolean sneak) throws GameActionException
	{
		//while(snailTrail.size()<2)
		//	snailTrail.add(new MapLocation(-1,-1));
		while(!filledTrail)
		{
			snailTrail[currentIndex] = new MapLocation(-1,-1);
			currentIndex = (currentIndex+1)%DataCache.trailLength;
			if(currentIndex == 0) filledTrail = true;
		}
		if(DataCache.rc.isActive()){
			//snailTrail.remove(0);
			//snailTrail.add(DataCache.selfLoc);
			snailTrail[currentIndex] = DataCache.selfLoc;
			currentIndex = (currentIndex+1)%DataCache.trailLength;
			for(int dirOffset: DataCache.dirOffsets){
				int forwardInt = chosenDirection.ordinal();
				Direction trialDir = DataCache.dirValues[(forwardInt+dirOffset+8)%8];
				if(canMove(trialDir, selfAvoiding, avoidEnemyHQ)){
					if(!sneak){
						DataCache.rc.move(trialDir);
					}else{
						DataCache.rc.sneak(trialDir);
					}
					//snailTrail.remove(0);
					//snailTrail.add(rc.getLocation());
					break;
				}
			}
			//System.out.println("I am at "+rc.getLocation()+", trail "+snailTrail.get(0)+snailTrail.get(1)+snailTrail.get(2));
		}
	}
}