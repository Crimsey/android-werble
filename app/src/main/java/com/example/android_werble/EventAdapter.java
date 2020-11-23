package com.example.android_werble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Data;
import com.example.android_werble.entities.Event;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventAdapterVH> {

    private List<Data<Event>> eventList;
    private Context context;

    public EventAdapter() {
    }

    public void setData(List<Data<Event>> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }



    @NonNull
    @Override
    public EventAdapterVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new EventAdapter.EventAdapterVH(LayoutInflater.from(context).inflate(R.layout.row_events,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull EventAdapterVH holder, int position) {
        Event event = eventList.get(position);

        String eventName = event.getName();
        String prefix;
        if(event.getEventStatusId() !=99){
            prefix ="A";
        }else {
            prefix ="D";
        }

        holder.prefix.setText(prefix);
        holder.eventName.setText(eventName);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public class EventAdapterVH extends RecyclerView.ViewHolder{

        TextView eventName;
        TextView prefix;
        ImageView imageView;

        public EventAdapterVH(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            prefix = itemView.findViewById(R.id.prefix);
            imageView = itemView.findViewById(R.id.imageMore);
        }
    }
}
