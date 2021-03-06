package org.biu.ufo.ui.activities;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.ViewToClickToExpand;
import it.gmariotti.cardslib.library.view.CardView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.biu.ufo.OttoBus;
import org.biu.ufo.R;
import org.biu.ufo.control.monitors.DrivingStatusMonitor.DrivingStatus;
import org.biu.ufo.control.utils.Calculator;
import org.biu.ufo.events.car.raw.EngineSpeedMessage;
import org.biu.ufo.events.car.raw.FuelLevelMessage;
import org.biu.ufo.events.car.raw.LocationMessage;
import org.biu.ufo.events.car.raw.VehicleSpeedMessage;
import org.biu.ufo.events.control.EstimatedDestinationMessage;
import org.biu.ufo.events.control.FuelRecommendationMessage;
import org.biu.ufo.events.control.TripCompleted;
import org.biu.ufo.events.user.ShowScreenLastTrip;
import org.biu.ufo.model.Feedback;
import org.biu.ufo.model.Location;
import org.biu.ufo.rest.Station;
import org.biu.ufo.tracker.ScreenDisplayEvent;
import org.biu.ufo.ui.cards.RecommendationCard;
import org.biu.ufo.ui.cards.RecommendationCardExpandInside;
import org.biu.ufo.ui.cards.RouteOverviewCard;
import org.biu.ufo.ui.cards.SquareCarDataCard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.otto.Subscribe;

@EFragment(R.layout.fragment_main)
public class FragmentMain extends Fragment {

	private static final int FUEL_LEVEL_PER_DAY = 20;
	private static final String FUEL_SUGGESTION_MSG_DAYS_PART1 = "You can drive for ";
	private static final String FUEL_SUGGESTION_MSG_DAYS_PART2 = " days.";
	private static final String FUEL_SUGGESTION_MSG_DEFAULT = "Drive Carefully!";
	private static final String FUEL_SUGGESTION_MSG_NO_NEAR_STATIONS = "No stations on near path";
	protected static final String EMAIL_ADDRESS = "baruchnoah@gmail.com";

	@Bean
	OttoBus bus;

	@ViewById(R.id.card_scrollview)
	ScrollView mScrollView;

	@ViewById
	CardView card_route_overview;

	@ViewById
	CardView card_fuel_suggestion;

	@ViewById
	Button more_button;
	
	@ViewById
	Button trip_summary_button;
	
	@ViewById
	CardView card_fuel_level;

	@ViewById
	CardView card_engine_speed;

	@ViewById
	CardView card_vehicle_speed;

	Context context;

	RecommendationCard recommendationCard;

	Location currentLocation;

	boolean displaysFuelSuggestion = false;

	@Override
	public void onResume() {
		super.onResume();
		bus.register(this);
		
		bus.post(new ScreenDisplayEvent(getClass().getSimpleName()));
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		bus.unregister(this);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		context = activity.getApplicationContext();
	}

	@AfterViews
	void initialize() {
		RouteOverviewCard routeOverviewCard = new RouteOverviewCard(getActivity());
		card_route_overview.setCard(routeOverviewCard);

		//		FuelSuggestionCard fuelSuggestionCard = new FuelSuggestionCard(getActivity());
		//		card_fuel_suggestion.setCard(fuelSuggestionCard);
		initFuelSuggestion(true, FUEL_SUGGESTION_MSG_DEFAULT);

		more_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), MainActivity_.class).putExtra(MainActivity.SELECT_SCREEN, MainActivity.SCREEN_STATIONS_LIST));

			}
		});
		
		trip_summary_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				bus.post(new ShowScreenLastTrip());
			}
		});


		SquareCarDataCard fuelLevelCard = new SquareCarDataCard(getActivity(), "Fuel", 0);
		card_fuel_level.setCard(fuelLevelCard);

		SquareCarDataCard engineSpeedCard = new SquareCarDataCard(getActivity(), "Engine", 0);
		card_engine_speed.setCard(engineSpeedCard);

		SquareCarDataCard vehicleCard = new SquareCarDataCard(getActivity(), "Speed", 0);
		card_vehicle_speed.setCard(vehicleCard);
	}

	private void initFuelSuggestion(boolean init, String message) {
		displaysFuelSuggestion = false;

		/*		BasicInfoCard card = new BasicInfoCard(getActivity(), message);
		 */
		//TODO: maybe create a custom card header 
		//Create a Card
		//Create a Card
		Card card = new Card(getActivity());

		//Create a CardHeader
		CardHeader header = new CardHeader(getActivity());

		//Set the header title
		header.setTitle(message);

		card.addCardHeader(header);

		//		card.setExpanded(true);
		//		card_fuel_suggestion.setExpanded(true);


		if (init){
			card_fuel_suggestion.setCard(card);
		}else{
			card_fuel_suggestion.replaceCard(card);
		}

		more_button.setVisibility(View.GONE);
	}

	@Subscribe
	public void onDrivingStatusChanged(DrivingStatus status) {		
		RouteOverviewCard oldCard = (RouteOverviewCard)card_route_overview.getCard();

		RouteOverviewCard card = new RouteOverviewCard(oldCard.getContext());
		card.setDestination(oldCard.getDestination());
		card.setDrivingState(status == DrivingStatus.DRIVING);
		card.initialize();
		
		card_route_overview.replaceCard(card);
	}

	public void showFeedbackDialog() {

		// Created a new Dialog
		final Dialog dialog = new Dialog(getActivity());

		// Set the title
		dialog.setTitle("Feedback");

		// inflate the layout
		dialog.setContentView(R.layout.fragment_edit_name);


		final TextView comment = (TextView)dialog.findViewById(R.id.editText1);
		final RatingBar stars = (RatingBar)dialog.findViewById(R.id.ratingBar);

		Button button = (Button) dialog.findViewById(R.id.SubmitButton);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Feedback driverFeedback = new Feedback();
				driverFeedback.setComment(comment.getEditableText());
				driverFeedback.setRating((int)stars.getRating());
				Toast.makeText(getActivity().getApplicationContext(),"Thank-you :)", Toast.LENGTH_SHORT).show();
		email(getActivity(),EMAIL_ADDRESS,"Feedback",driverFeedback.getComment()+ " " +driverFeedback.getStarsCount());
		dialog.dismiss();
			}
		});
		// Display the dialog
		dialog.show();
	}




	public void email(Context context, String to, String subject, String body) {
		StringBuilder builder = new StringBuilder("mailto:" + Uri.encode(to));
		if (subject != null) {
			builder.append("?subject=" + Uri.encode(Uri.encode(subject)));
			if (body != null) {
				builder.append("&body=" + Uri.encode(Uri.encode(body)));
			}
		}
		String uri = builder.toString();
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse(uri));
		context.startActivity(intent);
	}

	@Subscribe
	public void onRouteCompletedMessage(TripCompleted message){
		showFeedbackDialog();
	}

	@Subscribe
	public void onEstimatedDestinationMessage(EstimatedDestinationMessage message) {
		RouteOverviewCard card = (RouteOverviewCard)card_route_overview.getCard();
		card.setDestination(message.getPlace());
		card_route_overview.refreshCard(card);
	}

	@UiThread
	@Subscribe
	public void onFuelRecommendationMessage(FuelRecommendationMessage message) {
		Log.e("FragmentMain", "onFuelNextRecommendation");
		if(message.shouldFuel()) {
			Log.e("FragmentMain", "shouldFuel");
			if(!message.getStations().isEmpty()) {
				Log.e("FragmentMain", "!isEmpty");
				displaysFuelSuggestion = true;
				Station station = message.getTopStation();
				//Create a Card
				recommendationCard = new RecommendationCard(getActivity(), message, station, currentLocation);

				//Set card in the cardView
				ViewToClickToExpand viewToClickToExpand =
						ViewToClickToExpand.builder()
						.highlightView(false)
						.setupView(card_fuel_suggestion);
				recommendationCard.setViewToClickToExpand(viewToClickToExpand);

				card_fuel_suggestion.setExpanded(true);
				recommendationCard.setExpanded(true);
				card_fuel_suggestion.replaceCard(recommendationCard);

				if (message.getStations().size()>1){
					more_button.setVisibility(View.VISIBLE);
				}
			}else{
				initFuelSuggestion(false, FUEL_SUGGESTION_MSG_NO_NEAR_STATIONS);
			}
		} else{
			initFuelSuggestion(false, FUEL_SUGGESTION_MSG_DEFAULT);
		}
	}

	@UiThread
	@Subscribe
	public void onFuelLevelUpdate(FuelLevelMessage message){
		SquareCarDataCard card = (SquareCarDataCard)card_fuel_level.getCard();
		card.setLine1Text(String.format("%.2f", message.getFuelLevelValue()) + " %");
		card.setLine2Text("");

		//		card.setBackgroundResource(new ColorDrawable(Color.RED));

		card_fuel_level.refreshCard(card);

		if (!displaysFuelSuggestion){
			int fuelDays = (int) (message.getFuelLevelValue()/FUEL_LEVEL_PER_DAY);
			String daysMessage = FUEL_SUGGESTION_MSG_DAYS_PART1 + String.valueOf(fuelDays)
					+ FUEL_SUGGESTION_MSG_DAYS_PART2 + " " + FUEL_SUGGESTION_MSG_DEFAULT;
			initFuelSuggestion(false, daysMessage);
		}


		//		fuelLevelLayout.setBackgroundResource(message.background);
		//		fuelLevelMainMessage.setText(message.mainMessage);
		//		fuelLevelsubMessage1.setText(message.subMessage_1);
		//		fuelLevelsubMessage2.setText(message.subMessage_2);
	}

	@UiThread
	@Subscribe
	public void onEngineSpeedUpdate(EngineSpeedMessage message) {
		SquareCarDataCard card = (SquareCarDataCard)card_engine_speed.getCard();
		card.setLine1Text(String.format("%d", message.getSpeed()));
		card.setLine2Text("RPM");
	}

	@UiThread
	@Subscribe
	public void onVehicleSpeedUpdate(VehicleSpeedMessage message) {

		SquareCarDataCard card = (SquareCarDataCard)card_vehicle_speed.getCard();
		card.setLine1Text(String.format("%.2f", message.speed));
		card.setLine2Text("km/h");
	}

	@UiThread
	@Subscribe
	public void onLocationUpdate(LocationMessage message) {
		currentLocation = message.getLocation();
		if (recommendationCard != null){
			RecommendationCardExpandInside recommendationCardInside = (RecommendationCardExpandInside)recommendationCard.getCardExpand();
			Station station = recommendationCardInside.getStation();
			double currentDistance = Calculator.distance(station.getLocation(), message.location);
			double distance = recommendationCardInside.getStationDistance();

			//above than 100 meter - update
			if (Math.abs(distance - currentDistance) >= 0.1){
				recommendationCard.setCurrentLocation(message.getLocation());
			}
		}
	}

}
