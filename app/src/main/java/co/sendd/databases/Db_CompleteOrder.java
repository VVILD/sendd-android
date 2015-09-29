package co.sendd.databases;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.Date;
import java.util.List;

import co.sendd.gettersandsetters.CompleteOrder;

/**
 * Created by Kuku on 08/03/15.
 */
@Table(name ="completeorder")
public class Db_CompleteOrder extends Model{
    @Column(name = "Order_id")
    public String Order_id;
    @Column(name = "paid")
    public String paid;
    @Column(name = "total_cost")
    public String total_cost;
    @Column(name = "Order_Status")
    public String Order_Status;
    @Column(name = "image_uri")
    public String image_uri;

    @Column(name = "drop_name")
    public String drop_name;
    @Column(name = "drop_phone")
    public String drop_phone;
    @Column(name = "drop_address")
    public String drop_address;
    @Column(name = "drop_pincode")
    public String drop_pincode;
    @Column(name = "category")
    public String category;

    @Column(name = "date")
    public Date date;
    @Column(name = "time")
    public String time;
    @Column(name = "pickup_name")
    public String pickup_name;
    @Column(name = "pickup_phone")
    public String pickup_phone;
    @Column(name = "pickup_address")
    public String pickup_address;

    @Column(name = "pickup_pincode")
    public String pickup_pincode;
    @Column(name = "cost")
    public String cost;
    @Column(name = "tracking_no")
    public String tracking_no;

    public static List<Db_CompleteOrder> getAllAddress() {
        return new Select()
                .from(Db_CompleteOrder.class)
                .execute();
    }

    public void AddToDB(CompleteOrder orders) {
        this.Order_id = orders.getOrder_id();
        this.paid = orders.getPaid();
        this.total_cost = orders.getCost();
        this.Order_Status = orders.getOrder_Status();
        this.image_uri = orders.getImage_uri();

        this.drop_name = orders.getDrop_name();
        this.drop_address = orders.getDrop_address();
        this.drop_pincode = orders.getDrop_pincode();
        this.drop_phone = orders.getDrop_phone();
        this.category = orders.getCategory();

        this.date = orders.getDate();
        this.time = orders.getTime();
        this.pickup_name = orders.getPickup_name();
        this.pickup_phone = orders.getPickup_phone();
        this.pickup_address = orders.getPickup_address();

        this.pickup_pincode = orders.getPickup_pincode();
        this.cost = orders.getCost();
        this.tracking_no = orders.getTracking_no();
        this.save();
        Log.i("Inside AddToDB", "Sender Address Added");

    }

    public void deleteAllItems(){
        new Delete().from(Db_CompleteOrder.class).execute();
    }

    public void updateStatus(String Imageuri,String Status){
        Db_CompleteOrder completeOrderDB = new Select()
                .from(Db_CompleteOrder.class)
                .where("image_uri =?",Imageuri)
                .executeSingle();
        if (completeOrderDB != null) {
            completeOrderDB.Order_Status = Status;
            completeOrderDB.save();
            Log.i("Inside Update","Status Changed");
        }

    }
}
