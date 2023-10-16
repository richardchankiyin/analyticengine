package com.richard.app;

public class Application {

	public static void main(String[] args) {
		String path = "src/main/resources/marketdata_hk_20230818.csv";
		if (args.length >= 1) {
			path = args[0];
		}
		AppContainer ac = new AppContainer(path, 1000, 1000, 100, 10, 1000, 1000);
		ac.start();
	}

}
