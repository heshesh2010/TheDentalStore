package com.heshamapps.heshe.thedentalstore.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.chip.Chip;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.heshamapps.heshe.thedentalstore.MainActivity;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;

import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.util.EasyLocationProvider;
import com.heshamapps.heshe.thedentalstore.view.GridSpacingItemDecoration;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.checkSelfPermission;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MainFragment extends Fragment {


    private FirestoreRecyclerAdapter adapter;


    FirebaseFirestore db;
    Query query;


    @BindView(R.id.mRecycler)
    RecyclerView rv;

    @BindView(R.id.filter_chip)
    Chip filterChip  ;

    @BindView(R.id.filter_chip2)
    Chip filterChip2  ;


    public MainFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        query =db.collection("products");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, view);
        // Inflate the layout for this fragment
        rv.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        rv.addItemDecoration(new GridSpacingItemDecoration(2,1, true));
        rv.setItemAnimator(new DefaultItemAnimator());


        CompoundButton.OnCheckedChangeListener filterChipListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Log.i("TAG", buttonView.getText() + "");
                if(buttonView.isChecked()){
                    updaterecycleview(buttonView.getText());
                }

                // should return to All tab
                else{}

            }
        };

        filterChip.setOnCheckedChangeListener(filterChipListener);
        filterChip2.setOnCheckedChangeListener(filterChipListener);

       /* Map<String, Object> city = new HashMap<>();
        city.put("title", "product");
        city.put("price", 40);
        city.put("image", "https://education.ket.org/wp-content/uploads/2016/06/id1671-717x376.jpg");

        db.collection("products").document()
                .set(city)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });*/
















        return view;
    }







    private  void updaterecycleview(CharSequence text){
        adapter.stopListening();
        query = FirebaseFirestore.getInstance().collection("products").whereEqualTo("type",text);

        getProductsList();
        adapter.startListening();


    }


    private  void getProductsList()
    {

        FirestoreRecyclerOptions<ProductModel> options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ProductModel, ProductHolder>(options) {
            @NonNull
            @Override
            public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.product_card, parent, false);

                return new ProductHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductHolder productHolder, int position, @NonNull ProductModel productModel) {


                productHolder.textPrice.setText(String.valueOf(productModel.getPrice()));
                productHolder.textTitle.setText(productModel.getTitle());
                Glide.with(getActivity())
                        .load(productModel.getImage())
                        .into(productHolder.imageView);

                productHolder.itemView.setOnClickListener(v -> {

                    DocumentSnapshot snapshot = getSnapshots().getSnapshot(productHolder.getAdapterPosition());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product", productModel);
                    bundle.putString("docID", snapshot.getId());

                    ProductFragment m_ProductFragment = new ProductFragment();
                    m_ProductFragment.setArguments(bundle);

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  m_ProductFragment).commit();


                });
            };



        };



        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


    class ProductHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.price)
        TextView textPrice;
        @BindView(R.id.thumbnail)
        ImageView imageView;
        @BindView(R.id.title)
        TextView textTitle;


        ProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onStart() {
        super.onStart();
        getProductsList();
        adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }
}
