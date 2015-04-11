package co.sendd.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.Address;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.helper.PlacesAdapter;
import co.sendd.helper.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Kuku on 19/02/15.
 */
public class Activity_Address_Search_Receiver extends BaseActivity implements AdapterView.OnItemClickListener {
    private static int checkbox_selected_index = -1;
    private ArrayList<Address> Address_list;
    private AddressList_Adapter madapter;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    public static String R_Name, R_Phone, R_Flat_no, R_Locality, R_City, R_State, R_Country, R_Pincode;
    public static ProgressDialog mProgress3;
    AutoCompleteTextView addressSearch;
    //Set Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    @Override
    public void onPause(){
        super.onPause();

    }
    //On Activity Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_search_receiver);
        setupUI(findViewById(R.id.main_parent));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        checkbox_selected_index = -1;
        //Initialize ListView And add Adapter
        mProgress3 = new ProgressDialog(this);
        ListView lv_Saved_Address = (ListView) findViewById(R.id.lvactivity_address_search_receiver_Saved_Address);
        madapter = new AddressList_Adapter(this, R.layout.list_item_address, ShowAddressToList());
        lv_Saved_Address.setAdapter(madapter);

        lv_Saved_Address.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                checkbox_selected_index = position;
                madapter.notifyDataSetChanged();
                if (checkbox_selected_index >= 0) {
                    final Dialog dialog = new Dialog(Activity_Address_Search_Receiver.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_camera);
                    dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                    dialog.show();
                    dialog.findViewById(R.id.bDialogContinue).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (isDeviceSupportCamera())

                            Activity_Address_Search_Receiver.R_Name = Address_list.get(position).getName();
                            Activity_Address_Search_Receiver.R_Phone = Address_list.get(position).getPhone();
                            Activity_Address_Search_Receiver.R_Flat_no = Address_list.get(position).getFlat_no();
                            Activity_Address_Search_Receiver.R_Locality = Address_list.get(position).getLocality();
                            Activity_Address_Search_Receiver.R_City = Address_list.get(position).getCity();
                            Activity_Address_Search_Receiver.R_State = Address_list.get(position).getState();
                            Activity_Address_Search_Receiver.R_Country = Address_list.get(position).getCountry();
                            Activity_Address_Search_Receiver.R_Pincode = Address_list.get(position).getPincode();
                            captureImage();
                        }
                    });
                    dialog.setCancelable(true);
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
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
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
                startActivity(newaddress);
                Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            }
        });


    }

    //Open Camera
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    //On Image Click
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                File imgFile = new File(fileUri.getPath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                File xxx = new File(fileUri.getPath());
                try {
                    FileOutputStream fOut = new FileOutputStream(xxx);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG, 10, fOut);
                    fOut.flush();
                    fOut.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                ItemList itemlist = new ItemList();
                Db_Item_List itemlistDB = new Db_Item_List();
                Utils utils = new Utils(this);
                itemlist.setOrderId(String.valueOf(utils.getOrderId()));
                itemlist.setImage_URI(fileUri.toString());
                itemlist.setShipping_Option("Set Shipping Option");
                itemlist.setDate(date);
                itemlist.setReceiver_Name(Activity_Address_Search_Receiver.R_Name);
                itemlist.setPhone(Activity_Address_Search_Receiver.R_Phone);
                itemlist.setFlat_no(Activity_Address_Search_Receiver.R_Flat_no);
                itemlist.setLocality(Activity_Address_Search_Receiver.R_Locality);
                itemlist.setCity(Activity_Address_Search_Receiver.R_City);
                itemlist.setState(Activity_Address_Search_Receiver.R_State);
                itemlist.setCountry(Activity_Address_Search_Receiver.R_Country);
                itemlist.setPinCode(Activity_Address_Search_Receiver.R_Pincode);
                itemlistDB.AddToDB(itemlist);
                Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                startActivity(i);
                finish();
                Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);

            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //Check if Device Supports camera
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            Toast.makeText(this, "Your Device is not camera supported", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //Create File Uri
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //Create File
    private static File getOutputMediaFile(int type) {

        // Create SD card Location
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Sendd");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("Sendd", "Oops! Failed create " + "Sendd" + " directory");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpeg");
        } else {
            return null;
        }
        return mediaFile;
    }

    //On Back Button Pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
    }

    //save file uri on state change
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("file_uri", fileUri);
        
    }

    //get file uri on state change
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    // On clicking any suggestions form AUto Text View
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
            int x=0;
            for (int j = 0; j <= i - 4; j++) {

                locality = locality.concat(parts[j]);
                if(x<i-4) {
                    locality = locality.concat(", ");
                }
                x=x+1;
            }
            ii.putExtra("Locality", locality);
            Log.i("Locality", locality);
            startActivity(ii);
            Activity_Address_Search_Receiver.this.overridePendingTransition(R.animator.pull_in_right, R.animator.push_out_left);
        } else {
            throw new IllegalArgumentException("");
        }
    }

    //Address Holder
    public class addresslist_holder {
        private TextView name;
        private TextView address;
        private CheckBox tickmark;
    }

    //Address Adapter
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

    //Address List Array
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
        Collections.reverse(Address_list);
        return Address_list;
    }
}