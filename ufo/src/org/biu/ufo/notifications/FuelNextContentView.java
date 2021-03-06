package org.biu.ufo.notifications;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;
import org.biu.ufo.R;
import org.biu.ufo.control.components.RouteEstimator.EstimatedRoute;
import org.biu.ufo.events.control.FuelRecommendationMessage;
import org.biu.ufo.rest.Station;
import org.biu.ufo.ui.utils.UnitConverter;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

@EViewGroup(R.layout.popup_fuel_next_layout)
public class FuelNextContentView extends RelativeLayout {

	@ViewById
	TextView popup_fuel_price;
	
	@ViewById
	TextView popup_fuel_price_currency;
	
	@ViewById
	TextView popup_distance;
	
	@ViewById
	TextView popup_distance_unit;

	@ViewById
	TextView popup_station_address;
	
	@ViewById
	ImageView popup_gas_station_logo;

	@ViewById
	Button popup_more_button;
	
	public FuelNextContentView(Context context) {
		this(context, null);
	}

	public FuelNextContentView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void fillContent(final FuelNextNotification notification, FuelRecommendationMessage message, EstimatedRoute estimatedRouteToStation) {
		Station station = message.getTopStation();
		popup_fuel_price.setText(String.format("%.2f", station.getPrice()));
		popup_fuel_price_currency.setText(UnitConverter.getResourceForPriceCurrency(station.getPriceCurrency()));
		
		if(estimatedRouteToStation != null) {
			popup_distance.setText(String.valueOf(estimatedRouteToStation.fullRoute.getDuration() / 1000));
			popup_distance_unit.setText("min");			
		} else {
			popup_distance.setText(String.format("%.2f", station.getDistance(message.getLocationAtRecommendTime())));
			popup_distance_unit.setText(UnitConverter.getResourceForDistanceUnit(station.getDistanceUnit()));			
		}	

		popup_station_address.setText(station.getAddress());
		popup_gas_station_logo.setImageResource(UnitConverter.getResourceForStationLogo(station.getCompanyEnglish()));					
		popup_more_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				notification.showMore();
			}
		});
		
		if(message.getFuelLevelAtRecommendTime() < 15) {
			setBackgroundColor(Color.RED);
		}
	}
}
