package com.thapasujan5.netanalzyerpro.Tools;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by Suzan on 2/05/2016.
 */
public class SendEmail {
    public SendEmail(Context context, String recipient, String subject, String content) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", recipient, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }
}
