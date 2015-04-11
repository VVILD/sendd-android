package co.sendd.helper;

import co.sendd.gettersandsetters.ForgotPass;
import co.sendd.gettersandsetters.Shipment;
import co.sendd.gettersandsetters.Orders;
import co.sendd.gettersandsetters.Otp;
import co.sendd.gettersandsetters.Register;
import co.sendd.gettersandsetters.RegisterUser;
import co.sendd.gettersandsetters.ShipmentDetails;
import co.sendd.gettersandsetters.ShippingDateTime;
import co.sendd.gettersandsetters.ShippingPrice;
import co.sendd.gettersandsetters.Users;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Created by Kuku on 17/02/15.
 */
public interface NetworkCalls {
    @POST("/user/")
    public void register(@Body RegisterUser registeruser, Callback<Users> userInfo);


    @PUT("/user/{user_uri}/")
    public void verify(@Path("user_uri") String user_uri, @Body Otp new_otp, Callback<Users> userverified);

    @Headers("X-HTTP-Method-Override: PATCH")
    @POST("/user/{user_number}/")
    public void updateprofile(@Path("user_number") String user_number, @Body RegisterUser registeruser, Callback<Users> userInfo);

    @POST("/order/")
    public void order(@Body Orders orders, Callback<Orders> order);

    @POST("/dateapp/")
    public void getDate(@Body ShippingDateTime shippingDateTime, Callback<ShippingDateTime> shippingDateTimeCallback);

    @GET("/shipment/{tracking_no}/")
    public void getShipmentDetails(@Path("tracking_no") int tracking_no,Callback<ShipmentDetails> shipmentDetailsCallback);
    @POST("/priceapp/")
    public void getPrice(@Body ShippingPrice shippingPrice, Callback<ShippingPrice> shippingPriceCallback);

    @POST("/forgotpass/")
    public void forgotPassword(@Body ForgotPass forgotPass, Callback<ForgotPass> forgotPassCallback);

    @Multipart
    @POST("/shipment/")
    public void shipment(@Part("img") TypedFile img,
                         @Part("drop_name") String drop_name,
                         @Part("drop_phone") String drop_phone,
                         @Part("drop_flat_no") String drop_flat_no,
                         @Part("drop_locality") String drop_locality,
                         @Part("drop_city") String drop_city,
                         @Part("drop_state") String drop_state,
                         @Part("drop_country") String drop_country,
                         @Part("drop_pincode") String drop_pincode,
                         @Part("order") String order,
                         @Part("category") String category,
                         Callback<Shipment> shipment);

    @POST("/loginsession/")
    public void login(@Body Register register, Callback<Register> registerCallback);



}
