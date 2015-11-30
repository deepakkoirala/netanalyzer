package com.thapasujan5.netanalzyerpro.actionMenu;

import android.content.Context;

import com.thapasujan5.netanalyzerpro.R;
import com.thapasujan5.netanalzyerpro.tools.SocialIntentProvider;

/**
 * Created by Suzan on 11/16/2015.
 */
public class Portal {

    public Portal(Context context) {
        context.startActivity(SocialIntentProvider.getOpenFacebookPageIntent(context, context.getString(R.string.portal_id), context.getString(R.string.portal_name)));
    }
}
