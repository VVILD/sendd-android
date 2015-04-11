package co.sendd.gettersandsetters;

import java.util.Date;

/**
 * Created by Kuku on 05/03/15.
 */
public class CompleteOrder {

    private String Order_id;
    private String paid;
    private String total_cost;
    private String Order_Status;
    private String image_uri;
    private String drop_name;
    private String drop_phone;
    private String drop_address;
    private String drop_pincode;
    private String category;
    private Date date;
    private String time;
    private String pickup_name;
    private String pickup_phone;
    private String pickup_address;
    private String pickup_pincode;
    private String cost;
    private String tracking_no;

    public String getOrder_id() {
        return Order_id;
    }

    public void setOrder_id(String order_id) {
        Order_id = order_id;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public void setTotal_cost(String total_cost) {
        this.total_cost = total_cost;
    }

    public String getOrder_Status() {
        return Order_Status;
    }

    public void setOrder_Status(String order_Status) {
        Order_Status = order_Status;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getDrop_name() {
        return drop_name;
    }

    public void setDrop_name(String drop_name) {
        this.drop_name = drop_name;
    }

    public String getDrop_phone() {
        return drop_phone;
    }

    public void setDrop_phone(String drop_phone) {
        this.drop_phone = drop_phone;
    }

    public String getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(String drop_address) {
        this.drop_address = drop_address;
    }

    public String getDrop_pincode() {
        return drop_pincode;
    }

    public void setDrop_pincode(String drop_pincode) {
        this.drop_pincode = drop_pincode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPickup_name() {
        return pickup_name;
    }

    public void setPickup_name(String pickup_name) {
        this.pickup_name = pickup_name;
    }

    public String getPickup_phone() {
        return pickup_phone;
    }

    public void setPickup_phone(String pickup_phone) {
        this.pickup_phone = pickup_phone;
    }

    public String getPickup_address() {
        return pickup_address;
    }

    public void setPickup_address(String pickup_address) {
        this.pickup_address = pickup_address;
    }

    public String getPickup_pincode() {
        return pickup_pincode;
    }

    public void setPickup_pincode(String pickup_pincode) {
        this.pickup_pincode = pickup_pincode;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getTracking_no() {
        return tracking_no;
    }

    public void setTracking_no(String tracking_no) {
        this.tracking_no = tracking_no;
    }
}
