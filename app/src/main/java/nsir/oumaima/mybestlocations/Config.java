package nsir.oumaima.mybestlocations;

import android.content.Context;
import android.content.SharedPreferences;

public class Config {
    public static String getBaseUrl(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String ip = preferences.getString("server_ip", null);
        if (ip != null && !ip.isEmpty()) {
            return "http://" + ip + "/servicephp/";
        } else {
            return null;
        }
    }

    public static String getAddPositionUrl(Context context) {
        String baseUrl = getBaseUrl(context);
        return baseUrl != null ? baseUrl + "addposition.php" : null;
    }

    public static String getGetAllUrl(Context context) {
        String baseUrl = getBaseUrl(context);
        return baseUrl != null ? baseUrl + "getall.php" : null;
    }

    public static String getDeleteUrl(Context context) {
        String baseUrl = getBaseUrl(context);
        return baseUrl != null ? baseUrl + "delete.php" : null;
    }


    //public static String IPSERVEUR="192.168.1.66";
    //public static String URL_GETALL="http://"+IPSERVEUR+"/servicephp/getall.php";
    //public static String URL_ADDPOSITION="http://"+IPSERVEUR+"/servicephp/addposition.php";
    //public static String URL_DELETE ="http://"+IPSERVEUR+"/servicephp/delete.php";
}
