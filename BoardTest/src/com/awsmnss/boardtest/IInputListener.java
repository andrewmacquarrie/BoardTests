package com.awsmnss.boardtest;

public interface IInputListener {
	void AnalogInputChanged(Integer num, Integer value);
	void DigitalInputChanged(Integer num, boolean value);
}
