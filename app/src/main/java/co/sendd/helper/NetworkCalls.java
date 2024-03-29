package co.sendd.helper;

import co.sendd.gettersandsetters.NewOrderObject;
import co.sendd.gettersandsetters.Orders;
import co.sendd.gettersandsetters.Otp;
import co.sendd.gettersandsetters.Pincode;
import co.sendd.gettersandsetters.Promo;
import co.sendd.gettersandsetters.Register;
import co.sendd.gettersandsetters.RegisterUser;
import co.sendd.gettersandsetters.Shipment;
import co.sendd.gettersandsetters.ShipmentDetails;
import co.sendd.gettersandsetters.ShippingDateTime;
import co.sendd.gettersandsetters.ShippingPrice;
import co.sendd.gettersandsetters.Users;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;

/**
 * Created by Kuku on 17/02/15.
 */
public interface NetworkCalls {
    @POST("/api/v2/user/")
    void register(@Body RegisterUser registeruser, Callback<Users> userInfo);

    @PUT("/api/v2/user/{user_uri}/")
    void verify(@Path("user_uri") String user_uri, @Body Otp new_otp, Callback<Users> userverified);

    @POST("/api/v2/loginsession/")
    void login(@Body Register register, Callback<Register> registerCallback);

    @Headers("X-HTTP-Method-Override: PATCH")
    @POST("/api/v2/user/{user_number}/")
    void sendGCMID(@Path("user_number") String user_number, @Body RegisterUser registeruser, Callback<RegisterUser> registerUserCallback);

     @POST("/api/v3/order/")
    void order(@Body NewOrderObject orders, Callback<Response> order);

    @POST("/api/v2/dateapp/")
    void getDate(@Body ShippingDateTime shippingDateTime, Callback<ShippingDateTime> shippingDateTimeCallback);

    @GET("/api/v2/shipment/{tracking_no}/")
    void getShipmentDetails(@Path("tracking_no") String tracking_no, Callback<ShipmentDetails> shipmentDetailsCallback);

    @GET("/api/v2/shipment/")
    void getPreviousShipments(@Query("q") String q, @Query("limit") String limit, Callback<Response> Shipments);

    @POST("/api/v2/priceapp/")
    void getPrice(@Body ShippingPrice shippingPrice, Callback<ShippingPrice> shippingPriceCallback);

    @POST("/api/v2/promocheck/")
    void checkPromo(@Body Promo promo, Callback<Promo> promoCallback);

    @POST("/api/v2/pincodecheck/")
    void checkPincode(@Body Pincode pincode, Callback<Pincode> pincodeCallback);

//    @Multipart
//    @POST("/api/v2/shipment/")
//    void shipment(@Part("img") TypedFile img,
//                  @Part("drop_name") String drop_name,
//                  @Part("drop_phone") String drop_phone,
//                  @Part("drop_flat_no") String drop_flat_no,
//                  @Part("drop_locality") String drop_locality,
//                  @Part("drop_city") String drop_city,
//                  @Part("drop_state") String drop_state,
//                  @Part("drop_country") String drop_country,
//                  @Part("drop_pincode") String drop_pincode,
//                  @Part("order") String order,
//                  @Part("category") String category,
//                  Callback<Shipment> shipment);

}
