package com.thapasujan5.netanalzyerpro.actionMenu;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.tools.SendEmail;
import com.thapasujan5.netanalzyerpro.tools.SocialIntentProvider;

public class About {
    Context context;

    public About(final Context context) {

        final Dialog d = new Dialog(context);

        d.requestWindowFeature(Window.FEATURE_NO_TITLE);
        d.setContentView(R.layout.dialog_about);
        TextView tvTitle, tvMessage;
        tvTitle = (TextView) d.findViewById(R.id.titleText);
        tvTitle.setText(context.getString(R.string.app_name) + " "
                + context.getString(R.string.version));
        tvMessage = (TextView) d.findViewById(R.id.message);
        tvMessage.setText("Developer:Sujan Thapa\nSydney, Australia\n\t"
                + context.getString(R.string.phone) + "\n"
                + context.getString(R.string.email));

        ImageView icon = (ImageView) d.findViewById(R.id.icon);
        icon.setImageResource(R.mipmap.ic_launcher);

        ImageView ivFb = (ImageView) d.findViewById(R.id.ivFbLogo);
        ivFb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(SocialIntentProvider
                        .getOpenFacebookIntent(context,
                                context.getString(R.string.fbID),
                                context.getString(R.string.fbName)));

            }
        });

        ImageView ivTwitter = (ImageView) d.findViewById(R.id.ivTwitterLogo);
        ivTwitter.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                context.startActivity(SocialIntentProvider
                        .getOpenTwitterIntent(null,
                                context.getString(R.string.twitterID)));

            }
        });


        ImageView ivProfilepic = (ImageView) d.findViewById(R.id.ivProfilePic);
        ivProfilepic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(context);
                ImageView img = new ImageView(context);
                img.setImageResource(R.mipmap.profile_picture_full);
                RelativeLayout pictureHolder = new RelativeLayout(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT);
                pictureHolder.addView(img, params);

                img.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss();
                    }
                });

                d.setContentView(pictureHolder);
                d.setTitle("Sujan Thapa");
                d.show();
            }
        });
        Button ok = (Button) d.findViewById(R.id.ok);
        Button cancel = (Button) d.findViewById(R.id.cancel);
        Button whatsnNew = (Button) d.findViewById(R.id.btnWhatsNew);
        whatsnNew.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                new WhatsNew(context, false); //just sending false meaning not to check preferences whether user prviously viewed or not
            }
        });
        ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Please Wait !", Toast.LENGTH_SHORT)
                        .show();
                Intent call = new Intent(Intent.ACTION_DIAL);
                call.setData(Uri.parse("tel:"
                        + context.getString(R.string.phone)));
                context.startActivity(call);
                ((Activity) context).overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);
            }
        });
        cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Please Wait !", Toast.LENGTH_SHORT)
                        .show();
                SendEmail.startEmailActivity(context,
                        "About:" + context.getString(R.string.app_name),
                        new String[]{context.getString(R.string.email) + ""});
                ((Activity) context).overridePendingTransition(R.anim.slide_in,
                        R.anim.slide_out);

            }
        });

        d.show();

    }
}
