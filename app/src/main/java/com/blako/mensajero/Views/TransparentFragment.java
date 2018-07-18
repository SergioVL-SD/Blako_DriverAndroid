package com.blako.mensajero.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.blako.mensajero.R;

/**
 * Created by franciscotrinidad on 1/12/16.
 */
public class TransparentFragment extends BaseFragment {


    private  View rootView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        if(rootView == null ) {


            rootView = inflater.inflate(R.layout.bko_transparent_fragment, null);




        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Fragment: TransparentFragment");
    }
}
