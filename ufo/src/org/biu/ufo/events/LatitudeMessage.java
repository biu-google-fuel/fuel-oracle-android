package org.biu.ufo.events;

import org.biu.ufo.Formats;

import com.openxc.measurements.Latitude;

public class LatitudeMessage {

	public String latitudeMessage;
	public double latitude;
	
	public LatitudeMessage(Latitude latitude){
		this.latitudeMessage = Formats.cordsformatter.format(latitude.getValue().doubleValue());
		this.latitude = latitude.getValue().doubleValue();
	}
}
