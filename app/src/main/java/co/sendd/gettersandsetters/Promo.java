package co.sendd.gettersandsetters;

/**
 * Created by harshkaranpuria on 5/15/15.
 */
public class Promo {
    private String phone;
    private String code;
    private String promomsg;
    private String valid;

    public String getValid() {
        return valid;
    }

    public void setValid(String valid) {
        this.valid = valid;
    }

    public String getPromomsg() {
        return promomsg;
    }

    public void setPromomsg(String promomsg) {
        this.promomsg = promomsg;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
