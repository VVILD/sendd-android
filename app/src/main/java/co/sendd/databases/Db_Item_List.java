package co.sendd.databases;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import co.sendd.gettersandsetters.ItemList;

/**
 * Created by Kuku on 21/02/15.
 */
@Table(name = "items_List")
public class Db_Item_List extends Model {
    @Column(name = "orderid")
    public String orderid;
    @Column(name = "date")
    public String date;
    @Column(name = "image_uri")
    public String image_uri;
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
    @Column(name = "setshippingoption")
    public String setshippingoption;

    public static List<Db_Item_List> getAllItems(String Orderid) {
        return new Select()
                .from(Db_Item_List.class)
                .where("orderid= ?", Orderid)
                .execute();
    }

    public void AddToDB(ItemList itemlist) {
        Log.i("Inside AddToDB", "Item Added");
        this.image_uri = itemlist.getImage_URI();
        this.name = itemlist.getReceiver_Name();
        this.phone = itemlist.getPhone();
        this.flat_no = itemlist.getFlat_no();
        this.locality = itemlist.getLocality();
        this.city = itemlist.getCity();
        this.state = itemlist.getState();
        this.country = itemlist.getCountry();
        this.pincode =itemlist.getPinCode();
        this.setshippingoption = itemlist.getShipping_Option();
        this.date = itemlist.getDate();
        this.orderid =itemlist.getOrderId();
        this.save();
        Log.i("Inside AddToDB", "Item Added");
    }

    public void updateShippingItem(String shippingtype ,String imageuri ) {
        Db_Item_List itmeListDB = new Select()
                .from(Db_Item_List.class).where("image_uri = ?",imageuri)
                .executeSingle();
        itmeListDB.setshippingoption = shippingtype;
        itmeListDB.save();
     }

    public void update(ItemList itemlist) {

        Db_Item_List itmeListDB = new Select()
                .from(Db_Item_List.class).where("image_uri = ?",itemlist.getImage_URI())
                .executeSingle();
        if (itmeListDB != null) {
            itmeListDB.image_uri = itemlist.getImage_URI();
            itmeListDB.name = itemlist.getReceiver_Name();
            itmeListDB.phone = itemlist.getPhone();
            itmeListDB.flat_no = itemlist.getFlat_no();
            itmeListDB.locality = itemlist.getLocality();
            itmeListDB.city = itemlist.getCity();
            itmeListDB.state = itemlist.getState();
            itmeListDB.country = itemlist.getCountry();
            itmeListDB.pincode =itemlist.getPinCode();
            itmeListDB.save();
            Log.i("Inside Update", "Item Updated");
        }

    }

    public boolean isItemNull(String imageuri) {
        Db_Item_List itmeListDB = new Select()
                .from(Db_Item_List.class).where("image_uri = ?", imageuri)
                .executeSingle();
        if (itmeListDB != null) {
            if (itmeListDB.locality == null) {
                Log.i("Called in DB", "yes:-)");
                return true;
            } else
                return false;
        } else {
            return false;
        }
    }

    public void updateImage(ItemList itemlist ,String imageuri) {

        Db_Item_List itmeListDB = new Select()
                .from(Db_Item_List.class).where("image_uri = ?",imageuri)
                .executeSingle();
        if (itmeListDB != null) {
            itmeListDB.image_uri = itemlist.getImage_URI();
            itmeListDB.save();
            Log.i("Inside Update", "Item Updated");
        }

    }

    public void deleteItem(String orderid,String image_uri){
        new Delete().from(Db_Item_List.class).where("orderid = ?", orderid).where("image_uri=?",image_uri).execute();
    }

    public void deleteAllItems(){
        new Delete().from(Db_Item_List.class).execute();
    }
 }