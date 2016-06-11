package brandon.tsai.travelledger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ty on 2016/6/8.
 */
public class Utils {

    public static int booleanToInt(boolean flag){
        return (flag)? 1 : 0;
    }
    public static boolean intToBoolean(int flag){
        if (flag == 0) {
            return false;
        }
        return true;
    }

    public static String getCurrentDate(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(new Date());
    }
}
