package co.sendd.gettersandsetters;

/**
 * Created by Kuku on 28/02/15.
 */
public class RegisterUser {
    private String phone;
    private String gcmid;
    private String deviceid;

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getGCMRegId() {
        return gcmid;
    }

    public void setGCMRegId(String GCMRegId) {
        this.gcmid = GCMRegId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


}
