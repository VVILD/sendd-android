package co.sendd.databases;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import co.sendd.gettersandsetters.Address;

import java.util.List;

@Table(name = "address_receiver")
public class Db_Address_Receiver extends Model{

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
        Log.i("Db_Address_Receiver","Address Added");
    }

    public void update(Address address){
        Db_Address_Receiver addressDb = new Select()
                .from(Db_Address_Receiver.class).where("phone = ?",address.getPhone()).where("name= ?",address.getName())
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
            Log.i("DATA ADDED"+addressDb.name+addressDb.phone+addressDb.locality+" ===",address.getName()+address.getPhone()+address.getFlat_no()+address.getLocality()+address.getCity()+address.getState());
            Log.i("Db_Address_Receiver","Address Updated");
        }
    }

    public static List<Db_Address_Receiver> getAllAddress() {
        return new Select()
                .from(Db_Address_Receiver.class)
                .execute();
    }

    public static Db_Address_Receiver getaddress(int id){
        Db_Address_Receiver address_reciever = new Select()
                .from(Db_Address_Receiver.class)
                .where("id= ?",id)
                .executeSingle();
        return address_reciever;
    }
    public void deleteAllItems(){
        new Delete().from(Db_Address_Receiver.class).execute();
    }
}