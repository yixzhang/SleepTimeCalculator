package com.example.sleepstatistic;

public class SpecialCalculator1 implements ICaculator{
	
	private static final double MONEY = 500.0;
	private double sum;
	private int count;
	private SpecialCalculator1(){}
	private static SpecialCalculator1 instance = new SpecialCalculator1();
	
	public static SpecialCalculator1 getInstance(){return instance;}

	@Override
	public void setTime(int time) {
		if(count == 10){
			sum = sum + MONEY;
			count = 0;
		}
		int standardMinutes = 23 * 60 + 15;
		if(time <= standardMinutes){
			count ++;
		}else{
			count = 0;
		}
	}

	@Override
	public double getAmount() {
		if(count == 10){
			sum = sum + MONEY;
			count = 0;
		}
		return sum;
	}

}
