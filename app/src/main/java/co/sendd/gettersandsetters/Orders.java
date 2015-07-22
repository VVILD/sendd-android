package co.sendd.gettersandsetters;

import java.util.Date;

/**
 * Created by Kuku on 2/25/2015.
 */
public class Orders {


    private String address;
    private String cancelled;
    private String flat_no;
    private Double latitude;
    private Double longitude;
    private String pincode;
    private String name;
    private String email;
    private String pick_now;
    private String code;
    private String user;
    private Date date;
    private String time;
    private String pickup_flat_no;
    private String pickup_locality;
    private String pickup_city;
    private String pickup_state;
    private String pickup_country;
    private String pickup_pincode;
    private String status;
    private String cost;
    private String paid;
    private String order_no;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPick_now() {
        return pick_now;
    }

    public void setPick_now(String pick_now) {
        this.pick_now = pick_now;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCancelled() {
        return cancelled;
    }

    public void setCancelled(String cancelled) {
        this.cancelled = cancelled;
    }

    public String getFlat_no() {
        return flat_no;
    }

    public void setFlat_no(String flat_no) {
        this.flat_no = flat_no;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }


    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
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

    public String getPickup_flat_no() {
        return pickup_flat_no;
    }

    public void setPickup_flat_no(String pickup_flat_no) {
        this.pickup_flat_no = pickup_flat_no;
    }

    public String getPickup_locality() {
        return pickup_locality;
    }

    public void setPickup_locality(String pickup_locality) {
        this.pickup_locality = pickup_locality;
    }

    public String getPickup_city() {
        return pickup_city;
    }

    public void setPickup_city(String pickup_city) {
        this.pickup_city = pickup_city;
    }

    public String getPickup_state() {
        return pickup_state;
    }

    public void setPickup_state(String pickup_state) {
        this.pickup_state = pickup_state;
    }

    public String getPickup_country() {
        return pickup_country;
    }

    public void setPickup_country(String pickup_country) {
        this.pickup_country = pickup_country;
    }

    public String getPickup_pincode() {
        return pickup_pincode;
    }

    public void setPickup_pincode(String pickup_pincode) {
        this.pickup_pincode = pickup_pincode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }
}
