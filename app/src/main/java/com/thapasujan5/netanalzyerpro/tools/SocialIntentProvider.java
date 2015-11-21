package com.thapasujan5.netanalzyerpro.tools;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

public class SocialIntentProvider {
    public static Intent getOpenFacebookIntent(Context context, String fbid,
                                               String fbname) {

        try {
            context.getPackageManager()
                    .getPackageInfo("com.facebook.katana", 0);
            return new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/"
                    + fbid));
        } catch (Exception e) {
            return new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.facebook.com/" + fbname));
        }
    }

    public static Intent getOpenTwitterIntent(String twitterid,
                                              String twittername) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=" + twittername));
            return intent;

        } catch (Exception e) {
            return (new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/#!/" + twittername)));
        }
    }

    public static Intent getOpenYoutubeIntent(Context context, String videoId, String ytChannelID) {

        try {
            if (videoId != null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                intent.putExtra("VIDEO_ID", videoId);
                return intent;
            } else {
                return (new Intent(Intent.ACTION_VIEW, Uri.parse(ytChannelID)));
            }
        } catch (Exception e) {
            return (new Intent(Intent.ACTION_VIEW, Uri.parse(ytChannelID)));
        }
    }

    public static Intent getLinkedinIntent(Context context, String id) {

        try {
            Intent linkedinIntent = new Intent(Intent.ACTION_VIEW);
            linkedinIntent.setClassName("com.linkedin.android", "com.linkedin.android.profile.ViewProfileActivity");
            linkedinIntent.putExtra("memberId", id);
            return linkedinIntent;
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://you"));
            final PackageManager packageManager = context.getPackageManager();
            final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.isEmpty()) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/profile/view?id=you"));
            }
            return intent;
        }
    }
}
