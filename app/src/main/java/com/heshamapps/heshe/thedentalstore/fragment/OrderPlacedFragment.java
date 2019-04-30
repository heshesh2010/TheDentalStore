package com.heshamapps.heshe.thedentalstore.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import android.app.Fragment;

import android.se.omapi.Session;
import android.util.Log;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;
import com.heshamapps.heshe.thedentalstore.Model.ProductModel;
import com.heshamapps.heshe.thedentalstore.R;
import com.heshamapps.heshe.thedentalstore.usersession.UserSession;

import java.util.Properties;

import javax.sql.DataSource;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.heshamapps.heshe.thedentalstore.usersession.UserSession.KEY_EMAIL;

public class OrderPlacedFragment extends Fragment {

    @BindView(R.id.orderid)
    TextView orderidview;

    private String orderid;
    UserSession session;
    FirebaseFirestore db;
    ProductModel cartcollect;
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
        db = FirebaseFirestore.getInstance();

        getActivity().setTitle("order placed");

        orderid = getArguments().getString("orderid");
        cartcollect = getArguments().getParcelable("cartproducts");

        orderidview.setText(orderid);

        sendEmail();
        updateStock();
        return view;
    }


    public void sendEmail() {
        session = new UserSession(getActivity());

        BackgroundMail.newBuilder(getActivity())
                .withSendingMessage("Sending order to Your Email !")
                .withUsername("shreen.ods2019@gmail.com")
                .withPassword("$S15#07#1997m$")
                .withMailto(session.getUserDetails().get(KEY_EMAIL))
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("order placed")
                .withBody("your order id = " + orderid +"is under preparation and it will deliver Within Three days")
                .withOnSuccessCallback(() -> {
                    //do some magic
                })
                .withOnFailCallback(() -> {
                    //do some magic
                })
                .send();
    }

    void updateStock(){

        // first get current stock then - from it
        final DocumentReference sfDocRef = db.collection("products").document(cartcollect.getId());

        db.runTransaction((Transaction.Function<Void>) transaction -> {
            DocumentSnapshot snapshot = transaction.get(sfDocRef);

            // Note: this could be done without a transaction
            //       by updating the population using FieldValue.increment()
            int newPopulation = Integer.valueOf(snapshot.get("currentStock").toString());

            transaction.update(sfDocRef, "currentStock", newPopulation-cartcollect.getNo_of_items());

            // Success
            return null;
        }).addOnSuccessListener(aVoid -> Log.d("TAG", "Transaction success!"))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("TAG", "Transaction failure.", e);
                    }
                });



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
