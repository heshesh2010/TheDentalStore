package com.heshamapps.heshe.thedentalstore.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heshamapps.heshe.thedentalstore.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderPlacedFragment extends Fragment {

    @BindView(R.id.orderid)
    TextView orderidview;

    private String orderid;

    public OrderPlacedFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view   = inflater.inflate(R.layout.fragment_order_placed, container, false);
        ButterKnife.bind(this, view);

        orderid = getArguments().getString("orderid");
        orderidview.setText(orderid);

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
