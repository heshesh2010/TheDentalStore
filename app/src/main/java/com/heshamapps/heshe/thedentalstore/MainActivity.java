package com.heshamapps.heshe.thedentalstore;

import android.app.Activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.google.firebase.auth.FirebaseAuth;

import com.heshamapps.heshe.thedentalstore.fragment.MainFragment;
import com.heshamapps.heshe.thedentalstore.util.DrawerUtil;

public class MainActivity extends FragmentActivity {


    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        new DrawerUtil(this,mToolbar,  FirebaseAuth.getInstance());

       getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new MainFragment()).commit();


    }


}
