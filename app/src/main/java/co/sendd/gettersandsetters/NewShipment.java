package co.sendd.gettersandsetters;

import java.io.File;

import retrofit.http.Part;
import retrofit.mime.TypedFile;

/**
 * Created by harshkaranpuria on 9/24/15.
 */
public class NewShipment {
    private String image;
    private String drop_name;
    private String drop_phone;
    private String category;
    private Drop_address drop_address;

    public Drop_address getDrop_address() {
        return drop_address;
    }

    public void setDrop_address(Drop_address drop_address) {
        this.drop_address = drop_address;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDrop_phone() {
        return drop_phone;
    }

    public void setDrop_phone(String drop_phone) {
        this.drop_phone = drop_phone;
    }

    public String getDrop_name() {
        return drop_name;
    }

    public void setDrop_name(String drop_name) {
        this.drop_name = drop_name;
    }

    public String getImg() {
        return image;
    }

    public void setImg(String img) {
        this.image = img;
    }


}
