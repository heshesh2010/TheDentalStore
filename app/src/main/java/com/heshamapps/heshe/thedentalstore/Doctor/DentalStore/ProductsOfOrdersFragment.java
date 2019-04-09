package com.heshamapps.heshe.thedentalstore.Doctor.DentalStore;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import android.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.Doctor.DentalStore.OrdersDetailsRecyclerViewAdapter;
import com.heshamapps.heshe.thedentalstore.Doctor.OrdersRecyclerViewAdapter;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductsOfOrdersFragment extends Fragment {


    @BindView(R.id.RecyclerView)
    RecyclerView RecyclerView;

    FirebaseFirestore db;
    ArrayList<ProductModel> productsOfOrder = new ArrayList<>();


    public ProductsOfOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_orders_deatils, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("product of order");

        productsOfOrder= getArguments().getParcelableArrayList("productsOfOrder");

        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());

        RecyclerView.setLayoutManager(recyclerLayoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(RecyclerView.getContext(),
                        recyclerLayoutManager.getOrientation());
        RecyclerView.addItemDecoration(dividerItemDecoration);


        OrdersDetailsRecyclerViewAdapter recyclerViewAdapter = new
                OrdersDetailsRecyclerViewAdapter(productsOfOrder,
                getActivity(), db);
        RecyclerView.setAdapter(recyclerViewAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
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
