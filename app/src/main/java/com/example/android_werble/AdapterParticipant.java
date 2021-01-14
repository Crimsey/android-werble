package com.example.android_werble;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.EventParticipant;

import java.util.List;

public class AdapterParticipant extends RecyclerView.Adapter{

    private List<EventParticipant> mParticipants;
    private RecyclerView mRecyclerView;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eLogin;
        public TextView eFirstName;
        public TextView eLastName;


        public MyViewHolder(View pItem) {
            super(pItem);
            eLogin = (TextView) pItem.findViewById(R.id.participantLogin);
        }
    }

    public AdapterParticipant(List<EventParticipant> pParticipants, RecyclerView pRecyclerView) {
        mParticipants = pParticipants;
        mRecyclerView = pRecyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // uzupełniamy layout wydarzenia
        EventParticipant participant = mParticipants.get(position);
        ((MyViewHolder) holder).eLogin.setText(participant.getLogin());

    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }
}
