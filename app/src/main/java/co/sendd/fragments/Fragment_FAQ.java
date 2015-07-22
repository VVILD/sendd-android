package co.sendd.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import co.sendd.R;
import co.sendd.activity.Activity_Main;
import co.sendd.helper.AnimatedExpandableListView;

/**
 * Created by Kuku on 19/03/15.
 */
public class Fragment_FAQ extends Fragment {
    ExpandabelListAdoptor listAdapter;
    //  ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    private AnimatedExpandableListView faq;
    private int last_group_clicked = 1000;
    private int current_group_clicked = 1000;

    public void onResume(){
        super.onResume();
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        Activity_Main.exit = false;
        ((Activity_Main) getActivity()).setActionBarTitle("FAQ's");
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Activity_Main.mProgress != null && Activity_Main.mProgress.isShowing()) {
            Activity_Main.mProgress.dismiss();
        }
        ((Activity_Main) getActivity()).setActionBarTitle("FAQ's");
        Activity_Main.exit = false;

        //  expListView = (ExpandableListView) view.findViewById(R.id.lvExp);
        prepareListData();
        listAdapter = new ExpandabelListAdoptor(getActivity(), listDataHeader, listDataChild);
        faq = (AnimatedExpandableListView) view.findViewById(R.id.lvExp);
        faq.setDividerHeight(0);
        faq.setAdapter(listAdapter);
         faq.setGroupIndicator(null);
        faq.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, final int groupPosition, long id) {
                current_group_clicked = groupPosition;

                if (last_group_clicked != 1000) {

                    if (groupPosition == last_group_clicked) {
                        faq.collapseGroupWithAnimation(last_group_clicked);
                        last_group_clicked = 1000;

                    } else {
                        faq.collapseGroupWithAnimation(last_group_clicked);
                        faq.expandGroupWithAnimation(groupPosition);
                        last_group_clicked = groupPosition;
                    }
                } else {
                    faq.expandGroupWithAnimation(groupPosition);
                    last_group_clicked = groupPosition;
                }
                return true;
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentview = getActivity().getLayoutInflater().inflate(R.layout.fragment_faq, container, false);

        return contentview;
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("What is Sendd ?");
        listDataHeader.add("What all items can I sendd ?");
        listDataHeader.add("How much does it cost ?");
        listDataHeader.add("Why do I need to sendd the picture ?");
        listDataHeader.add("Who does the packaging? How and when is it done?");
        listDataHeader.add("How do I make the payment ?");
        listDataHeader.add("How will I track my parcel ? ");
        listDataHeader.add("How much time will it take to deliver ? ");
        listDataHeader.add("What if my shipment is damaged/lost in transit ?");
        listDataHeader.add("How do I sendd shipments outside India? ");
        listDataHeader.add("How to calculate the volumetric weight of the shipment?");
        // Adding child data
        List<String> one = new ArrayList<>();
        one.add("We are an on demand shipping service. We come to your place to pick up your items, pack them using our customized boxes (if the items are unboxed), and sendd them anywhere in the world using the most cost effective & reliable shipping options.");
        List<String> two = new ArrayList<>();
        two.add("Currently we can pick up items that weigh less than 30 kgs. We will ship anything as long as it can fit in the backseat of a car. We do not ship alcoholic beverages, animals, firearms, cash, counterfeit items, hazardous materials, liquids, tobacco products and items prohibited by law.");
        List<String> three = new ArrayList<>();
        three.add("We will charge you the most economical price for the shipping option you select. We don't take any extra charge for picking up of the item & packing it.");
        List<String> four = new ArrayList<>();
        four.add("Picture is needed so that we know the size of item to pick up. The vehicles used by our pickup team vary from bikes to cars. ");
        List<String> five = new ArrayList<>();
        five.add("Packaging is done by our pick up representative for free when he/she comes for pick up. If there are multiple items in a shipment, we will provide the best possible packaging so that the shipping cost is minimized. If your item is already packed, our pick up representative may ask for that to be opened to ensure that it complies with our policies. He/she will help you pack it again in such a case.");
        List<String> six = new ArrayList<>();
        six.add("Currently we are accepting cash payment at the time of pickup. We will soon be launching online payment options. ");
        List<String> seven = new ArrayList<>();
        seven.add("You can go to My Shipments option in the app & track the status of your shipment there. Also you can track it on the website using your unique tracking ID.");
        List<String> eight = new ArrayList<>();
        eight.add("Currently we have three shipping options i.e. Bulk, Standard & Premium. Depending on the option you select, it can take anywhere between 1 to 7 days. ");
        List<String> nine = new ArrayList<>();
        nine.add("Our safe packaging ensures minimum chances of damage. However, if the item is still received in a damaged condition, kindly contact us within 48 hours of receipt of the same. \n" +
                "You will be compensated upto a maximum of Rs. 10,000( Rs. 50,000 if the item is insured by you at the time of booking at an extra payment of 2%) as per our Terms of Use. To know more, kindly call us on +91-8080028081 or consult with our pick up representative when he/she comes for pick up.");
        List<String> ten = new ArrayList<>();
        ten.add("Currently we aren't accepting international shipments through mobile app. However, you can call us on +91-8080028081 or mail us at help@sendd.co to book an international shipment. ");
        List<String> eleven = new ArrayList<>();
        eleven.add("The volumetric weight (in kgs) can be calculated by the following formula: length(cm)x breadth(cm)x height(cm)/5000.\n" +
                "Shipping cost is calculated using the higher of the two weights- actual or volumetric. ");

        listDataChild.put(listDataHeader.get(0), one);
        listDataChild.put(listDataHeader.get(1), two);
        listDataChild.put(listDataHeader.get(2), three);
        listDataChild.put(listDataHeader.get(3), four);
        listDataChild.put(listDataHeader.get(4), five);
        listDataChild.put(listDataHeader.get(5), six);
        listDataChild.put(listDataHeader.get(6), seven);
        listDataChild.put(listDataHeader.get(7), eight);
        listDataChild.put(listDataHeader.get(8), nine);
        listDataChild.put(listDataHeader.get(9), ten);
        listDataChild.put(listDataHeader.get(10), eleven);
    }

    class ExpandabelListAdoptor extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

        private Context _context;
        private List<String> _listDataHeader;
        private HashMap<String, List<String>> _listDataChild;


        ExpandabelListAdoptor(Context con, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
            this._context = con;
            this._listDataChild = listDataChild;
            this._listDataHeader = listDataHeader;
        }

        @Override
        public Object getChild(int groupPosition, int childPosititon) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .get(childPosititon);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            final String childText = (String) getChild(groupPosition, childPosition);

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expand_list_item, null);
            }

            TextView txtListChild = (TextView) convertView
                    .findViewById(R.id.lblListItem);

            if (groupPosition == 2) {
                txtListChild.setText(Html.fromHtml("We will charge you the most economical price for the shipping option you select. We don't take any extra charge for picking up of the item & packing it. To get an estimate cost of shipping use our rate calculator or view our " + "<a href=\"http://sendd.co/ratecard.jpg\">Rate Card</a> "));
                txtListChild.setMovementMethod(LinkMovementMethod.getInstance());
            } else {
                txtListChild.setText(childText);
            }

            return convertView;
        }

        @Override
        public int getRealChildrenCount(int groupPosition) {
            return this._listDataChild.get(this._listDataHeader.get(groupPosition))
                    .size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this._listDataHeader.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return this._listDataHeader.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {

            String headerTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) this._context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.expand_list_group, null);
            }

            TextView lblListHeader = (TextView) convertView
                    .findViewById(R.id.lblListHeader);
            lblListHeader.setTypeface(null, Typeface.BOLD);
            lblListHeader.setText(headerTitle);

            return convertView;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public int getRealChildType(int groupPosition, int childPosition) {
            if (childPosition == 0) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public void notifyGroupExpanded(int groupPosition) {
            super.notifyGroupExpanded(groupPosition);
         }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

}
