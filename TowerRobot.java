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
		rc = myRC;
		try
		{
			MapLocation attackLocation = new MapLocation(rc.getLocation().x + 10, rc.getLocation().y + 10);
			rc.attackSquare(attackLocation);
		}
		catch (Exception e)
		{
			rc.setIndicatorString(0,"error1");
		}
		rc.yield();
	}
	// noise tower attack
	public void towerAttack(MapLocation attackLocation)
	{
		try
		{
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
			rc.setIndicatorString(0,"error2");
		}
	}
}