package com.example.Alharm.alharm.MissingPersone;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.Alharm.alharm.R;

/**
 * Created by 450 G1 on 25/03/2018.
 */

public class DialogSearch extends DialogFragment implements View.OnClickListener {
    private static final int REQUEST_PERMISSION_SCREEN = 1001;
    Button nameBtn, imgBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            dismiss();
            AlertDialog.Builder alert1 = new AlertDialog.Builder(getActivity(), R.style.AlertDialogStyle);
            alert1.setTitle("");
            alert1.setMessage("من فضلك فعل الصلاحيات");
            alert1.setCancelable(false);
            alert1.setPositiveButton("موافق", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getActivity().getPackageName()));
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    startActivityForResult(intent, REQUEST_PERMISSION_SCREEN);
                }
            });

            alert1.setNegativeButton("الغاء", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dismiss();
                }
            });
            alert1.show();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dialog_search_method, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        nameBtn = view.findViewById(R.id.name_btn);
        imgBtn = view.findViewById(R.id.image_btn);

        nameBtn.setOnClickListener(this);
        imgBtn.setOnClickListener(this);


        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        MyDialogFragment fragment = null;
        switch (id) {
            case R.id.name_btn:
                fragment = new DialogSearchName();
                break;
            case R.id.image_btn:
                fragment = new DialogSearchImage();
                break;
        }
        fragment.setListener((SearchListener) getActivity());
        showfragment(fragment);
    }

    private void showfragment(MyDialogFragment fragment) {
        fragment.show(getActivity().getSupportFragmentManager(), "");
        this.dismiss();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PERMISSION_SCREEN) {

            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                dismiss();
            }

        }
    }
}
