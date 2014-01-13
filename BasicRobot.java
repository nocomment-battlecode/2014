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
	abstract public void run(RobotController myRC);
}