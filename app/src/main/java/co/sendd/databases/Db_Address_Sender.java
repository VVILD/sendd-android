package co.sendd.databases;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import co.sendd.gettersandsetters.Address;

import java.util.List;

/**
 * Created by Kuku on 16/02/15.
 */
@Table(name = "address_sender")
public class Db_Address_Sender extends Model{
    @Column(name = "name")
    public String name;
    @Column(name = "phone")
    public String phone;
    @Column(name = "flat_no")
    public String flat_no;
    @Column(name = "locality")
    public String locality;
    @Column(name = "city")
    public String city;
    @Column(name = "state")
    public String state;
    @Column(name = "country")
    public String country;
    @Column(name = "pincode")
    public String pincode;


    public void AddToDB(Address address) {
        this.name = address.getName();
        this.phone = address.getPhone();
        this.flat_no = address.getFlat_no();
        this.locality = address.getLocality();
        this.city = address.getCity();
        this.state = address.getState();
        this.country = address.getCountry();
        this.pincode = address.getPincode();
        this.save();
        Log.i("Inside AddToDB", "Sender Address Added");

    }
/*
    public static String getCountry() {
        Db_Address_Sender addressDb = new Select()
                .from(Db_Address_Sender.class)
                .executeSingle();
        if (addressDb != null) {
            return addressDb.country;
        }
        return null;
    }
    public static String getFlat_no() {
        Db_Address_Sender addressDb = new Select()
                .from(Db_Address_Sender.class)
                .executeSingle();
        if (addressDb != null) {
            return addressDb.flat_no;
        }
        return null;
    }
    public static String getCity() {
        Db_Address_Sender addressDb = new Select()
                .from(Db_Address_Sender.class)
                .executeSingle();
        if (addressDb != null) {
            return addressDb.city;
        }
        return null;
    }

    public static String getState() {
        Db_Address_Sender addressDb = new Select()
                .from(Db_Address_Sender.class)
                .executeSingle();
        if (addressDb != null) {
            return addressDb.state;
        }
        return null;
    }
    public static String getPincode() {
        Db_Address_Sender addressDb = new Select()
                .from(Db_Address_Sender.class)
                .executeSingle();
        if (addressDb != null) {
            return addressDb.pincode;
        }
        return null;
    }
*/
    public void update(Address address){
        Db_Address_Sender addressDb = new Select()
                .from(Db_Address_Sender.class).where("phone = ?",address.getPhone()).where("name= ?",address.getName())
                .executeSingle();
        if (addressDb != null) {
            addressDb.name = address.getName();
            addressDb.phone = address.getPhone();
            addressDb.flat_no = address.getFlat_no();
            addressDb.locality = address.getLocality();
            addressDb.city = address.getCity();
            addressDb.state = address.getState();
            addressDb.country = address.getCountry();
            addressDb.pincode = address.getPincode();
            addressDb.save();
            Log.i("Inside AddToDB", "Sender Address Updated");

        }

    }
    public static List<Db_Address_Sender> getAllAddress() {
        return new Select()
                .from(Db_Address_Sender.class)
                .execute();
    }
    public static Db_Address_Sender getaddress(int id) {
        Db_Address_Sender address_sender = new Select()
                .from(Db_Address_Sender.class)
                .where("id= ?", id)
                .executeSingle();
        return address_sender;
    }
    public void deleteAllItems(){
        new Delete().from(Db_Address_Sender.class).execute();
    }
}