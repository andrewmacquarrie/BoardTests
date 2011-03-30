package com.awsmnss.boardtest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;
import android.view.Window;

public class BoardTest extends Activity implements IInputListener {
	private SeekBar slider;
	private K8055 k8055;
	private ImageView[] digitalInputImages;
	private Handler hRefresh;
	private final int UPDATE_DIGITAL_INPUT = 1234567;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.main);
        setProgressBarVisibility(true);

        slider = (SeekBar) this.findViewById(R.id.slider);
        slider.setOnSeekBarChangeListener(changeListener);
        
        digitalInputImages = new ImageView[5];
        
        digitalInputImages[0] = (ImageView)this.findViewById(R.id.di1);
        digitalInputImages[1] = (ImageView)this.findViewById(R.id.di2);
        digitalInputImages[2] = (ImageView)this.findViewById(R.id.di3);
        digitalInputImages[3] = (ImageView)this.findViewById(R.id.di4);
        digitalInputImages[4] = (ImageView)this.findViewById(R.id.di5);
        
		hRefresh = new Handler(){
			@Override
			public void handleMessage(Message msg) {
			switch(msg.what){
			     case UPDATE_DIGITAL_INPUT:
			    	 digitalInputImages[msg.arg1 - 1].setImageResource(msg.arg2 == 1 ? R.drawable.bullet_green : R.drawable.bullet_red);
			       break;
			   }
			}
		};
        
        try {
        	k8055 = new K8055(0);
        	
        	for (int i = 1; i < 6; i++) {
        		digitalInputImages[i - 1].setImageResource(k8055.getDigialInput(i) ? R.drawable.bullet_green : R.drawable.bullet_red);
        	}
        	
        	k8055.addListener(this);
        } catch (K8055CommunicationException e) {
        	e.printStackTrace();
        }
    }
    
	@Override
	protected void onDestroy() {
		
		k8055 = null;
		super.onDestroy();
	}
    
	private OnSeekBarChangeListener changeListener = new OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar v, int progress, boolean fromUser) {
        	try {
				k8055.setAnalogOutput(1, progress);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	};
	
    public void toggleOutput(View view) {
    	Integer num = Integer.parseInt((String)view.getTag());
    	boolean on = ((ToggleButton)view).isChecked();
    	try {
			k8055.setDigitalOutput(num, on);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

	@Override
	public void AnalogInputChanged(Integer num, Integer value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void DigitalInputChanged(Integer num, boolean value) {
		hRefresh.sendMessage(Message.obtain(hRefresh, UPDATE_DIGITAL_INPUT, num, value ? 1 : 0));
	}
}