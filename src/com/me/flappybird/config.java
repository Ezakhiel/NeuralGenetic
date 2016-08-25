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
	
	public static int KjumpHeight = 30;
	public static float KjumpDura = 0.3f;
	
	public static float KtimeAddPipe = 1.4f;
	public static float PipeWidth = 30.0f;
	
	public static float BirdWidth = 20.0f;
	public static float BirdHeigth = 32.0f;
	
	public static int makeMapTimer = 10;
	
	public static float KholeBetwenPipe = 128;
	
	public static float FallSpeed = 2.0f;
	
	// NEURAL NETWORK VARS
	
	public static int random(int min, int max) {
		Random random = new Random();
		return random.nextInt(max - min + 1) + min;
	}
	public static double random() {
		Random random = new Random();
		return random.nextDouble();
	}
	
	public static int screenWidth = 360;
	public static int screenHeigth = 432;
	public static int dataWidth = 10;
	public static int dataHeigth = 10;
	
	public static int trueXOrigo = (int) (screenWidth/2 - BirdWidth);
	public static int trueYOrigo = KlandHeight;
	public static int trueWidth = (int) (screenWidth/2 + BirdWidth);
	public static int trueHeigth = screenHeigth - KlandHeight;
}
