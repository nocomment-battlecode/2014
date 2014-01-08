package examplefuncsplayer;

import battlecode.common.*;

public class HQRobot {
	public RobotController rc;
	public void hqCode()
	{
		try
		{
			spawn(); // spawn robots when possible
			// sense broadcasting robots and direct robots to go there
			Robot[] enemyBroadcastingRobots = rc.senseBroadcastingRobots(rc.getTeam().opponent());
			// store in quick access hash array
			rc.yield();
		}
		catch (Exception e)
		{
		}
	}
	public void spawn() throws GameActionException
	{
		//Check if a robot is spawnable and spawn one if it is
		if (rc.isActive() && rc.senseRobotCount() < GameConstants.MAX_ROBOTS) {
			Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null)
			{
				rc.spawn(toEnemy);
			}
		}
	}
	public void findEnemyPastures()
	{
		// print out locations of enemy pastures to communication network
		// have cowboyCode draw from this repository, this probably saves code
	}
}
