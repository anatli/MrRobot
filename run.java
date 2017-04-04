package Robott;

import robocode.control.*;
import robocode.control.events.*;
import sun.print.resources.serviceui_de;
import java.util.Random;

public class run {
	
	
 public static void main(String[] args)
 {
	 final int NumObstacles = 30;
	 Random rnd = new Random();
	 rnd.setSeed(29);
	 boolean [][] MyMatrix = new boolean [10][10] ;
	 int numTanks = 0;
	 
 // Create the RobocodeEngine
 RobocodeEngine engine =
 new RobocodeEngine(new java.io.File("C:/robocode"));
 // Run from C:/Robocode
 // Show the Robocode battle view
 engine.setVisible(true);
 // Create the battlefield
 int NumPixelRows=64*10; // 10 tiles of 64 pixels
 int NumPixelCols=64*10;
 BattlefieldSpecification battlefield = new BattlefieldSpecification(NumPixelRows, NumPixelCols);
 // 800x600
 // Setup battle parameters
 int numberOfRounds = 1;
 long inactivityTime = 10000000;
 double gunCoolingRate = 1.0;
 int sentryBorderSize = 50;
 boolean hideEnemyNames = false;
 /*
 * Create obstacles and place them at random so that no pair of obstacles
 * are at the same position
 */
 RobotSpecification[] modelRobots =engine.getLocalRepository("sample.SittingDuck,atl.atlRobot*");
 RobotSpecification[] existingRobots =
 new RobotSpecification[NumObstacles+1];
 RobotSetup[] robotSetups = new RobotSetup[NumObstacles+1];
 
	
		while (numTanks < 30){
			int numRandR = rnd.nextInt(10);
			int numRandC = rnd.nextInt(10);
			if(MyMatrix[numRandR][numRandC] == false){
				MyMatrix[numRandR][numRandC] = true;
				
				 double InitialObstacleRow = (numRandR*64+32);
				 double InitialObstacleCol = (numRandC*64+32);
				 
				 existingRobots[numTanks]=modelRobots[0];
				 robotSetups[numTanks]=new RobotSetup(InitialObstacleRow,InitialObstacleCol,0.0);
				numTanks++;
			}
		}
	 

 /*
 * Create the agent and place it in a random position without obstacle
 */
 existingRobots[NumObstacles]=modelRobots[1]; 
 
 		 double InitialAgentRow=31; 
		 double InitialAgentCol=31;
		 robotSetups[NumObstacles]=new RobotSetup(InitialAgentRow,InitialAgentCol,0.0);
		 /* Create and run the battle */
		 BattleSpecification battleSpec =
		 new BattleSpecification(battlefield,
		 numberOfRounds,
		 inactivityTime,
		 gunCoolingRate,
		 sentryBorderSize,
		 hideEnemyNames,
		 existingRobots,
		 robotSetups);
		 // Run our specified battle and let it run till it is over
		 engine.runBattle(battleSpec, true); // waits till the battle finishes
		 // Cleanup our RobocodeEngine
		 engine.close();
		 // Make sure that the Java VM is shut down properly
		 System.exit(0);
		 }
		} 