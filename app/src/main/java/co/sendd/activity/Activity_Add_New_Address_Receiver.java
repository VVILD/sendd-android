package co.sendd.activity;

import android.app.Dialog;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import co.sendd.R;
import co.sendd.databases.Db_Address_Receiver;
import co.sendd.databases.Db_Item_List;
import co.sendd.gettersandsetters.Address;
import co.sendd.gettersandsetters.ItemList;
import co.sendd.helper.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Kuku on 19/02/15.
 */
public class Activity_Add_New_Address_Receiver extends BaseActivity implements TextWatcher {

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private Uri fileUri;
    private Button SaveAddress;
    private EditText etReceiverName, etReceiverFlatno, etReceiverNumber, etReceiverCountry, etReceiverLocality, etReceiverCity, etReceiverState;
    private AutoCompleteTextView etReceiverPincode;
    String[] pincodes;
    boolean matched =false;

    //Set Font
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(new CalligraphyContextWrapper(newBase));
    }

    public void Init() {

        pincodes = getResources().getStringArray(R.array.pincode);
        ArrayAdapter adapter = new ArrayAdapter (this, android.R.layout.simple_list_item_1, pincodes);

        etReceiverName = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Name);
        etReceiverName.addTextChangedListener(this);
        etReceiverFlatno = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_flatno);
        etReceiverFlatno.addTextChangedListener(this);
        etReceiverNumber = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Phone);
        etReceiverNumber.addTextChangedListener(this);
        etReceiverCountry = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Country);
        etReceiverCountry.addTextChangedListener(this);
        etReceiverLocality = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_Locality);
        etReceiverLocality.addTextChangedListener(this);
        etReceiverCity = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_City);
        etReceiverCity.addTextChangedListener(this);
        etReceiverState = (EditText) findViewById(R.id.etactivity_add_new_address_receiver_State);
        etReceiverState.addTextChangedListener(this);
        etReceiverPincode = (AutoCompleteTextView) findViewById(R.id.etactivity_add_new_address_receiver_Pincode);
        etReceiverPincode.addTextChangedListener(this);
        etReceiverPincode.setAdapter(adapter);

        if (getIntent() != null) {
            etReceiverCountry.setText(getIntent().getStringExtra("Country"));
            etReceiverLocality.setText(getIntent().getStringExtra("Locality"));
            etReceiverCity.setText(getIntent().getStringExtra("City"));
            etReceiverState.setText(getIntent().getStringExtra("State"));
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_add_new_address_receiver_toolbar);
        SaveAddress = (Button) findViewById(R.id.bactivity_add_new_address_receiver_SaveAddress);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.arrow_back_icon_small);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                                                 @Override
                                                 public void onClick(View v) {
                                                     finish();
                                                     Activity_Add_New_Address_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
                                                 }
                                             }
        );
    }

    //On Activity Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_address_receiver);
        setupUI(findViewById(R.id.main_parent));
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        //Initialize EditTexts
        Init();

        //Save New Address And Open Camera
        SaveAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(etReceiverName.getText().toString())) {
                    if (!TextUtils.isEmpty(etReceiverNumber.getText().toString())) {
                        if (!TextUtils.isEmpty(etReceiverFlatno.getText().toString())) {
                            if (!TextUtils.isEmpty(etReceiverLocality.getText().toString())) {
                                if (!TextUtils.isEmpty(etReceiverCity.getText().toString())) {
                                    if (!TextUtils.isEmpty(etReceiverState.getText().toString())) {
                                        if (!TextUtils.isEmpty(etReceiverCountry.getText().toString())) {
                                            if (!TextUtils.isEmpty(etReceiverPincode.getText().toString())) {
                                                for (int i = 0; i < pincodes.length; i++) {
                                                    if (pincodes[i].equals(etReceiverPincode.getText().toString())) {
                                                        matched = true;
                                                        break;
                                                    }
                                                }
                                                if (matched) {
                                                    final Dialog dialog = new Dialog(Activity_Add_New_Address_Receiver.this);
                                                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                                    dialog.setContentView(R.layout.dialog_camera);
                                                    dialog.getWindow().setBackgroundDrawable((new ColorDrawable(android.graphics.Color.TRANSPARENT)));
                                                    dialog.show();
                                                    dialog.findViewById(R.id.bDialogContinue).setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            dialog.dismiss();
                                                            if (isDeviceSupportCamera()) {
                                                                Address address = new Address();
                                                                address.setName(etReceiverName.getText().toString());
                                                                address.setPhone(etReceiverNumber.getText().toString());
                                                                address.setFlat_no(etReceiverFlatno.getText().toString());
                                                                address.setLocality(etReceiverLocality.getText().toString());
                                                                address.setCity(etReceiverCity.getText().toString());
                                                                address.setState(etReceiverState.getText().toString());
                                                                address.setCountry(etReceiverCountry.getText().toString());
                                                                address.setPincode(etReceiverPincode.getText().toString());
                                                                Db_Address_Receiver address_receiverDB = new Db_Address_Receiver();
                                                                address_receiverDB.AddToDB(address);
                                                                captureImage();
                                                            }
                                                        }
                                                    });
                                                    dialog.setCancelable(true);
                                                } else {
                                                    Toast.makeText(Activity_Add_New_Address_Receiver.this, "Currently we don't serve in the provided location", Toast.LENGTH_LONG).show();
                                                }
                                            } else {
                                                etReceiverPincode.setError("* Pincode is required");
                                            }
                                        } else {
                                            etReceiverCountry.setError("* Country is required");
                                        }
                                    } else {
                                        etReceiverState.setError("* State is required");
                                    }
                                } else {
                                    etReceiverCity.setError("* City is required");
                                }
                            } else {
                                etReceiverLocality.setError("* Locality is required");
                            }
                        } else {
                            etReceiverFlatno.setError("* Flatno is required");
                        }
                    } else {
                        etReceiverNumber.setError("* Number is required");
                    }
                } else {
                    etReceiverName.setError("* Name is required");
                }
            }
        });
    }

    //Start Camera
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    //After Capturing the image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                File imgFile = new File(fileUri.getPath());
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                File xxx = new File(fileUri.getPath());
                try {
                    FileOutputStream fOut = new FileOutputStream(xxx);
                    myBitmap.compress(Bitmap.CompressFormat.JPEG,10, fOut);
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
                itemlist.setReceiver_Name(etReceiverName.getText().toString());
                itemlist.setPhone(etReceiverNumber.getText().toString());
                itemlist.setFlat_no(etReceiverFlatno.getText().toString());
                itemlist.setLocality(etReceiverLocality.getText().toString());
                itemlist.setCity(etReceiverCity.getText().toString());
                itemlist.setState(etReceiverState.getText().toString());
                itemlist.setCountry(etReceiverCountry.getText().toString());
                itemlist.setPinCode(etReceiverPincode.getText().toString());
                itemlistDB.AddToDB(itemlist);
                Log.i("Item List item added ", "item added for order id =" + utils.getOrderId());
                Intent i = new Intent(getApplicationContext(), Activity_Main.class);
                startActivity(i);
                finish();
                Activity_Add_New_Address_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }

        }
    }

    //check if device supports the camera
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            Toast.makeText(this, "Your Device is not camera supported", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //generate file uri
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    //Create a File
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

    //if Back button is pressed
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Activity_Add_New_Address_Receiver.this.overridePendingTransition(R.animator.pull_in_left, R.animator.push_out_right);
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

    public void onResume() {
        super.onResume();
        setupUI(findViewById(R.id.main_parent));
    }

    //TEXT WATCHER options
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!etReceiverName.getText().toString().isEmpty()) etReceiverName.setError(null, null);
        if (!etReceiverFlatno.getText().toString().isEmpty()) etReceiverFlatno.setError(null, null);
        if (!etReceiverNumber.getText().toString().isEmpty()) etReceiverNumber.setError(null, null);
        if (!etReceiverLocality.getText().toString().isEmpty()) etReceiverLocality.setError(null, null);
        if (!etReceiverCity.getText().toString().isEmpty()) etReceiverCity.setError(null, null);
        if (!etReceiverState.getText().toString().isEmpty()) etReceiverState.setError(null, null);
        if (!etReceiverPincode.getText().toString().isEmpty()) etReceiverPincode.setError(null, null);
        if (!etReceiverCountry.getText().toString().isEmpty()) etReceiverCountry.setError(null, null);
    }
    @Override
    public void afterTextChanged(Editable s) {

    }
}