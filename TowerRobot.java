package teamxxx;

import battlecode.common.*;

public class TowerRobot extends BasicRobot
{
	public RobotController rc;
	public TowerRobot() throws GameActionException
	{
	}
	public void run(RobotController myRC)
	{
		rc.yield();
	}
	// noise tower attack
	public void towerAttack(MapLocation attackLocation)
	{
		try
		{
		// code to get attacklocation
		if (true)
		{
			rc.attackSquare(attackLocation);
		}
		else
		{
			rc.attackSquareLight(attackLocation);
		}
		}
		catch (Exception e)
		{
			
		}
	}
}