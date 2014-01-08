package teamxxx;

import battlecode.common.*;

public class HQRobot extends BasicRobot
{
	public RobotController rc;
	public HQRobot() throws GameActionException
	{
	}
	public void run(RobotController myRC)
	{
		rc = myRC;
		try
		{
			Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			canSpawn(); // spawn robots when possible
			// sense broadcasting robots and direct robots to go there
			Robot[] enemyBroadcastingRobots = rc.senseBroadcastingRobots(rc.getTeam().opponent());
		}
		catch (Exception e)
		{
		}
		rc.yield();
	}
	public void canSpawn() throws GameActionException
	{
		//Check if a robot is spawnable and spawn one if it is
		if (rc.isActive() && rc.senseRobotCount() < GameConstants.MAX_ROBOTS)
		{
			Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			rc.spawn(toEnemy);
			/*
			if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null)
			{
				rc.spawn(toEnemy);
			}
			*/
		}
	}
	public void findEnemyPastures()
	{
		// print out locations of enemy pastures to communication network
		// have cowboyCode draw from this repository, this probably saves code
	}
}