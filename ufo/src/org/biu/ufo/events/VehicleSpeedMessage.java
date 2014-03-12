package org.biu.ufo.events;

import com.openxc.measurements.VehicleSpeed;

public class VehicleSpeedMessage {

	public String vehicleSpeed;
	public double speed;
	
	public VehicleSpeedMessage(VehicleSpeed vehicleSpeed){
		this.vehicleSpeed = String.valueOf(vehicleSpeed.getValue().doubleValue());
		this.speed = vehicleSpeed.getValue().doubleValue();
	}
}
