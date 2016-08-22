package com.me.mygdxgame.Main;

import cs.ubb.neural.gui.ConfigUI;

public class Main {

	public static void main(String[] args){
		(new Thread(new ConfigUI())).start();

	}
}
