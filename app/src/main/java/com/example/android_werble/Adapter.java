package com.example.android_werble;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Event;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter{

    private List<Event> mEvents;
    private RecyclerView mRecyclerView;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eName;
        public TextView eLocation;
        public TextView eDatetime;


        public MyViewHolder(View pItem) {
            super(pItem);
            eName = (TextView) pItem.findViewById(R.id.eventName);
            eLocation = (TextView) pItem.findViewById(R.id.eventLocation);
            eDatetime = (TextView) pItem.findViewById(R.id.eventDatetime);

        }
    }

    public Adapter(List<Event> pEvents, RecyclerView pRecyclerView) {
        //super();
        mEvents = pEvents;
        mRecyclerView = pRecyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.test, parent, false);

        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // odnajdujemy indeks klikniętego elementu
                int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                // usuwamy element ze źródła danych
                mEvents.remove(positionToDelete);
                // poniższa metoda w animowany sposób usunie element z listy
                notifyItemRemoved(positionToDelete);
            }
        });


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // uzupełniamy layout wydarzenia
        Event event = mEvents.get(position);
        ((MyViewHolder) holder).eName.setText(event.getName());
        ((MyViewHolder) holder).eLocation.setText(event.getLocation());
        ((MyViewHolder) holder).eDatetime.setText(event.getDatetime());

    }

    @Override
    public int getItemCount() {
        return mEvents.size();
    }
}




/*import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.android_werble.entities.Event;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Adapter extends ArrayAdapter<Event> implements Filterable{
    private OnItemClickListener listener;
    private Date today=new Date();
    private ArrayList<Event> eventList;
    private ArrayList<Event> tmpList;

    public Adapter(@NonNull Context context
            ,List<Event> resource) {
        super(context,-1,resource);
        listener=(OnItemClickListener) context;
        this.eventList= (ArrayList<Event>) resource;
        tmpList=(ArrayList<Event>)resource;
        System.out.println("WYWOŁANY KONSTRUKTOR");
    }
    interface OnItemClickListener{
        void onItemClickListener(Event event);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        System.out.println("performuje");
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults filtered=new FilterResults();
                ArrayList<Event> results=new ArrayList<Event>();

                if(constraint.toString().length()>0){
                    if(eventList!=null && eventList.size()>0){
                        for(final Event e:eventList){
                            if(e.getName().toLowerCase().contains(constraint.toString())){
                                results.add(e);
                            }
                        }
                    }
                    filtered.values=results;
                    filtered.count=results.size();
                }
                else {
                    filtered.values=tmpList;
                    filtered.count=tmpList.size();
                    System.out.println("count : "+filtered.count);
                }
                return filtered;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(constraint.length()>0 && results.count>0){
                    System.out.println("constraint: "+constraint+" len: "+constraint.length());
                    eventList.clear();
                    eventList.addAll((ArrayList<Event>) results.values);
                    notifyDataSetChanged();
                }
                else{
                    eventList=tmpList;
                    notifyDataSetChanged();
                }
            }
        };
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView
            , @NonNull ViewGroup parent){
        final Event event=getItem(position);
        if(convertView==null) {
            convertView= LayoutInflater
                    .from(getContext())
                    .inflate(R.layout.event,parent,false);
        }
        TextView eventName=convertView.findViewById(R.id.eventName);
        TextView eventDatetime=convertView.findViewById(R.id.eventDatetime);
        TextView eventLocation=convertView.findViewById(R.id.eventLocation);
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yy");
        /*try {
            Date drugExpdate=format.parse(drug.getExpDate());
            if(today.after(drugExpdate)) {
                drugDate.setTextColor(Color.RED);
            }else{
                drugDate.setTextColor(Color.WHITE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }*/

  /*      eventName.setText(event.getName());
        eventDatetime.setText(event.getDatetime());
        /*if(drug.getUnit()==1) {
            drugQuantity.setText(String.valueOf(drug.getQuantity())+" szt");
        }
        else{
            drugQuantity.setText(String.valueOf(drug.getQuantity())+" ml");
        }*/
      /*  convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClickListener(event);
            }
        });
        return convertView;
    }

}*/