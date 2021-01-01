package com.example.android_werble;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import butterknife.BindView;

public class ReviewEditActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "EventEditActivity";

    //@BindView(R.id.reviewEditContent2)
    TextInputEditText reviewEditContent;
    //@BindView(R.id.reviewEditRating2)
    RatingBar reviewEditRating;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviewedit);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
