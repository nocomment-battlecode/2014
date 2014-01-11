package teamxxx;

import battlecode.common.*;
import java.util.*;

public class CowboyRobotOri extends BasicRobot
{
	public RobotController rc;
	public boolean hugLeft = false, goneAround = false;
	public boolean[][][] BLOCK_DIRS;
	public int[] prohibitedDirs = {-1,-1};
	public MapLocation startLoc;
	public Direction startDesiredDir;
	public enum STATE {BUGGING, FLOCKING};
	public STATE state = STATE.FLOCKING;
	public CowboyRobotOri() throws GameActionException
	{
	}
	public void run(RobotController myRC) // for hunter robots
	{
		rc = myRC;
		try
		{
			//findShootingTarget();
			//findEnemyPastures();
			rc.move(getNextDir(rc.senseEnemyHQLocation()));
			// tryToMove(rc.getLocation().directionTo(rc.senseEnemyHQLocation()));
		}
		catch (Exception e)
		{
		}
		rc.yield();
	}
	public void tryToMove(Direction desiredDirection) throws GameActionException
	{
		// this should try to move in the desired direction, and when it can't it should try directions close to it
		// should replace with actual navigation code later
		if (rc.canMove(desiredDirection))
		{
			rc.move(desiredDirection);
		}
		else
		{
			if (rc.canMove(desiredDirection.rotateLeft()))
			{
				rc.move(desiredDirection.rotateLeft());
			}
			else if (rc.canMove(desiredDirection.rotateRight()))
			{
				rc.move(desiredDirection.rotateRight());
			}
		}
	}
	public void findShootingTarget() throws GameActionException
	{
		// shoot at the closest pasture in shooting range that has the lowest health
		Robot[] enemyRobotsCanShoot = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
		if (enemyRobotsCanShoot.length != 0)
		{
			for (Robot enemyRobot: enemyRobotsCanShoot)
			{
				RobotInfo robotInfo = rc.senseRobotInfo(enemyRobot);
				rc.attackSquare(robotInfo.location);
			}
		}
	}
	public void findEnemyPastures() throws GameActionException
	{
		// always look for pastures
		MapLocation[] enemyPastures = rc.sensePastrLocations(rc.getTeam().opponent());
		if (enemyPastures.length != 0)
		{
			int closestEnemyPastureDistance = 1000000;
			MapLocation closestEnemyPasture = null;
			// go towards nearest pasture
			// should add code to find second nearest if nearest will be destroyed by the time we get there
			for (MapLocation enemyPasture: enemyPastures)
			{
				if (enemyPasture.distanceSquaredTo(rc.getLocation()) < closestEnemyPastureDistance)
				{
					closestEnemyPasture = enemyPasture;
					closestEnemyPastureDistance = enemyPasture.distanceSquaredTo(rc.getLocation());
				}
			}
			// need code to try to move in adjacent directions
			tryToMove(rc.getLocation().directionTo(closestEnemyPasture));
		}
	}

	// bugging code to get direction that we should move in next
	public Direction getNextDir(MapLocation target)
	{
		Direction desiredDir = rc.getLocation().directionTo(target);
		// If we are here don't do anything
		if (desiredDir == Direction.NONE || desiredDir == Direction.OMNI)
		{
			rc.setIndicatorString(0, desiredDir.toString());
			return desiredDir;
		}
		// If we are bugging around an object, see if we have gotten past it
		if (state == STATE.BUGGING)
		{
			// If we are closer to the target than when we started, and we can
			// move in the ideal direction, then we are past the object
			// change to canMove later
			if (rc.getLocation().distanceSquaredTo(target) < startLoc.distanceSquaredTo(target) && rc.canMove(desiredDir))
			{
				prohibitedDirs = new int[] {-1,-1};
				goneAround = false;
				state = STATE.FLOCKING;
			}
		}
		switch (state)
		{
		case FLOCKING:
			Direction newDir = flockInDir(desiredDir, target);
			if (newDir != null)
			{
				return newDir;
			}
			state = STATE.BUGGING;
			startLoc = rc.getLocation();
			startDesiredDir = desiredDir;
			// intentional fallthrough
		case BUGGING:
			// rc.setIndicatorString(0, "bugging");
			/*if (goneAround && (desiredDir == startDesiredDir.rotateLeft().rotateLeft() || desiredDir == startDesiredDir.rotateRight().rotateRight()))
			{
				prohibitedDirs[0] = -1;
			}
			if (desiredDir == startDesiredDir.opposite())
			{
				prohibitedDirs[0] = -1;
				goneAround = true;
			}*/
			Direction moveDir = hug(desiredDir, false);
			if (moveDir == null)
			{
				moveDir = desiredDir;
			}
			//rc.setIndicatorString(0, desiredDir.toString());
			return moveDir;
		}
		return null;
	}

	//take the three directions closest to your ideal direction, order them in priority order, and check to see if you can move in any of those directions.
	// If so, you return that direction and continue on your merry way. If not, we proceed to the next part of the getNextMove() function.
	public Direction flockInDir(Direction desiredDir, MapLocation target)
	{
		Direction[] directions = new Direction[3];
		directions[0] = desiredDir;
		Direction left = desiredDir.rotateLeft();
		Direction right = desiredDir.rotateRight();
		boolean leftIsBetter = (rc.getLocation().add(left).distanceSquaredTo(target) < rc.getLocation().add(right).distanceSquaredTo(target));
		directions[1] = (leftIsBetter ? left : right);
		directions[2] = (leftIsBetter ? right : left);
		for (int i = 0; i < 3; i++)
		{
			// change this to canMove later
			if (rc.canMove(directions[i]))
			{
				return directions[i];
			}
		}
		return null;
	}

/*	public Direction hug(Direction desiredDir, MapLocation target)
	{
		if (rc.canMove(desiredDir))
		{
			return desiredDir;
		}
		
		boolean leftIsBetter = (rc.getLocation().add(left).distanceSquaredTo(target) < rc.getLocation().add(right).distanceSquaredTo(target));
	}*/
	
	private Direction hug (Direction desiredDir, boolean recursed)
	{
		//
		if (canMove(desiredDir))
		{
			return desiredDir;
		}
		Direction tryDir = turn(desiredDir);
		MapLocation tryLoc = rc.getLocation().add(tryDir);
		for (int i = 0; i < 8 && !canMove(tryDir) && (rc.senseTerrainTile(tryLoc) == TerrainTile.NORMAL || rc.senseTerrainTile(tryLoc) == TerrainTile.ROAD); i++)
		{
			tryDir = turn(tryDir);
			tryLoc = rc.getLocation().add(tryDir);
		}
		// If the loop failed (found no directions or encountered the map edge)
		if (!canMove(tryDir) || (rc.senseTerrainTile(tryLoc) == TerrainTile.OFF_MAP || rc.senseTerrainTile(tryLoc) == TerrainTile.VOID))
		{
			hugLeft = !hugLeft;
			if (recursed)
			{
				// We've tried hugging in both directions...
				if (prohibitedDirs[0] != -1 && prohibitedDirs[1] != -1)
				{
					// We were prohibiting certain directions before.
					// try again allowing those directions
					prohibitedDirs[1] = -1;
					return hug(desiredDir, false);
				}
				else
				{
					// Complete failure. Reset the state and start over.
					return null;
				}
			}
			// mark recursed as true and try hugging the other direction
			return hug(desiredDir, true);
		}
		// If we're moving in a new cardinal direction, store it.
		if (!tryDir.isDiagonal())
		{
			if (turn(turn(Direction.values()[prohibitedDirs[0]])) == tryDir)
			{
				prohibitedDirs[0] = tryDir.opposite().ordinal();
				prohibitedDirs[1] = -1;
			}
			else
			{
				prohibitedDirs[1] = prohibitedDirs[0];
				prohibitedDirs[0] = tryDir.opposite().ordinal();
			}
		}
		return tryDir;
	}
	private boolean canMove(Direction dir)
	{
		if (BLOCK_DIRS[prohibitedDirs[0]][prohibitedDirs[1]][dir.ordinal()])
		{
			return false;
		}
		if (canMove(dir))
		{
			return true;
		}
		return false;
	}

	private Direction turn(Direction dir)
	{
		return (hugLeft ? dir.rotateRight() : dir.rotateLeft());
	}
	
	// Okay, makes sense. But what's that stuff with myProhibitedDirs at the end?
	//Okay, well to make sure that we don't end up retracing our steps, we store the past 2 directions we have come from and make sure we don't go in those directions.
	//MyProhibitedDirs is an int array of length 2, and stores two direction ordinals. The way it is used is in the canMove() function.

	// Okay, so what's going on here? Well, BLOCK_DIRS is a boolean[][][] array that is initialized in the constructor.
	// Essentially, it's a lookup table that allows us to very quickly say,
	// Given that I was moving from direction myProhibitedDirs[1], and then from direction myProhibitedDirs[0], should I block movement in direction dir?
	// The boolean value in the array is the answer to that question. Here is how we calculate BLOCK_DIRS.
	//Remember, a value of true means that we will not allow movement in that direction.
	void initializeBlockedDirs()
	{
		for (Direction d: Direction.values())
		{
			if (d == Direction.NONE || d == Direction.OMNI)
				continue;
			for (Direction b: Direction.values())
			{
				// if d is diagonal, allow all directions
				if (!d.isDiagonal())
				{
					// Blocking a dir that is the first prohibited dir, or one
					// rotation to the side
					BLOCK_DIRS[d.ordinal()][b.ordinal()][d.ordinal()] = true;
					BLOCK_DIRS[d.ordinal()][b.ordinal()][d.rotateLeft().ordinal()] = true;
					BLOCK_DIRS[d.ordinal()][b.ordinal()][d.rotateRight().ordinal()] = true;
					// b is diagonal, ignore it
					if (!b.isDiagonal() && b != Direction.NONE && b != Direction.OMNI)
					{
						// Blocking a dir that is the second prohibited dir, or one
						// rotation to the side
						BLOCK_DIRS[d.ordinal()][b.ordinal()][b.ordinal()] = true;
						BLOCK_DIRS[d.ordinal()][b.ordinal()][b.rotateLeft().ordinal()] = true;
						BLOCK_DIRS[d.ordinal()][b.ordinal()][b.rotateRight().ordinal()] = true;
					}
				}
			}
		}
	}
	// So looking back at canMove(), it makes sense.
	// If we are trying to move in a direction that are adjacent to either of our previous two travel directions, we don't allow that.
	// Okay, there's one more piece to this that I need to add.
	// What happens if you start off with your goal North of you, then you bug around to the point where the goal is South?
	// Because of the way that we're storing previous directions, that can actually cause problems.
	// So we use a variable called goneAround to track if you've managed to go around the target or not. This is the final addition to getNextMove():

}