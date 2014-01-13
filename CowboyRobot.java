package team089;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class CowboyRobot extends BasicRobot
{	
	static RobotController rc;
	static Random randall = new Random();
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	//boolean becomingPasture = false;
	//SOLDIER data:
	static int myBand = 100;
	static int pathCreatedRound = -1;
	public CowboyRobot(RobotController myRC) throws GameActionException
	{
		super(myRC);
	}
	public void run(RobotController rcIn)
	{
		rc=rcIn;
		Comms.rc = rcIn;
		randall.setSeed(rc.getRobot().getID());
		BreadthFirst.rc=rcIn;//slimmed down init
		//MapLocation goal = getRandomLocation();
		//path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
		//VectorFunctions.printPath(path,bigBoxSize);
		while(true){
			try{
				if (rc.isActive())
				{
					if (rc.sensePastrLocations(rc.getTeam()).length < 2)
					{
						rc.construct(RobotType.PASTR);
						rc.setIndicatorString(0, ""+rc.sensePastrLocations(rc.getTeam()).length);
					}
					else
					{
						runSoldier();
					}
				}
			}catch (Exception e){
				rc.setIndicatorString(0, "dumb error 1");
				e.printStackTrace();
			}
			rc.yield();
		}
	}

	private static void runSoldier() throws GameActionException {
		//follow orders from HQ
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam().opponent());
		if(enemyRobots.length>0){//SHOOT AT, OR RUN TOWARDS, ENEMIES
			MapLocation[] robotLocations = VectorFunctions.robotsToLocations(enemyRobots, rc);
			MapLocation closestEnemyLoc = VectorFunctions.findClosest(robotLocations, rc.getLocation());
			if (closestEnemyLoc != null)
			{
				if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<rc.getType().attackRadiusMaxSquared){//close enough to shoot
					if(rc.isActive()){
						rc.attackSquare(closestEnemyLoc);
					}
			}
			}else{//not close enough to shoot, so try to go shoot
				Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
				simpleMove(towardClosest);
			}
		}else{//NAVIGATION BY DOWNLOADED PATH
			rc.setIndicatorString(0, "team "+myBand+", path length "+path.size());
			if(path.size()<=1){
				//check if a new path is available
				int broadcastCreatedRound = rc.readBroadcast(myBand);
				if(pathCreatedRound<broadcastCreatedRound){
					rc.setIndicatorString(1, "downloading path");
					pathCreatedRound = broadcastCreatedRound;
					path = Comms.downloadPath();
				}
			}
			if(path.size()>0){
				//follow breadthFirst path
				Direction bdir = BreadthFirst.getNextDirection(path, DataCache.bigBoxSize);
				BasicPathing.tryToMove(bdir, true, rc);
			}
		}
		//Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		//BasicPathing.tryToMove(towardEnemy, true, rc, directionalLooks, allDirections);//was Direction.SOUTH_EAST

		//Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		//simpleMove(towardEnemy);

	}

	private static MapLocation getRandomLocation() {
		return new MapLocation(randall.nextInt(rc.getMapWidth()),randall.nextInt(rc.getMapHeight()));
	}

	private static void simpleMove(Direction desiredDir) throws GameActionException{
		if (rc.isActive())
		{
			// try different directional offsets in case the desired direction does not work
			for (int tryOffset: DataCache.dirOffsets)
			{
				int desiredInt = desiredDir.ordinal();
				Direction tryDir = DataCache.dirValues[(desiredInt + tryOffset + 8) % 8];
				if(rc.canMove(tryDir))
				{
					rc.move(tryDir);
					break;
				}
			}
		}
	}
}