package com.heshamapps.heshe.thedentalstore;

import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.heshamapps.heshe.thedentalstore.fragment.MainFragment;
import com.heshamapps.heshe.thedentalstore.util.DrawerUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mToolbar.setTitle("The Dental Store ");
        new DrawerUtil(this,mToolbar,  FirebaseAuth.getInstance());



        setContentView(R.layout.activity_main);




        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new MainFragment()).commit();


    }


}
