package org.biu.ufo.ui.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardHeader.OnClickCardHeaderOtherButtonListener;

import org.biu.ufo.R;
import org.biu.ufo.control.events.user.PeekNewDestinationMessage;
import org.biu.ufo.model.Location;
import org.biu.ufo.model.Place;
import org.biu.ufo.ui.activities.MainActivity;
import org.biu.ufo.ui.utils.NavigationIntent;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class RouteOverviewCard extends Card {

    protected TextView mText;
    protected ImageButton mButtonOpenGPS ;
    
    protected Place destination;
    protected boolean isDriving;
    
    public RouteOverviewCard(Context context) {
        super(context, R.layout.card_content_destination_overview);
        destination = null;
        isDriving = false;
        initialize();
    }    

    public Place getDestination() {
		return destination;
	}
    
    public boolean isDriving() {
		return isDriving;
	}

    public void setDestination(Place dest) {
        this.destination = dest;
    }
    
    public void setDrivingState(boolean drivingState) {
        this.isDriving = drivingState;
    }
    
    public String getHeaderTitle() {
        String title = "Ready";
        if(isDriving) {
        	title = "Driving";
        }
        if(destination != null && !TextUtils.isEmpty(destination.getLabel())) {
        	title += " "  + destination.getLabel();
        } 
        
        return title;
    }
    
    private void initialize() {
        CardHeader header = new CardHeader(getContext());
        header.setTitle(getHeaderTitle());        	
        header.setOtherButtonDrawable(R.drawable.card_menu_button_rounded_overflow);
        header.setOtherButtonVisible(true);
        header.setOtherButtonClickListener(new OnClickCardHeaderOtherButtonListener() {
			@Override
			public void onButtonItemClick(Card card, View view) {
				((MainActivity)getContext()).getBus().post(new PeekNewDestinationMessage());
			}
		});
        addCardHeader(header);
        
        //Add ClickListener
        setOnClickListener(new OnCardClickListener() {
            @Override
            public void onClick(Card card, View view) {
            	if(destination != null) {
                    Intent intent = NavigationIntent.getNavigationIntent(
                    		new Location(destination.getAddress().getLatitude(), destination.getAddress().getLongitude()));
                    getContext().startActivity(intent);            		
            	}
            }
        });
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //Retrieve elements
        mText = (TextView) parent.findViewById(R.id.destination_text);

        if (mText != null && destination != null)
            mText.setText(destination.toString());

    }
}
