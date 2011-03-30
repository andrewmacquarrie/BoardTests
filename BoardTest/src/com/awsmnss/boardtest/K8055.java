package com.awsmnss.boardtest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;

public class K8055 {
	private int _boardNumber;
	private List<IInputListener> _inputListeners = new ArrayList<IInputListener>();
	private Thread _listenerThread = null;
	
	public K8055(int boardNumber) throws K8055CommunicationException {
		_boardNumber = boardNumber;
		setAllDigitalOutputs(false);
		setAnalogOutput(1, 0);
		setAnalogOutput(2, 0);
	}
	
	protected void finalize() throws Throwable
	{
		if (_listenerThread != null)
			_listenerThread.stop();
	} 
	
	public void addListener(IInputListener listener) {
		_inputListeners.add(listener);
		if (_listenerThread == null) {
			_listenerThread = new ListenerThread(this);
			_listenerThread.start();
		}
	}
	
	public void setDigitalOutput(Integer num, boolean state) throws K8055CommunicationException,
																	IllegalArgumentException {
		assertDigitalOutputNumber(num);
		writeTo(out(num), state ? "1" : "0");
	}
	
	public void setAllDigitalOutputs(boolean state) throws K8055CommunicationException {
		writeTo("outputs", state ? "255" : "0");
	}
	
	public boolean getDigitalOutput(Integer num) throws K8055CommunicationException,
														IllegalArgumentException {
		assertDigitalOutputNumber(num);
		return readFrom(out(num)) == "1";
	}
	
	public void setAnalogOutput(Integer num, Integer value) throws	K8055CommunicationException, 
																	IllegalArgumentException {
		assertAnalogOutputNumber(num);
		assertAnalogOutputValue(value);
		writeTo(dac(num), value.toString());
	}
	
	public Integer getAnalogOutput(Integer num) throws K8055CommunicationException,
													   IllegalArgumentException {
		assertAnalogOutputNumber(num);
		return Integer.parseInt(readFrom(dac(num)));
	}
	
	public boolean getDigialInput(Integer num) throws K8055CommunicationException,
													  IllegalArgumentException {
		assertDigitalInputNumber(num);
		return readFrom(in(num)) == "1";
	}
	
	public Integer getAnalogInput(Integer num) throws K8055CommunicationException, 
													  IllegalArgumentException {
		assertAnalogInputNumber(num);
		return Integer.parseInt(readFrom(adc(num)));
	}
	
	private void writeTo(String method, String value) throws K8055CommunicationException {
		try {
			FileWriter fstream = new FileWriter(buildPath(method), true);
			fstream.write(value);
	    	fstream.close();
		} catch (Exception e) {
			throw new K8055CommunicationException();
		}
	}
	
	private String readFrom(String method) throws K8055CommunicationException {
		try {
			FileReader fstream = new FileReader(buildPath(method));
			BufferedReader reader = new BufferedReader(fstream, 8);
			String result = reader.readLine();
			reader.close();
			fstream.close();
			return result.toString();
		} catch (Exception e) {
			throw new K8055CommunicationException();
		}
	}

	private String buildPath(String method) {
		return String.format("/proc/k8055/%s/%s", _boardNumber, method);
	}
	
	private String out(Integer num) {
		return "out" + num;
	}
	
	private String in(Integer num) {
		return "in" + num;
	}
	
	private String dac(Integer num) {
		return "dac" + num;
	}
	
	private String adc(Integer num) {
		return "adc" + num;
	}
	
	private void assertDigitalOutputNumber(Integer num) throws IllegalArgumentException {
		if (num < 1 || num > 8)
			throw new IllegalArgumentException("num");
	}
	
	private void assertDigitalInputNumber(Integer num) throws IllegalArgumentException {
		if (num < 1 || num > 5)
			throw new IllegalArgumentException("num");
	}
	
	private void assertAnalogOutputNumber(Integer num) throws IllegalArgumentException {
		if (num < 1 || num > 2)
			throw new IllegalArgumentException("num");
	}
	
	private void assertAnalogInputNumber(Integer num) throws IllegalArgumentException {
		if (num < 1 || num > 2)
			throw new IllegalArgumentException("num");
	}
	
	private void assertAnalogOutputValue(Integer value) throws IllegalArgumentException {
		if (value < 0 || value > 255)
			throw new IllegalArgumentException("value");
	}
	
	class ListenerThread extends Thread {
		private K8055 _owner;
		private Integer _lastDigitalState = -1;

		public ListenerThread(K8055 owner) {
			_owner = owner;
		}
		
		public void run() {
			try {
				while (true) {
					Integer digitalState = Integer.parseInt(_owner.readFrom("inputs"));
					if (_lastDigitalState == -1)
						_lastDigitalState = digitalState;
					
					if (digitalState != _lastDigitalState) {
						if ((digitalState & 1) != (_lastDigitalState & 1))
							notifyListenersOfDigitalInput(1, (digitalState & 1) == 1);
						
						if ((digitalState & 2) != (_lastDigitalState & 2))
							notifyListenersOfDigitalInput(2, (digitalState & 2) == 2);
						
						if ((digitalState & 4) != (_lastDigitalState & 4))
							notifyListenersOfDigitalInput(3, (digitalState & 4) == 4);
						
						if ((digitalState & 8) != (_lastDigitalState & 8))
							notifyListenersOfDigitalInput(4, (digitalState & 8) == 8);
						
						if ((digitalState & 16) != (_lastDigitalState & 16))
							notifyListenersOfDigitalInput(5, (digitalState & 16) == 16);
						
						if ((digitalState & 32) != (_lastDigitalState & 32))
							notifyListenersOfDigitalInput(6, (digitalState & 32) == 32);
						
						if ((digitalState & 64) != (_lastDigitalState & 64))
							notifyListenersOfDigitalInput(7, (digitalState & 64) == 64);
						
						if ((digitalState & 128) != (_lastDigitalState & 128))
							notifyListenersOfDigitalInput(8, (digitalState & 128) == 128);
								
						_lastDigitalState = digitalState;
					}
					
					Thread.sleep(20);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		private void notifyListenersOfDigitalInput(Integer num, boolean value) {
			Log.d("K8055", "Digital Input" + num.toString() + " changed to: " + (value ? "TRUE" : "FALSE"));
			Object[] listeners = _owner._inputListeners.toArray();
			for (Object inputListener: listeners) {
			    ((IInputListener)inputListener).DigitalInputChanged(num, value);
			}
		}
	}
}
