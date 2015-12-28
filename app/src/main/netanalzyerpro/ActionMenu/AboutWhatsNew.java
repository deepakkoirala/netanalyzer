package com.thapasujan5.netanalzyerpro.ActionMenu;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.thapasujan5.netanalyzerpro.R;

/**
 * Created by Suzan on 11/8/2015.
 */
public class AboutWhatsNew {
    Context context;
    boolean check = true;

    public AboutWhatsNew(Context context, boolean condition) {
        this.context = context;
        this.check = condition;
        runCode();

    }

    public AboutWhatsNew(Context context) {
        this.context = context;
        runCode();
    }

    private void runCode() {


        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (check == false) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }
        if (prefs.getBoolean("firstTime", true) == true) {
            final Dialog d = new Dialog(context);
            d.requestWindowFeature(Window.FEATURE_NO_TITLE);
            d.setContentView(R.layout.dialog_newfeatures);

            TextView title, message;
            title = (TextView) d.findViewById(R.id.titleText);
            title.setText("What's new in " + context.getString(R.string.version));

            message = (TextView) d.findViewById(R.id.message);
            message.setText(R.string.newFeatures);
            message.setGravity(Gravity.LEFT);


            Button ok = (Button) d.findViewById(R.id.ok);
            ok.setText("Ok");
            ok.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstTime", false);
                    editor.commit();
                    d.dismiss();
                }
            });
            d.show();
            d.setCancelable(false);
        }
    }
}
