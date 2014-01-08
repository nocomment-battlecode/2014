package teamxxx;

import battlecode.common.*;

public class LocationHash
{
	private static final int HASH = Math.max(GameConstants.MAP_MAX_WIDTH, GameConstants.MAP_MAX_HEIGHT);
	private boolean[][] has = new boolean[HASH][HASH];
	public void add(MapLocation location)
	{
		int x = location.x % HASH;
		int y = location.y % HASH;
		if (!has[x][y])
		{
			has[x][y] = true;
		}
	}
	public void remove(MapLocation location)
	{
		int x = location.x % HASH;
		int y = location.y % HASH;
		if (has[x][y]){
			has[x][y] = false;
		}
	}
	public boolean contains(MapLocation location)
	{
		return has[location.x % HASH][location.y % HASH];
	}
}