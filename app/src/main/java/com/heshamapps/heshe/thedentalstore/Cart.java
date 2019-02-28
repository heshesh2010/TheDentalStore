package com.heshamapps.heshe.thedentalstore;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.heshamapps.heshe.thedentalstore.util.DrawerUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class Cart extends Activity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //to get user session data
    private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    FirestoreRecyclerAdapter adapter;

    FirebaseFirestore db;
    Query query;
    FirestoreRecyclerOptions<ProductModel> options;

    private LottieAnimationView tv_no_item;
    private LottieAnimationView emptyCart;

    private ArrayList<ProductModel> cartCollect;
    private float totalcost=0;
    private int totalproducts=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        ButterKnife.bind(this);
        new DrawerUtil(this,mToolbar,  FirebaseAuth.getInstance());

        init();
        //retrieve session values and display on listviews
        getValues();

        //SharedPreference for Cart Value
        session = new UserSession(getApplicationContext());

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





    }

   void init(){
       mRecyclerView = findViewById(R.id.recyclerview);
       tv_no_item = findViewById(R.id.tv_no_cards);
       emptyCart = findViewById(R.id.empty_cart);
       cartCollect = new ArrayList<>();
       db = FirebaseFirestore.getInstance();


   }













    private void populateRecyclerView() {

         adapter = new FirestoreRecyclerAdapter<ProductModel, ProductViewHolder>(options) {

            @Override
            public void onBindViewHolder(ProductViewHolder viewHolder, int position, ProductModel model) {

                if(tv_no_item.getVisibility()== View.VISIBLE){
                    tv_no_item.setVisibility(View.GONE);
                }
                viewHolder.cardName.setText(model.getTitle());
                viewHolder.cardPrice.setText("₹ "+ model.getPrice());
                viewHolder.cardCount.setText("Quantity : "+ model.getNo_of_items());
                Picasso.with(Cart.this).load(model.getImage()).into(viewHolder.cardImage);

                totalcost += model.getNo_of_items()*model.getPrice();
                totalproducts += model.getNo_of_items();
                cartCollect.add(model);

                viewHolder.cardDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(Cart.this,getItem(position).getTitle(),Toast.LENGTH_SHORT).show();
                        DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
                        snapshot.getReference().delete();
                        session.decreaseCartValue();
                        startActivity(new Intent(Cart.this, Cart.class));
                        finish();
                    }
                });


            }
            @Override
            public ProductViewHolder onCreateViewHolder(ViewGroup group, int i) {
                View view = LayoutInflater.from(group.getContext())
                        .inflate(R.layout.cart_item_layout, group, false);
                return new ProductViewHolder(view);
            }
            @Override
            public void onError(FirebaseFirestoreException e) {
                Log.e("error", e.getMessage());
            }
        };
        mRecyclerView.setAdapter(adapter);


    }

    public void checkout(View view) {
        if(session.getCartValue()>0) {
            Intent intent = new Intent(Cart.this,Checkout.class);
            intent.putExtra("totalprice",Float.toString(totalcost));
            intent.putExtra("totalproducts",Integer.toString(totalproducts));
            intent.putExtra("cartproducts",cartCollect);
            startActivity(intent);
            finish();        }
            else if(session.getCartValue() == 0)  {
            Toasty.error(getApplicationContext(),"You must add product to Cart first ").show();
        }


    }

    //viewHolder for our Firebase UI
    public static class ProductViewHolder extends RecyclerView.ViewHolder{

        TextView cardName;
        ImageView cardImage;
        TextView cardPrice;
        TextView cardCount;
        ImageView cardDelete;

        View mView;
        public ProductViewHolder(View v) {
            super(v);
            mView = v;
            cardName = v.findViewById(R.id.cart_prtitle);
            cardImage = v.findViewById(R.id.image_cartlist);
            cardPrice = v.findViewById(R.id.cart_prprice);
            cardCount = v.findViewById(R.id.cart_prcount);
            cardDelete = v.findViewById(R.id.deletecard);
        }
    }


    private void getValues() {

        //create new session object by passing application context
        session = new UserSession(getApplicationContext());

        //validating session
        session.isLoggedIn();

        //get User details if logged in
        user = session.getUserDetails();

        name = user.get(UserSession.KEY_NAME);
        email = user.get(UserSession.KEY_EMAIL);
        mobile = user.get(UserSession.KEY_MOBiLE);
        photo = user.get(UserSession.KEY_PHOTO);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(session.getCartValue()>0) {
            adapter.startListening();
        }else if(session.getCartValue() == 0)  {
        }
    }

}
