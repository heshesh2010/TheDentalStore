package com.heshamapps.heshe.thedentalstore.Admin;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import android.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ManageOrdersFragment extends Fragment {


    FirebaseAuth mFirebaseAuth;
    FirebaseFirestore db;
    private UserSession session;
    private static final String TAG = "ViewOrdersFragment";

    @BindView(R.id.ordersRecyclerView)
     RecyclerView ManageOrdersRecyclerView;

    public ManageOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        session = new UserSession(getActivity());

    }


    @Override
    public void onStart() {
        super.onStart();
        getDocumentsFromCollection();
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_doctor_orders, container, false);
        ButterKnife.bind(this, view);
        getActivity().setTitle("Order History");


        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(getActivity().getApplicationContext());
        ManageOrdersRecyclerView.setLayoutManager(recyclerLayoutManager);

        DividerItemDecoration dividerItemDecoration =
                new DividerItemDecoration(ManageOrdersRecyclerView.getContext(),
                        recyclerLayoutManager.getOrientation());
        ManageOrdersRecyclerView.addItemDecoration(dividerItemDecoration);

        return view;
    }

    private void getDocumentsFromCollection() {


        db.collection("orders").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<PlacedOrderModel> list = new ArrayList<>();
                    for (DocumentSnapshot  document : task.getResult()) {
                        list.add(  document.toObject(PlacedOrderModel.class));
                    }
                    ManageOrdersRecyclerViewAdapter recyclerViewAdapter = new
                            ManageOrdersRecyclerViewAdapter(list,
                            getActivity(), db);
                    ManageOrdersRecyclerView.setAdapter(recyclerViewAdapter);
                    Log.d(TAG, list.toString());
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });



        db.collection("orders")
                .addSnapshotListener(getActivity(), new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                        for(DocumentChange doc : documentSnapshots.getDocumentChanges()){
                            doc.getDocument().toObject(PlacedOrderModel.class);
                            //do something...
                        }
                    }
                });





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
