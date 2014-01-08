package examplefuncsplayer;

import battlecode.common.*;

public class CowboyRobot
{
	public RobotController rc;
	public void cowboyCode() // for hunter robots
	{
		// shoot at the closest pasture in shooting range that has the lowest health
		Robot[] enemyRobotsCanShoot = rc.senseNearbyGameObjects(Robot.class, , rc.getTeam().opponent());
		if (enemyRobotsCanShoot.length != 0)
		{
			for (enemyRobot: enemyRobotsCanShoot)
			{
				if (RobotInfo robotInfo = rc.senseRobotInfo(nearbyEnemies[0]);
				rc.attackSquare(robotInfo.location);)
			}
		}
		// always look for pastures
		MapLocation[] enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
		if (enemyPastures.length != 0)
		{
			int closestEnemyPastureDistance = 1000000;
			MapLocation closestEnemyPasture;
			// go towards nearest pasture
			// should add code to find second nearest if nearest will be destroyed by the time we get there
			for (enemyPasture: enemyPastures)
			{
				if (enemyPasture.distanceSquaredTo(rc.getLocation) < closestEnemyPasture)
				{
					closestEnemyPasture = enemyPasture;
					closestEnemyPastureDistance = enemyPasture.distanceSquaredTo(rc.getLocation);
				}
			}
			// need code to try to move in adjacent directions
			tryToMove(rc.getLocation.directionTo(closestEnemyPasture));
		}
		// move towards enemy HQ if no other directions
		else
		{
			tryToMove(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
		}
		rc.yield();
	}
	public void tryToMove(Direction desiredDirection)
	{
		// this should try to move in the desired direction, and when it can't it should try directions close to it
		// should replace with actual navigation code later
		if (rc.canMove(desiredDirection))
		{
			rc.move(desiredDirection);
		}
		else
		{
			if (rc.canMove(desiredDirection.rotateLeft()))
			{
				rc.move(desiredDirection.rotateLeft())
			}
			else if (rc.canMove(desiredDirection.rotateRight()))
			{
				rc.move(desiredDirection.rotateRight())
			}
		}
	}
}