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

public class AdapterEvent extends RecyclerView.Adapter implements Filterable {

    private List<Event> mEvents;
    private List<Event> displayedList;


    private RecyclerView mRecyclerView;
    private OnNoteListener mOnNoteListener;

    private Context contextAdapter;

    /*public AdapterEvent(List<Event> events, OnNoteListener onNoteListener){
        this.mEvents = events;
        this.mOnNoteListener = onNoteListener;
    }*/

    //private ArrayList<Note>
    ConstraintLayout eventID;
    Call<Message> callJoin;
    ApiService service;

    @NonNull
    @Override
    public Filter getFilter() {
        System.out.println("performuje");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filtered=new FilterResults();
                List<Event> results = new ArrayList<>();

                if(constraint.toString().length()>0){
                    if(mEvents!=null && mEvents.size()>0){
                        for(final Event e:mEvents){
                            if(e.getName().toLowerCase().contains(constraint.toString())){
                            //|| e.getLocation().toLowerCase().contains(constraint.toString()))
                                results.add(e);
                            }
                        }
                    }
                    filtered.values=results;
                    filtered.count=results.size();
                }
                else {
                    filtered.values= new ArrayList<>(displayedList);
                    filtered.count=displayedList.size();
                    System.out.println("count : "+filtered.count);
                }
                return filtered;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(constraint.length()>0 && results.count>0){
                    System.out.println("constraint: "+constraint+" len: "+constraint.length());
                    mEvents.clear();
                    mEvents.addAll((ArrayList<Event>) results.values);
                    notifyDataSetChanged();
                }
                else{
                    mEvents=new ArrayList<>(displayedList);
                    notifyDataSetChanged();
                }
            }
        };
    }




    private class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView eName;
        public TextView eLocation;
        public TextView eDatetime;
        public Button join;
        public TextView eDistance;

        OnNoteListener onNoteListener;

        public MyViewHolder(View pItem, OnNoteListener onNoteListener) {
            super(pItem);
            eName = (TextView) pItem.findViewById(R.id.eventName);
            eLocation = (TextView) pItem.findViewById(R.id.eventLocation);
            eDatetime = (TextView) pItem.findViewById(R.id.eventDatetime);
            join = (Button) pItem.findViewById(R.id.join);
            eDistance = (TextView) pItem.findViewById(R.id.eventDistance);

            this.onNoteListener = onNoteListener;
            pItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public AdapterEvent(List<Event> pEvents, RecyclerView pRecyclerView,OnNoteListener pOnNoteListener,Context context) {
        //super();
        mEvents = pEvents;
        mRecyclerView = pRecyclerView;
        mOnNoteListener = pOnNoteListener;
        contextAdapter=context;

        displayedList = new ArrayList<>(mEvents);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event, parent, false);

        eventID = view.findViewById(R.id.eventID);


        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // odnajdujemy indeks klikniętego elementu
                //int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                // usuwamy element ze źródła danych
                //mEvents.remove(positionToDelete);
                // poniższa metoda w animowany sposób usunie element z listy
                //notifyItemRemoved(positionToDelete);
            }
        });



        return new MyViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Integer eId;
        // uzupełniamy layout wydarzenia
        Event event = mEvents.get(position);
        int eId = event.getEventId();

        ((MyViewHolder) holder).eName.setText(event.getName());
        ((MyViewHolder) holder).eLocation.setText("Location: "+event.getLocation());
        ((MyViewHolder) holder).eDatetime.setText("Datetime: "+event.getDatetime());
        ((MyViewHolder) holder).eDistance.setText("Distance: "+event.getDistance().toString());

        ((MyViewHolder) holder).join.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //eId = event.getEventId();
                        //Intent i = new Intent(this, SingleEventActivity.class);
                        Log.w(TAG,"eId: "+eId);
                        callJoin = service.joinEvent(eId);
                        callJoin.enqueue(new Callback<Message>() {
                            @Override
                            public void onResponse(Call<Message> call, Response<Message> response) {
                                Log.w(TAG, "You have joined!: " + response);

                            }

                            @Override
                            public void onFailure(Call<Message> call, Throwable t) {
                                Log.w(TAG, "onFailure: " + t.getMessage());

                            }
                        });
                    }
                }
        );

        eventID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int variable = 1;
                Intent myIntent = new Intent(contextAdapter, EventSingleActivity.class);
                myIntent.putExtra("event_id",String.valueOf(eId));
                myIntent.putExtra("variable",String.valueOf(variable));
                System.out.println(TAG+" variable= "+variable);
                //send additional variable to check if click is from adapter or from map
                contextAdapter.startActivity(myIntent);

            }
        });

    }


    public interface OnNoteListener{
        void  onNoteClick(int position);
            //Intent intent = new Intent(this, EventActivity.class);
    }

    @Override
    public int getItemCount() {

        return mEvents.size();
    }
}
