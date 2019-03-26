package com.heshamapps.heshe.thedentalstore.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.se.omapi.Session;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.heshamapps.heshe.thedentalstore.R;

import java.util.Properties;

import javax.sql.DataSource;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderPlacedFragment extends Fragment {

    @BindView(R.id.orderid)
    TextView orderidview;

    private String orderid;

    public OrderPlacedFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view   = inflater.inflate(R.layout.fragment_order_placed, container, false);
        ButterKnife.bind(this, view);

        orderid = getArguments().getString("orderid");
        orderidview.setText(orderid);

        sendEmail();
        return view;
    }


    public void sendEmail() {

        BackgroundMail.newBuilder(getActivity())
                .withUsername("shreen.ods2019@gmail.com")
                .withPassword("$S15#07#1997m$")
                .withMailto("hesham.elnemr@gmail.com")
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("this is the subject")
                .withBody("this is the body")
                .withOnSuccessCallback(() -> {
                    //do some magic
                })
                .withOnFailCallback(() -> {
                    //do some magic
                })
                .send();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
