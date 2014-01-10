package teamxxx;

import battlecode.common.*;
import java.util.ArrayList;

public class TowerRobot extends BasicRobot
{
	public RobotController rc;

	public ArrayList<MapLocation> works = new ArrayList<MapLocation>();
	ArrayList<MapLocation> attackPossibilities = new ArrayList<MapLocation>();
	static int tryCount = 0;
	static int delayCount = 0;

	public int[] diffsX = {-18, -15, -12, -9, -3, 3, 9, 12, 15, 18, 15, 12, 9, 3, -3, -9, -12, -15, -12, -9, -6, 0, 6, 9, 12, 9, 6, 0, -6, -9, -6, -3, 3, 6, 3, -3};
	public int[] diffsY = {0, 5, 10, 15, 15, 15, 15, 10, 5, 0, -5, -10, -15, -15, -15, -15, -10, -5, 0, 5, 10, 10, 10, 5, 0, -5, -10, -10, -10, -5, 0, 5, 5, 0, -5, -5};
/*
	if (Team.opponent() == Team A)
	{
		public Team team = Team.B;
	}
	else
	{
		Team team = Team.A;
	}
*/
	public TowerRobot() throws GameActionException
	{
	}
	public void run(RobotController myRC)
	{
		rc = myRC;
		try
		{
			rc.setIndicatorString(1, "got into loop");
			if (rc.isActive())
			{
				rc.setIndicatorString(1, "active");
				findClosePastrs();
				rc.setIndicatorString(1, "found pastrs");
				attackPossibilities.clear();
				for (int i = 0; i < works.size(); i++)
				{
					for (int j = 0; j < 36; j++)
					{
						attackPossibilities.add(works.get(i).add(diffsX[j], diffsY[j]));
					}
				}
				ArrayList<MapLocation> attackPossInRange = filterInRange(attackPossibilities);
				rc.setIndicatorString(0, String.valueOf(tryCount) + " " + String.valueOf(attackPossInRange.size()));
				if (tryCount > attackPossInRange.size() - 1)
					tryCount = 0;
				if (delayCount > 5)
				{
					tryCount++;
					delayCount = 0;
				}
				MapLocation attackLocation = attackPossInRange.get(tryCount);
				rc.setIndicatorString(1, "attacking");
				//rc.setIndicatorString(0, String.valueOf(tryCount) + " " + String.valueOf(attackPossInRange.size()));
				rc.attackSquare(attackLocation);
				delayCount++;
				
			}
		}
		catch (Exception e)
		{
			rc.setIndicatorString(0, "dumb error 1");
		}
		rc.yield();
	}

	// filters an ArrayList of MapLocation for being within range of the noisetower
	public ArrayList<MapLocation> filterInRange(ArrayList<MapLocation> locs)
	{
		try
		{
			rc.setIndicatorString(2, "filtering");
			ArrayList<MapLocation> toReturn = new ArrayList<MapLocation>();
			MapLocation myLoc = rc.getLocation();
			for (int i = 0; i < locs.size(); i++)
			{
				if (locs.get(i).distanceSquaredTo(myLoc) <= 400 && rc.senseTerrainTile(locs.get(i)) != TerrainTile.OFF_MAP)
					toReturn.add(locs.get(i));
			}
			return toReturn;
		}
		catch (Exception e)
		{
			rc.setIndicatorString(0, "dumb error 2");
		}
		return null;
	}
	// find close pastures that are yours (returns arraylist of MapLocation)
	public void findClosePastrs()
	{
		try
		{
			if (true)
			{
				rc.setIndicatorString(2, "finding pastrs");
				ArrayList<MapLocation> pastrs = new ArrayList<MapLocation>();
				MapLocation[] pastrArray = rc.sensePastrLocations(rc.getTeam());
				for (int i = 0; i < pastrArray.length; i++)
				{
					pastrs.add(pastrArray[i]);
				}
				works = filterInRange(pastrs);
			}
		}
		catch (Exception e)
		{
			rc.setIndicatorString(0, "dumb error 3");
		}
	}
	// noise tower attack
	public void towerAttack(MapLocation attackLocation)
	{
		try
		{
			rc.setIndicatorString(2, "currently attacking");
			// code to get attacklocation
			if (true)
			{
				if (attackLocation.x > 0 && attackLocation.x < GameConstants.MAP_MAX_WIDTH && attackLocation.y > 0 && attackLocation.y < GameConstants.MAP_MAX_HEIGHT)
					rc.attackSquare(attackLocation);
			}
			else
			{
				rc.attackSquareLight(attackLocation);
			}
		}
		catch (Exception e)
		{
			rc.setIndicatorString(0, "dumb error 4");
		}
	}
}