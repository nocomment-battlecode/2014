package teamxxx;

import battlecode.common.*;

public class HQRobot extends BasicRobot
{
	public RobotController rc;
	public Robot[] enemyRobots;

	public HQRobot(RobotController myRC) throws GameActionException
	{
		super(myRC);
	}

	public void run(RobotController myRC)
	{
		rc = myRC;
		try
		{
			// update enemy pasture locations
			DataCache.enemyPastureLocs = rc.sensePastrLocations(rc.getTeam().opponent());
			tryToSpawn(DataCache.enemyHQDir); // spawn robots when possible
			// attack enemy robots when they are in range
			Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, DataCache.HQShootRangeSq, rc.getTeam().opponent());
			if (enemyRobots.length > 0)
			{
				attack();
			}
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
		}
		rc.yield();
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
		MapLocation[] enemyLocs = new MapLocation[enemyRobots.length];
		for(int i=0; i < enemyRobots.length; i++)
		{
			Robot enemyRobot = enemyRobots[i];
			RobotInfo enemyInfo = rc.senseRobotInfo(enemyRobot);
			enemyLocs[i] = enemyInfo.location;
		}
		MapLocation closestEnemyLoc = VectorFunctions.findClosest(enemyLocs, rc.getLocation());
		rc.attackSquare(closestEnemyLoc);
	}

	public void findEnemyPastures()
	{
		// print out locations of enemy pastures to communication network
		// have cowboyCode draw from this repository, this probably saves code
	}
}