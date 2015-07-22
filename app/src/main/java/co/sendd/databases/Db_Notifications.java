package co.sendd.databases;

import android.util.Log;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.util.List;

import co.sendd.gettersandsetters.Notification;

@Table(name = "notifications")
public class Db_Notifications extends Model {
    @Column(name = "title")
    public String title;
    @Column(name = "message")
    public String message;

    public static List<Db_Notifications> getAllNotif() {
        return new Select()
                .from(Db_Notifications.class)
                .execute();
    }

    public void AddToDB(Notification notification) {
        Log.i("Inside AddToDB", "Item Added");
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.save();
    }

    public void deleteAllItems() {
        new Delete().from(Db_Notifications.class).execute();
    }

}