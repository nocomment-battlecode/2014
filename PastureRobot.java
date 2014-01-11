package teamxxx;

import battlecode.common.*;

public class PastureRobot extends BasicRobot
{
	public RobotController rc;
	public PastureRobot(RobotController myRC) throws GameActionException
	{
		super(myRC);
	}
	public void run(RobotController myRC)
	{
		rc.yield();
	}
	public void pastureCode()
	{
		// make pastures do computations here, like which places are optimal to herd
	}
}