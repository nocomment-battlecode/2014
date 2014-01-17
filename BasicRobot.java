package team089;

import battlecode.common.RobotController;

public abstract class BasicRobot
{
	public RobotController rc;
	public BasicRobot(RobotController myRC)
	{
		rc = myRC;
		DataCache.initialize(this);
	}
	public abstract void run(RobotController myRC);
}