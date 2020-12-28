package com.example.android_werble;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.EventParticipant;
import com.example.android_werble.entities.EventReview;

import java.util.List;

public class AdapterReview extends  RecyclerView.Adapter {

    private List<EventReview> mReviews;
    private RecyclerView mRecyclerView;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView eRating;
        public TextView eContent;

        public MyViewHolder(View pItem) {
            super(pItem);
            eRating = (TextView) pItem.findViewById(R.id.reviewRating);
            eContent = (TextView) pItem.findViewById(R.id.reviewContent);
        }
    }

    public AdapterReview(List<EventReview> pReviews, RecyclerView pRecyclerView) {
        mReviews = pReviews;
        mRecyclerView = pRecyclerView;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review, parent, false);

        // dla elementu listy ustawiamy obiekt OnClickListener,
        // który usunie element z listy po kliknięciu na niego

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // odnajdujemy indeks klikniętego elementu
                int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                // usuwamy element ze źródła danych
                mReviews.remove(positionToDelete);
                // poniższa metoda w animowany sposób usunie element z listy
                notifyItemRemoved(positionToDelete);
            }
        });


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // uzupełniamy layout recenzji
        EventReview review = mReviews.get(position);
        ((MyViewHolder) holder).eRating.setText(String.valueOf(review.getRating()));
        ((MyViewHolder) holder).eContent.setText(review.getContent());

    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }


}
