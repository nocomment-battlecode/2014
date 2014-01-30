package team089;

import java.util.ArrayList;
import java.util.Random;

import battlecode.common.*;

public class CowboyRobot extends BasicRobot
{	
	static RobotController rc;
	static Random randall = new Random();
	static ArrayList<MapLocation> path = new ArrayList<MapLocation>();
	//boolean becomingPasture = false;
	static boolean die = false;
	//SOLDIER data:
	static int myBand = 100;
	static int pathCreatedRound = -1;
	static boolean isBugging = false;
	static boolean goneAround = false;
	static boolean hugLeft = false;
	static MapLocation startBuggingLoc;
	static Direction startDir;
	static int buggingDistance;
	static MapLocation desiredLoc;
	static RobotInfo[] curInfo;
	static int[] myProhibitedDirs = new int[]{-1,-1};
	public CowboyRobot(RobotController myRC) throws GameActionException
	{
		super(myRC);
	}
	public void run(RobotController rcIn)
	{
		rc=rcIn;
		Comms.rc = rcIn;
		randall.setSeed(rc.getRobot().getID());
		BreadthFirst.rc=rcIn;//slimmed down init
		//MapLocation goal = getRandomLocation();
		//path = BreadthFirst.pathTo(VectorFunctions.mldivide(rc.getLocation(),bigBoxSize), VectorFunctions.mldivide(goal,bigBoxSize), 100000);
		//VectorFunctions.printPath(path);
		/*isBugging = false;
		goneAround = false;
		hugLeft = false;
		int[] myProhibitedDirs = {-1,-1};*/
		while(true){
			try{
				if (rc.isActive())
				{
					runSoldier();
					DataCache.updateVariables();
				}
			}catch (Exception e){
				e.printStackTrace();
			}
			rc.yield();
		}
	}

	private static void runSoldier() throws GameActionException {
		//follow orders from HQ
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class,10000,rc.getTeam().opponent());
		Robot[] alliedRobots = rc.senseNearbyGameObjects(Robot.class,rc.getType().sensorRadiusSquared*2,rc.getTeam());//was 
		if(enemyRobots.length>0){//SHOOT AT, OR RUN TOWARDS, DETECTABLY NEAR ENEMIES
			rc.setIndicatorString(0, "Updated: " + String.valueOf(Clock.getRoundNum()) + " attacking");
			MapLocation[] enemyRobotLocations = VectorFunctions.robotsToLocations(enemyRobots, true);
			if(enemyRobotLocations.length==0){//only HQ is in view
				rc.setIndicatorString(2, "Updated: " + String.valueOf(Clock.getRoundNum()) + " navigateByPath");
				navigateByPath(alliedRobots);
			}else{//shootable robots are in view
				if(DataCache.numPastures < 2) considerBuildingPastr(alliedRobots);
				rc.setIndicatorString(2, "Updated: " + String.valueOf(Clock.getRoundNum()) + " attacking");
				MapLocation closestEnemyLoc = VectorFunctions.findClosest(enemyRobotLocations, rc.getLocation());
				boolean closeEnoughToShoot = closestEnemyLoc.distanceSquaredTo(rc.getLocation())<=rc.getType().attackRadiusMaxSquared;
				if (DataCache.beingShotAt) BasicPathing.tryToMove(DataCache.selfLoc.directionTo(closestEnemyLoc).opposite(),false,true,false);
				else if((alliedRobots.length+1)>=enemyRobots.length){//attack when you have superior numbers
					//					attackClosest(closestEnemyLoc);
					attackLowest(closestEnemyLoc);
				}else{//otherwise regroup
					regroup(enemyRobots,alliedRobots,closestEnemyLoc);
				}
			}
		}
		else if(Clock.getRoundNum() > 0){
			if(DataCache.numPastures < 5) considerBuildingPastr(alliedRobots);
			rc.setIndicatorString(0, "Updated: " + String.valueOf(Clock.getRoundNum()) + " just moving");
			desiredLoc = DataCache.enemyHQLoc;
			Direction nextDir = getNextDirectionBug();
			if (nextDir != null) BasicPathing.tryToMove(nextDir,false,true,false);
		}
		else if (DataCache.rallyPoint != null){
			desiredLoc = DataCache.rallyPoint;
			Direction nextDir = getNextDirectionBug();
			if (nextDir != null) BasicPathing.tryToMove(nextDir,false,true,false);
			//NAVIGATION BY DOWNLOADED PATH
			//navigateByPath(alliedRobots);
			//Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			//BasicPathing.tryToMove(towardEnemy, true, rc, directionalLooks, allDirections);//was Direction.SOUTH_EAST

			//Direction towardEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
			//simpleMove(towardEnemy);
		}
	}

	private static void navigateByPath(Robot[] alliedRobots) throws GameActionException{
		if(path.size()<=1){//
			//check if a new path is available
			int broadcastCreatedRound = rc.readBroadcast(myBand);
			if(pathCreatedRound<broadcastCreatedRound){//download new place to go
				pathCreatedRound = broadcastCreatedRound;
				path = Comms.downloadPath();
			}else{//just waiting around. Consider building a pastr
				considerBuildingPastr(alliedRobots);
			}
		}
		if(path.size()>0){
			//follow breadthFirst path...
			Direction bdir = BreadthFirst.getNextDirection(path, DataCache.bigBoxSize);
			//...except if you are getting too far from your allies
			MapLocation[] alliedRobotLocations = VectorFunctions.robotsToLocations(alliedRobots, true);
			if(alliedRobotLocations.length>0){
				MapLocation allyCenter = VectorFunctions.meanLocation(alliedRobotLocations);
				if(rc.getLocation().distanceSquaredTo(allyCenter)>16){
					bdir = rc.getLocation().directionTo(allyCenter);
				}
			}
			BasicPathing.tryToMove(bdir, true,true, false);
		}
	}

	private static void considerBuildingPastr(Robot[] alliedRobots) throws GameActionException {
		if(alliedRobots.length>1){//there must be allies nearby for defense
			MapLocation[] alliedPastrs =rc.sensePastrLocations(rc.getTeam());
			if(rc.readBroadcast(50)+60<Clock.getRoundNum()){//no allied robot can be building a pastr at the same time
				for(int i=0;i<20;i++){
					MapLocation checkLoc = VectorFunctions.mladd(rc.getLocation(),new MapLocation(randall.nextInt(8)-4,randall.nextInt(8)-4));
					if(rc.canSenseSquare(checkLoc)){
						double numberOfCows = rc.senseCowsAtLocation(checkLoc);
						if(numberOfCows>1000){//there must be a lot of cows there
							if(alliedPastrs.length==0){//there must not be another pastr nearby
								buildPastr(checkLoc);
							}else{
								MapLocation closestAlliedPastr = VectorFunctions.findClosest(alliedPastrs, checkLoc);
								if(closestAlliedPastr.distanceSquaredTo(checkLoc)>GameConstants.PASTR_RANGE*5){
									buildPastr(checkLoc);
								}
							}
						}
					}
				}
			}
			if(DataCache.numTowers < DataCache.numPastures) {
				if(rc.readBroadcast(51) + 60<Clock.getRoundNum()){
					curInfo = new RobotInfo[alliedRobots.length];
					for(int i=0; i<alliedRobots.length;i++){
						curInfo[i] = rc.senseRobotInfo(alliedRobots[i]);
						if(curInfo[i].type == RobotType.PASTR) {
							for(int j=0; j< 20; j++) {
								MapLocation checkLoc = VectorFunctions.mladd(rc.getLocation(),new MapLocation(randall.nextInt(8)-4,randall.nextInt(8)-4));
								if(rc.canSenseSquare(checkLoc)){
									if(curInfo[i].location.distanceSquaredTo(checkLoc) < 17){
										buildNT(checkLoc);
									}
								}
							}
						}
					}
				}
			}
			defendPastrs(alliedPastrs);
		}
	}

	private static void defendPastrs(MapLocation[] alliedPastrs) throws GameActionException {
		rc.setIndicatorString(1, "Updated: " + String.valueOf(Clock.getRoundNum()) + " defending");
		MapLocation closestAlliedPastr = VectorFunctions.findClosest(alliedPastrs, DataCache.selfLoc);
		if (closestAlliedPastr != null) BasicPathing.tryToMove(DataCache.selfLoc.directionTo(closestAlliedPastr),false,true,false);
	}

	private static void buildPastr(MapLocation checkLoc) throws GameActionException {
		rc.setIndicatorString(1, "Updated: " + String.valueOf(Clock.getRoundNum()) + " pasturing");
		rc.broadcast(50, Clock.getRoundNum());
		for(int i=0;i<100;i++){//for 100 rounds, try to build a pastr
			if(rc.isActive()){
				if(rc.getLocation().equals(checkLoc)){
					rc.construct(RobotType.PASTR);
				}else{
					Direction towardCows = rc.getLocation().directionTo(checkLoc);
					BasicPathing.tryToMove(towardCows, true,true, true);
				}
			}
			rc.yield();
		}
	}

	private static void buildNT(MapLocation checkLoc) throws GameActionException {
		rc.setIndicatorString(1, "Updated: " + String.valueOf(Clock.getRoundNum()) + " building NT");
		rc.broadcast(51, Clock.getRoundNum());
		for(int i=0; i<100; i++){//for 100 rounds, try to build a noise tower
			if(rc.isActive()){
				if(rc.getLocation().equals(checkLoc)){
					rc.construct(RobotType.NOISETOWER);
				}else{
					Direction towardGoal = rc.getLocation().directionTo(checkLoc);
					BasicPathing.tryToMove(towardGoal, true, true, false);
				}
			}
			rc.yield();
		}
	}

	private static void regroup(Robot[] enemyRobots, Robot[] alliedRobots,MapLocation closestEnemyLoc) throws GameActionException {
		int enemyAttackRangePlusBuffer = (int) Math.pow((Math.sqrt(rc.getType().attackRadiusMaxSquared)+1),2);
		if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<=enemyAttackRangePlusBuffer){//if within attack range, back up
			Direction awayFromEnemy = rc.getLocation().directionTo(closestEnemyLoc).opposite();
			BasicPathing.tryToMove(awayFromEnemy, true,true,false);
		}else{//if outside attack range, group up with allied robots
			MapLocation[] alliedRobotLocations = VectorFunctions.robotsToLocations(enemyRobots, false);
			MapLocation alliedRobotCenter = VectorFunctions.meanLocation(alliedRobotLocations);
			Direction towardAllies = rc.getLocation().directionTo(alliedRobotCenter);
			BasicPathing.tryToMove(towardAllies, true,true, false);
		}
	}

	/*	private static void attackClosest(MapLocation closestEnemyLoc) throws GameActionException {
		//attacks the closest enemy or moves toward it, if it is out of range
		if(closestEnemyLoc.distanceSquaredTo(rc.getLocation())<=rc.getType().attackRadiusMaxSquared){//close enough to shoot
			if(rc.isActive()){
				rc.attackSquare(closestEnemyLoc);
			}
		}else{//not close enough to shoot, so try to go shoot
			Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
			//simpleMove(towardClosest);
			BasicPathing.tryToMove(towardClosest, true,true, false);
		}
	}*/

	public static void attackLowest(MapLocation closestEnemyLoc) throws GameActionException	{
		Robot[] enemyRobots = rc.senseNearbyGameObjects(Robot.class, rc.getType().attackRadiusMaxSquared, rc.getTeam().opponent());
		if (enemyRobots.length != 0){
			double lowestHealth = 1000;
			Robot lowestRobot = null;
			for (int i = 0; i < enemyRobots.length; i++)
			{
				double robotHealth = rc.senseRobotInfo(enemyRobots[i]).health;
				if (robotHealth < lowestHealth){
					lowestHealth = robotHealth;
					lowestRobot = enemyRobots[i];
				}
			}
			if(rc.isActive() && lowestRobot != null){
				rc.attackSquare(rc.senseRobotInfo(lowestRobot).location);
			}
		}else{//not close enough to shoot, so try to go shoot
			Direction towardClosest = rc.getLocation().directionTo(closestEnemyLoc);
			//simpleMove(towardClosest);
			BasicPathing.tryToMove(towardClosest, true, true, false);
		}
	}

	// shoots at cows in enemy pastures first
	public static void attackCows(){

	}

	private static MapLocation getRandomLocation() {
		return new MapLocation(randall.nextInt(rc.getMapWidth()),randall.nextInt(rc.getMapHeight()));
	}

	private static void simpleMove(Direction desiredDir) throws GameActionException{
		if (rc.isActive())
		{
			// try different directional offsets in case the desired direction does not work
			for (int tryOffset: DataCache.dirOffsets)
			{
				int desiredInt = desiredDir.ordinal();
				Direction tryDir = DataCache.dirValues[(desiredInt + tryOffset + 8) % 8];
				if(rc.canMove(tryDir))
				{
					rc.move(tryDir);
					break;
				}
			}
		}
	}
	
	public static Direction getNextDirectionBug(){
		Direction desiredDir = DataCache.selfLoc.directionTo(desiredLoc);
		if (isBugging){
			if (BasicPathing.canMove(DataCache.selfLoc.directionTo(desiredLoc),true,true) && DataCache.selfLoc.distanceSquaredTo(desiredLoc) <= buggingDistance){
				isBugging = false;
			}
		}
		if (!isBugging){
			Direction newDir = flockInDir(desiredDir);
			if (newDir != null){
				return newDir;
			}
			isBugging = true;
			startBuggingLoc = DataCache.selfLoc;
			startDir = desiredDir;
			buggingDistance = DataCache.selfLoc.distanceSquaredTo(desiredLoc);
		}
		if(isBugging){
			if (goneAround && (desiredDir == startDir.rotateLeft().rotateLeft() || desiredDir == startDir.rotateRight().rotateRight())) {
				myProhibitedDirs[0] = -1;
			}
			if (desiredDir == startDir.opposite()) {
				myProhibitedDirs[0] = -1;
				goneAround = true;
			}
			Direction moveDir = hug(desiredDir, false);
			if (moveDir == null) {
				moveDir = desiredDir;
			}
			return moveDir;
		}
		return null;
		//if (BasicPathing.canMove(DataCache.selfLoc.directionTo(desiredLoc))) rc.move(desiredDir);
	}

	private static Direction turn(Direction dir){
		return (hugLeft ? dir.rotateRight() : dir.rotateLeft());
	}

	private static Direction hug (Direction desiredDir, boolean recursed){
		if (BasicPathing.canMove(desiredDir,true,true)){
			return desiredDir;
		}
		Direction tryDir = turn(desiredDir);
		MapLocation tryLoc = DataCache.selfLoc.add(tryDir);
		for (int i=0; i<8 && !rc.canMove(tryDir); i++){
		//for (int i=0; i<8 && !BasicPathing.canMove(tryDir,true,true); i++){
			tryDir = turn(tryDir);
			tryLoc = DataCache.selfLoc.add(tryDir);
		}
		// If the loop failed (found no directions or encountered the map edge)
		if (!rc.canMove(tryDir)) {
		//if (!BasicPathing.canMove(tryDir,true,true)) {
			hugLeft = !hugLeft;
			if (recursed){
				// We've tried hugging in both directions...
				if (myProhibitedDirs[0] != -1 && myProhibitedDirs[1] != -1) {
					// We were prohibiting certain directions before.
					// try again allowing those directions
					myProhibitedDirs[1] = -1;
					return hug(desiredDir, false);
				}
				else {
					// Complete failure. Reset the state and start over.
					//reset();
					return null;
				}
			}
			// mark recursed as true and try hugging the other direction
			return hug(desiredDir, true);
		}
		// If we're moving in a new cardinal direction, store it.
		if (!tryDir.isDiagonal()) {
			if (myProhibitedDirs[0] != -1 && turn(turn(DataCache.dirValues[myProhibitedDirs[0]])) == tryDir) {
				myProhibitedDirs[0] = tryDir.opposite().ordinal();
				myProhibitedDirs[1] = -1;
			} else {
				myProhibitedDirs[1] = myProhibitedDirs[0];
				myProhibitedDirs[0] = tryDir.opposite().ordinal();
			}
		}
		//rc.setIndicatorString(0,tryDir.toString());
		return tryDir;
	}

	private static Direction flockInDir(Direction desiredDir){
		Direction[] directions = new Direction[3];
		directions[0] = desiredDir;
		Direction left = desiredDir.rotateLeft();
		Direction right = desiredDir.rotateRight();
		boolean leftIsBetter = (DataCache.selfLoc.add(left).distanceSquaredTo(desiredLoc) < DataCache.selfLoc.add(right).distanceSquaredTo(desiredLoc));
		directions[1] = (leftIsBetter ? left : right);
		directions[2] = (leftIsBetter ? right : left);
		for (int i = 0; i < directions.length; i++){
//			if (BasicPathing.canMove(directions[i],true,true)){
			if (rc.canMove(directions[i])){
				return directions[i];
			}
		}
		return null;
	}
}