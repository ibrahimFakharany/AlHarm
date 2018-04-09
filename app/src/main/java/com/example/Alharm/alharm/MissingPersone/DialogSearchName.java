package com.example.Alharm.alharm.MissingPersone;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.Alharm.alharm.R;

/**
 * Created by 450 G1 on 25/03/2018.
 */

public class DialogSearchName extends MyDialogFragment implements View.OnClickListener {
    EditText nameEt;
    Button searchBtn;
    SearchNameDialogListener nameListener = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_search_name_method, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nameEt = view.findViewById(R.id.name);
        searchBtn = view.findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(this);
        nameListener = (SearchNameDialogListener) listener;

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View view) {
        String name = nameEt.getText().toString();
        if(name!=null){
            nameListener.onFinishNameDialog(name);
            this.dismiss();
        }

    }
}
