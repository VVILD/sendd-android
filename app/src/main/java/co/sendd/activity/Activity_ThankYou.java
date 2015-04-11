package co.sendd.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import co.sendd.helper.Utils;
import co.sendd.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Kuku on 08/03/15.
 */
public class Activity_ThankYou extends Activity {
    Button  newBooking, PreviousBooking;
    TextView text;
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thankyou);
        Utils utils =new Utils(this);
        utils.updateOrderId();
        text =(TextView)findViewById(R.id.tvThankyouText2);
       if( getIntent().getExtras() != null){
            if(getIntent().getExtras().getBoolean("PickLater")){
                text.setText("Shipment booked successfully. Sendd representative will be at your doorstep at your scheduled time. Sit back and relax.");
            }else if(!getIntent().getExtras().getBoolean("PickLater")){
                text.setText("Shipment booked successfully. Sendd representative will be at your doorstep within an hour. Sit back and relax.");
            }
       }
        newBooking =(Button)findViewById(R.id.bBookAnother);
        PreviousBooking =(Button)findViewById(R.id.ViewPrevious);
        newBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Activity_ThankYou.this,Activity_Main.class);
                startActivity(i);
                Activity_ThankYou.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        });
        PreviousBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Activity_ThankYou.this,Activity_Main.class);
                 i.putExtra("openpreviousBooking",true);
                Activity_ThankYou.this.startActivity(i);
                Activity_ThankYou.this.overridePendingTransition(R.animator.fade_in,R.animator.fade_out);
                finish();
            }
        });
    }
}
