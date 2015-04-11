package co.sendd.databases;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import co.sendd.gettersandsetters.Users;

/**
 * Created by Kuku on 16/02/15.
 */
@Table(name = "user")
public class Db_User extends Model{

    @Column(name = "phone")
    public String phone;
    @Column(name = "email")
    public String email;
    @Column(name = "name")
    public String name;
    @Column(name = "apikey")
    public String apikey;


    public void AddToDB(Users user) {
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.name = user.getName();
        this.apikey = user.getApikey();
        this.save();
        Log.i("Inside AddToDB","User Added");
    }

    public static String getName() {
        Db_User userDB = new Select()
                .from(Db_User.class)
                .executeSingle();
        if (userDB != null) {
            return userDB.name;
        }
        return null;
    }
    public static String getPhoneNumber() {
        Db_User userDB = new Select()
                .from(Db_User.class)
                .executeSingle();
        if (userDB != null) {
            return userDB.phone;
        }
        return null;
    }
    public static String getEmail() {
        Db_User userDB = new Select()
                .from(Db_User.class)
                .executeSingle();
        if (userDB != null) {
            return userDB.email;
        }
        return null;
    }

    public static String getapikey() {
        Db_User userDB = new Select()
                .from(Db_User.class)
                .executeSingle();
        if (userDB != null) {
            return userDB.apikey;
        }
        return null;
    }

    public void update(Users user){

        Db_User userDB = new Select()
                .from(Db_User.class)
                .executeSingle();
        if (userDB != null) {
             userDB.email = user.getEmail();
            userDB.name = user.getName();
             userDB.save();
            Log.i("Inside Update","User Updated");
        }

    }
    public void deleteRecord(){
        if( new Select()
                .from(Db_User.class)
                .executeSingle() != null)
        new Delete().from(Db_User.class).execute();
    }
}