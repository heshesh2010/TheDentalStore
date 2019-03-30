package com.heshamapps.heshe.thedentalstore.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.heshamapps.heshe.thedentalstore.view.ViewDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;


public class CheckoutFragment extends Fragment {
    @BindView(R.id.delivery_date)
    TextView deliveryDate;
    @BindView(R.id.no_of_items)
    TextView noOfItems;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    @BindView(R.id.main_activity_multi_line_radio_group)
    MultiLineRadioGroup mainActivityMultiLineRadioGroup;
    @BindView(R.id.ordername)
    MaterialEditText ordername;
    @BindView(R.id.orderemail)
    MaterialEditText orderemail;
    @BindView(R.id.ordernumber)
    MaterialEditText ordernumber;
    @BindView(R.id.orderaddress)
    MaterialEditText orderaddress;
    @BindView(R.id.orderpincode)
    MaterialEditText orderpincode;


    private ArrayList<ProductModel> cartcollect= new ArrayList<>();
    private String payment_mode="COD",order_reference_id;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no,getPlaced_user_id;
    private UserSession session;
    private HashMap<String,String> user;
    private FirebaseFirestore mDatabaseReference;
    private String currdatetime;


    public CheckoutFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new UserSession(getActivity());
        session.isLoggedIn();
        mDatabaseReference = FirebaseFirestore.getInstance();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view   = inflater.inflate(R.layout.fragment_checkout, container, false);
        ButterKnife.bind(this, view);

        //setting total price
        totalAmount.setText(getArguments().getString("totalprice").toString());

        //setting number of products
        noOfItems.setText(getArguments().getString("totalproducts").toString());

        cartcollect = getArguments().getParcelableArrayList("cartproducts");

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
        productdetails();
    }


    private void productdetails() {


        //delivery date
        SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, 7);  // number of days to add
        String tomorrow = (formattedDate.format(c.getTime()));
        deliveryDate.setText(tomorrow);

        mainActivityMultiLineRadioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ViewGroup group, RadioButton button) {


                switch(button.getText().toString()) {
                    case "PayPal":
                        break;
                    case "visa/master":
                        ViewDialog alert = new ViewDialog();
                        alert.showDialog(getActivity(), "Error de conexión al servidor");
                        break;
                    case "COD":
                        break;
                    default:
                }

                payment_mode=button.getText().toString();

            }
        });

        user = session.getUserDetails();

        placed_user_name=user.get(UserSession.KEY_NAME);
        getPlaced_user_email=user.get(UserSession.KEY_EMAIL);
        getPlaced_user_mobile_no=user.get(UserSession.KEY_MOBiLE);
        getPlaced_user_id=user.get(UserSession.KEY_UID);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
        currdatetime = sdf.format(new Date());
    }

    @OnClick(R.id.place_order)
    public void PlaceOrder(View view) {

        if (validateFields(view)) {

            order_reference_id = getordernumber();

            //adding user details to the database under orders table
            mDatabaseReference.collection("orders").document(order_reference_id).set(getProductObject())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(getActivity(), "added to Orders", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toasty.error(getActivity(), "Not added to Orders" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


            //adding products to the order
            for(ProductModel model:cartcollect){
                mDatabaseReference.collection("orders").document(order_reference_id).collection("items").document().set(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toasty.success(getActivity(), "items added to orders", Toast.LENGTH_SHORT).show();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getActivity(), "items Not added to orders" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            mDatabaseReference.collection("Cart").document(getPlaced_user_id).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Bundle bundle = new Bundle();
                            bundle.putString("orderid",  order_reference_id);

                            OrderPlacedFragment m_OrderPlacedFragment = new OrderPlacedFragment();
                            m_OrderPlacedFragment.setArguments(bundle);

                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  m_OrderPlacedFragment).commit();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

            session.setCartValue(0);



        }
    }

    private boolean validateFields(View view) {

        if (ordername.getText().toString().length() == 0 || orderemail.getText().toString().length() == 0 || ordernumber.getText().toString().length() == 0 || orderaddress.getText().toString().length() == 0 ||
                orderpincode.getText().toString().length() == 0) {
            Snackbar.make(view, "Kindly Fill all the fields", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            return false;
        } else if (orderemail.getText().toString().length() < 4 || orderemail.getText().toString().length() > 30) {
            orderemail.setError("Email Must consist of 4 to 30 characters");
            return false;
        } else if (!orderemail.getText().toString().matches("^[A-za-z0-9.@]+")) {
            orderemail.setError("Only . and @ characters allowed");
            return false;
        } else if (!orderemail.getText().toString().contains("@") || !orderemail.getText().toString().contains(".")) {
            orderemail.setError("Email must contain @ and .");
            return false;
        } else if (ordernumber.getText().toString().length() < 4 || ordernumber.getText().toString().length() > 12) {
            ordernumber.setError("Number Must consist of 10 characters");
            return false;
        } else if (orderpincode.getText().toString().length() < 6 || ordernumber.getText().toString().length() > 8){
            orderpincode.setError("Pincode must be of 6 digits");
            return false;
        }

        return true;
    }

    public PlacedOrderModel getProductObject() {
        return new PlacedOrderModel(order_reference_id,noOfItems.getText().toString(),totalAmount.getText().toString(),deliveryDate.getText().toString(),payment_mode,ordername.getText().toString(),orderemail.getText().toString(),ordernumber.getText().toString(),orderaddress.getText().toString(),orderpincode.getText().toString(),placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no,getPlaced_user_id,"In progress");
    }

    public String getordernumber() {

        return currdatetime.replaceAll("-","");
    }

}
