package com.example.sleepstatistic;

public class NormalCalculator implements ICaculator{
	
	private double sum;
	private NormalCalculator(){}
	private static NormalCalculator instance = new NormalCalculator();
	
	public static NormalCalculator getInstance(){return instance;}

	@Override
	public void setTime(int time) {
		int standardMinutes = 23 * 60 + 15;
		sum = sum + (standardMinutes - time) * 0.3;
	}

	@Override
	public double getAmount() {
		return sum;
	}

}
