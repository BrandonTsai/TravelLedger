package brandon.tsai.travelledger;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;


/**
 * Created by ty on 2015/7/8.
 */
public class DB {

    static SQLiteDatabase db;
    final static String DB_NAME = "JPbestbuy";
    final static String TAG = "DataBase";


    public DB(Context context) {
        initDatabase(context);
    }

    public static void initDatabase(Context context) {
        File database = context.getApplicationContext().getDatabasePath(DB_NAME + ".db");
        if (!database.exists()) {
            // Database does not exist so copy it from assets here
            db = context.openOrCreateDatabase(DB_NAME, context.MODE_PRIVATE, null);
            createTable();
            Log.d(TAG, "Init Database");
        } else {
            Log.d(TAG, "Found Database " + DB_NAME);
        }
    }

    private static void createTable() {
        try {
            db.execSQL("CREATE TABLE IF NOT EXISTS sheets ("
                    + "_id INTEGER PRIMARY KEY autoincrement,"
                    + "name TEXT,"
                    + "date TEXT,"
                    + "type TEXT,"
                    + "price TEXT,"
                    + "cash INTEGER"
                    + ");");
            db.execSQL("CREATE TABLE IF NOT EXISTS items ("
                    + "_id INTEGER PRIMARY KEY autoincrement,"
                    + "name TEXT,"
                    + "price TEXT,"
                    + "amount INTEGER,"
                    + "rate INTEGER,"
                    + "sid INTEGER"
                    + ");");
        } catch (Exception e) {
            Log.d(TAG, "create table error");
            e.printStackTrace();
        }
    }

    // ---------------- Sheets ----------------------

    public static int addSheets(String name, String date, String price, String type, boolean cashflag) {
        ContentValues cv = new ContentValues(5);
        cv.put("name", name);
        cv.put("date", date);
        cv.put("price", price);
        cv.put("type", type);
        int cash = Utils.booleanToInt(cashflag);
        cv.put("cash", cash);
        long noteId = db.insert("sheets", null, cv);
        Log.d(TAG, "New sheet: " + date + "-" + name);
        return (int) noteId;
    }

    public static Cursor getSheets() {
        Cursor cursor = db.query("sheets", new String[]{"_id", "name", "date", "cash", "price", "type"}, null,
                null, null, null, null);
        return cursor;
    }

    public static Cursor getSheetById(int sid) {
        Cursor cursor = db.query("sheets", new String[]{"name", "date", "price", "type", "cash"}, "_id=" + sid ,
                null, null, null, null);
        cursor.moveToFirst();
        return cursor;
    }

    public static void updateSheetName(int tagId, String name) {
        ContentValues cv=new ContentValues(1);
        cv.put("name", name);
        db.update("sheets", cv, "_id=" + tagId, null);
    }

    public static void updateSheet(int sid, String name, String date, String price, String type, boolean cashflag) {
        Log.d(TAG, "update "+ sid + "," + name);
        ContentValues cv=new ContentValues(5);
        int cash = Utils.booleanToInt(cashflag);
        cv.put("name",name);
        cv.put("date",date);
        cv.put("price",price);
        cv.put("type",type);
        cv.put("cash", cash);
        db.update("sheets", cv, "_id=" + sid, null);
    }

    public static void deleteSheet(int id) {
        db.delete("sheets", "_id='" + id + "'", null);
    }


    // ---------------- Items ----------------------


    public static int addItems(String name, String price, int amount, int sid) {
        Log.d(TAG, "add item:" + name);
        ContentValues cv = new ContentValues(5);
        cv.put("name", name);
        cv.put("price", price);
        cv.put("amount", amount);
        cv.put("rate", 0);
        cv.put("sid", sid);
        long id = db.insert("items", null, cv);
        Log.d(TAG, "New items: " + name + "-" + price);
        return (int) id;
    }



    public static Cursor getItems(int sid) {
        Cursor cursor = db.query("items", new String[]{"_id", "name", "price", "amount", "rate", "sid"}, "sid='" + sid + "'",
                null, null, null, null);
        return cursor;
    }

    public static void updateItem(int id, String name, String price, int amount) {
        ContentValues cv=new ContentValues(3);
        cv.put("name", name);
        cv.put("price", price);
        cv.put("amount", amount);
        db.update("items", cv, "_id=" + id, null);
    }

    public static void deleteItem(int id) {
        db.delete("items", "_id='" + id + "'", null);
    }

    public static void deleteItemsWithSheetID(int sid) {
        db.delete("items", "sid='" + sid + "'", null);
    }

    public static boolean hasTaxFreeItem(int sid) {
        Cursor cursor = db.query("items", new String[]{"_id", "name", "rate", "sid"}, "sid='" + sid + "' AND name='"+ Consts.TAX_FREE+"'",
                null, null, null, null);
        if (cursor.getCount() > 0) {
            return true;
        }
        return false;
    }

    public static int addDiscount(String name, int discount, int sid) {
        ContentValues cv = new ContentValues(5);
        cv.put("name", name);
        cv.put("price", 0);
        cv.put("amount", 0);
        cv.put("rate", discount);
        cv.put("sid", sid);
        long id = db.insert("items", null, cv);
        Log.d(TAG, "New Discount: " + discount);
        return (int) id;
    }

    public static void updateDiscount(int id, int discount) {
        ContentValues cv=new ContentValues(1);
        cv.put("rate", discount);
        db.update("items", cv, "_id=" + id, null);
    }



























//
//
//    public static void updateNote(int noteId, String name, String transport, String note, String link, String location, String type) {
//        ContentValues cv=new ContentValues(6);
//
//        cv.put("name", name);
//        cv.put("transport", transport);
//        cv.put("note", note);
//        cv.put("link", link);
//        cv.put("location", location);
//        cv.put("type", type);
//
//        db.update("notes", cv, "_id=" + noteId, null);
//    }
//
//    public static void addNoteTag(int noteId, int tagId) {
//        ContentValues cv = new ContentValues(2);
//        cv.put("noteId", noteId);
//        cv.put("tagId", tagId);
//        db.insert("notetags", null, cv);
//    }
//
//    public static void addNoteTag(int noteId, String tagName) {
//        int tagId = getOrAddTagId(tagName);
//        addNoteTag(noteId, tagId);
//    }
//
//    public static void deleteNote(int noteId) {
//        db.delete("notetags","noteId='"+ noteId+"'",null);
//        db.delete("notes", "_id='" + noteId + "'", null);
//    }
//
//    public static void deleteTag(int tagId) {
//        db.delete("notetags","tagId='"+tagId+"'", null);
//        db.delete("tags","_id='"+ tagId+"'",null);
//    }
//

//
//
//    public static void deleteNoteTag(int noteId, String tagName) {
//        Cursor cursor = getTagIdByName(tagName);
//        if (cursor.getCount() != 0) {
//            cursor.moveToFirst();
//            int tagId = cursor.getInt(0);
//            db.delete("notetags","noteId='"+ noteId+"' AND tagId='"+tagId+"'", null);
//        }
//    }
//
//
//
//    public static Cursor getNotesByTagId(int tagId) {
//        if (tagId > 0) {
//            String queryCmd = "SELECT _id, type, name, transport, note, link, location FROM notes where _id IN ( SELECT noteId FROM notetags where tagId=" + tagId + " ) ORDER BY type DESC";
//            Log.d(TAG, queryCmd);
//            return db.rawQuery(queryCmd, null);
//        } else {
//            return getNotesWithoutTag();
//        }
//    }
//
//    public static Cursor getNotesWithoutTag() {
//        String queryCmd = "SELECT _id, type, name, transport, note, link, location FROM notes where _id NOT IN ( SELECT noteId FROM notetags GROUP BY noteId ) ORDER BY type DESC";
//        Log.d(TAG, queryCmd);
//        return db.rawQuery(queryCmd, null);
//    }
//
//
//
//    public static Cursor getTagsByNoteId(int noteId) {
//        String queryCmd = "SELECT _id, name FROM tags where _id IN ( SELECT tagId FROM notetags where noteId=" + noteId + " )";
//        Log.d(TAG, queryCmd);
//        return db.rawQuery(queryCmd, null);
//    }
//
//    public static Cursor getNotesByNoteId(int noteId) {
//        Cursor cursor = db.query("notes", new String[]{"_id", "type", "name", "transport", "note", "link", "location"}, "_id='"+ noteId+"'" ,
//                null, null, null, null);
//        return cursor;
//    }
//
//    public static void addNotesFromCSV(List<String[]> csvList, String tag) throws FileNotFoundException {
//
//
//        int tagId = getOrAddTagId(tag);
//        Log.d(TAG, "tagId:" + tagId);
//
//        for (String[] line: csvList){
//            String type = line[0];
//            String name = line[1];
//            String transport = line[2];
//            String note = line[3];
//            String link = line[4];
//            String location = "0, 0";
//            try {
//                location = line[5];
//            } catch (ArrayIndexOutOfBoundsException e) {
//                location = "0, 0";
//            }
//            int noteId = addNote(name, transport, note, link, location, type);
//            Log.d(TAG, "noteId:" + noteId);
//
//            addNoteTag(noteId, tagId);
//        }
//    }
//
//    public static Integer getOrAddTagId(String name) {
//        Cursor cursor = getTagIdByName(name);
//        if (cursor.getCount() == 0) {
//            addTag(name);
//            cursor = getTagIdByName(name);
//        }
//        cursor.moveToFirst();
//        return cursor.getInt(0);
//    }
//
//    public static Cursor getTagIdByName(String name) {
//        Cursor cursor = db.query("tags", new String[]{"_id"}, "name = '" + name + "'",
//                null, null, null, null);
//        return cursor;
//    }
//
//    public static void addTag(String name) {
//        ContentValues cv = new ContentValues(1);
//        cv.put("name", name);
//        db.insert("tags", null, cv);
//    }
//
//    public static Cursor getTags() {
//        Cursor cursor = db.query("tags", new String[]{"_id", "name"}, null,
//                null, null, null, null);
//        return cursor;
//    }


}



