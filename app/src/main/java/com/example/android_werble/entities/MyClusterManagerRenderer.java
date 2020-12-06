package com.example.android_werble.entities;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.example.android_werble.ClusterMarker;
import com.example.android_werble.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MyClusterManagerRenderer extends DefaultClusterRenderer<ClusterMarker> {

    private final IconGenerator iconGenerator; //rendering of images
    private final ImageView imageView;
    private final int markerWidth;
    private final int markerHeight;

    public MyClusterManagerRenderer(Context context, GoogleMap map, ClusterManager<ClusterMarker> clusterManager,
                                    IconGenerator iconGenerator, ImageView imageView, int markerWidth, int markerHeight) {
        super(context, map, clusterManager);
        this.iconGenerator = iconGenerator;
        this.imageView = imageView;
        this.markerWidth = markerWidth;
        this.markerHeight = markerHeight;

        iconGenerator = new IconGenerator(context.getApplicationContext());
        imageView = new ImageView(context.getApplicationContext());
        //markerWidth = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        //markerHeight = (int) context.getResources().getDimension(R.dimen.custom_marker_image);
        //imageView.setLayoutParams(new ViewGroup.LayoutParams(markerWidth,markerHeight));
        //int padding = (int) context.getResources().getDimension(R.dimen.custom_marker_padding);
        //imageView.setPadding(padding,padding,padding,padding);
        iconGenerator.setContentView(imageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(@NonNull ClusterMarker item, @NonNull MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        imageView.setImageResource(item.getIconPicture());
        Bitmap icon = iconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(item.getTitle());
    }

    @Override
    protected boolean shouldRenderAsCluster(@NonNull Cluster<ClusterMarker> cluster) {

        return super.shouldRenderAsCluster(cluster);
    }
}
