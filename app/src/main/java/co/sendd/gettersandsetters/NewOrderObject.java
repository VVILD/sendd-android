package co.sendd.gettersandsetters;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by harshkaranpuria on 9/24/15.
 */
public class NewOrderObject {
    private String address;
    private Date date;
    private String flat_no;
    private Double latitude;
    private Double longitude;
    private NameEmailObject namemail;
    private String pick_now;
    private String pincode;
    private String code;
    private String status;
    private String time;
    private String user;
    private String way;
    private ArrayList<NewShipment> shipments;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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

    public NameEmailObject getNamemail() {
        return namemail;
    }

    public void setNamemail(NameEmailObject namemail) {
        this.namemail = namemail;
    }

    public String getPick_now() {
        return pick_now;
    }

    public void setPick_now(String pick_now) {
        this.pick_now = pick_now;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public ArrayList<NewShipment> getShipments() {
        return shipments;
    }

    public void setShipments(ArrayList<NewShipment> shipments) {
        this.shipments = shipments;
    }
}
