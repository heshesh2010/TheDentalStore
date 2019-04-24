package com.heshamapps.heshe.thedentalstore.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.heshamapps.heshe.thedentalstore.BuildConfig;
import com.heshamapps.heshe.thedentalstore.Model.PlacedOrderModel;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;
import com.heshamapps.heshe.thedentalstore.view.ViewDialog;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

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



    // private AddressResultReceiver mResultReceiver;
    // removed here because cause wrong code when implemented and
    // its not necessary like the author says

    //Define fields for Google API Client
    private FusedLocationProviderClient mFusedLocationClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    private LocationCallback mLocationCallback;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 14;


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

        getActivity().setTitle("check out");

        //setting total price
        totalAmount.setText(getArguments().getString("totalprice").toString());

        //setting number of products
        noOfItems.setText(getArguments().getString("totalproducts").toString());

        cartcollect = getArguments().getParcelableArrayList("cartproducts");




        return view;
    }

    @OnClick(R.id.place_order2)
    public void PlaceOrder2(View view) {

        try {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                //   txtLatitude.setText(String.valueOf(location.getLatitude()));
                                //  txtLongitude.setText(String.valueOf(location.getLongitude()));
                                Log.d("tagh",String.valueOf(location.getLongitude()));
                                getAddress(location.getLatitude(), location.getLongitude());
                            }
                        }
                    });
            locationRequest = LocationRequest.create();
            locationRequest.setInterval(5000);
            locationRequest.setFastestInterval(1000);
            if (orderaddress.getText().toString().equals(""))
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            else
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    for (Location location : locationResult.getLocations()) {
                        // Update UI with location data
                        //   txtLatitude.setText(String.valueOf(location.getLatitude()));
                        //   txtLongitude.setText(String.valueOf(location.getLongitude()));
                        Log.d("tagh",String.valueOf(location.getLongitude()));
                        getAddress(location.getLatitude(), location.getLongitude());

                    }
                }

                ;
            };
        } catch (SecurityException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!checkPermissions()) {
            startLocationUpdates();
            requestPermissions();
        } else {
            getLastLocation();
            startLocationUpdates();
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
        productdetails();

    }

    @Override
    public void onPause() {
//        stopLocationUpdates();
        super.onPause();
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
                    case "visa/master":
                        ViewDialog alert = new ViewDialog();
                        alert.showDialog(getActivity(), String.valueOf(totalAmount.getText().toString()));


                    // and then do whatever you want with that
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

                            getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment_frame,  m_OrderPlacedFragment).addToBackStack(null).commit();
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
        } else if (orderpincode.getText().toString().length() < 6 || orderpincode.getText().toString().length() > 8){
            orderpincode.setError("Pincode must be of 6 or 7 digits");
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






    @Override
    public void onResume() {
        super.onResume();


    }





    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }


    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i("TAG", "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.default_web_client_id, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i("TAG", "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i("TAG", "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i("TAG", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            } else {
                // Permission denied.

                // Notify the user via a SnackBar that they have rejected a core permission for the
                // app, which makes the Activity useless. In a real app, core permissions would
                // typically be best requested during a welcome-screen flow.

                // Additionally, it is important to remember that a permission might have been
                // rejected without asking the user for permission (device policy or "Never ask
                // again" prompts). Therefore, a user interface affordance is typically implemented
                // when permissions are denied. Otherwise, your app could appear unresponsive to
                // touches or interactions which have required permissions.
                showSnackbar(R.string.fui_email_account_creation_error, R.string.settings,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // Build intent that displays the App settings screen.
                                Intent intent = new Intent();
                                intent.setAction(
                                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null);
                                intent.setData(uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }
                        });
            }
        }
    }


    /**
     * Provides a simple way of getting a device's location and is well suited for
     * applications that do not require a fine-grained location and that do not need location
     * updates. Gets the best and most recent location currently available, which may be null
     * in rare cases when a location is not available.
     * <p>
     * Note: this method should be called after location permission has been granted.
     */
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            lastLocation = task.getResult();



                            getAddress(lastLocation.getLatitude(), lastLocation.getLongitude());

                            Log.d("tagh",String.valueOf(lastLocation.getLongitude()));
                        } else {

                            Toasty.info(getActivity(), "No Location detected ", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void getAddress(double latitude, double longitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude,longitude, 1);
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();// Here 1 represent max location result to returned, by documents it recommended 1 to 5
            orderaddress.setText(country+","+city+","+postalCode+","+knownName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stopLocationUpdates() {
        if (mFusedLocationClient != null) {
            try {
                final Task<Void> voidTask = mFusedLocationClient.removeLocationUpdates(mLocationCallback);
                if (voidTask.isSuccessful()) {
                    Log.d("TAG","StopLocation updates successful! ");
                } else {
                    Log.d("TAG","StopLocation updates unsuccessful! " + voidTask.toString());
                }
            }
            catch (SecurityException exp) {
                Log.d("TAG", " Security exception while removeLocationUpdates");
            }
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.requestLocationUpdates(locationRequest, mLocationCallback, null);
    }

    // private void showSnackbar(final String text) {
    //    if (canvasLayout != null) {
    //        Snackbar.make(canvasLayout, text, Snackbar.LENGTH_LONG).show();
    //    }
    //}
    // this also cause wrong code and as I see it dont is necessary
    // because the same method which is really used


    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(getActivity().findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

























    @Override
    public void onDestroy() {
        super.onDestroy();
       stopLocationUpdates();
    }






}
