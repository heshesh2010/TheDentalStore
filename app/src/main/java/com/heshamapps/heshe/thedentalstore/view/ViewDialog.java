package com.heshamapps.heshe.thedentalstore.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.craftman.cardform.Card;
import com.craftman.cardform.CardForm;
import com.craftman.cardform.OnPayBtnClickListner;
import com.heshamapps.heshe.thedentalstore.R;

import es.dmoral.toasty.Toasty;

public class ViewDialog {
    CardForm cardForm;

    public void showDialog(Activity activity, String msg){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog);

            cardForm = (CardForm) dialog.findViewById(R.id.card_form);
        cardForm.setAmount(msg);

        Button btn = (Button) (cardForm.getRootView().findViewById(R.id.btn_pay));
        btn.setText("Pay");


        cardForm.setPayBtnClickListner(new OnPayBtnClickListner() {
            @Override
            public void onClick(Card card) {
                //Your code here!! use card.getXXX() for get any card property
                //for instance card.getName();
                dialog.dismiss();
                Toasty.success(activity,"payment done").show();

            }
        });

        ((TextView)cardForm.getRootView().findViewById(R.id.payment_amount)).setText(msg);

        //

       // Button dialogButton = (Button) dialog.findViewById(R.id.close);
      /*  dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });*/

        dialog.show();

    }
}
