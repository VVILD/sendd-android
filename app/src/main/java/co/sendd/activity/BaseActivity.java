package co.sendd.activity;

import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import static co.sendd.helper.Utils.hideSoftKeyboard;

/**
 * Created by harshkaranpuria on 3/27/15.
 */

public class BaseActivity extends ActionBarActivity {

    public void setupUI(View view) {

        if(!(view instanceof EditText) && !(view instanceof ScrollView)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(BaseActivity.this);
                    return false;
                }
            });
        }
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
