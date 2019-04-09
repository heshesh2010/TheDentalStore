package com.heshamapps.heshe.thedentalstore.fragment;

import android.content.Context;
import android.os.Bundle;

import android.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;


public class CartFragment extends Fragment {


    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;

    private String email;
    private String photo;
    private String mobile;

    private StaggeredGridLayoutManager mLayoutManager;
    FirestoreRecyclerAdapter adapter;

    FirebaseFirestore db;
    Query query;
    FirestoreRecyclerOptions<ProductModel> options;

    @BindView(R.id.tv_no_cards)
     LottieAnimationView tv_no_item;

    @BindView(R.id.empty_cart)
    LottieAnimationView emptyCart;


    private ArrayList<ProductModel> cartCollect = new ArrayList<>();
    private float totalcost=0;
    private int totalproducts=0;

    @BindView(R.id.mRecycler)
    RecyclerView mRecyclerView;


    public CartFragment() {
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
            View view=   inflater.inflate(R.layout.fragment_cart, container, false);
        ButterKnife.bind(this, view);

        session = new UserSession(getActivity());
        getActivity().setTitle("cart");
        //validating session
        session.isLoggedIn();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        query = FirebaseFirestore.getInstance()
                .collection("Cart");

        options = new FirestoreRecyclerOptions.Builder<ProductModel>()
                .setQuery(query, ProductModel.class)
                .build();

        if(session.getCartValue()>0) {
            populateRecyclerView();
        }else if(session.getCartValue() == 0)  {
            tv_no_item.setVisibility(View.GONE);
            emptyCart.setVisibility(View.VISIBLE);
        }



        return  view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(session.getCartValue()>0) {
            adapter.startListening();
        }else if(session.getCartValue() == 0)  {
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
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

    private void populateRecyclerView() {

        adapter = new FirestoreRecyclerAdapter<ProductModel, CartFragment.ProductViewHolder>(options) {

            @Override
            public void onBindViewHolder(CartFragment.ProductViewHolder viewHolder, int position, ProductModel model) {

                if(tv_no_item.getVisibility()== View.VISIBLE){
                    tv_no_item.setVisibility(View.GONE);
                }
                viewHolder.cardName.setText(model.getTitle());
                viewHolder.cardPrice.setText("$ "+ model.getPrice());
                viewHolder.cardCount.setText("Quantity : "+ model.getNo_of_items());
                Picasso.with(getActivity()).load(model.getImage()).into(viewHolder.cardImage);

                totalcost += model.getPrice();
                totalproducts += model.getNo_of_items();
                cartCollect.add(model);

                viewHolder.cardDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity(),getItem(position).getTitle(),Toast.LENGTH_SHORT).show();
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                        snapshot.getReference().delete();
                        session.decreaseCartValue();
                        getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new CartFragment()).commit();

                    }
                });


            }
            @Override
            public CartFragment.ProductViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cart_item_layout, group, false);
                return new CartFragment.ProductViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        mRecyclerView.setAdapter(adapter);


    }

    @OnClick(R.id.checkout)
    public void checkout(View view) {
        if(session.getCartValue()>0) {


            Bundle bundle = new Bundle();
            bundle.putString("totalprice",  Float.toString(totalcost));
            bundle.putString("totalproducts",  Integer.toString(totalproducts));

            bundle.putParcelableArrayList("cartproducts", cartCollect);

            CheckoutFragment m_CheckoutFragment = new CheckoutFragment();
            m_CheckoutFragment.setArguments(bundle);

            getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_frame,  m_CheckoutFragment).commit();


        }
            //  finish();
        else {
                Toasty.error(getActivity(), "You must add product to Cart first ").show();
            }

        }


    //viewHolder for our Firebase UI
    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.cart_prtitle)
        TextView cardName;
        @BindView(R.id.image_cartlist)
        ImageView cardImage;
        @BindView(R.id.cart_prcount)
        TextView cardCount;
        @BindView(R.id.deletecard)
        ImageView cardDelete;
        @BindView(R.id.cart_prprice)
        TextView cardPrice;


        View mView;
        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }
    }




}
