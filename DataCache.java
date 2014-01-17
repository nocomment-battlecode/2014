package team089;

import battlecode.common.*;

public class DataCache
{
	public static BasicRobot robot;
	public static RobotController rc;
	public static Direction dirValues[] = Direction.values();
	public static int dirOffsets[] = new int[]{0,1,-1,2,-2,3,-3,4};
	public static int mapBase = Math.max(GameConstants.MAP_MAX_HEIGHT,GameConstants.MAP_MAX_WIDTH)+1;
	public static int coarseWidth;
	public static int coarseHeight;
	public static int maxPathLength = 100;
	public static int numCowboys;
	public static int numTowers;
	public static int numPastures;
	public static int numRobots = numCowboys + 2 * numPastures + 3 * numTowers;
	public static MapLocation HQLoc;
	public static MapLocation enemyHQLoc;
	public static MapLocation rallyPoint;
	public static MapLocation targetedPastr;
	public static MapLocation selfLoc;
	public static Direction enemyHQDir;
	public static int HQShootRangeSq = 15;
	public static int bigBoxSize = 4;
	public static int trailLength = 2;
	public static boolean makePastures = false;
	public static MapLocation[] enemyPastureLocs;
	public static RobotInfo[] allyInfo;
	public static Robot[] allyRobots;
	
	public static void initialize(BasicRobot myRobot)
	{
		robot = myRobot;
		rc = robot.rc;
		selfLoc = rc.getLocation();
		HQLoc = rc.senseHQLocation();
		enemyHQLoc = rc.senseEnemyHQLocation();
		enemyHQDir = HQLoc.directionTo(enemyHQLoc);
		coarseWidth = rc.getMapWidth()/bigBoxSize;
		coarseHeight = rc.getMapHeight()/bigBoxSize;
	}
	public static void updateVariables() throws GameActionException
	{
		selfLoc = rc.getLocation();
		allyRobots = rc.senseNearbyGameObjects(Robot.class, 10000, rc.getTeam());
		allyInfo = new RobotInfo[allyRobots.length];
		for (int i = 0; i < allyRobots.length; i++)
		{
			allyInfo[i] = rc.senseRobotInfo(allyRobots[i]);
			if (allyInfo[i].type == RobotType.SOLDIER)
			{
				numCowboys++;
			}
			else if (allyInfo[i].type == RobotType.PASTR)
			{
				numPastures++;
			}
			else if (allyInfo[i].type == RobotType.NOISETOWER)
			{
				numTowers++;
			}
		}
	}
}