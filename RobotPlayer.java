package teamxxx;

import battlecode.common.*;
import java.util.Random;
import java.util.ArrayList;

public class RobotPlayer
{
	static RobotController rc;
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
				e.printStackTrace();
			}
		}
	}
}
