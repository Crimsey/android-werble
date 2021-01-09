package com.example.android_werble;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Event;
import com.example.android_werble.entities.Message;
import com.example.android_werble.network.ApiService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static androidx.core.content.ContextCompat.startActivity;

public class AdapterEvent extends RecyclerView.Adapter<AdapterEvent.ViewHolder> implements Filterable {

    private List<Event> mEvents;
    private List<Event> displayedList;
    private Context contextAdapter;
    ConstraintLayout eventID;
    Call<Message> callJoin;
    ApiService service;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView eName;
        public TextView eLocation;
        public TextView eDatetime;
        public Button join;
        public TextView eDistance;
        public Event eEvent;

        public ViewHolder(View pItem) {
            super(pItem);
            eName = (TextView) pItem.findViewById(R.id.eventName);
            eLocation = (TextView) pItem.findViewById(R.id.eventLocation);
            eDatetime = (TextView) pItem.findViewById(R.id.eventDatetime);
            //join = (Button) pItem.findViewById(R.id.join);
            eDistance = (TextView) pItem.findViewById(R.id.eventDistance);


            pItem.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View pItem) {
                     int variable = 1;
                     Intent myIntent = new Intent(contextAdapter, EventSingleActivity.class);
                     myIntent.putExtra("event_id", String.valueOf(eEvent.getEventId()));
                     myIntent.putExtra("variable", String.valueOf(variable));

                     //send additiona variable to check if click is from adapter or from map
                     contextAdapter.startActivity(myIntent);
                 }
             }
            );



        }
    }

    public AdapterEvent(List<Event> pEvents, Context context) {
        //super();
        mEvents = pEvents;
        displayedList = new ArrayList<>(pEvents);
        contextAdapter = context;
    }


    @NonNull
    @Override
    public Filter getFilter() {
        System.out.println("performuje");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filtered = new FilterResults();
                List<Event> results = new ArrayList<>();

                if (constraint.toString().length() > 0) {
                    if (mEvents != null && mEvents.size() > 0) {
                        for (final Event e : mEvents) {
                            if (e.getName().toLowerCase().contains(constraint.toString())) {
                                //|| e.getLocation().toLowerCase().contains(constraint.toString()))
                                results.add(e);
                            }
                        }
                    }
                    filtered.values = results;
                    filtered.count = results.size();
                } else {
                    filtered.values = new ArrayList<>(displayedList);
                    filtered.count = displayedList.size();
                }
                return filtered;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (constraint.length() > 0 && results.count > 0) {
                    mEvents.clear();
                    mEvents.addAll((ArrayList<Event>) results.values);
                    notifyDataSetChanged();
                } else {
                    mEvents = new ArrayList<>(displayedList);
                    notifyDataSetChanged();
                }
            }
        };
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.eEvent = mEvents.get(position);
        holder.eName.setText(holder.eEvent.getName());
        holder.eLocation.setText("Location: " + holder.eEvent.getLocation());
        holder.eDatetime.setText("Datetime: " + holder.eEvent.getDatetime());
        holder.eDistance.setText("Distance: " + holder.eEvent.getDistance().toString()+"km");


    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}
