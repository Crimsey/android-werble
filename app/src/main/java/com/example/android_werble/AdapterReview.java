package com.example.android_werble;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_werble.entities.EventParticipant;
import com.example.android_werble.entities.EventReview;
import com.example.android_werble.entities.User;
import com.example.android_werble.network.ApiService;
import com.example.android_werble.network.RetrofitBuilder;

import java.util.List;

public class AdapterReview extends  RecyclerView.Adapter {

    private Context mContext;

    ApiService service;
    TokenManager tokenManager;

    Call<EventParticipant> callEventParticipant;
    Call<User>callUser;
    
    
    private List<EventReview> mReviews;
    private RecyclerView mRecyclerView;

    private class MyViewHolder extends RecyclerView.ViewHolder {
        public RatingBar eRating;
        public TextView eContent;
        public TextView eAuthor;
        public TextView eDatetime;


        public MyViewHolder(View pItem) {
            super(pItem);
            eRating = (RatingBar) pItem.findViewById(R.id.reviewRating);
            eContent = (TextView) pItem.findViewById(R.id.reviewContent);
            eAuthor = (TextView) pItem.findViewById(R.id.reviewAuthor);
            eDatetime = (TextView) pItem.findViewById(R.id.reviewDatetime);

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
                //int positionToDelete = mRecyclerView.getChildAdapterPosition(v);
                // usuwamy element ze źródła danych
                //mReviews.remove(positionToDelete);
                // poniższa metoda w animowany sposób usunie element z listy
                //notifyItemRemoved(positionToDelete);
            }
        });

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // uzupełniamy layout recenzji
        EventReview review = mReviews.get(position);
        ((MyViewHolder) holder).eRating.setRating(review.getRating());
        ((MyViewHolder) holder).eContent.setText(review.getContent());
        ((MyViewHolder) holder).eAuthor.setText(review.getLogin());
        ((MyViewHolder) holder).eDatetime.setText(review.getCreatedAt().substring(0,10)+" ");


    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }


}
