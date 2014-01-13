package team089;

import battlecode.common.RobotController;

public class RobotPlayer
{
	public static RobotController rc;
	public static void run(RobotController myRC)
	{
		rc = myRC;
		BasicRobot robot = null;
		while(true)
		{
			try
			{
				switch (rc.getType())
				{
				case SOLDIER:
					robot = new CowboyRobot(rc);
					break;
				case PASTR:
					robot = new PastureRobot(rc);
					break;
				case NOISETOWER:
					robot = new TowerRobot(rc);
					break;
				case HQ:
					robot = new HQRobot(rc);
					break;
				default:
					break;
				}
				robot.run(rc);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
