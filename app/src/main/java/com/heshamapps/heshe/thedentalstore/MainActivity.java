package com.heshamapps.heshe.thedentalstore;

import android.app.Activity;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.heshamapps.heshe.thedentalstore.fragment.MainFragment;
import com.heshamapps.heshe.thedentalstore.util.DrawerUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
       // getSupportActionBar().setHomeButtonEnabled(false);
        new DrawerUtil(this,mToolbar,FirebaseAuth.getInstance());


        getFragmentManager().beginTransaction().replace(R.id.fragment_frame,  new MainFragment()).commit();


    }


}
