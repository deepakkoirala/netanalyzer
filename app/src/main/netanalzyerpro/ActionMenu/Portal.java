package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.content.Context;
import android.widget.Toast;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.Tools.SocialIntentProvider;

/**
 * Created by Suzan on 11/16/2015.
 */
public class Portal {

    public Portal(Context context) {
        Toast.makeText(context.getApplicationContext(), "Please wait !", Toast.LENGTH_LONG).show();
        context.startActivity(SocialIntentProvider.getOpenFacebookPageIntent(context, context.getString(R.string.portal_id), context.getString(R.string.portal_name)));
    }
}
