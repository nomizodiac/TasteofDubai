package org.progos.tasteofdubaicms.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import org.progos.tasteofdubaicms.model.Chef;
import org.progos.tasteofdubaicms.model.Restaurant;
import org.progos.tasteofdubaicms.model.RestaurantItem;
import org.progos.tasteofdubaicms.model.Schedule;
import org.progos.tasteofdubaicms.model.ScheduleItem;
import org.progos.tasteofdubaicms.model.ScheduleSectionedItem;
import org.progos.tasteofdubaicms.model.VenueMap;
import org.progos.tasteofdubaicms.utility.Commons;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class DataBaseHelper extends SQLiteOpenHelper {

    private final Context myContext;
    private SQLiteDatabase myDataBase;
    private static DataBaseHelper mInstance;

    // Database details
    private static String dbPath = Commons.DB_PATH;
    private static String dbName = Commons.DB_NAME;

    // Table Restaurants details
    private String TABLE_RESTAURANTS = "Restaurants";
    private static String KEY_RESTAURANT_ID = "RestaurantId";
    private static String KEY_RESTAURANT_IMG_URL = "RestaurantImgUrl";
    private static String KEY_RESTAURANT_HAS_CAT = "HasCat";

    // Table Restaurant item details
    private String TABLE_RESTAURANT_ITEMS = "RestaurantItems";
    private static String KEY_REST_ITEM_TITLE = "Title";
    private static String KEY_REST_ITEM_CATEGORY = "Category";
    private static String KEY_REST_ITEM_DESCRIPTION = "Description";
    private static String KEY_REST_ITEM_PRICE = "Price";

    // Table Chefs details
    private String TABLE_CHEFS = "Chefs";
    private static String KEY_CHEF_ID = "ChefId";
    private static String KEY_CHEF_NAME = "ChefName";
    private static String KEY_CHEF_IMG_URL = "ChefImgUrl";
    private static String KEY_CHEF_DESCRIPTION = "ChefDescription";

    // Table Schedules details
    private String TABLE_SCHEDULES = "Schedules";
    private static String KEY_SCHEDULE_ID = "ScheduleId";
    private static String KEY_SCHEDULE_DAY = "Day";
    private static String KEY_SCHEDULE_DATE = "Date";

    // Table Schedule item details
    private String TABLE_SCHEDULE_ITEMS = "ScheduleItems";
    private static String KEY_SCHEDULE_ITEM_DETAILS = "SectionItemDetails";

    // Table Maps
    private String TABLE_MAPS = "Maps";
    private static String KEY_MAP_ID = "MapId";
    private static String KEY_MAP_TITLE = "MapTitle";

    // Table Schedule item details
    private String TABLE_MAP_ITEMS = "MapItems";
    private static String KEY_MAP_URL = "MapUrl";
    private static String KEY_MAP_LEGEND_RESTAURANsTS = "LegendRestaurants";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     *
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, dbName, null, 1);
        this.myContext = context;
        try {
            createDataBase();
            openDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get default instance of the class to keep it a singleton
     *
     * @param context the application context
     */
    public static DataBaseHelper getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new DataBaseHelper(context);
        }
        return mInstance;
    }

    //********************************************************************************************************************************/

    /**
     * Creates a empty database on the system and rewrites it with your own
     * database.
     */
    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
            //do nothing - database already exist
        } else {
            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    //********************************************************************************************************************************/

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     *
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase() {

        SQLiteDatabase checkDB = null;
        try {
            String myPath = dbPath + dbName;
            File file = new File(myPath);
            if (file.exists() && !file.isDirectory())
                checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database doesn't exist yet.
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    //********************************************************************************************************************************/

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(dbName);
        // Path to the just created empty db
        String outFileName = dbPath + dbName;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    //********************************************************************************************************************************/

    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = dbPath + dbName;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    //********************************************************************************************************************************/

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }

    //********************************************************************************************************************************/

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    //********************************************************************************************************************************/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //********************************************************************************************************************************/

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    //********************************************************************************************************************************/

    public int addRestaurants(ArrayList<Restaurant> restaurants) throws SQLException {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < restaurants.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_RESTAURANT_ID, restaurants.get(i).getId());
            values.put(KEY_RESTAURANT_IMG_URL, restaurants.get(i).getImgUrl());

            //Checking if restaurant already exist in local db
            int messageCount = getRecordCountRestaurantById(restaurants.get(i).getId());
            if (messageCount == 0) {
                //If restaurant does not exist already, Insert new record in messages table
                long idRowInserted = db.insert(TABLE_RESTAURANTS, null, values);
                if (idRowInserted != -1)
                    noOfRowsInserted++;
            } else {
                //If restaurant exist already, Update it
                db.update(TABLE_RESTAURANTS, values, KEY_RESTAURANT_ID + " = ?", new String[]{restaurants.get(i).getId()});
            }
        }
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    public ArrayList<Restaurant> getRestaurants() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Restaurant> restaurants = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RESTAURANTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String keyRestaurantId = cursor.isNull(cursor.getColumnIndex(KEY_RESTAURANT_ID)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_RESTAURANT_ID));
                String keyRestaurantImageUrl = cursor.isNull(cursor.getColumnIndex(KEY_RESTAURANT_IMG_URL)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_RESTAURANT_IMG_URL));
                String keyRestaurantHasCat = cursor.isNull(cursor.getColumnIndex(KEY_RESTAURANT_HAS_CAT)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_RESTAURANT_HAS_CAT));

                Restaurant restaurant = new Restaurant(keyRestaurantId, keyRestaurantImageUrl, keyRestaurantHasCat);
                restaurants.add(restaurant);
            }
            while (cursor.moveToNext());
        }
        return restaurants;
    }

    //********************************************************************************************************************************/

    public int updateRestaurant(Restaurant restaurant) throws SQLException {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_RESTAURANT_IMG_URL, restaurant.getImgUrl());
        values.put(KEY_RESTAURANT_HAS_CAT, restaurant.getHasCat());
        noOfRowsInserted = db.update(TABLE_RESTAURANTS, values, KEY_RESTAURANT_ID + "=?", new String[]{restaurant.getId()});
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    private int getRecordCountRestaurantById(String id) {
        // TODO Auto-generated method stub
        int nCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RESTAURANTS, null, KEY_RESTAURANT_ID + " = ?", new String[]{id}, null, null, null);
        nCount = cursor.getCount();
        cursor.close();
        return nCount;
    }

    //********************************************************************************************************************************/

    public int deleteRestaurant(Restaurant restaurant) throws SQLException {
        int rowAffected = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        rowAffected = db.delete(TABLE_RESTAURANTS, KEY_RESTAURANT_ID + " = ?", new String[]{restaurant.getId()});
        db.close();
        return rowAffected;
    }

    //********************************************************************************************************************************/

    public int addChefs(ArrayList<Chef> chefs) {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < chefs.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_CHEF_ID, chefs.get(i).getId());
            values.put(KEY_CHEF_NAME, chefs.get(i).getName());
            values.put(KEY_CHEF_DESCRIPTION, chefs.get(i).getDescription());
            values.put(KEY_CHEF_IMG_URL, chefs.get(i).getImageUrl());

            //Checking if chef already exist in local db
            int messageCount = getRecordCountChefById(chefs.get(i).getId());
            if (messageCount == 0) {
                //If chef does not exist already, Insert new record
                long idRowInserted = db.insert(TABLE_CHEFS, null, values);
                if (idRowInserted != -1)
                    noOfRowsInserted++;
            } else {
                //If chef exist already, Update it
                db.update(TABLE_CHEFS, values, KEY_CHEF_ID + " = ?", new String[]{chefs.get(i).getId()});
            }
        }
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    public ArrayList<Chef> getChefs() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Chef> chefs = new ArrayList<>();
        Cursor cursor = db.query(TABLE_CHEFS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String keyChefId = cursor.isNull(cursor.getColumnIndex(KEY_CHEF_ID)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_CHEF_ID));
                String keyChefName = cursor.isNull(cursor.getColumnIndex(KEY_CHEF_NAME)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_CHEF_NAME));
                String keyChefImageUrl = cursor.isNull(cursor.getColumnIndex(KEY_CHEF_IMG_URL)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_CHEF_IMG_URL));
                String keyChefDescription = cursor.isNull(cursor.getColumnIndex(KEY_CHEF_DESCRIPTION)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_CHEF_DESCRIPTION));

                Chef chef = new Chef(keyChefId, keyChefName, keyChefImageUrl, keyChefDescription);
                chefs.add(chef);
            }
            while (cursor.moveToNext());
        }
        return chefs;
    }

    //********************************************************************************************************************************/

    private int getRecordCountChefById(String id) {
        // TODO Auto-generated method stub
        int nCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_CHEFS, null, KEY_CHEF_ID + " = ?", new String[]{id}, null, null, null);
        nCount = cursor.getCount();
        cursor.close();
        return nCount;
    }

    //********************************************************************************************************************************/

    public int addSchedules(ArrayList<Schedule> schedules) {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < schedules.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_SCHEDULE_ID, schedules.get(i).getId());
            values.put(KEY_SCHEDULE_DAY, schedules.get(i).getDay());
            values.put(KEY_SCHEDULE_DATE, schedules.get(i).getDate());

            //Checking if chef already exist in local db
            int messageCount = getRecordCountScheduleById(schedules.get(i).getId());
            if (messageCount == 0) {
                //If chef does not exist already, Insert new record
                long idRowInserted = db.insert(TABLE_SCHEDULES, null, values);
                if (idRowInserted != -1)
                    noOfRowsInserted++;
            } else {
                //If chef exist already, Update it
                db.update(TABLE_SCHEDULES, values, KEY_SCHEDULE_ID + " = ?", new String[]{schedules.get(i).getId()});
            }
        }
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    public int addMaps(ArrayList<VenueMap> venueMaps) {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < venueMaps.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_MAP_ID, venueMaps.get(i).getId());
            values.put(KEY_MAP_TITLE, venueMaps.get(i).getMapTitle());

            //Checking if map already exist in local db
            int messageCount = getRecordCountMapById(venueMaps.get(i).getId());
            if (messageCount == 0) {
                //If map  does not exist already, Insert new record
                long idRowInserted = db.insert(TABLE_MAPS, null, values);
                if (idRowInserted != -1)
                    noOfRowsInserted++;
            } else {
                //If map exist already, Update it
                db.update(TABLE_MAPS, values, KEY_MAP_ID + " = ?", new String[]{venueMaps.get(i).getId()});
            }
        }
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    public ArrayList<Schedule> getSchedules() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Schedule> schedules = new ArrayList<>();
        Cursor cursor = db.query(TABLE_SCHEDULES, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String keyScheduleId = cursor.isNull(cursor.getColumnIndex(KEY_SCHEDULE_ID)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_SCHEDULE_ID));
                String keyScheduleDay = cursor.isNull(cursor.getColumnIndex(KEY_SCHEDULE_DAY)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_SCHEDULE_DAY));
                String keyScheduleDate = cursor.isNull(cursor.getColumnIndex(KEY_SCHEDULE_DATE)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_SCHEDULE_DATE));

                Schedule schedule = new Schedule(keyScheduleId, keyScheduleDay, keyScheduleDate);
                schedules.add(schedule);
            }
            while (cursor.moveToNext());
        }
        return schedules;
    }


    //********************************************************************************************************************************/

    private int getRecordCountScheduleById(String id) {
        // TODO Auto-generated method stub
        int nCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SCHEDULES, null, KEY_SCHEDULE_ID + " = ?", new String[]{id}, null, null, null);
        nCount = cursor.getCount();
        cursor.close();
        return nCount;
    }

    //********************************************************************************************************************************/
    public ArrayList<VenueMap> getMaps() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<VenueMap> venueMaps = new ArrayList<>();
        Cursor cursor = db.query(TABLE_MAPS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String mapId = cursor.isNull(cursor.getColumnIndex(KEY_MAP_ID)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_MAP_ID));
                String mapTitle = cursor.isNull(cursor.getColumnIndex(KEY_MAP_TITLE)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_MAP_TITLE));
                VenueMap schedule = new VenueMap(mapId, mapTitle);
                venueMaps.add(schedule);
            }
            while (cursor.moveToNext());
        }
        return venueMaps;
    }

    //********************************************************************************************************************************/

    private int getRecordCountMapById(String id) {
        // TODO Auto-generated method stub
        int nCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_MAPS, null, KEY_MAP_ID + " = ?", new String[]{id}, null, null, null);
        nCount = cursor.getCount();
        cursor.close();
        return nCount;
    }

    //********************************************************************************************************************************/

    public int addRestaurantItems(ArrayList<RestaurantItem> restaurantItems) {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        for (int i = 0; i < restaurantItems.size(); i++) {
            ContentValues values = new ContentValues();
            values.put(KEY_REST_ITEM_TITLE, restaurantItems.get(i).getTitle());
            values.put(KEY_REST_ITEM_CATEGORY, restaurantItems.get(i).getCategory());
            values.put(KEY_REST_ITEM_DESCRIPTION, restaurantItems.get(i).getDescription());
            values.put(KEY_REST_ITEM_PRICE, restaurantItems.get(i).getPrice());
            values.put(KEY_RESTAURANT_ID, restaurantItems.get(i).getRestaurantId());

            int messageCount = getRecordCountRestaurantItem(restaurantItems.get(i).getTitle(), restaurantItems.get(i).getRestaurantId());
            if (messageCount == 0) {
                long idRowInserted = db.insert(TABLE_RESTAURANT_ITEMS, null, values);
                if (idRowInserted != -1)
                    noOfRowsInserted++;
            } else {
                db.update(TABLE_RESTAURANT_ITEMS, values, KEY_REST_ITEM_TITLE + " = ? AND " + KEY_RESTAURANT_ID + " = ?", new String[]{restaurantItems.get(i).getTitle(), restaurantItems.get(i).getRestaurantId()});
            }
        }
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    public ArrayList<RestaurantItem> getRestaurantItems(Restaurant restaurant) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<RestaurantItem> restaurantItems = new ArrayList<>();
        Cursor cursor = db.query(TABLE_RESTAURANT_ITEMS, null, KEY_RESTAURANT_ID + " = ?", new String[]{restaurant.getId()}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String keyRestItemTitle = cursor.isNull(cursor.getColumnIndex(KEY_REST_ITEM_TITLE)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_REST_ITEM_TITLE));
                String keyRestItemCategory = cursor.isNull(cursor.getColumnIndex(KEY_REST_ITEM_CATEGORY)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_REST_ITEM_CATEGORY));
                String keyRestItemDescription = cursor.isNull(cursor.getColumnIndex(KEY_REST_ITEM_DESCRIPTION)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_REST_ITEM_DESCRIPTION));
                String keyRestItemPrice = cursor.isNull(cursor.getColumnIndex(KEY_REST_ITEM_PRICE)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_REST_ITEM_PRICE));

                RestaurantItem restaurantItem = new RestaurantItem(keyRestItemTitle, keyRestItemCategory, keyRestItemDescription, keyRestItemPrice, restaurant.getId());
                restaurantItems.add(restaurantItem);
            }
            while (cursor.moveToNext());
        }
        return restaurantItems;
    }

    private int getRecordCountRestaurantItem(String itemTitle, String restaurantId) {
        int nCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_RESTAURANT_ITEMS, null, KEY_REST_ITEM_TITLE + " = ? AND " + KEY_RESTAURANT_ID + " = ?", new String[]{itemTitle, restaurantId}, null, null, null);
        nCount = cursor.getCount();
        cursor.close();
        return nCount;
    }

    //********************************************************************************************************************************/

    public int addScheduleItemDetails(String scheduleId, String scheduleDetails) {

        int noOfRowsInserted = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SCHEDULE_ID, scheduleId);
        values.put(KEY_SCHEDULE_ITEM_DETAILS, scheduleDetails);
        int messageCount = getRecordCountScheduleItem(scheduleId);
        if (messageCount == 0) {
            long idRowInserted = db.insert(TABLE_SCHEDULE_ITEMS, null, values);
            if (idRowInserted != -1)
                noOfRowsInserted++;
        } else {
            db.update(TABLE_SCHEDULE_ITEMS, values, KEY_SCHEDULE_ID + " = ?", new String[]{scheduleId});
        }
        db.close();
        return noOfRowsInserted;
    }

    //********************************************************************************************************************************/

    public String getScheduleItemDetails(String scheduleId) {
        String keyScheduleItemSectionDetail = null;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SCHEDULE_ITEMS, null, KEY_SCHEDULE_ID + " = ?", new String[]{scheduleId}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                keyScheduleItemSectionDetail = cursor.isNull(cursor.getColumnIndex(KEY_SCHEDULE_ITEM_DETAILS)) ? "" : cursor.getString(cursor.getColumnIndex(KEY_SCHEDULE_ITEM_DETAILS));
            }
            while (cursor.moveToNext());
        }
        return keyScheduleItemSectionDetail;
    }

    //********************************************************************************************************************************/

    private int getRecordCountScheduleItem(String scheduleId) {
        int nCount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_SCHEDULE_ITEMS, null, KEY_SCHEDULE_ID + " = ?", new String[]{scheduleId}, null, null, null);
        nCount = cursor.getCount();
        cursor.close();
        return nCount;
    }
}