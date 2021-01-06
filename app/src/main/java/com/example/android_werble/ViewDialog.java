package com.example.android_werble;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDialogFragment;

public class ViewDialog extends AppCompatDialogFragment {

    private ViewDialogListener listener;

    public void showDialog(Activity activity){
        listener = (ViewDialog.ViewDialogListener) activity;

        final Dialog dialog = new Dialog(activity);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_deletereview);
        dialog.setTitle("Are you sure?");


        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button delete = (Button) dialog.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onDeleteClick();
                dialog.dismiss();
            }
        });

        dialog.show();}

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            try {
                listener = (ViewDialog.ViewDialogListener) context;
            } catch (ClassCastException e) {
                throw new ClassCastException(context.toString() +
                        "must implement DeleteReviewListener");
            }
        }


        public interface ViewDialogListener {
            void onDeleteClick();
        }

}
