package team089;

import battlecode.common.*;

public class DataCache
{
	public static BasicRobot robot;
	public static RobotController rc;
	public static Direction dirValues[] = Direction.values();
	public static int dirOffsets[] = new int[]{0,1,-1,2,-2,3,-3,4};
	public static int numCowboys;
	public static int numTowers;
	public static int numPastures;
	public static int numRobots = numCowboys + 2 * numPastures + 3 * numTowers;
	public static MapLocation HQLoc;
	public static MapLocation enemyHQLoc;
	public static MapLocation rallyPoint;
	public static MapLocation targetedPastr;
	public static Direction enemyHQDir;
	public static int HQShootRangeSq = 15;
	public static int bigBoxSize = 5;
	public static boolean makePastures = false;
	public static MapLocation[] enemyPastureLocs;
	public static RobotInfo[] allyInfo;
	public static Robot[] allyRobots;
	
	public static void initialize(BasicRobot myRobot)
	{
		robot = myRobot;
		rc = robot.rc;
		HQLoc = rc.senseHQLocation();
		enemyHQLoc = rc.senseEnemyHQLocation();
		enemyHQDir = HQLoc.directionTo(enemyHQLoc);
	}
	public static void updateVariables() throws GameActionException
	{
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