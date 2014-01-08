package teamxxx;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		BasicRobot robot = null;
		while(true)
		{
			try
			{
				switch (rc.getType())
				{
				case HQ:
					robot = new HQRobot();
					break;
				case SOLDIER:
					robot = new CowboyRobot();
					break;
				case NOISETOWER:
					robot = new TowerRobot();
					break;
				case PASTR:
					robot = new PastureRobot();
					break;
				default:
					break;
				}
				robot.run(rc);
			}
			catch(Exception e)
			{
			}
		}
	}
}
