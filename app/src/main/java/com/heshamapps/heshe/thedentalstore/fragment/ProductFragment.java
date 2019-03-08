package com.heshamapps.heshe.thedentalstore.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.heshamapps.heshe.thedentalstore.Login.LoginActivity;
import com.heshamapps.heshe.thedentalstore.Model.CommentModel;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;


public class ProductFragment extends Fragment {

    private ProductModel m_ProductModel;
    private FirebaseFirestore mDatabaseReference;
    private int quantity = 1;
    private UserSession session;
    private String userMobile, userEmail;
    String docID;
    boolean return_result ;
    FirebaseFirestore db;
    Query query;
    private FirestoreRecyclerAdapter adapter;

    @BindView(R.id.mRecycler)
    RecyclerView rv;

    @BindView(R.id.productImage)
    ImageView productImage;

    @BindView(R.id.like_btn)
    TextView like_btn;

    @BindView(R.id.productTitle)
    TextView productTitle;

    @BindView(R.id.productPrice)
    TextView productPrice;

    @BindView(R.id.productDesc)
    TextView productDesc;

    @BindView(R.id.productExpire)
    TextView productExpire;

    @BindView(R.id.quantityProductPage)
    EditText quantityProductPage;

    int price=0 ;
    int TempPrice =0;

    public ProductFragment() {
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
        View view   = inflater.inflate(R.layout.fragment_product, container, false);
        ButterKnife.bind(this, view);

        m_ProductModel = (ProductModel) getArguments().getParcelable("product");
        docID=  getArguments().getString("docID");
        Picasso.with(getActivity()).load(m_ProductModel.getImage()).into(productImage);
        session = new UserSession(getActivity());
        productTitle.setText(m_ProductModel.getTitle());
        productPrice.setText(String.valueOf(m_ProductModel.getPrice()));
        productDesc.setText(m_ProductModel.getDesc());
        productExpire .setText( m_ProductModel.getExpired_date());
         price = Integer.parseInt(productPrice.getText().toString());
        TempPrice=price;

        quantityProductPage.setText("1");

        //setting textwatcher for no of items field
        quantityProductPage.addTextChangedListener(productCount);

        userMobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
        userEmail = session.getUserDetails().get(UserSession.KEY_EMAIL);
        mDatabaseReference = FirebaseFirestore.getInstance();

        rv.setLayoutManager(new LinearLayoutManager(getActivity()));
        rv.setItemAnimator(new DefaultItemAnimator());




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


    @Override
    public void onStart() {
        super.onStart();
        checkLikedStatus();
        getCommentObject();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (adapter != null) {
            adapter.stopListening();
        }
    }


    boolean checkLikedStatus(){


        mDatabaseReference.collection("likes").document(docID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        for (Map.Entry<String, Object> entry : map.entrySet()) {
                            if (entry.getKey().equals("status")) {
                                if((boolean)entry.getValue()){
                                    like_btn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_thumb_up_blue_700_24dp, 0, 0, 0);
                                    return_result= true;
                                }
                                else {
                                    like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_grey_700_24dp, 0, 0, 0);
                                    return_result = false;
                                }
                            }
                        }
                    }
                }
            }
        });


        return return_result;
    }


    @OnClick(R.id.like_btn)
    public void likeProduct(View view) {
        Map<String, Object> data = new HashMap<>();
        data.put("productId", docID);
        data.put("userId", "hesham0");
        data.put("status", !checkLikedStatus());
        mDatabaseReference.collection("likes").document(docID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // if liked
                        if(checkLikedStatus()) {
                            like_btn.setText("liked");
                            like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_blue_700_24dp, 0, 0, 0);
                            Toasty.success(getActivity(), "liked", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            like_btn.setText("disLiked");
                            like_btn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_thumb_up_grey_700_24dp, 0, 0, 0);
                            Toasty.success(getActivity(), "disLiked", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        like_btn.setText(R.string.like);
                        like_btn.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_thumb_up_grey_700_24dp, 0, 0, 0);
                        Toasty.error(getActivity(), "error" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    @OnClick(R.id.addToCart_btn)
    public void addToCart(View view) {
        session.checkLogin();
        if(session.isLoggedIn()){

            mDatabaseReference.collection("Cart").document(session.getUserDetails().get(UserSession.KEY_UID))
                    .set(getProductObject())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(getActivity(), "added to Cart", Toast.LENGTH_SHORT).show();
                            session.increaseCartValue();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(getActivity(), "Not added to Cart" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else{

            Toasty.error(getActivity(), "you must login first" , Toast.LENGTH_SHORT).show();
            Intent i = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivity(i);

        }



    }


    @OnClick(R.id.fab)
    public void goToCart(View view) {

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new CartFragment()).commit();
        //finish();

    }

    @OnClick(R.id.share_btn)
    public void shareProduct(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Found amazing " + productTitle.getText().toString() + "On online dental Store";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    @OnClick(R.id.comment_btn)
    public void addCommentPopup(View view){

        Map<String, Object> map = new HashMap<>();


        final EditText taskEditText = new EditText(view.getContext());
        AlertDialog dialog = new AlertDialog.Builder(view.getContext())
                .setTitle("Add a new comment")
                .setMessage("please add your comment on product ")
                .setView(taskEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String task = String.valueOf(taskEditText.getText());
                        map.put("CommentUser", "hesham");
                        map.put("productId", docID);
                        map.put("Comment", task);
                        map.put("Image",session.getUserDetails().get("photo"));
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        Date todayDate = Calendar.getInstance().getTime();
                        String todayString = formatter.format(todayDate);
                        map.put("CommentDate",todayString);

                        mDatabaseReference.collection("products").document(docID).collection("comments").document()
                                .set(map)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toasty.success(getActivity(), "Comment Added", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toasty.error(getActivity(), "Comment Not added " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();


    }
    private ProductModel getProductObject() {

        return new ProductModel(m_ProductModel.getId(),m_ProductModel.getTitle(),m_ProductModel.getPrice(), m_ProductModel.getDesc(), m_ProductModel.getImage(),Integer.parseInt(quantityProductPage.getText().toString()), userEmail, userMobile,m_ProductModel.getExpired_date());

    }


    private void getCommentObject(){

        query = FirebaseFirestore.getInstance()
                .collection("products").document(docID).collection("comments");

        FirestoreRecyclerOptions<CommentModel> options = new FirestoreRecyclerOptions.Builder<CommentModel>()
                .setQuery(query, CommentModel.class)
                .build();


        adapter = new FirestoreRecyclerAdapter<CommentModel, ProductFragment.commentHolder>(options) {
            @NonNull
            @Override
            public ProductFragment.commentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.comment_item_layout, parent, false);

                return new ProductFragment.commentHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductFragment.commentHolder commentHolder, int position, @NonNull CommentModel commentModel) {


                commentHolder.full_comment.setText(String.valueOf(commentModel.getComment()));
                commentHolder.comment_date.setText(commentModel.getCommentDate());
                commentHolder.comment_user.setText(String.valueOf(commentModel.getCommentUser()));


                Glide.with(getActivity())
                        .load(session.getUserDetails().get("photo"))
                        .into(commentHolder.userProfileImg);

            };
        };



        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }



    //check that product count must not exceed 500
    TextWatcher productCount = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (quantityProductPage.getText().toString().equals("")) {
                quantityProductPage.setText("0");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            //none
            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500) {
                Toasty.error(getActivity(), "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
            }
        }

    };

    @OnClick(R.id.decrementQuantity_btn)
    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
            TempPrice=TempPrice-price;

            productPrice.setText(""+TempPrice);
        }
    }

    @OnClick(R.id.incrementQuantity_btn)
    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
            TempPrice=TempPrice+price;
            productPrice.setText(""+TempPrice);

        } else {
            Toasty.error(getActivity(), "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
        }
    }

    class commentHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.comment_user)
        TextView comment_user;
        @BindView(R.id.userProfileImg)
        ImageView userProfileImg;
        @BindView(R.id.comment_date)
        TextView comment_date;
        @BindView(R.id.full_comment)
        TextView full_comment;



        commentHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
