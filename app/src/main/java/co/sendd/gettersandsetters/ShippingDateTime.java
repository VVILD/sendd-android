package co.sendd.gettersandsetters;

/**
 * Created by harshkaranpuria on 3/28/15.
 */
public class ShippingDateTime {
    private String economy;
    private String pincode;
    private String premium;
    private String standard;

    public String getEconomy() {
        return economy;
    }

    public void setEconomy(String economy) {
        this.economy = economy;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }
}
