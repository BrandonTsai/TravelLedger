package brandon.tsai.travelledger;

import java.util.Collections;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Brandon on 16/6/10.
 */
public class Consts {
    public final static String TAX_FREE="Tax Free";
    public final static String DISCOUNT="Discount";
    public final static String TIPS="Tips";

    public static Map<Integer, String> SHEET_TYPE;
    static {
        Map<Integer, String> aMap = new Hashtable<>();
        aMap.put(R.id.radioBtn_sheet_type_food, "Food");
        aMap.put(R.id.radioBtn_sheet_type_shop, "Shopping");
        aMap.put(R.id.radioBtn_sheet_type_transport, "Transport");
        aMap.put(R.id.radioBtn_sheet_type_hotel, "Hotel");
        aMap.put(R.id.radioBtn_sheet_type_other, "Others");

        SHEET_TYPE = Collections.unmodifiableMap(aMap);
    }

    public static Map<String, Integer> SHEET_TYPE_REVERT;
    static {
        Map<String, Integer> aMap = new Hashtable<>();
        aMap.put("Food", R.id.radioBtn_sheet_type_food);
        aMap.put("Shopping", R.id.radioBtn_sheet_type_shop);
        aMap.put("Transport", R.id.radioBtn_sheet_type_transport);
        aMap.put("Hotel", R.id.radioBtn_sheet_type_hotel);
        aMap.put("Others", R.id.radioBtn_sheet_type_other);

        SHEET_TYPE_REVERT = Collections.unmodifiableMap(aMap);
    }

}
