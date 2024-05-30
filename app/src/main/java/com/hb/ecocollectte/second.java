package com.hb.ecocollectte;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class second extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);


        TextView textView = view.findViewById(R.id.textViewSecond);
        textView.setText("Thanks to artificial intelligence");

        return view;
    }
}