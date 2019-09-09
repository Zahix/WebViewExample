package com.example.zahid.webviewexample.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.zahid.webviewexample.R;

public class NoInternetScreen extends Fragment {

    View view;

    Animation animation;
    ImageView googleIV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_no_internet,container,false);
        googleIV = (ImageView)view.findViewById(R.id.imageview_no_internet);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

//        animation = AnimationUtils.loadAnimation(getActivity(),R.anim.linear_interpolator);
//        googleIV.startAnimation(animation);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        animation.cancel();
    }
}
