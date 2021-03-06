package org.biu.ufo.events.control;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.biu.ufo.control.utils.UnitConverter;
import org.biu.ufo.model.Location;
import org.biu.ufo.rest.Station;
import org.biu.ufo.rest.Station.CapacityUnit;

public class FuelRecommendationMessage {
	public static final double STATION_SCORE_PRICE = 10.0;//0.8;
	public static final double STATION_SCORE_ROUTE_DIS = 0.2; 

	
	private long time;
	
	Double fuelAmount;
	Double fuelLevelAtRecommendTime;

	Location locationAtRecommendTime;
	List<Station> stations = new LinkedList<Station>();
	List<Station> sortedStations;

	public FuelRecommendationMessage() {
		this.time = System.currentTimeMillis();
	}
	
	public long getTime() {
		return time;
	}
	
	public void setFuelLevel(double fuelLevel) {
		this.fuelLevelAtRecommendTime = fuelLevel;

	}
	
	public Double getFuelLevelAtRecommendTime() {
		return fuelLevelAtRecommendTime;
	}
	
	public void setLocation(Location locationAtRecommendTime) {
		this.locationAtRecommendTime = locationAtRecommendTime;
	}
	
	public Location getLocationAtRecommendTime() {
		return locationAtRecommendTime;
	}
	
	public void addStations(List<Station> stations) {
		for(Station station : stations) {
			if(!this.stations.contains(station)) {
				this.stations.add(station);
			}
		}
	}
	
	public void sortStations() {
		Collections.sort(stations, new Comparator<Station>() {

			@Override
			public int compare(Station lhs, Station rhs) {
				
				//return Double.compare(lhs.getDistance(locationAtRecommendTime), rhs.getDistance(locationAtRecommendTime));
				
				double lhs_score = lhs.getPrice()*STATION_SCORE_PRICE 
						//+ lhs.getDistance(locationAtRecommendTime)*(1-(STATION_SCORE_PRICE))
						+ lhs.getDistanceFromRoute()*(STATION_SCORE_ROUTE_DIS)
						;

				double rhs_score = rhs.getPrice()*STATION_SCORE_PRICE 
						//+ rhs.getDistance(locationAtRecommendTime)*(1-(STATION_SCORE_PRICE))
						+ rhs.getDistanceFromRoute()*(STATION_SCORE_ROUTE_DIS)
						;

				//the lower the better (lower price, closer...)
				return Double.compare(lhs_score, rhs_score);

			}
		});
		sortedStations = stations;
	}

	public List<Station> getStations() {
		if (sortedStations == null){
			sortStations();
		}
		return sortedStations;
	}
	
	
	public Station getTopStation() {
		if(stations.isEmpty()) {
			return null;
		}
		return getStations().get(0);
	}	

	public boolean shouldFuel() {
		return locationAtRecommendTime != null;
	}

	public Double getFuelAmount(CapacityUnit unit) {
		return UnitConverter.getAverageGasTankSize(unit) * ((100 - fuelLevelAtRecommendTime)/100.0);
	}
	
}
