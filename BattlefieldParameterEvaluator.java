package neural;



import java.awt.Color;

import java.awt.image.BufferedImage;

import java.io.File;

import java.io.IOException;

import java.util.Arrays;

import java.util.Random;



import javax.imageio.ImageIO;



import org.encog.engine.network.activation.ActivationSigmoid;

import org.encog.ml.data.MLData;

import org.encog.ml.data.MLDataPair;

import org.encog.ml.data.MLDataSet;

import org.encog.ml.data.basic.BasicMLDataSet;

import org.encog.ml.train.MLTrain;

import org.encog.neural.data.NeuralDataSet;

import org.encog.neural.data.basic.BasicNeuralDataSet;

import org.encog.neural.networks.BasicNetwork;

import org.encog.neural.networks.layers.BasicLayer;

import org.encog.neural.networks.training.Train;

import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;



import neural.BattlefieldParameterEvaluator.BattleObserver;

import robocode.BattleResults;

import robocode.control.BattleSpecification;

import robocode.control.BattlefieldSpecification;

import robocode.control.RobocodeEngine;

import robocode.control.RobotSetup;

import robocode.control.RobotSpecification;

import robocode.control.events.BattleAdaptor;

import robocode.control.events.BattleCompletedEvent;

import robocode.control.events.BattleErrorEvent;

import robocode.control.events.BattleMessageEvent;



public class BattlefieldParameterEvaluator {

	// Minimum allowable battlefield size is 400

	final static int MAXBATTLEFIELDSIZE = 4000;

	// Minimum allowable gun cooling rate is 0.1

	final static double MAXGUNCOOLINGRATE = 10;

	final static int NUMBATTLEFIELDSIZES = 601;

	final static int NUMCOOLINGRATES = 501;

	final static int NUMSAMPLES = 1000;

	// Number of inputs for the multilayer perceptron (size of the input

	// vectors)

	final static int NUM_NN_INPUTS = 2;

	// Number of hidden neurons of the neural network

	final static int NUM_NN_HIDDEN_UNITS = 50;

	// Number of epochs for training

	final static int NUM_TRAINING_EPOCHS = 100000;

	static int NdxBattle;

	static double[] FinalScore1;

	static double[] FinalScore2;



	public static void main(String[] args) {

		double[] BattlefieldSize = new double[NUMSAMPLES];

		double[] GunCoolingRate = new double[NUMSAMPLES];



		FinalScore1 = new double[NUMSAMPLES];

		FinalScore2 = new double[NUMSAMPLES];

		Random rng = new Random(15L);

		// Disable log messages from Robocode

		RobocodeEngine.setLogMessagesEnabled(false);

		// Create the RobocodeEngine

		// Run from C:/Robocode

		RobocodeEngine engine = new RobocodeEngine(new java.io.File("C:/Robocode"));

		// Add our own battle listener to the RobocodeEngine

		engine.addBattleListener(new BattleObserver());

		// Show the Robocode battle view

		engine.setVisible(false);

		// Setup the battle specification

		// Setup battle parameters

		int numberOfRounds = 1;

		long inactivityTime = 100;

		int sentryBorderSize = 50;

		boolean hideEnemyNames = false;

		// Get the robots and set up their initial states

		RobotSpecification[] competingRobots = engine.getLocalRepository("sample.RamFire,sample.TrackFire");

		RobotSetup[] robotSetups = new RobotSetup[2];

		for (NdxBattle = 0; NdxBattle < NUMSAMPLES; NdxBattle++) {// Choose the

																	// battlefield

																	// size and

																	// gun

																	// cooling

																	// rate

			BattlefieldSize[NdxBattle] = MAXBATTLEFIELDSIZE * (0.1 + 0.9 * rng.nextDouble());

			GunCoolingRate[NdxBattle] = MAXGUNCOOLINGRATE * (0.1 + 0.9 * rng.nextDouble());

			// Create the battlefield

			BattlefieldSpecification battlefield = new BattlefieldSpecification((int) BattlefieldSize[NdxBattle],

					(int) BattlefieldSize[NdxBattle]);

			// Set the robot positions

			robotSetups[0] = new RobotSetup(BattlefieldSize[NdxBattle] / 2.0, BattlefieldSize[NdxBattle] / 3.0, 0.0);

			robotSetups[1] = new RobotSetup(BattlefieldSize[NdxBattle] / 2.0, 2.0 * BattlefieldSize[NdxBattle] / 3.0,

					0.0);

			// Prepare the battle specification

			BattleSpecification battleSpec = new BattleSpecification(battlefield, numberOfRounds, inactivityTime,

					GunCoolingRate[NdxBattle], sentryBorderSize, hideEnemyNames, competingRobots, robotSetups);



			// Run our specified battle and let it run till it is over

			engine.runBattle(battleSpec, true); // waits till the battle

												// finishes

		}



		// Cleanup our RobocodeEngine

		engine.close();

		System.out.println(Arrays.toString(BattlefieldSize));

		System.out.println(Arrays.toString(GunCoolingRate));

		System.out.println(Arrays.toString(FinalScore1));

		System.out.println(Arrays.toString(FinalScore2));



		// Create the training dataset for the neural network

		double[][] RawInputs = new double[NUMSAMPLES][NUM_NN_INPUTS];

		double[][] RawOutputs = new double[NUMSAMPLES][1];
		
		double[][] TrainingInp = new double[NUMSAMPLES*3/4][NUM_NN_INPUTS];
		
		double[][] TrainingOut = new double[NUMSAMPLES*3/4][1];
		
		double[][] ValidationInp = new double[NUMSAMPLES/4][NUM_NN_INPUTS];
		
		double[][] ValidationOut = new double[NUMSAMPLES/4][1];

		for (int NdxSample = 0; NdxSample < NUMSAMPLES; NdxSample++) {

			// IMPORTANT: normalize the inputs and the outputs to

			// the interval [0,1]       

			RawInputs[NdxSample][0] = BattlefieldSize[NdxSample] / MAXBATTLEFIELDSIZE;

			RawInputs[NdxSample][1] = GunCoolingRate[NdxSample] / MAXGUNCOOLINGRATE;

			RawOutputs[NdxSample][0] = FinalScore1[NdxSample] / 250;

		}

		TrainingInp=Arrays.copyOfRange(RawInputs, 0, NUMSAMPLES*3/4);
		
		TrainingOut=Arrays.copyOfRange(RawOutputs, 0, NUMSAMPLES*3/4);

		ValidationInp=Arrays.copyOfRange(RawInputs, NUMSAMPLES*3/4, NUMSAMPLES);
		
		ValidationOut=Arrays.copyOfRange(RawOutputs, NUMSAMPLES*3/4, NUMSAMPLES);
		
		MLDataSet MyTrainingDataSet = new BasicNeuralDataSet(TrainingInp, TrainingOut);
		
		MLDataSet MyValidatingDataSet = new BasicNeuralDataSet(ValidationInp, ValidationOut);

		// Create and train the neural network  


		BasicNetwork network = new BasicNetwork();



		BasicLayer input = new BasicLayer(null, true, NUM_NN_INPUTS); // Two inputs, (battlefield_size,gun_cooling_rate)

		BasicLayer hiddenLayer = new BasicLayer(new ActivationSigmoid(), true, NUM_NN_HIDDEN_UNITS); // 50 hidden neurons 

		BasicLayer output = new BasicLayer(new ActivationSigmoid(), true, 1); // 1 output, Score



		network.addLayer(input);

		network.addLayer(hiddenLayer);

		network.addLayer(output);

		// input.setContextFedBy(hiddenLayer);

		// hiddenLayer.setContextFedBy(output);

		network.getStructure().finalizeStructure();

		network.reset();



		// training data

		MLTrain train = new ResilientPropagation(network, MyTrainingDataSet);

		System.out.println("Training network...");

		// do training with different number of hidden layers and then pick the

		// one with the best score

		int epoch = 0;

		int count = 0;

		double error = 0.0;

		double bestError = 1;

		do {
			train.iteration();
			
			error=network.calculateError(MyValidatingDataSet);

			System.out.println("Epoch #" + epoch + " Error:" + error);

			epoch++;

			if(error < bestError){

				bestError=error;

				count = 0;

			}else{

				count++;

			}	

		} while ((epoch < NUM_TRAINING_EPOCHS) && (count < 5000));

		train.finishTraining();

		

		



		// test the neural network

		System.out.println("Neural Network Results:");

//		for (MLDataPair pair : MyTrainingDataSet) {
//
//			final MLData output1 = network.compute(pair.getInput());
//
//			System.out.println(pair.getInput().getData(0) + "," + pair.getInput().getData(1) + ", actual="
//
//					+ output1.getData(0) + ",ideal=" + pair.getIdeal().getData(0));
//
//		}
//
//
//
//		System.out.println("Training completed.");
//
//		System.out.println("Testing network...");

		// Generate test samples to build an output image

		int[] OutputRGBint = new int[NUMBATTLEFIELDSIZES * NUMCOOLINGRATES];

		Color MyColor;

		double MyValue = 0;

		double[][] MyTestData = new double[NUMBATTLEFIELDSIZES * NUMCOOLINGRATES][NUM_NN_INPUTS];

		for (int NdxBattleSize = 0; NdxBattleSize < NUMBATTLEFIELDSIZES; NdxBattleSize++) {

			for (int NdxCooling = 0; NdxCooling < NUMCOOLINGRATES; NdxCooling++) {

				MyTestData[NdxCooling + NdxBattleSize * NUMCOOLINGRATES][0] = 0.1

						+ 0.9 * ((double) NdxBattleSize) / NUMBATTLEFIELDSIZES;

				MyTestData[NdxCooling + NdxBattleSize * NUMCOOLINGRATES][1] = 0.1

						+ 0.9 * ((double) NdxCooling) / NUMCOOLINGRATES;

			}

		}	

		

		// Simulate the neural network with the test samples and fill a matrix

		for (int NdxBattleSize = 0; NdxBattleSize < NUMBATTLEFIELDSIZES; NdxBattleSize++) {// 601

			for (int NdxCooling = 0; NdxCooling < NUMCOOLINGRATES; NdxCooling++) {// 501



				//double MyResult = MyTestData[NdxCooling + NdxBattleSize * NUMCOOLINGRATES][0];

				double[] MyResult = new double[1];

				network.compute(MyTestData[NdxCooling + NdxBattleSize * NUMCOOLINGRATES], MyResult);

				MyValue = ClipColor(MyResult[0]);



				MyColor = new Color((float) MyValue, (float) MyValue, (float) MyValue);//(red,green,blue)

				OutputRGBint[NdxCooling + NdxBattleSize * NUMCOOLINGRATES] = MyColor.getRGB();

			}

		}



		System.out.println("Testing completed.");// Plot the training samples

		for (int NdxSample = 0; NdxSample < NUMSAMPLES; NdxSample++) {

			MyValue = ClipColor(FinalScore1[NdxSample] / 250);

			MyColor = new Color((float) MyValue, (float) MyValue, (float) MyValue);

			int MyPixelIndex = (int) (Math

					.round(NUMCOOLINGRATES * ((GunCoolingRate[NdxSample] / MAXGUNCOOLINGRATE) - 0.1) / 0.9)

					+ Math.round(NUMBATTLEFIELDSIZES * ((BattlefieldSize[NdxSample] / MAXBATTLEFIELDSIZE) - 0.1) / 0.9)

							* NUMCOOLINGRATES);

			if ((MyPixelIndex >= 0) && (MyPixelIndex < NUMCOOLINGRATES * NUMBATTLEFIELDSIZES)) {

				OutputRGBint[MyPixelIndex] = MyColor.getRGB();

			}

		}

		BufferedImage img = new BufferedImage(NUMCOOLINGRATES, NUMBATTLEFIELDSIZES, BufferedImage.TYPE_INT_RGB);

		img.setRGB(0, 0, NUMCOOLINGRATES, NUMBATTLEFIELDSIZES, OutputRGBint, 0, NUMCOOLINGRATES);

		File f = new File("hello.png");

		try {

			ImageIO.write(img, "png", f);

		} catch (IOException e) {

			// TODO Auto‐generated catch block

			e.printStackTrace();

		}



		System.out.println("Image generated.");

		// Make sure that the Java VM is shut down properly

		System.exit(0);

	}



	// MAIN FINISHES



	/*

	 *    * Clip a color value (double precision) to lie in the valid range

	 * [0,1]    

	 */

	public static double ClipColor(double Value) {

		if (Value < 0.0) {

			Value = 0.0;

		}

		if (Value > 1.0) {

			Value = 1.0;

		}

		return Value;

	}



	//

	// Our private battle listener for handling the battle event we are

	// interested in.

	//

	static class BattleObserver extends BattleAdaptor {

		// Called when the battle is completed successfully with battle

		// results



		public void onBattleCompleted(BattleCompletedEvent e) {

			System.out.println("--Battle has completed --");// Get the indexed

															// battle results

			BattleResults[] results = e.getIndexedResults();

			// Print out the indexed results with the robot names

			System.out.println("Battle results:");

			for (BattleResults result : results) {

				System.out.println("  " + result.getTeamLeaderName() + ": " + result.getScore());

			}

			// Store the scores of the robots

			BattlefieldParameterEvaluator.FinalScore1[NdxBattle] = results[0].getScore();

			BattlefieldParameterEvaluator.FinalScore2[NdxBattle] = results[1].getScore();

		}// Called

			// when

			// the

			// game

			// sends

			// out

			// an

			// information

			// message

			// during

			// the

			// battle



		public void onBattleMessage(BattleMessageEvent e) {

			// System.out.println("Msg> " + e.getMessage());

		}// Called when the game sends out an error message during the

			// battle



		public void onBattleError(BattleErrorEvent e) {

			System.out.println("Err> " + e.getError());

		}

	}

}