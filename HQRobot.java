package team089;

import battlecode.common.*;
import java.util.Random;

public class HQRobot extends BasicRobot
{
	public RobotController rc;
	static Random randall = new Random();

	public HQRobot(RobotController myRC) throws GameActionException
	{
		super(myRC);
	}

	public void run(RobotController myRC)
	{
		rc = myRC;
		try
		{
			Comms.rc = myRC;
			randall.setSeed(rc.getRobot().getID());
			rc.broadcast(101,VectorFunctions.locToInt(VectorFunctions.mldivide(rc.senseHQLocation(),DataCache.bigBoxSize)));//this tells soldiers to stay near HQ to start
			rc.broadcast(102,-1);//and to remain in squad 1
			tryToSpawn(DataCache.enemyHQDir);
			BreadthFirst.init(rc);
			DataCache.rallyPoint = VectorFunctions.mladd(VectorFunctions.mldivide(VectorFunctions.mlsubtract(rc.senseEnemyHQLocation(),rc.senseHQLocation()),3),rc.senseHQLocation());
			while(true){
				try{
					runHQ();
				}catch (Exception e){
					rc.setIndicatorString(0, "dumb error 1");
					//e.printStackTrace();
				}
				rc.yield();
			}			

			/*// update enemy pasture locations
			DataCache.enemyPastureLocs = rc.sensePastrLocations(rc.getTeam().opponent());
			DataCache.numPastures = rc.sensePastrLocations(rc.getTeam()).length;
			tryToSpawn(DataCache.enemyHQDir); // spawn robots when possible
			// attack enemy robots when they are in range
			Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, DataCache.HQShootRangeSq, rc.getTeam().opponent());
			if (enemyRobots.length > 0)
			{
				attack();
			}*/
			/*			MapLocation[] enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
			if (enemyPastures.length != 0)
			{
				rc.setIndicatorString(0, "x: " + enemyPastures[0].x + "y: " + enemyPastures[0].y);
			}*/
			// sense broadcasting robots and direct robots to go there
			//Robot[] enemyBroadcastingRobots = rc.senseBroadcastingRobots(rc.getTeam().opponent());
		}
		catch (Exception e)
		{
			rc.setIndicatorString(0, "dumb error 2");
		}
		rc.yield();
	}

	private void runHQ() throws GameActionException
	{
		DataCache.updateVariables();
		//TODO consider updating the rally point to an allied pastr 
		Robot[] alliedRobots = rc.senseNearbyGameObjects(Robot.class,100000000,rc.getTeam());
		if(Clock.getRoundNum()>400 && alliedRobots.length<5){//call a retreat
			MapLocation startPoint = findAverageAllyLocation(alliedRobots);
			Comms.findPathAndBroadcast(2,startPoint,rc.senseHQLocation(),DataCache.bigBoxSize,2);
			DataCache.rallyPoint = rc.senseHQLocation();
		}else{//not retreating
			//tell them to go to the rally point
			Comms.findPathAndBroadcast(1,rc.getLocation(),DataCache.rallyPoint,DataCache.bigBoxSize,2);
			//if the enemy builds a pastr, tell sqaud 2 to go there.
			MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
			if(enemyPastrs.length>0){
				MapLocation startPoint = findAverageAllyLocation(alliedRobots);
				DataCache.targetedPastr = getNextTargetPastr(enemyPastrs,startPoint);
				//broadcast it
				Comms.findPathAndBroadcast(2,startPoint,DataCache.targetedPastr,DataCache.bigBoxSize,2);
			}
		}
		//consider attacking
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam().opponent());
		if(rc.isActive()&&enemyRobots.length>0){
			MapLocation[] enemyRobotLocations = VectorFunctions.robotsToLocations(enemyRobots, true);
			MapLocation closestEnemyLoc = VectorFunctions.findClosest(enemyRobotLocations, rc.getLocation());
			if(rc.canAttackSquare(closestEnemyLoc))
				rc.attackSquare(closestEnemyLoc);
		}

		//after telling them where to go, consider spawning
		tryToSpawn(DataCache.enemyHQDir);
	}
	
	private MapLocation findAverageAllyLocation(Robot[] alliedRobots) throws GameActionException {
		//find average soldier location
		MapLocation[] alliedRobotLocations = VectorFunctions.robotsToLocations(alliedRobots, true);
		MapLocation startPoint;
		if(alliedRobotLocations.length>0){
			startPoint = VectorFunctions.meanLocation(alliedRobotLocations);
			if(Clock.getRoundNum()%100==0)//update rally point from time to time
				DataCache.rallyPoint=startPoint;
		}else{
			startPoint = rc.senseHQLocation();
		}
		return startPoint;
	}

	private static MapLocation getNextTargetPastr(MapLocation[] enemyPastrs,MapLocation startPoint) {
		if(enemyPastrs.length==0)
			return null;
		if(DataCache.targetedPastr!=null){//a targeted pastr already exists
			for(MapLocation m:enemyPastrs){//look for it among the sensed pastrs
				if(m.equals(DataCache.targetedPastr)){
					return DataCache.targetedPastr;
				}
			}
		}//if the targeted pastr has been destroyed, then get a new one
		return VectorFunctions.findClosest(enemyPastrs, startPoint);
	}

	//Check if a robot is spawnable in the desired direction and spawn one if it is
	public void tryToSpawn(Direction desiredDir) throws GameActionException
	{
		if (rc.isActive() && DataCache.numRobots < GameConstants.MAX_ROBOTS)
		{
			// try different directional offsets in case the desired direction does not work
			for (int tryOffset: DataCache.dirOffsets)
			{
				int desiredInt = desiredDir.ordinal();
				Direction tryDir = DataCache.dirValues[(desiredInt + tryOffset + 8) % 8];
				if(rc.canMove(tryDir))
				{
					rc.spawn(tryDir);
					break;
				}
			}
		}
	}

	// attack the closest enemy robot, will code in a better heuristic later (most net damage = damage to enemy - damage to self)
	public void attack() throws GameActionException
	{
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().attackRadiusMaxSquared,rc.getTeam().opponent());
		if(enemyRobots.length>0){//SHOOT AT, OR RUN TOWARDS, ENEMIES
			MapLocation[] robotLocations = VectorFunctions.robotsToLocations(enemyRobots, true);
			MapLocation closestEnemyLoc = VectorFunctions.findClosest(robotLocations, rc.getLocation());
			if (closestEnemyLoc != null)
			{
				rc.attackSquare(closestEnemyLoc);
			}
		}
	}

	public void findEnemyPastures()
	{
		// print out locations of enemy pastures to communication network
		// have cowboyCode draw from this repository, this probably saves code
	}
}