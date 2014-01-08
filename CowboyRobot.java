package teamxxx;

import battlecode.common.*;

public class CowboyRobot extends BasicRobot
{
	public RobotController rc;
	public CowboyRobot() throws GameActionException
	{
	}
	public void run(RobotController myRC) // for hunter robots
	{
		rc = myRC;
		try
		{
			// shoot at the closest pasture in shooting range that has the lowest health
			Robot[] enemyRobotsCanShoot = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
			if (enemyRobotsCanShoot.length != 0)
			{
				for (Robot enemyRobot: enemyRobotsCanShoot)
				{
					RobotInfo robotInfo = rc.senseRobotInfo(enemyRobot);
					rc.attackSquare(robotInfo.location);
				}
			}
			// always look for pastures
			MapLocation[] enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
			if (enemyPastures.length != 0)
			{
				int closestEnemyPastureDistance = 1000000;
				MapLocation closestEnemyPasture = null;
				// go towards nearest pasture
				// should add code to find second nearest if nearest will be destroyed by the time we get there
				for (MapLocation enemyPasture: enemyPastures)
				{
					if (enemyPasture.distanceSquaredTo(rc.getLocation()) < closestEnemyPastureDistance)
					{
						closestEnemyPasture = enemyPasture;
						closestEnemyPastureDistance = enemyPasture.distanceSquaredTo(rc.getLocation());
					}
				}
				// need code to try to move in adjacent directions
				tryToMove(rc.getLocation().directionTo(closestEnemyPasture));
			}
			// move towards enemy HQ if no other directions
			else
			{
				tryToMove(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
			}
		}
		catch (Exception e)
		{
		}
	}
	public void tryToMove(Direction desiredDirection) throws GameActionException
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
				rc.move(desiredDirection.rotateLeft());
			}
			else if (rc.canMove(desiredDirection.rotateRight()))
			{
				rc.move(desiredDirection.rotateRight());
			}
		}
	}
}