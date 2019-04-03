package com.heshamapps.heshe.thedentalstore.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.craftman.cardform.CardForm;
import com.heshamapps.heshe.thedentalstore.R;

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
        TextView amount = (TextView) (cardForm.getRootView().findViewById(R.id.payment_amount));
        amount.setText(msg);

        //

       // Button dialogButton = (Button) dialog.findViewById(R.id.close);
      /*  dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });*/

        dialog.show();

    }
}
