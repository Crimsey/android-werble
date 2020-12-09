package com.example.android_werble;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.Event;
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
            eFirstName = (TextView) pItem.findViewById(R.id.participantFirstName);
            eLastName = (TextView) pItem.findViewById(R.id.participantLastName);
        }
    }

    public AdapterParticipant(List<EventParticipant> pParticipants, RecyclerView pRecyclerView) {
        //super();
        mParticipants = pParticipants;
        mRecyclerView = pRecyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.participant, parent, false);

        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // odnajdujemy indeks klikniętego elementu
                int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                // usuwamy element ze źródła danych
                mParticipants.remove(positionToDelete);
                // poniższa metoda w animowany sposób usunie element z listy
                notifyItemRemoved(positionToDelete);
            }
        });


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // uzupełniamy layout wydarzenia
        EventParticipant participant = mParticipants.get(position);
        ((MyViewHolder) holder).eLogin.setText(participant.getLogin());
        ((MyViewHolder) holder).eFirstName.setText(participant.getFirstName());
        ((MyViewHolder) holder).eLastName.setText(participant.getLastName());

    }

    @Override
    public int getItemCount() {
        return mParticipants.size();
    }
}
