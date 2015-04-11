package co.sendd.navdrawer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import co.sendd.databases.Db_User;
import co.sendd.helper.Utils;
import co.sendd.R;

import java.util.ArrayList;

public class NavDrawer_Adapter extends BaseAdapter {

    private Context context;
    private ArrayList<Drawer_Item> navDrawerItems;

    public NavDrawer_Adapter(Context context, ArrayList<Drawer_Item> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }


    @Override
    public int getCount() {
        return navDrawerItems.size() + 1;
    }

    @Override
    public Object getItem(int position) {

        return navDrawerItems.get(position);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return 0;
        else return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.nav_drawer_first_item, parent, false);
            } else {
                LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                convertView = mInflater.inflate(R.layout.nav_drawer_list_item, parent, false);
            }
        }
        if (getItemViewType(position) != 0) {

            TextView txtTitle = (TextView) convertView.findViewById(R.id.nav_drawer_item_title);
            txtTitle.setText(navDrawerItems.get(position - 1).getTitle());
            ImageView seticon = (ImageView) convertView.findViewById(R.id.nav_drawer_item_icon);
            Utils utils = new Utils(context);

            switch (position) {
                case 1:
                    seticon.setImageResource(R.drawable.home_icon);
                    break;
                case 2:
                    seticon.setImageResource(R.drawable.my_shipments_icon);
                    break;
                case 3:
                    seticon.setImageResource(R.drawable.basic_calculator_icon);
                    break;
                case 4:
                    seticon.setImageResource(R.drawable.faq_icon);
                    break;
                case 5:
                    seticon.setImageResource(R.drawable.customer_support_icon);
                    break;



            }
        } else {
            TextView txtTitle = (TextView) convertView.findViewById(R.id.title_first_nav_item);
            TextView txtNumber = (TextView) convertView.findViewById(R.id.settings_first_nav_item);

            Db_User accessDB = new Db_User();
            if (accessDB.getName()== null) {
                txtTitle.setText("Hello");

            } else {
                txtTitle.setText(accessDB.getName());
             }
        }

        return convertView;
    }
}
