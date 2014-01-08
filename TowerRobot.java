package examplefuncsplayer;

import battlecode.common.*;

public class TowerRobot
{
	public RobotController rc;
	public void towerCode()
	{
		rc.yield();
	}
	// noise tower attack
	public void towerAttack(MapLocation attackLocation)
	{
		// code to get attacklocation
		if ()
		{
			rc.attack(attackLocation);
		}
		else
		{
			rc.attackSquareLight(attackLocation);
		}
	}
}