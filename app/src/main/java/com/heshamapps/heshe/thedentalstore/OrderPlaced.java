package com.heshamapps.heshe.thedentalstore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.heshamapps.heshe.thedentalstore.util.DrawerUtil;

public class OrderPlaced extends Activity {

    @BindView(R.id.orderid)
    TextView orderidview;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private String orderid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_placed);
        ButterKnife.bind(this);
        new DrawerUtil(this,mToolbar,  FirebaseAuth.getInstance());

          initialize();
}

    private void initialize() {
        orderid = getIntent().getStringExtra("orderid");
        orderidview.setText(orderid);
    }

    public void finishActivity(View view) {
        finish();
    }
}
