package co.sendd.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.sendd.activity.Activity_Main;
import co.sendd.R;

/**
 * Created by Kuku on 18/02/15.
 */
public class Fragment_Customer_Support  extends Fragment{
    TextView call;
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.fragment_customer_support,container,false);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((Activity_Main) getActivity()).setActionBarTitle("Customer Support");
        Activity_Main.exit =false;
        Activity_Main.noShipmentTv.setVisibility(View.GONE);
        call =(TextView)view.findViewById(R.id.callphone);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:+" + call.getText().toString().trim()));
                startActivity(callIntent );
                getActivity().overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        });
        if (Activity_Main.mProgress!=null && Activity_Main.mProgress.isShowing()){
            Activity_Main.mProgress.dismiss();
        }
    }
    public void onResume(){
        super.onResume();
        ((Activity_Main) getActivity()).setActionBarTitle("Customer Support");
        Activity_Main.exit =false;
        Activity_Main.noShipmentTv.setVisibility(View.GONE);
        if (Activity_Main.mProgress!=null && Activity_Main.mProgress.isShowing()){
            Activity_Main.mProgress.dismiss();
        }
    }
}
