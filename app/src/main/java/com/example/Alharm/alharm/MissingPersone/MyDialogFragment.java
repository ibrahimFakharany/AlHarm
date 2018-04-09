package com.example.Alharm.alharm.MissingPersone;

import android.support.v4.app.DialogFragment;

/**
 * Created by 450 G1 on 25/03/2018.
 */

public class MyDialogFragment extends DialogFragment{
    protected SearchListener listener;

    public void setListener(SearchListener listener) {
        this.listener = listener;
    }


}
