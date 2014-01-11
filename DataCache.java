package teamxxx;

import battlecode.common.*;

public class DataCache
{
	public static BasicRobot robot;
	public static RobotController rc;
	public static Direction dirValues[] = Direction.values();
	public static int dirOffsets[] = new int[]{0,1,-1,2,-2,3,-3,4};
	public static int numCowboys = 0;
	public static int numTowers = 0;
	public static int numPastures = 0;
	public static int numRobots = numCowboys + 2 * numPastures + 3 * numTowers;
	public static MapLocation HQLoc;
	public static MapLocation enemyHQLoc;
	public static Direction enemyHQDir;
	public static int HQShootRangeSq = 15;
	public static MapLocation[] enemyPastureLocs;
	
	public static void initialize(BasicRobot myRobot)
	{
		robot = myRobot;
		rc = robot.rc;
		HQLoc = rc.senseHQLocation();
		enemyHQLoc = rc.senseEnemyHQLocation();
		enemyHQDir = HQLoc.directionTo(enemyHQLoc);
	}
}