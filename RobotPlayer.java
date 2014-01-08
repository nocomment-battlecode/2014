package examplefuncsplayer;

import battlecode.common.*;
import java.util.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		while(true)
		{
			try
			{
				switch (rc.getType())
				{
				case RobotType.HQ:
					hqCode();
					break;
				case RobotType.SOLDIER:
					cowboyCode();
					break;
				case RobotType.NOISETOWER:
					towerCode();
					break;
				case RobotType.PASTR:
					pastureCode();
					break;
				default:
					break;
				}
			}
			catch(Exception e)
			{
			}
		}
	}
	
	// store locations in hash grid
	
	
	
	
	
}
