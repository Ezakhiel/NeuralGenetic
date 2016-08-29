package com.me.flappybird;

import java.util.Random;

public class config {
	
	public static String SoundsJump = "Jump";
	public static String SoundsHit = "Hit";
	public static String SoundsLandhit = "Landhit";
	public static String SoundsScore = "Score";
	
	public static int KlandHeight = 112;
	public static float KlandWidth = 330;
	
	public static float KmoveLeftDura = 3;
	
	
	public static float KtimeAddPipe = 6;
	public static float PipeWidth = 30.0f;
	
	public static float BirdWidth = 20.0f;
	public static float BirdHeigth = 32.0f;
	
	
	public static float KholeBetwenPipe = BirdHeigth * 4;
	
	public static float KjumpHeight = BirdHeigth * 1.5f;
	public static float KjumpDura = 0.3f;
	
	//public static float FallSpeed = 0.3f;
	
	// NEURAL NETWORK VARS
	
	public static Random random = new Random();
	
	public static double MUTATIONLOWERBOUND = 0f;
	public static double MUTATIONUPPERBOUND = 1f;
	
	public static int random(int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}
	public static double random() {
		return random.nextDouble();
	}
	
	public static double randDecrease(double number, double rangeMax, double rangeMin){
		//double change = random.nextDouble();
		//return (number > change) ? number-change : 0;
		return 0;
	}
	
	public static double randIncrease(double number, double rangeMax, double rangeMin){
		//double change = random.nextDouble();
		//return (number+change > 1) ? 1 : number+change;
		return 1;
	}
	
	public static double randMutate(double number, double rangeMax, double rangeMin){
		double change = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
		if (random.nextDouble() > 0.5)
			return (number+change > 1) ? 1 : number+change;
		else
			return (number > change) ? number-change : 0;
	}
	public static int screenWidth = 360;
	public static int screenHeigth = 432;
	public static int dataWidth = 10;
	public static int dataHeigth = 10;
	
	public static int trueXOrigo = (int) (screenWidth/2 - BirdWidth);
	public static int trueYOrigo = KlandHeight;
	public static int trueWidth = (int) (screenWidth/2 + BirdWidth);
	public static int trueHeigth = screenHeigth - KlandHeight;
	
	public static float PIPEVALUE = 1.0f;
	public static float NOTHING = 0.0f;
	public static float BRDVALUE = 1.0f;

	public static float makeDecisionTimer = 0.2f;
	
	public static int ONEGENTIMER = 15;
	public static int GENETICAGEOUT = 7;

	public static int PERCEPTRONSIZEPIPE = dataHeigth * (dataWidth-1);
	public static int PERCEPTRONSIZEBIRD =  dataHeigth;
	public static int OUTPUTLAYERSIZE = 1;
	
	public static int CONNECTIONCOUNT =  PERCEPTRONSIZEBIRD * PERCEPTRONSIZEPIPE
										+ PERCEPTRONSIZEPIPE;
	
	
}
