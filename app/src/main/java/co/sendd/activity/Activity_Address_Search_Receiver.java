package co.sendd.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.Address;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.helper.PlacesAdapter;
import co.sendd.helper.Utils;

public class Activity_Address_Search_Receiver extends BaseActivity implements AdapterView.OnItemClickListener {
    private static int checkbox_selected_index = -1;
    private ArrayList<Address> Address_list;
    private AddressList_Adapter madapter;
    private AutoCompleteTextView addressSearch;

    @Override
    public void onPause() {
        super.onPause();

    }

    //On Activity Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search_receiver);
        setupUI(findViewById(R.id.main_parent));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //Initialize ListView And add Adapter
        ListView lv_Saved_Address = (ListView) findViewById(R.id.lvactivity_address_search_receiver_Saved_Address);
        madapter = new AddressList_Adapter(this, R.layout.list_item_address, ShowAddressToList());
        lv_Saved_Address.setAdapter(madapter);
        lv_Saved_Address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                checkbox_selected_index = position;
                madapter.notifyDataSetChanged();
                if (checkbox_selected_index >= 0) {

                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    Utils utils = new Utils(Activity_Address_Search_Receiver.this);

                    //Create an Item In item list
                    ItemList itemlist = new ItemList();
                    itemlist.setOrderId(String.valueOf(utils.getOrderId()));
                    itemlist.setImage_URI(getIntent().getStringExtra("ImageURI"));
                    itemlist.setShipping_Option("Set Shipping Option");
                    itemlist.setDate(date);
                    itemlist.setReceiver_Name(Address_list.get(position).getName());
                    itemlist.setPhone(Address_list.get(position).getPhone());
                    itemlist.setFlat_no(Address_list.get(position).getFlat_no());
                    itemlist.setLocality(Address_list.get(position).getLocality());
                    itemlist.setCity(Address_list.get(position).getCity());
                    itemlist.setState(Address_list.get(position).getState());
                    itemlist.setCountry(Address_list.get(position).getCountry());
                    itemlist.setPinCode(Address_list.get(position).getPincode());
                    Db_Item_List itemlistDB = new Db_Item_List();
                    itemlistDB.AddToDB(itemlist);
                    //Item Created

                    //Open Main Activity(Orders page)
                    Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                    startActivity(i);
                    finish();
                    Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

                } else {
                    Toast.makeText(Activity_Address_Search_Receiver.this, "Select an address from list", Toast.LENGTH_LONG).show();
                }

            }
        });
        //Place Suggestions Auto Complete Text View
        addressSearch = (AutoCompleteTextView) findViewById(R.id.etactivity_address_search_receiver_SearchAddress);
        addressSearch.setAdapter(new PlacesAdapter(Activity_Address_Search_Receiver.this, R.layout.autocomplete_list));
        addressSearch.setOnItemClickListener(this);

        //Initialize toolBar
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_address_search_receiver_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                        Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                    }
                }
        );
        //Add Address Button Manually
        Button bAddAddressManually = (Button) findViewById(R.id.bactivity_address_search_receiver_AddManually);
        bAddAddressManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newaddress = new Intent(getApplicationContext(), Activity_Add_New_Address_Receiver.class);
                newaddress.putExtra("ImageURI", getIntent().getStringExtra("ImageURI"));
                startActivity(newaddress);
                Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        });


    }


    //On Back Button Pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    // On clicking any suggestions form Auto Text View
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        String locality = "";
        if (str.contains(", ")) {
            String[] parts = str.split(", ");
            int i = parts.length;
            Intent ii = new Intent(getApplicationContext(), Activity_Add_New_Address_Receiver.class);
            if (i <= 1) {
                ii.putExtra("Country", parts[i - 1]);
                ii.putExtra("State", "");
                ii.putExtra("City", "");
            } else if (i <= 2) {
                ii.putExtra("Country", parts[i - 1]);
                ii.putExtra("State", parts[i - 2]);
                ii.putExtra("City", "");
            } else {
                ii.putExtra("Country", parts[i - 1]);
                ii.putExtra("State", parts[i - 2]);
                ii.putExtra("City", parts[i - 3]);
            }
            int x = 0;
            for (int j = 0; j <= i - 4; j++) {

                locality = locality.concat(parts[j]);
                if (x < i - 4) {
                    locality = locality.concat(", ");
                }
                x = x + 1;
            }
            ii.putExtra("Locality", locality);
            ii.putExtra("ImageURI", getIntent().getStringExtra("ImageURI"));
            startActivity(ii);
            Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
        } else {
            throw new IllegalArgumentException("");
        }
    }

    //Get all Saved Addresses in Address_Receiver DB.
    public ArrayList<Address> ShowAddressToList() {
        List<Db_Address_Receiver> list = Db_Address_Receiver.getAllAddress();
        Address_list = new ArrayList<>();
        for (int i = 0; i < Db_Address_Receiver.getAllAddress().size(); i++) {
            Db_Address_Receiver addDBReceiver = list.get(i);
            Address address = new Address();
            address.setName(addDBReceiver.name);
            address.setPhone(addDBReceiver.phone);
            address.setFlat_no(addDBReceiver.flat_no);
            address.setLocality(addDBReceiver.locality);
            address.setCity(addDBReceiver.city);
            address.setState(addDBReceiver.state);
            address.setCountry(addDBReceiver.country);
            address.setPincode(addDBReceiver.pincode);
            Address_list.add(address);
        }
        // Show last added address first.
        Collections.reverse(Address_list);
        return Address_list;
    }

    //Address Holder
    public class addresslist_holder {
        private TextView name;
        private TextView address;
        private CheckBox tickmark;
    }

    //Set up Address Adapter
    public class AddressList_Adapter extends ArrayAdapter<Address> {

        private Context c;
        private List<Address> address_list;

        public AddressList_Adapter(Context context, int resource, List<Address> objects) {
            super(context, resource, objects);
            this.c = context;
            this.address_list = objects;
        }


        @Override
        public void add(Address object) {
            super.add(object);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return address_list.size();
        }

        @Override
        public Address getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public int getPosition(Address item) {
            return super.getPosition(item);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            addresslist_holder addresslist_holder;
            if (convertView == null) {
                //initialize holder
                addresslist_holder = new addresslist_holder();
                LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item_address, parent, false);
                addresslist_holder.name = (TextView) convertView.findViewById(R.id.tvList_Item_Address_Name);
                addresslist_holder.address = (TextView) convertView.findViewById(R.id.ivList_item_Address_AddressLine);
                addresslist_holder.tickmark = (CheckBox) convertView.findViewById(R.id.ivList_item_Address_RightCheck);
                convertView.setTag(addresslist_holder);
            } else {
                addresslist_holder = (addresslist_holder) convertView.getTag();
            }
            //setup list objects
            addresslist_holder.name.setText(address_list.get(position).getName());
            addresslist_holder.address.setText(address_list.get(position).getFlat_no() + ", " + address_list.get(position).getLocality() + address_list.get(position).getCity() + ", " + address_list.get(position).getState());
            if (Activity_Address_Search_Receiver.checkbox_selected_index == position) {
                addresslist_holder.tickmark.setChecked(true);
            } else {
                addresslist_holder.tickmark.setChecked(false);
            }
            return convertView;
        }
    }
}