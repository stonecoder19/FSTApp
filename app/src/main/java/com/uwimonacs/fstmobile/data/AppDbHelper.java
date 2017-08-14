package com.uwimonacs.fstmobile.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;

import com.uwimonacs.fstmobile.models.locations.Place;
import com.uwimonacs.fstmobile.models.locations.Vertex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Akinyele on 2/4/2017.
 */

public class AppDbHelper extends SQLiteOpenHelper implements DbHelper{
    private static final String TAG = "com.android.comp3901";

    public static final Integer DATABASE_VERSION = 38;
    public static final String DATABASE_NAME = "findme.db";
    private static String DB_PATH = "";



    // Database For Vertices variable.
    public static final String VERTICES_TABLE ="Vertices";
    public static final String V_NAME ="Vname";
    public static final String V_ID ="V_ID";
    public static final String V_LAT ="V_Latitude";
    public static final String V_LONG ="V_Longitude";
    public static final String V_TYPE ="V_Type";
    public static final String V_LANDMARK="V_Landmark";
    public static final String V_LEVEL= "level";


    //Vertices Categories
    public static final String CAT_Computer = "COMPUTING";
    public static final String CAT_Engineering = "ENGINEERING";
    public static final String CAT_Physics = "PHYSICS";
    public static final String CAT_Mathematics = "MATHEMATICS";
    public static final String CAT_Chemistry = "CHEMISTRY";
    public static final String CAT_LifeScience = "LIFE SCIENCES";
    public static final String CAT_GeographyGeology = "GEOGRAPHY and GEOLOGY";
    public static final String CAT_Hall = "Hall";
    public static final String CAT_Biology = "BIOLOGY";


    // Database for edges on the graph.
    public static final String EDGES_TABLE ="Edges";
    public static final String E_SOURCE ="source";
    public static final String E_DESTINATION ="destination";
    public static final String E_WEIGHT ="weight";
    public static final String E_LEVEL = "level";


    // Database Room table variable.
    public static final String ROOM_TABLE ="room_data";
    public static final String RT_ID ="RM_ID";
    public static final String RT_NAME ="RM_Name";
    public static final String RT_LAT ="RM_Latitude";
    public static final String RT_LONG ="RM_Longitude";
    public static final String RT_DESC ="RM_Description";
    public static final String RT_FLOOR ="RM_Floor";
    public static final String RT_KNOWN ="RM_Known";
    public static final String RT_FAM ="RM_Familiarity";
    public static final String RT_BUILDING ="RM_Building";
    public static final String RT_LANDMARK="RT_Landmark";
    public static final String RT_CATEGORY = "Category";

    // public static final String RT_COL_8 ="image";


    //Database for Buildings variable.
    public static final String BUILDING_TABLE ="Buildings";
    public static final String B_ID = "B_ID";
    public static final String B_LAT = "B_Latitude";
    public static final String B_LONG = "B_Longitude";
    public static final String B_NAME ="B_Name";
    public static final String B_ROOMS ="B_Rooms";
    public static final String B_FLOORS = "B_Floors";
    public static final String B_KNOWN ="B_Known";
    public static final String B_FAM ="B_Familiarity";
    public static final String B_LANDMARK="B_Landmark";
    public static final String B_CATEGORY = "Category";


    private static AppDbHelper mInstance = null;

    public static AppDbHelper getInstance(Context ctx){
        if (mInstance == null) {
            mInstance = new AppDbHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    private AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        //TODO CREATE TABLE RELATION FOR DB COLUMNS

        /*
         + Room Table
         */
        db.execSQL("CREATE TABLE " + ROOM_TABLE + " ( " +
                    RT_ID + " TEXT, " +
                    RT_NAME + " TEXT, " +
                    RT_LAT + " REAL, " +
                    RT_LONG + " REAL, " +
                    RT_DESC + " TEXT, " +
                    RT_BUILDING + " TEXT DEFAULT 'NONE', " +
                    RT_FLOOR + " INT, " +
                    RT_KNOWN + " INT, " +
                    RT_FAM + " REAL, " +
                    RT_CATEGORY + " TEXT DEFAULT 'Other', " +
                    RT_LANDMARK + " INT DEFAULT 0, " +
                   "PRIMARY KEY ("+RT_LAT+","+RT_LONG+","+RT_ID+") ); ");

        /*
         * Building Table
         */
        db.execSQL(" CREATE TABLE " + BUILDING_TABLE + " ( " +
                B_ID + " TEXT, " +
                B_NAME + " TEXT, " +
                B_LAT + " REAL, " +
                B_LONG + " REAL, " +
                B_ROOMS + " TEXT, " +
                B_FLOORS+ " INT, " +
                B_KNOWN + " INT, " +
                B_FAM + " REAL, " +
                B_LANDMARK + " INT DEFAULT 0, " +
                B_CATEGORY + " TEXT DEFAULT 'Other', " +
                "PRIMARY KEY ("+B_LAT+","+B_LONG+","+B_ID+") ); ");


        db.execSQL(" CREATE TABLE " + VERTICES_TABLE + " (" +
                    V_ID  + " TEXT, " +
                    V_NAME + " TEXT, " +
                    V_LAT + " REAL, " +
                    V_LONG  + " REAL, " +
                    V_TYPE + " TEXT," +
                    V_LANDMARK + " INT DEFAULT 0, " +
                    V_LEVEL + " INTEGER, "+
                "PRIMARY KEY ("+V_LAT+","+V_LONG+","+V_ID+" ) ); " );


        db.execSQL(" CREATE TABLE IF NOT EXISTS " + EDGES_TABLE + " ( " +
                    E_DESTINATION + " TEXT, " +
                    E_SOURCE + " TEXT, " +
                    E_WEIGHT + " INTEGER, " +
                    E_LEVEL + " INTEGER, "+
                    "PRIMARY KEY ("+E_DESTINATION+","+E_SOURCE+") );");

        //db.execSQL(InsertRooms);


        generateDB(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ROOM_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VERTICES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + BUILDING_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EDGES_TABLE);
        onCreate(db);
    }


    /*
     *Generates all the initial database values
     */
    public void generateDB(SQLiteDatabase db){

        generateRooms(db);
        generateEdges(db);
        generateVertices(db);
        generateBuildings(db);
        return ;
    }

    /*
     * Generate rooms
     */
    public void generateRooms(SQLiteDatabase db){

        ContentValues rooms = new ContentValues();

        //" ('Chemistry Lecture Theatre', 18.004490, -76.750003, 'Description', 1 , 0 , 0) " +


        //TODO ADD building column to rooms
        rooms.put(RT_ID,"CHETR1");
        rooms.put(RT_NAME,"Chemistry Tutorial Room 1");
        rooms.put(RT_LAT,18.004593);
        rooms.put(RT_LONG,-76.750123);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"CHETR2");
        rooms.put(RT_NAME,"Chemistry Tutorial Room 2");
        rooms.put(RT_LAT,18.004577);
        rooms.put(RT_LONG,-76.750150);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,3 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"ACL");
        rooms.put(RT_NAME,"Analytical Chemistry Lab");
        rooms.put(RT_LAT,18.004733);//18.004755, -76.749839
        rooms.put(RT_LONG,-76.749870);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"C06P");
        rooms.put(RT_NAME,"Preliminary Chemistry Lab");
        rooms.put(RT_LAT,18.004197);//18.004197, -76.749622
        rooms.put(RT_LONG,-76.749622);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PCL");
        rooms.put(RT_NAME,"Physical Chemistry Lab");
        rooms.put(RT_LAT,18.004002);//18.004002, -76.749451
        rooms.put(RT_LONG,-76.749451);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"C2");
        rooms.put(RT_NAME,"Chemistry Lecture Theatre 2");
        rooms.put(RT_LAT,18.004348);
        rooms.put(RT_LONG,-76.749758);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"C3");
        rooms.put(RT_NAME,"Chemistry Lecture Theatre 3");
        rooms.put(RT_LAT,18.004379);
        rooms.put(RT_LONG,-76.749753);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"C5");
        rooms.put(RT_NAME,"Chemistry Lecture Theatre 5");
        rooms.put(RT_LAT,18.004496);//18.004496, -76.750018
        rooms.put(RT_LONG,-76.750018);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR, 1 );
        rooms.put(RT_KNOWN, 0 );
        rooms.put(RT_FAM, 0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"C6");
        rooms.put(RT_NAME,"Chemistry Lecture Theatre 6");
        rooms.put(RT_LAT,18.004690);
        rooms.put(RT_LONG,-76.750036);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"C7");
        rooms.put(RT_NAME,"Chemistry Lecture Theatre 7");
        rooms.put(RT_LAT,18.004723);
        rooms.put(RT_LONG,-76.749975);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"FCL");
        rooms.put(RT_NAME,"Food Chemistry Lab");
        rooms.put(RT_LAT,18.003868);//18.003868, -76.749724
        rooms.put(RT_LONG,-76.749724);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"AIL");
        rooms.put(RT_NAME,"Advanced Inorganic Chemistry Lab");
        rooms.put(RT_LAT,18.003944);//18.003944, -76.749530
        rooms.put(RT_LONG,-76.749530);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"SLT1");
        rooms.put(RT_NAME,"Science Lecture Theatre 1");
        rooms.put(RT_LAT,18.005176);//18.005176, -76.749894
        rooms.put(RT_LONG,-76.749894);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR, 1);
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM, 0);
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"SLT2");
        rooms.put(RT_NAME,"Science Lecture Theatre 2");
        rooms.put(RT_LAT,18.005209);
        rooms.put(RT_LONG,-76.749750);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"SLT3");
        rooms.put(RT_NAME,"Science Lecture Theatre 3");
        rooms.put(RT_LAT,18.005384);
        rooms.put(RT_LONG,-76.750077);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"COMPLR");
        rooms.put(RT_NAME,"Computing Lecture Room");
        rooms.put(RT_LAT,18.005986);//18.005986, -76.749733
        rooms.put(RT_LONG,-76.749733);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,3 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        //TODO changed room name to match vertex name
        rooms.put(RT_ID,"COMLAB");
        rooms.put(RT_NAME,"Computer Science Lab Room" );
        rooms.put(RT_LAT,18.005164);//18.005164, -76.750173
        rooms.put(RT_LONG,-76.750173);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"CPGR");
        rooms.put(RT_NAME,"Computing Post-Graduate Room");
        rooms.put(RT_LAT,18.006040);//18.006040, -76.749681
        rooms.put(RT_LONG,-76.749681);
        rooms.put(RT_DESC,"");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"CR1");
        rooms.put(RT_NAME,"Computer Science Tutorial Room 1");
        rooms.put(RT_LAT,18.005032); //18.005032,-76.750159
        rooms.put(RT_LONG,-76.750159);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"CR2");
        rooms.put(RT_NAME,"Computer Science Tutorial Room 2");
        rooms.put(RT_LAT,18.005073);//18.005073, -76.750086
        rooms.put(RT_LONG,-76.750086);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"NGRM-WS");
        rooms.put(RT_NAME,"Mechanical Engineering Workshop");
        rooms.put(RT_LAT,18.005130);
        rooms.put(RT_LONG,-76.748741);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Engineering);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"NGR-LRA");
        rooms.put(RT_NAME,"Engineering Lecture Room A");
        rooms.put(RT_LAT,18.004834);
        rooms.put(RT_LONG,-76.749991);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Engineering);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"NGR-LRB");
        rooms.put(RT_NAME,"Engineering Lecture Room B");
        rooms.put(RT_LAT,18.004794);
        rooms.put(RT_LONG,-76.750063);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Engineering);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"NGR-Dra");
        rooms.put(RT_NAME,"Engineering Drafting Room");
        rooms.put(RT_LAT,18.005722);
        rooms.put(RT_LONG,-76.749694);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Engineering);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"IFLT");
        rooms.put(RT_NAME,"Inter-Faculty Lecture Theatre");
        rooms.put(RT_LAT,18.005648);
        rooms.put(RT_LONG,-76.748699);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_LifeScience);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"MLT1");
        rooms.put(RT_NAME,"Math Lecture Theatre 1");
        rooms.put(RT_LAT,18.004945); //18.004945,-76.749473
        rooms.put(RT_LONG,-76.749473);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Mathematics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"MLT2");
        rooms.put(RT_NAME,"Math Lecture Theatre 2");
        rooms.put(RT_LAT,18.004884);
        rooms.put(RT_LONG,-76.749417);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Mathematics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"MLT3");
        rooms.put(RT_NAME,"Math Lecture Theatre 3");
        rooms.put(RT_LAT,18.004994);
        rooms.put(RT_LONG,-76.749409);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Mathematics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"MLAB");
        rooms.put(RT_NAME,"Math Computer Lab");
        rooms.put(RT_LAT,18.004886);//18.004907, -76.749519
        rooms.put(RT_LONG,-76.749537);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Mathematics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"BLT");
        rooms.put(RT_NAME,"FST Biology Lecture Theatre");
        rooms.put(RT_LAT,18.006292);
        rooms.put(RT_LONG,-76.750432);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"DLSADB");
        rooms.put(RT_NAME,"DLS Advanced Biology Lab");
        rooms.put(RT_LAT,18.006011);//18.006011, -76.749629
        rooms.put(RT_LONG,-76.749629);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSAQU");
        rooms.put(RT_NAME,"DLS Aquatic Sciences Lab");
        rooms.put(RT_LAT,18.005991);
        rooms.put(RT_LONG,-76.750105);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSCOM");
        rooms.put(RT_NAME,"DLS Computer Room");
        rooms.put(RT_LAT,18.005868);//18.005868, -76.749929
        rooms.put(RT_LONG,-76.749929);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Computer);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSENT");
        rooms.put(RT_NAME,"DLS Entomology Lab");
        rooms.put(RT_LAT,18.006293);
        rooms.put(RT_LONG,-76.749763);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSFOR");
        rooms.put(RT_NAME,"DLS Forestry Eco Lab");
        rooms.put(RT_LAT,18.005933);//18.005933, -76.749901
        rooms.put(RT_LONG,-76.749901);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSINT");
        rooms.put(RT_NAME,"DLS Introductory Lab 13");
        rooms.put(RT_LAT,18.006473);//18.006473, -76.750288
        rooms.put(RT_LONG,-76.750288);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSLB2");
        rooms.put(RT_NAME,"DLS Lab 2");
        rooms.put(RT_LAT,18.005610);//18.005610, -76.750590
        rooms.put(RT_LONG,-76.750590);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSLB3");
        rooms.put(RT_NAME,"DLS Lab 3");
        rooms.put(RT_LAT,18.006006);//18.006006, -76.750619
        rooms.put(RT_LONG,-76.750619);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSLB4");
        rooms.put(RT_NAME,"DLS Lab 4");
        rooms.put(RT_LAT,18.006006);
        rooms.put(RT_LONG,-76.750619);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSPBL");
        rooms.put(RT_NAME,"DLS preliminary Biology Lab");
        rooms.put(RT_LAT,18.006192);
        rooms.put(RT_LONG,-76.750141);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSPHY");
        rooms.put(RT_NAME,"DLS Physiology Lab");
        rooms.put(RT_LAT,18.006611);//18.006611, -76.750176
        rooms.put(RT_LONG,-76.750176);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"DLSSM1");
        rooms.put(RT_NAME,"DLS Seminar Room 1");
        rooms.put(RT_LAT,18.006090);
        rooms.put(RT_LONG,-76.750511);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM2");
        rooms.put(RT_NAME,"DLS Seminar Room 2");
        rooms.put(RT_LAT,18.006435);//18.006435, -76.750349
        rooms.put(RT_LONG,-76.750349);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM3");
        rooms.put(RT_NAME,"DLS Seminar Room 3");
        rooms.put(RT_LAT,18.006611);//18.006611, -76.750176
        rooms.put(RT_LONG,-76.750176);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM4");
        rooms.put(RT_NAME,"DLS Seminar Room 4");
        rooms.put(RT_LAT,18.005868);//18.005868, -76.749929
        rooms.put(RT_LONG,-76.749929);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM5");
        rooms.put(RT_NAME,"DLS Seminar Room 5");
        rooms.put(RT_LAT,18.005868);//18.005868, -76.749929
        rooms.put(RT_LONG,-76.749929);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM6");
        rooms.put(RT_NAME,"DLS Seminar Room 6");
        rooms.put(RT_LAT,18.005868);//18.005868, -76.749929
        rooms.put(RT_LONG,-76.749929);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM7");
        rooms.put(RT_NAME,"DLS Seminar Room 7");
        rooms.put(RT_LAT,18.005933);//18.005933, -76.749901
        rooms.put(RT_LONG,-76.749901);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSSM8");
        rooms.put(RT_NAME,"DLS Seminar Room 8");
        rooms.put(RT_LAT,18.005933);
        rooms.put(RT_LONG,-76.749901);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"DLSMVL");
        rooms.put(RT_NAME,"DLS Molec & Virology Lab");
        rooms.put(RT_LAT,18.005933);//18.005933, -76.749901
        rooms.put(RT_LONG,-76.749901);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"GGCLAB");
        rooms.put(RT_NAME,"Geography/Geology Computer Lab");
        rooms.put(RT_LAT,18.006091);
        rooms.put(RT_LONG,-76.748902);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_GeographyGeology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"GGLAB1");
        rooms.put(RT_NAME,"Laboratory 1");
        rooms.put(RT_LAT,18.006136);
        rooms.put(RT_LONG,-76.748880);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"GGLAB2");
        rooms.put(RT_NAME,"Laboratory 2");
        rooms.put(RT_LAT,18.006132);//18.005610, -76.750590
        rooms.put(RT_LONG,-76.749017);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"GGLAB3");
        rooms.put(RT_NAME,"Laboratory 3");
        rooms.put(RT_LAT,18.006134);
        rooms.put(RT_LONG,-76.748966);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,0 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"GGLAB4");
        rooms.put(RT_NAME,"Laboratory 4");
        rooms.put(RT_LAT,18.005969);
        rooms.put(RT_LONG,-76.749099);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,0 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Biology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"GGTUTR");
        rooms.put(RT_NAME,"Geography/Geology Tutorial Room");
        rooms.put(RT_LAT,18.006117);
        rooms.put(RT_LONG,-76.748861);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_GeographyGeology);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"PHYS-A");
        rooms.put(RT_NAME,"Physics Lecture Room A");
        rooms.put(RT_LAT,18.005238);//18.005238, -76.749097
        rooms.put(RT_LONG,-76.749097);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PHYS-B");
        rooms.put(RT_NAME,"Physics Lecture Room B");
        rooms.put(RT_LAT,18.005149);//18.005149, -76.749037
        rooms.put(RT_LONG,-76.749037);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PHYS-C");
        rooms.put(RT_NAME,"Physics Lecture Room C");
        rooms.put(RT_LAT,18.004993);//18.004993, -76.748912
        rooms.put(RT_LONG,-76.748912);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PHYS-D");
        rooms.put(RT_NAME,"Physics Lecture Room D");
        rooms.put(RT_LAT,18.004523);//18.004523, -76.749032
        rooms.put(RT_LONG,-76.749032);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PHYS-E");
        rooms.put(RT_NAME,"Physics Lecture Room E");
        rooms.put(RT_LAT,18.005058);//18.005058, -76.749287
        rooms.put(RT_LONG,-76.749287);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PHYTUT");
        rooms.put(RT_NAME,"Physics Tutorial Room");
        rooms.put(RT_LAT,18.005302);//18.005302, -76.749143
        rooms.put(RT_LONG,-76.749143);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"PREPHY");
        rooms.put(RT_NAME,"Preliminary Physics Lab");
        rooms.put(RT_LAT,18.004797);
        rooms.put(RT_LONG,-76.748785);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"CHEPHY");
        rooms.put(RT_NAME,"Chem/Phys Lecture Theatre");
        rooms.put(RT_LAT,18.004370);
        rooms.put(RT_LONG,-76.749152);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Chemistry);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"VIRLAB");
        rooms.put(RT_NAME,"Virtual Computer Lab");
        rooms.put(RT_LAT,18.004736); //18.004736,-76.748973
        rooms.put(RT_LONG,-76.748973);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        //TODO update fluids lab values to actual values
        //Check to see if its at the write place
        rooms.put(RT_ID,"NGR-F-L");
        rooms.put(RT_NAME,"Fluids Laboratory");
        rooms.put(RT_LAT,18.005346);
        rooms.put(RT_LONG,-76.750207);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,3 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Engineering);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"SOLARB");
        rooms.put(RT_NAME,"Solar Lab");
        rooms.put(RT_LAT,18.005569);
        rooms.put(RT_LONG,-76.749115);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,3 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();


        rooms.put(RT_ID,"EL1LAB");
        rooms.put(RT_NAME,"Level 1 Electronics Lab");
        rooms.put(RT_LAT,18.004574);
        rooms.put(RT_LONG,-76.748972);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"EL2LAB");
        rooms.put(RT_NAME,"Level 2 Electronics Lab");
        rooms.put(RT_LAT,18.004561);//18.004561, -76.748850
        rooms.put(RT_LONG,-76.748850);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,2 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"EL3LAB");
        rooms.put(RT_NAME,"Level 3 Electronics Lab");
        rooms.put(RT_LAT,18.004718);
        rooms.put(RT_LONG,-76.750202);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,0 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();

        rooms.put(RT_ID,"NGR-S-L");
        rooms.put(RT_NAME,"Soils Laboratory");
        rooms.put(RT_LAT,18.005288);
        rooms.put(RT_LONG,-76.750332);
        rooms.put(RT_DESC,"Description");
        rooms.put(RT_FLOOR,1 );
        rooms.put(RT_KNOWN,0 );
        rooms.put(RT_FAM,0 );
        rooms.put(RT_CATEGORY,CAT_Physics);
        db.insert(ROOM_TABLE,null, rooms);
        rooms.clear();



//        rooms.put(RT_ID,"");
//        rooms.put(RT_NAME,"");
//        rooms.put(RT_LAT, );
//        rooms.put(RT_LONG, );
//        rooms.put(RT_DESC,"");
//        rooms.put(RT_FLOOR, );
//        rooms.put(RT_KNOWN, );
//        rooms.put(RT_FAM, );
//        db.insert(ROOM_TABLE,null, rooms);
//        rooms.clear();


        return ;
    }


    public void generateBuildings(SQLiteDatabase db){
        ContentValues buildings = new ContentValues();

        buildings.put(B_ID,"DOM");
        buildings.put(B_NAME,"Department of Mathematics");
        buildings.put(B_LAT,18.004807);
        buildings.put(B_LONG,-76.749583);
        buildings.put(B_ROOMS,"MLAB");
        buildings.put(B_FLOORS,1);
        buildings.put(B_KNOWN,0);
        buildings.put(B_FAM,0);
        buildings.put(RT_CATEGORY,CAT_Mathematics);
        db.insert(BUILDING_TABLE,null,buildings);
        buildings.clear();

        buildings.put(B_ID,"COMLABB");
        buildings.put(B_NAME,"Computer Science Lab Building");
        buildings.put(B_LAT,18.005242 ); //18.005242,-76.750182
        buildings.put(B_LONG,-76.750182);
        buildings.put(B_ROOMS,"CR2,CR1");
        buildings.put(B_FLOORS,1);
        buildings.put(B_KNOWN,0);
        buildings.put(B_FAM,0);
        buildings.put(RT_CATEGORY,CAT_Computer);
        db.insert(BUILDING_TABLE,null,buildings);
        buildings.clear();

        buildings.put(B_ID,"DOC");
        buildings.put(B_NAME,"Department of Computing Building");
        buildings.put(B_LAT,18.005761);//18.005761, -76.750078
        buildings.put(B_LONG,-76.750078);
        buildings.put(B_ROOMS,"");
        buildings.put(B_FLOORS,3);
        buildings.put(B_KNOWN,0);
        buildings.put(B_FAM,0);
        buildings.put(RT_CATEGORY,CAT_Computer);
        db.insert(BUILDING_TABLE,null,buildings);
        buildings.clear();



        buildings.put(B_ID,"DLSEB");
        buildings.put(B_NAME,"DLS Entomology Lab Building");
        buildings.put(B_LAT,18.006255 );
        buildings.put(B_LONG,-76.749714);
        buildings.put(B_ROOMS,"");
        buildings.put(B_FLOORS,1);
        buildings.put(B_KNOWN,0);
        buildings.put(B_FAM,0);
        buildings.put(RT_CATEGORY,CAT_Biology);
        db.insert(BUILDING_TABLE,null,buildings);
        buildings.clear();

//        buildings.put(B_ID," ");
//        buildings.put(B_NAME,"");
//        buildings.put(B_LAT, );
//        buildings.put(B_LONG,);
//        building.put(B_ROOMS,"");
//        buildings.put(B_FLOORS,);
//        buildings.put(B_KNOWN,0);
//        buildings.put(B_FAM,0);
//        db.insert(BUILDING_TABLE,null,buildings);
//        buildings.clear();
    }


    /*
     * Generate Vertices
     */
    public void generateVertices(SQLiteDatabase db){

        ContentValues vertices = new ContentValues();



        /****
         * ROOMS
         ****/
        vertices.put(V_ID,"SLT1");
        vertices.put(V_NAME,"Science Lecture Theatre 1" );
        vertices.put(V_LAT,  18.005176);//18.005176, -76.749894
        vertices.put(V_LONG, -76.749894);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SLT2" );
        vertices.put(V_NAME,"Science Lecture Theatre 2" );
        vertices.put(V_LAT, 18.005209 );
        vertices.put(V_LONG, -76.749750);
        vertices.put(V_TYPE,"ROOM");
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SLT3"  );
        vertices.put(V_NAME,"Science Lecture Theatre 3"  );
        vertices.put(V_LAT,18.005384  );
        vertices.put(V_LONG,-76.750077   );
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C7");
        vertices.put(V_NAME,"Chemistry Lecture Theatre 7"  );
        vertices.put(V_LAT,18.004723 );
        vertices.put(V_LONG,-76.749975);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C6"  );
        vertices.put(V_NAME,"Chemistry Lecture Theatre 6"  );
        vertices.put(V_LAT,18.004690);
        vertices.put(V_LONG,-76.750036);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"C5" );
        vertices.put(V_NAME,"Chemistry Lecture Theatre 5" );
        vertices.put(V_LAT,18.004496);//18.004496, -76.750018
        vertices.put(V_LONG,-76.750018);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"C2");
        vertices.put(V_NAME,"Chemistry Lecture Theatre 2");
        vertices.put(V_LAT,18.004348);
        vertices.put(V_LONG,-76.749758);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C3"  );
        vertices.put(V_NAME,"Chemistry Lecture Theatre 3");
        vertices.put(V_LAT,18.004379);
        vertices.put(V_LONG,-76.749753);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"ACL"  );
        vertices.put(V_NAME,"Analytical Chemistry Lab");
        vertices.put(V_LAT,18.004733);//18.004755, -76.749839
        vertices.put(V_LONG,-76.749870);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"AIL");
        vertices.put(V_NAME,"Advanced Inorganic Chemistry Lab"  );
        vertices.put(V_LAT,18.003944);//18.003944, -76.749530
        vertices.put(V_LONG,-76.749530);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PCL"  );
        vertices.put(V_NAME,"Physical Chemistry Lab"  );
        vertices.put(V_LAT,18.004002);//18.004002, -76.749451
        vertices.put(V_LONG,-76.749451);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C06P"  );
        vertices.put(V_NAME,"Preliminary Chemistry Lab"  );
        vertices.put(V_LAT,18.004197);//18.004197, -76.749622
        vertices.put(V_LONG,-76.749622);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"CHETR1"  );
        vertices.put(V_NAME,"Chemistry Tutorial Room 1");
        vertices.put(V_LAT,18.004593);
        vertices.put(V_LONG,-76.750123);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CHETR2"  );
        vertices.put(V_NAME,"Chemistry Tutorial Room 2");
        vertices.put(V_LAT,18.004577);
        vertices.put(V_LONG,-76.750150);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"COMLABB");
        vertices.put(V_NAME,"Computer Science Lab Building"  );
        vertices.put(V_LAT,18.005242);//18.005242,-76.750182
        vertices.put(V_LONG,-76.750182);
        vertices.put(V_TYPE ,"Building" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CR1");
        vertices.put(V_NAME,"Computer Science Tutorial Room 1"  );
        vertices.put(V_LAT,18.005032);//18.005032,-76.750159
        vertices.put(V_LONG,-76.750159);
        vertices.put(V_TYPE ,"ROOM");
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CR2");
        vertices.put(V_NAME,"Computer Science Tutorial Room 2");
        vertices.put(V_LAT,18.005073);//18.005073, -76.750086
        vertices.put(V_LONG,-76.750086);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"COMLAB");
        vertices.put(V_NAME,"Computer Science Lab Room" );
        vertices.put(V_LAT,18.005164);//18.005164, -76.750173
        vertices.put(V_LONG,-76.750173);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"COMPLR"  );
        vertices.put(V_NAME,"Computing Lecture Room"  );
        vertices.put(V_LAT,18.005986);//18.005986, -76.749733
        vertices.put(V_LONG,-76.749733);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CPGR"  );
        vertices.put(V_NAME,"Computing Post-Graduate Room"  );
        vertices.put(V_LAT,18.006040);//18.006040, -76.749681
        vertices.put(V_LONG,-76.749681);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DOC"  );
        vertices.put(V_NAME,"Department of Computing Building"  );
        vertices.put(V_LAT,18.005761);//18.005761, -76.750078
        vertices.put(V_LONG,-76.750078);
        vertices.put(V_TYPE ,"Building" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();



        vertices.put(V_ID,"NGR-LRA");
        vertices.put(V_NAME,"Engineering Lecture Room A");
        vertices.put(V_LAT,18.004834);
        vertices.put(V_LONG,-76.749991);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"NGR-LRB");
        vertices.put(V_NAME,"Engineering Lecture Room B");
        vertices.put(V_LAT,18.004794);
        vertices.put(V_LONG,-76.750063);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"NGRM-WS");
        vertices.put(V_NAME,"Mechanical Engineering Workshop");
        vertices.put(V_LAT,18.005130);
        vertices.put(V_LONG,-76.748741);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"NGR-Dra"  );
        vertices.put(V_NAME,"Engineering Drafting Room"  );
        vertices.put(V_LAT,18.005722);
        vertices.put(V_LONG,-76.749694);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        //TODO WRONG VALUES
        vertices.put(V_ID,"NGR-F-L"  );
        vertices.put(V_NAME,"Fluids Laboratory"  );
        vertices.put(V_LAT,18.005346);
        vertices.put(V_LONG,-76.750207);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"NGR-S-L" );
        vertices.put(V_NAME,"Soils Laboratory"  );
        vertices.put(V_LAT,18.005288);
        vertices.put(V_LONG,-76.750332);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"DLSADB"  );
        vertices.put(V_NAME,"DLS Advanced Biology Lab"  );
        vertices.put(V_LAT,18.006011);//18.006011, -76.749629
        vertices.put(V_LONG,-76.749629);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSAQU"  );
        vertices.put(V_NAME,"DLS Aquatic Sciences Lab"  );
        vertices.put(V_LAT,18.005991);
        vertices.put(V_LONG,-76.750105);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSCOM"  );
        vertices.put(V_NAME,"DLS Computer Room"  );
        vertices.put(V_LAT,18.005868);//18.005868, -76.749929
        vertices.put(V_LONG,-76.749929);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSENT"  );
        vertices.put(V_NAME,"DLS Entomology Lab"  );
        vertices.put(V_LAT,18.006293);
        vertices.put(V_LONG,-76.749763);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSEB"  );
        vertices.put(V_NAME,"DLS Entomology Lab Building"  );
        vertices.put(V_LAT,18.006255);
        vertices.put(V_LONG,-76.749714);
        vertices.put(V_TYPE ,"BUILDING" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSFOR"  );
        vertices.put(V_NAME,"DLS Forestry Eco Lab"  );
        vertices.put(V_LAT,18.005933);//18.005933, -76.749901
        vertices.put(V_LONG,-76.749901);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSINT"  );
        vertices.put(V_NAME,"DLS Introductory Lab 13"  );
        vertices.put(V_LAT,18.006473);//18.006473, -76.750288
        vertices.put(V_LONG,-76.750288);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"DLSLB2"  );
        vertices.put(V_NAME,"DLS Lab 2"  );
        vertices.put(V_LAT,18.005610);//18.005610, -76.750590
        vertices.put(V_LONG,-76.750590);
        vertices.put(V_TYPE,"ROOM" );


        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSLB3"  );
        vertices.put(V_NAME,"DLS Lab 3"  );
        vertices.put(V_LAT,18.006006);//18.006006, -76.750619
        vertices.put(V_LONG,-76.750619);
        vertices.put(V_TYPE,"ROOM" );


        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSLB4"  );
        vertices.put(V_NAME,"DLS Lab 4"  );
        vertices.put(V_LAT,18.006006);
        vertices.put(V_LONG,-76.750619);
        vertices.put(V_TYPE,"ROOM" );


        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSMVL"  );
        vertices.put(V_NAME,"DLS Molec & Virology Lab"  );
        vertices.put(V_LAT,18.005933);
        vertices.put(V_LONG,-76.749901);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSPBL"  );
        vertices.put(V_NAME,"DLS Preliminary Biology Lab"  );
        vertices.put(V_LAT,18.006192);
        vertices.put(V_LONG,-76.750141);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSPHY"  );
        vertices.put(V_NAME,"DLS Physiology Lab"  );
        vertices.put(V_LAT,18.006611);//18.006611, -76.750176
        vertices.put(V_LONG,-76.750176);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM1"  );
        vertices.put(V_NAME,"DLS Seminar Room 1"  );
        vertices.put(V_LAT,18.006090);
        vertices.put(V_LONG,-76.750511);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM2"  );
        vertices.put(V_NAME,"DLS Seminar Room 2"  );
        vertices.put(V_LAT,18.006435);//18.006435, -76.750349
        vertices.put(V_LONG,-76.750349);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM3"  );
        vertices.put(V_NAME,"DLS Seminar Room 3"  );
        vertices.put(V_LAT,18.006611);//18.006611, -76.750176
        vertices.put(V_LONG,-76.750176);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM4"  );
        vertices.put(V_NAME,"DLS Seminar Room 4"  );
        vertices.put(V_LAT,18.005868);//18.005868, -76.749929
        vertices.put(V_LONG,-76.749929);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM5"  );
        vertices.put(V_NAME,"DLS Seminar Room 5"  );
        vertices.put(V_LAT,18.005868);//18.005868, -76.749929
        vertices.put(V_LONG,-76.749929);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM6"  );
        vertices.put(V_NAME,"DLS Seminar Room 6"  );
        vertices.put(V_LAT,18.005868);//18.005868, -76.749929
        vertices.put(V_LONG,-76.749929);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM7"  );
        vertices.put(V_NAME,"DLS Seminar Room 7"  );
        vertices.put(V_LAT,18.005933);//18.005933, -76.749901
        vertices.put(V_LONG,-76.749901);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DLSSM8"  );
        vertices.put(V_NAME,"DLS Seminar Room 8"  );
        vertices.put(V_LAT,18.005933);//18.005933, -76.749901
        vertices.put(V_LONG,-76.749901);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BLT");
        vertices.put(V_NAME,"FST Biology Lecture Theatre"  );
        vertices.put(V_LAT,18.006292);
        vertices.put(V_LONG,-76.750432);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"EL1LAB"  );
        vertices.put(V_NAME,"Level 1 Electronics Lab"  );
        vertices.put(V_LAT,18.004574);
        vertices.put(V_LONG,-76.748972);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"EL2LAB"  );
        vertices.put(V_NAME,"Level 2 Electronics Lab"  );
        vertices.put(V_LAT,18.004561);//18.004561, -76.748850
        vertices.put(V_LONG,-76.748850);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"EL3LAB"  );
        vertices.put(V_NAME,"Level 3 Electronics Lab"  );
        vertices.put(V_LAT,18.004718);
        vertices.put(V_LONG,-76.750202);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,0);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"FCL"  );
        vertices.put(V_NAME,"Food Chemistry Lab"  );
        vertices.put(V_LAT,18.003868);//18.003868, -76.749724
        vertices.put(V_LONG,-76.749724);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GGCLAB"  );
        vertices.put(V_NAME,"Geography/Geology Computer Lab"  );
        vertices.put(V_LAT,18.006091);
        vertices.put(V_LONG,-76.748902);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GGLAB1");
        vertices.put(V_NAME,"Laboratory 1"  );
        vertices.put(V_LAT,18.006136);
        vertices.put(V_LONG,-76.748880);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GGLAB2"  );
        vertices.put(V_NAME,"Laboratory 2"  );
        vertices.put(V_LAT,18.006132);
        vertices.put(V_LONG,-76.749017);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GGLAB3"  );
        vertices.put(V_NAME,"Laboratory 3"  );
        vertices.put(V_LAT,18.006134 );
        vertices.put(V_LONG,-76.748966);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GGLAB4"  );
        vertices.put(V_NAME,"Laboratory 4"  );
        vertices.put(V_LAT,18.005969);
        vertices.put(V_LONG,-76.749099);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GGTUTR"  );
        vertices.put(V_NAME,"Geography/Geology Tutorial Room"  );
        vertices.put(V_LAT,18.006117);
        vertices.put(V_LONG,-76.748861);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"IFLT"  );
        vertices.put(V_NAME,"Inter-Faculty Lecture Theatre"  );
        vertices.put(V_LAT,18.005648);
        vertices.put(V_LONG,-76.748699);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"DOM");
        vertices.put(V_NAME,"Department of Mathematics");
        vertices.put(V_LAT, 18.004807);
        vertices.put(V_LONG,-76.749583);
        vertices.put(V_TYPE ,"Building");
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MLAB"  );
        vertices.put(V_NAME,"Math Computer Lab"  );
        vertices.put(V_LAT,18.004886);//18.004907, -76.749519
        vertices.put(V_LONG,-76.749537);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MLT1"  );
        vertices.put(V_NAME,"Math Lecture Theatre 1"  );
        vertices.put(V_LAT,18.004945); //18.004945,-76.749473
        vertices.put(V_LONG,-76.749473);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MLT2"  );
        vertices.put(V_NAME,"Math Lecture Theatre 2"  );
        vertices.put(V_LAT,18.004884);
        vertices.put(V_LONG,-76.749417);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MLT3"  );
        vertices.put(V_NAME,"Math Lecture Theatre 3"  );
        vertices.put(V_LAT,18.004994);
        vertices.put(V_LONG,-76.749409);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PHYTUT"  );
        vertices.put(V_NAME,"Physics Tutorial Room"  );
        vertices.put(V_LAT,18.005302);//18.005302, -76.749143
        vertices.put(V_LONG,-76.749143);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"PHYS-A");
        vertices.put(V_NAME,"PHYSICS LECTURE ROOM A" );
        vertices.put(V_LAT,18.005238);//18.005238, -76.749097
        vertices.put(V_LONG,-76.749097);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PHYS-B");
        vertices.put(V_NAME,"PHYSICS LECTURE ROOM B" );
        vertices.put(V_LAT,18.005149); //18.005149, -76.749037
        vertices.put(V_LONG,-76.749037);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PHYS-C");
        vertices.put(V_NAME,"PHYSICS LECTURE ROOM C" );
        vertices.put(V_LAT,18.004993);//18.004993, -76.748912
        vertices.put(V_LONG,-76.748912);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PHYS-D");
        vertices.put(V_NAME,"PHYSICS LECTURE ROOM D" );
        vertices.put(V_LAT,18.004523);///18.004523, -76.749032
        vertices.put(V_LONG,-76.749032);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PHYS-E");
        vertices.put(V_NAME,"PHYSICS LECTURE ROOM E" );
        vertices.put(V_LAT,18.005058);//18.005058, -76.749287
        vertices.put(V_LONG,-76.749287);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PREPHY"  );
        vertices.put(V_NAME,"Preliminary Physics Lab"  );
        vertices.put(V_LAT,18.004797);
        vertices.put(V_LONG,-76.748785);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SOLARB"  );
        vertices.put(V_NAME,"Solar Lab"  );
        vertices.put(V_LAT,18.005569);
        vertices.put(V_LONG,-76.749115);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"VIRLAB" );
        vertices.put(V_NAME,"Virtual Computer Lab"  );
        vertices.put(V_LAT,18.004736);// //18.004736,-76.748973
        vertices.put(V_LONG,-76.748973);
        vertices.put(V_TYPE,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CHEPHY");
        vertices.put(V_NAME,"Chem/Phys Lecture Theatre" );
        vertices.put(V_LAT,18.004370);
        vertices.put(V_LONG,-76.749152);
        vertices.put(V_TYPE ,"ROOM" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        /***
         *  Nodes for Stairs
         ***/


        vertices.put(V_ID,"ICLS"  );
        vertices.put(V_NAME,"Inorganic Chem lab Stairs"  );
        vertices.put(V_LAT,18.003972);//18.003972, -76.749573
        vertices.put(V_LONG,-76.749573);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C2C3S"  );
        vertices.put(V_NAME,"Stairs to C2 and C3" );
        vertices.put(V_LAT,18.004351);
        vertices.put(V_LONG,-76.749682);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PPLS"  );
        vertices.put(V_NAME,"Prelim Physics Lab Stairs"  );
        vertices.put(V_LAT,18.004817);
        vertices.put(V_LONG,-76.748844);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PES"  );
        vertices.put(V_NAME,"Phys-E Stairs"  );
        vertices.put(V_LAT,18.005124);//18.005124, -76.749088
        vertices.put(V_LONG,-76.749088);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GS"  );
        vertices.put(V_NAME,"Geo Stairs");
        vertices.put(V_LAT,18.006064);
        vertices.put(V_LONG,-76.748764);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PS1"  );
        vertices.put(V_NAME,"Physics Stairs 1"  );
        vertices.put(V_LAT,18.005490);
        vertices.put(V_LONG,-76.749112);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PS2"  );
        vertices.put(V_NAME,"Physics Stairs 2"  );
        vertices.put(V_LAT,18.005490);
        vertices.put(V_LONG,-76.749112);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"ACLS"  );
        vertices.put(V_NAME,"Analytical Chem Lab stairs"  );
        vertices.put(V_LAT,18.004784);
        vertices.put(V_LONG,-76.749857);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"ES"  );
        vertices.put(V_NAME,"Engineering Stairs"  );
        vertices.put(V_LAT,18.004757);
        vertices.put(V_LONG,-76.750148);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CSS1"  );
        vertices.put(V_NAME,"Comp Sci Stairs 1"  );
        vertices.put(V_LAT,18.005772);//18.005772, -76.750013
        vertices.put(V_LONG,-76.750013);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CSS2"  );
        vertices.put(V_NAME,"Comp Sci Stairs 2"  );
        vertices.put(V_LAT,18.005772);
        vertices.put(V_LONG,-76.750013);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SR3S"  );
        vertices.put(V_NAME,"Seminar Room 3 Stairs"  );
        vertices.put(V_LAT,18.006634);//18.006634, -76.750138
        vertices.put(V_LONG,-76.750138);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SR1S"  );
        vertices.put(V_NAME,"Seminar Room 1 Stairs"  );
        vertices.put(V_LAT,18.006054);//18.006054, -76.750567
        vertices.put(V_LONG,-76.750567);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"EL2S"  );
        vertices.put(V_NAME,"Electronics Lab 2 Stairs"  );
        vertices.put(V_LAT,18.004621);
        vertices.put(V_LONG,-76.748864);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C5S");
        vertices.put(V_NAME,"C5 Stairs ");
        vertices.put(V_LAT,18.004575);//18.004575, -76.750166
        vertices.put(V_LONG,-76.750166);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C5S2");
        vertices.put(V_NAME,"C5 Stairs 2ND FLOOR ");
        vertices.put(V_LAT,18.004575);//18.004575, -76.750166
        vertices.put(V_LONG,-76.750166);
        vertices.put(V_TYPE ,"STAIRS" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        /****
         *  Unimportant nodes
         ****/
        vertices.put(V_ID,"SJ0"  );
        vertices.put(V_NAME,"Spine Junction 0"  );
        vertices.put(V_LAT,18.004442);
        vertices.put(V_LONG,-76.749224);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ1"  );
        vertices.put(V_NAME,"Spine Junction"  );
        vertices.put(V_LAT,18.004466);
        vertices.put(V_LONG,-76.749309);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"LSJ"  );
        vertices.put(V_NAME,"Lower Spine Junction"  );
        vertices.put(V_LAT,18.004423);
        vertices.put(V_LONG,-76.749354);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ2"  );
        vertices.put(V_NAME,"Spine Junction 2"  );
        vertices.put(V_LAT,18.004590);
        vertices.put(V_LONG,-76.749407);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ3"  );
        vertices.put(V_NAME,"Spine Junction 3"  );
        vertices.put(V_LAT,18.004986);//18.004986, -76.749709
        vertices.put(V_LONG,-76.749709);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ4"  );
        vertices.put(V_NAME,"Spine Junction 4"  );
        vertices.put(V_LAT,18.005195);
        vertices.put(V_LONG,-76.749814);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ5"  );
        vertices.put(V_NAME,"Spine Junction 5"  );
        vertices.put(V_LAT,18.005456);
        vertices.put(V_LONG,-76.749957);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ6"  );
        vertices.put(V_NAME,"Spine Junction 6"  );
        vertices.put(V_LAT,18.005576);
        vertices.put(V_LONG,-76.750006);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ7"  );
        vertices.put(V_NAME,"Spine Junction 7"  );
        vertices.put(V_LAT,18.005753);//
        vertices.put(V_LONG,-76.750102);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ8"  );
        vertices.put(V_NAME,"Spine Junction 8"  );
        vertices.put(V_LAT,18.005832);
        vertices.put(V_LONG,-76.750142);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ9"  );
        vertices.put(V_NAME,"Spine Junction9"  );
        vertices.put(V_LAT,18.005951);
        vertices.put(V_LONG,-76.750204);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ10"  );
        vertices.put(V_NAME,"Spine Junction 10"  );
        vertices.put(V_LAT,18.006114);//18.006114, -76.750305
        vertices.put(V_LONG,-76.750305);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ11"  );
        vertices.put(V_NAME,"Spine Junction 11"  );
        vertices.put(V_LAT,18.006135);
        vertices.put(V_LONG,-76.750360);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SJ12"  );
        vertices.put(V_NAME,"Spine Junction 12"  );
        vertices.put(V_LAT,18.006243);
        vertices.put(V_LONG,-76.750440);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PN0"  );
        vertices.put(V_NAME,"Physics Node 0"  );
        vertices.put(V_LAT,18.004648);
        vertices.put(V_LONG,-76.748900);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PN1"  );
        vertices.put(V_NAME,"Physics Node 1"  );
        vertices.put(V_LAT,18.004584);
        vertices.put(V_LONG,-76.748831);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PN2"  );
        vertices.put(V_NAME,"Physics Node 2"  );
        vertices.put(V_LAT,18.004782);//18.004782, -76.748771
        vertices.put(V_LONG,-76.748771);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PN3"  );
        vertices.put(V_NAME,"Physics Node 3"  );
        vertices.put(V_LAT,18.004858);//18.004858, -76.748815
        vertices.put(V_LONG,-76.748815);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PN4"  );
        vertices.put(V_NAME,"Physics Node 4"  );
        vertices.put(V_LAT,18.004839);//18.004839, -76.748874
        vertices.put(V_LONG,-76.748874);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PN5"  );
        vertices.put(V_NAME,"Physics Node 5"  );
        vertices.put(V_LAT,18.004979);//18.004979, -76.748965
        vertices.put(V_LONG,-76.748965);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PEN0"  );
        vertices.put(V_NAME,"Phys-E Node 0"  );
        vertices.put(V_LAT,18.005095);//18.005095, -76.749139
        vertices.put(V_LONG,-76.749139);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PEN1"  );
        vertices.put(V_NAME,"Phys-E node 1"  );
        vertices.put(V_LAT,18.005132);//18.005132, -76.749169
        vertices.put(V_LONG,-76.749169);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PEN2"  );
        vertices.put(V_NAME,"Phys-E node 2"  );
        vertices.put(V_LAT,18.005314);//18.005314, -76.749212
        vertices.put(V_LONG,-76.749212);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PEN3"  );
        vertices.put(V_NAME,"Phys-E node 3"  );
        vertices.put(V_LAT,18.005517);
        vertices.put(V_LONG,-76.749204 );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PEN4"  );
        vertices.put(V_NAME,"Phys-E node 4"  );
        vertices.put(V_LAT,18.005535);
        vertices.put(V_LONG,-76.749056);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PEN5"  );
        vertices.put(V_NAME,"Phys-E node 5"  );
        vertices.put(V_LAT,18.005361);
        vertices.put(V_LONG,-76.749134);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CN0"  );
        vertices.put(V_NAME,"Chemistry Node 0"  );
        vertices.put(V_LAT,18.004393);//18.004791, -76.749830
        vertices.put(V_LONG,-76.749705);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CN1"  );
        vertices.put(V_NAME,"Chemistry Node 1"  );
        vertices.put(V_LAT,18.004376);
        vertices.put(V_LONG,-76.749724);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CJ0"  );
        vertices.put(V_NAME,"Chemistry Junction 0"  );
        vertices.put(V_LAT,18.004332);
        vertices.put(V_LONG,-76.749740);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CJ1"  );
        vertices.put(V_NAME,"Chemistry Junction 1"  );
        vertices.put(V_LAT,18.004174);//18.004174, -76.749987
        vertices.put(V_LONG,-76.749987);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CPN0"  );
        vertices.put(V_NAME,"Chem/Phys Node 0"  );
        vertices.put(V_LAT,18.004298);//18.004298, -76.749239
        vertices.put(V_LONG,-76.749239);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CPN1"  );
        vertices.put(V_NAME,"Chem/Phys Node 1"  );
        vertices.put(V_LAT,18.004101);
        vertices.put(V_LONG,-76.749350);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CPN2"  );
        vertices.put(V_NAME,"Chem/Phys Node 2"  );
        vertices.put(V_LAT,18.004127);
        vertices.put(V_LONG,-76.749384);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CPN3"  );
        vertices.put(V_NAME,"Chem/Phys Node 3"  );
        vertices.put(V_LAT,18.004161);
        vertices.put(V_LONG,-76.749398);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MW0"  );
        vertices.put(V_NAME,"Mechanical Workshop 0"  );
        vertices.put(V_LAT,18.005452);//18.005452, -76.748941
        vertices.put(V_LONG,-76.748941);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MW1"  );
        vertices.put(V_NAME,"Mechanical Workshop 1"  );
        vertices.put(V_LAT,18.005492);
        vertices.put(V_LONG,-76.748860);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MW2"  );
        vertices.put(V_NAME,"Mechanical Workshop 2"  );
        vertices.put(V_LAT,18.005568);
        vertices.put(V_LONG,-76.748695);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GJ0"  );
        vertices.put(V_NAME,"Geography Junction 0"  );
        vertices.put(V_LAT,18.005866);
        vertices.put(V_LONG,-76.749053);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"GJ1"  );
        vertices.put(V_NAME,"Geography Junction 1"  );
        vertices.put(V_LAT,18.006143);
        vertices.put(V_LONG,-76.748665);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GJ2"  );
        vertices.put(V_NAME,"Geography Junction  2"  );
        vertices.put(V_LAT,18.006136);
        vertices.put(V_LONG,-76.749013);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GJ3"  );
        vertices.put(V_NAME,"Geography Junction 3"  );
        vertices.put(V_LAT,18.006088);
        vertices.put(V_LONG,-76.749033);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"GJ4"  );
        vertices.put(V_NAME,"Geography Junction 4"  );
        vertices.put(V_LAT,18.006100);
        vertices.put(V_LONG,-76.748754);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"GN0"  );
        vertices.put(V_NAME,"Geography Node 0"  );
        vertices.put(V_LAT,18.005983);
        vertices.put(V_LONG,-76.748664);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MN0"  );
        vertices.put(V_NAME,"Math Node 0"  );
        vertices.put(V_LAT,18.004853);  //18.004859, -76.749498
        vertices.put(V_LONG,-76.749516);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MN1"  );
        vertices.put(V_NAME,"Math Node 1"  );
        vertices.put(V_LAT,18.004909);
        vertices.put(V_LONG,-76.749446);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"MN2"  );
        vertices.put(V_NAME,"Math Node 2"  );
        vertices.put(V_LAT,18.004953);
        vertices.put(V_LONG,-76.749385);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CJ"  );
        vertices.put(V_NAME,"Chem Junction"  );
        vertices.put(V_LAT,18.004672);
        vertices.put(V_LONG,-76.749750);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CM"  );
        vertices.put(V_NAME,"Chem Math"  );
        vertices.put(V_LAT,18.004746);
        vertices.put(V_LONG,-76.749703);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CAN0"  );
        vertices.put(V_NAME,"Chem Analytical Node 0"  );
        vertices.put(V_LAT,18.004802);
        vertices.put(V_LONG,-76.749846);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CAN1"  );
        vertices.put(V_NAME,"Chem Analytical Node 1"  );
        vertices.put(V_LAT,18.004767);
        vertices.put(V_LONG,-76.749889);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


        vertices.put(V_ID,"CS0"  );
        vertices.put(V_NAME,"Computer Science 0"  );
        vertices.put(V_LAT,18.005352);
        vertices.put(V_LONG,-76.750142  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CS1"  );
        vertices.put(V_NAME,"Computer Science 1"  );
        vertices.put(V_LAT,18.005286 );
        vertices.put(V_LONG,-76.750116  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CS2"  );
        vertices.put(V_NAME,"Computer Science 2"  );
        vertices.put(V_LAT,18.005218 );
        vertices.put(V_LONG,-76.750156  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CS3"  );
        vertices.put(V_NAME,"Computer Science 3"  );
        vertices.put(V_LAT,18.005198 );
        vertices.put(V_LONG,-76.750188  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"CS4"  );
        vertices.put(V_NAME,"Computer Science 4"  );
        vertices.put(V_LAT,18.005186 );
        vertices.put(V_LONG,-76.750245  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"PRE0"  );
        vertices.put(V_NAME,"Prelim Bio Node 0"  );
        vertices.put(V_LAT,18.006157 );//18.006157, -76.750208
        vertices.put(V_LONG,-76.750208);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SN0"  );
        vertices.put(V_NAME,"Seminar Node 0"  );
        vertices.put(V_LAT,18.006000);//18.006000, -76.750535
        vertices.put(V_LONG,-76.750535);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SN1"  );
        vertices.put(V_NAME,"Seminar Node 1"  );
        vertices.put(V_LAT,18.006037 );//18.006037, -76.750558
        vertices.put(V_LONG,-76.750558  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"SN2"  );
        vertices.put(V_NAME,"Seminar Node 2"  );
        vertices.put(V_LAT,18.006037 );//18.006037, -76.750558
        vertices.put(V_LONG,-76.750558  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BN0"  );
        vertices.put(V_NAME,"Biology Node 0 "  );
        vertices.put(V_LAT,18.006366 );//18.006366, -76.750342
        vertices.put(V_LONG,-76.750342);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BN1"  );
        vertices.put(V_NAME,"Biology Node 1"  );
        vertices.put(V_LAT,18.006522);//18.006522, -76.750439
        vertices.put(V_LONG,-76.750439);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BN2"  );
        vertices.put(V_NAME,"Biology Node 2 "  );
        vertices.put(V_LAT,18.006682 );//18.006682, -76.750144
        vertices.put(V_LONG,-76.750144);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BN3"  );
        vertices.put(V_NAME,"Biology Node 3 "  );
        vertices.put(V_LAT,18.006588 );//18.006588, -76.750082
        vertices.put(V_LONG,-76.750082);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BN4"  );
        vertices.put(V_NAME,"Biology Node 4"  );
        vertices.put(V_LAT,18.006399);//18.006399, -76.750320
        vertices.put(V_LONG,-76.750320);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"BN5"  );
        vertices.put(V_NAME,"Biology Node 5"  );
        vertices.put(V_LAT,18.006430);//18.006430, -76.750264
        vertices.put(V_LONG,-76.750264);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"AQ0"  );
        vertices.put(V_NAME," Aquatic Lab Node 0"  );
        vertices.put(V_LAT,18.006079 );
        vertices.put(V_LONG,-76.749948  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"AQ1"  );
        vertices.put(V_NAME," Aquatic Lab Node 1 "  );
        vertices.put(V_LAT,18.005962 );//18.005962, -76.749902
        vertices.put(V_LONG,-76.749902);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"AQ2"  );
        vertices.put(V_NAME," Aquatic Lab Node 2"  );
        vertices.put(V_LAT,18.006020);//18.006020, -76.749800
        vertices.put(V_LONG,-76.749800);
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"AQ3"  );
        vertices.put(V_NAME," Aquatic Lab Node 3"  );
        vertices.put(V_LAT,18.006100 );
        vertices.put(V_LONG,-76.749667  );
        vertices.put(V_TYPE ,"Unimportant Node" );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"C5BN");
        vertices.put(V_NAME,"C5 back node ");
        vertices.put(V_LAT,18.004596);//18.004601, -76.750187
        vertices.put(V_LONG,-76.750182);
        vertices.put(V_TYPE ,"Unimportant Node " );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"COMSN1");
        vertices.put(V_NAME,"Comp sci node 1");
        vertices.put(V_LAT,18.005856);//18.005856, -76.750083
        vertices.put(V_LONG,-76.750083);
        vertices.put(V_TYPE ,"Unimportant Node " );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"COMSN0");
        vertices.put(V_NAME,"Comp sci node 0");
        vertices.put(V_LAT,18.005792);//18.005792, -76.750048
        vertices.put(V_LONG,-76.750048);
        vertices.put(V_TYPE ,"Unimportant Node " );
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"COMSN3");
        vertices.put(V_NAME,"Comp sci node 3");
        vertices.put(V_LAT,18.005838);//18.005838, -76.750052
        vertices.put(V_LONG,-76.750052);
        vertices.put(V_TYPE ,"Unimportant Node " );
        vertices.put(V_LEVEL,3);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"COMSN2");
        vertices.put(V_NAME,"Comp sci node 2");
        vertices.put(V_LAT,18.005820);//18.005820, -76.750039
        vertices.put(V_LONG,-76.750039);
        vertices.put(V_TYPE ,"Unimportant Node " );
        vertices.put(V_LEVEL,2);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();




        /**
         * CUSTOM LANDMARKS
         * V_ID show not have any caps
         */

        vertices.put(V_ID,"jackie");
        vertices.put(V_NAME,"Jackie's Stall");
        vertices.put(V_LAT,18.005010 );
        vertices.put(V_LONG,-76.749584);
        vertices.put(V_TYPE ,"Place");
        vertices.put(V_LANDMARK, 1);
        vertices.put(V_LEVEL,1);
        
        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"library");
        vertices.put(V_NAME,"Science and Technology Library");
        vertices.put(V_LAT,18.005293 );//18.005293, -76.749455
        vertices.put(V_LONG,-76.749455);
        vertices.put(V_TYPE ,"Place");
        vertices.put(V_LANDMARK, 1);
        vertices.put(V_LEVEL,1);

        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"facultyoffice");
        vertices.put(V_NAME,"Science and Technology Faculty office");
        vertices.put(V_LAT,18.005362 );//18.005362, -76.749884
        vertices.put(V_LONG,-76.749884);
        vertices.put(V_TYPE ,"Place");
        vertices.put(V_LANDMARK, 1);
        vertices.put(V_LEVEL,1);

        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();

        vertices.put(V_ID,"juici");
        vertices.put(V_NAME,"Juici Patty");
        vertices.put(V_LAT,18.005007);//18.005007, -76.748527
        vertices.put(V_LONG,-76.748527);
        vertices.put(V_TYPE ,"Place");
        vertices.put(V_LANDMARK, 1);
        vertices.put(V_LEVEL,1);

        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();
//
//        vertices.put(V_ID,""  );    18.005010, -76.749584
//        vertices.put(V_NAME,""  );
//        vertices.put(V_LAT,18. );
//        vertices.put(V_LONG,-76.  );
//        vertices.put(V_TYPE ,"Unimportant Node" );
//
//                db.insert(VERTICES_TABLE,null,vertices);
//        vertices.clear();
//


      vertices.clear();
 }

    /*
     * Generate Edges
     */
    public void generateEdges(SQLiteDatabase db){

        ContentValues edges = new ContentValues();

        edges.put(E_SOURCE,"DOM");
        edges.put(E_DESTINATION,"MN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MN0");
        edges.put(E_DESTINATION,"MLAB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MN0");
        edges.put(E_DESTINATION,"MN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MN1");
        edges.put(E_DESTINATION,"MLT1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MN1");
        edges.put(E_DESTINATION,"MLT2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MN1");
        edges.put(E_DESTINATION,"MN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MN2");
        edges.put(E_DESTINATION,"MLT3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C5");
        edges.put(E_DESTINATION,"CJ");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CJ");
        edges.put(E_DESTINATION,"CM");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CM");
        edges.put(E_DESTINATION,"DOM");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CJ");
        edges.put(E_DESTINATION,"CAN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CAN0");
        edges.put(E_DESTINATION,"ACLS");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"ACLS");
        edges.put(E_DESTINATION,"CAN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CAN1");
        edges.put(E_DESTINATION,"ACL");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CAN0");
        edges.put(E_DESTINATION,"C7");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C7");
        edges.put(E_DESTINATION,"C6");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C6");
        edges.put(E_DESTINATION,"C5BN");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C5BN");
        edges.put(E_DESTINATION,"C5S");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"C5S");
        edges.put(E_DESTINATION,"CHETR1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C5S");
        edges.put(E_DESTINATION,"C5S2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"C5S2");
        edges.put(E_DESTINATION,"CHETR2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,3);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();



        edges.put(E_SOURCE,"DOM");
        edges.put(E_DESTINATION,"SJ3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ3");
        edges.put(E_DESTINATION,"PEN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ3");
        edges.put(E_DESTINATION,"NGR-LRA");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"NGR-LRA");
        edges.put(E_DESTINATION,"NGR-LRB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"NGR-LRB");
        edges.put(E_DESTINATION,"ES");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"ES");
        edges.put(E_DESTINATION,"EL3LAB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,0);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ3");
        edges.put(E_DESTINATION,"SJ4");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ4");
        edges.put(E_DESTINATION,"SLT2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ4");
        edges.put(E_DESTINATION,"SLT1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ4");
        edges.put(E_DESTINATION,"SJ5");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ5");
        edges.put(E_DESTINATION,"SJ6");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ6");
        edges.put(E_DESTINATION,"NGR-Dra");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ5");
        edges.put(E_DESTINATION,"SLT3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SLT3");
        edges.put(E_DESTINATION,"CS0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS0");
        edges.put(E_DESTINATION,"CS1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS1");
        edges.put(E_DESTINATION,"COMLABB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMLABB");
        edges.put(E_DESTINATION,"CS3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS3");
        edges.put(E_DESTINATION,"COMLAB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS3");
        edges.put(E_DESTINATION,"CS2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS3");
        edges.put(E_DESTINATION,"CS4");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS2");
        edges.put(E_DESTINATION,"CR2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS4");
        edges.put(E_DESTINATION,"CR1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CS0");
        edges.put(E_DESTINATION,"NGR-F-L");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"NGR-F-L");
        edges.put(E_DESTINATION,"NGR-S-L");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ6");
        edges.put(E_DESTINATION,"SJ7");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ7");
        edges.put(E_DESTINATION,"DOC");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ7");
        edges.put(E_DESTINATION,"SJ8");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ8");
        edges.put(E_DESTINATION,"DLSLB2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ8");
        edges.put(E_DESTINATION,"SJ9");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ9");
        edges.put(E_DESTINATION,"DLSAQU");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"DLSAQU");
        edges.put(E_DESTINATION,"AQ0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"AQ0");
        edges.put(E_DESTINATION,"AQ1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"AQ1");
        edges.put(E_DESTINATION,"AQ2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"AQ2");
        edges.put(E_DESTINATION,"AQ3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"AQ3");
        edges.put(E_DESTINATION,"DLSEB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"DLSEB");
        edges.put(E_DESTINATION,"DLSENT");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"DOM");
        edges.put(E_DESTINATION,"SJ2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ2");
        edges.put(E_DESTINATION,"SJ1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ2");
        edges.put(E_DESTINATION,"C2C3S");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C2C3S");
        edges.put(E_DESTINATION,"CN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CN0");
        edges.put(E_DESTINATION,"CN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CN1");
        edges.put(E_DESTINATION,"C3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CN1");
        edges.put(E_DESTINATION,"C2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"C2C3S");
        edges.put(E_DESTINATION,"CJ0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CJ0");
        edges.put(E_DESTINATION,"CJ1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CJ1");
        edges.put(E_DESTINATION,"FCL");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CJ0");
        edges.put(E_DESTINATION,"C06P");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ1");
        edges.put(E_DESTINATION,"VIRLAB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ1");
        edges.put(E_DESTINATION,"SJ0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ0");
        edges.put(E_DESTINATION,"CHEPHY");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ1");
        edges.put(E_DESTINATION,"LSJ");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"LSJ");
        edges.put(E_DESTINATION,"CPN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CPN0");
        edges.put(E_DESTINATION,"CPN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CPN2");
        edges.put(E_DESTINATION,"CPN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CPN2");
        edges.put(E_DESTINATION,"CPN3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CPN1");
        edges.put(E_DESTINATION,"PCL");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CPN3");
        edges.put(E_DESTINATION,"ICLS");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"ICLS");
        edges.put(E_DESTINATION,"AIL");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ0");
        edges.put(E_DESTINATION,"PHYS-D");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PHYS-D");
        edges.put(E_DESTINATION,"EL1LAB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"EL1LAB");
        edges.put(E_DESTINATION,"PN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PN0");
        edges.put(E_DESTINATION,"EL2S");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"EL2S");
        edges.put(E_DESTINATION,"PN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PN1");
        edges.put(E_DESTINATION,"EL2LAB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"PN0");
        edges.put(E_DESTINATION,"PN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PN2");
        edges.put(E_DESTINATION,"PREPHY");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"PREPHY");
        edges.put(E_DESTINATION,"PPLS");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PPLS");
        edges.put(E_DESTINATION,"PN3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"PN3");
        edges.put(E_DESTINATION,"PHYS-C");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PHYS-C");
        edges.put(E_DESTINATION,"PHYS-B");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"PHYS-B");
        edges.put(E_DESTINATION,"PHYS-A");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PHYS-B");
        edges.put(E_DESTINATION,"PES");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PHYS-A");
        edges.put(E_DESTINATION,"PHYTUT");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PES");
        edges.put(E_DESTINATION,"PEN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PEN0");
        edges.put(E_DESTINATION,"PEN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PEN1");
        edges.put(E_DESTINATION,"PHYS-E");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PES");
        edges.put(E_DESTINATION,"PEN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PEN2");
        edges.put(E_DESTINATION,"MW0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MW0");
        edges.put(E_DESTINATION,"MW1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MW1");
        edges.put(E_DESTINATION,"MW2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"MW2");
        edges.put(E_DESTINATION,"IFLT");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"VIRLAB");
        edges.put(E_DESTINATION,"PN4");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PN4");
        edges.put(E_DESTINATION,"PN5");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PN5");
        edges.put(E_DESTINATION,"NGRM-WS");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"NGRM-WS");
        edges.put(E_DESTINATION,"MW0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PN4");
        edges.put(E_DESTINATION,"PPLS");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        //BACK TO BIOLOGY DEPT

        edges.put(E_SOURCE,"SJ9");
        edges.put(E_DESTINATION,"SJ10");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ10");
        edges.put(E_DESTINATION,"PRE0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ10");
        edges.put(E_DESTINATION,"SN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ10");
        edges.put(E_DESTINATION,"SJ11");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PRE0");
        edges.put(E_DESTINATION,"DLSPBL");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SN0");
        edges.put(E_DESTINATION,"SN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SN1");
        edges.put(E_DESTINATION,"SR1S");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SR1S");
        edges.put(E_DESTINATION,"DLSSM1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"SN1");
        edges.put(E_DESTINATION,"DLSLB3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"SN2");
        edges.put(E_DESTINATION,"DLSLB4");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"SR1S");
        edges.put(E_DESTINATION,"SN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"SJ11");
        edges.put(E_DESTINATION,"SJ12");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"SJ12");
        edges.put(E_DESTINATION,"BLT");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"PRE0");
        edges.put(E_DESTINATION,"BN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"BN0");
        edges.put(E_DESTINATION,"BN4");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"BN4");
        edges.put(E_DESTINATION,"DLSSM2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"BN4");
        edges.put(E_DESTINATION,"BN5");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"BN5");
        edges.put(E_DESTINATION,"DLSINT");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"BN0");
        edges.put(E_DESTINATION,"BN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"BN1");
        edges.put(E_DESTINATION,"BN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"BN2");
        edges.put(E_DESTINATION,"BN3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"BN3");
        edges.put(E_DESTINATION,"SR3S");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"SR3S");
        edges.put(E_DESTINATION,"DLSSM3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"SR3S");
        edges.put(E_DESTINATION,"DLSPHY");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


        edges.put(E_SOURCE,"DOC");
        edges.put(E_DESTINATION,"COMSN0");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN0");
        edges.put(E_DESTINATION,"COMSN1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN1");
        edges.put(E_DESTINATION,"CPGR");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN1");
        edges.put(E_DESTINATION,"DLSFOR");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN1");
        edges.put(E_DESTINATION,"DLSSM8");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN1");
        edges.put(E_DESTINATION,"DLSMVL");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN1");
        edges.put(E_DESTINATION,"DLSSM7");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN0");
        edges.put(E_DESTINATION,"CSS1");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,1);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CSS1");
        edges.put(E_DESTINATION,"CSS2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CSS2");
        edges.put(E_DESTINATION,"COMSN3");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,3 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN3");
        edges.put(E_DESTINATION,"COMPLR");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,3 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"CSS1");
        edges.put(E_DESTINATION,"COMSN2");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2);
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN2");
        edges.put(E_DESTINATION,"DLSADB");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN2");
        edges.put(E_DESTINATION,"DLSCOM");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN2");
        edges.put(E_DESTINATION,"DLSSM4");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN2");
        edges.put(E_DESTINATION,"DLSSM5");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();

        edges.put(E_SOURCE,"COMSN2");
        edges.put(E_DESTINATION,"DLSSM6");
        edges.put(E_WEIGHT, 1);
        edges.put(E_LEVEL,2 );
        db.insert(EDGES_TABLE,null,edges);
        edges.clear();


//        edges.put(E_SOURCE,"");
//        edges.put(E_DESTINATION,"");
//        edges.put(E_WEIGHT, 1);
//        db.insert(EDGES_TABLE,null,edges);
//        edges.clear();




    }


    /**
     *
     * Database Queries
     */


    /**
     *
     * Takes a string and query it for a room or a building withing the database.
     * @param location
     * @return returns the row/s that math or contains the query string;
     */
    @Override
    public Cursor findLocation(String location ){
        location.toLowerCase();
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor res = db.rawQuery( "SELECT * FROM " + ROOM_TABLE + " WHERE LOWER("+ RT_NAME + ") like LOWER('%"+ location +"%') " +
                "OR  LOWER("+ RT_ID + ") like LOWER('%"+ location +"%');", null);


        if(res.getCount()<1){
            res = db.rawQuery( "SELECT * FROM " + BUILDING_TABLE + " WHERE LOWER("+ B_NAME + ") like LOWER('%"+ location +"%') " +
                    "OR  LOWER("+ B_ID + ") like LOWER('%"+ location +"%');", null);

        }
        res.moveToFirst();
        return res;
    }



    /**
     * Returns a specific location from withing the database base
     * @param lat
     * @param lng
     * @param id
     * @return
     */
    @Override
    public Cursor findLocation(double lat , double lng, String id) {

       // Log.d(TAG, "findLocation: " + id+" " + lat +" "+ lng);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery( "SELECT * FROM " + ROOM_TABLE + " WHERE "+ RT_LAT + " == "+ lat +" " +
                " AND "+ RT_LONG +" == "+ lng +" AND "+RT_ID+" LIKE '"+id+"';", null);


        if(res.getCount()<1){
            res = db.rawQuery( "SELECT * FROM " + BUILDING_TABLE + " WHERE "+ B_ID +" LIKE '"+ id +"' " +
                    " AND "+ B_LAT  +" == "+ lat + " AND "+B_LONG+" == "+ lng+";", null);
        }
        res.moveToFirst();
        return res;
    }



    @Override
    public Cursor getVertices(){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + VERTICES_TABLE + " WHERE 1 ", null);

        res.moveToFirst();
        db.close();
        return res;
    }


    @Override
    public Cursor getRooms(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + ROOM_TABLE + " WHERE 1 ", null);

        res.moveToFirst();
        return res;
    }

    @Override
    public Cursor getBuilding(){
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("SELECT * FROM " + BUILDING_TABLE + " WHERE 1 ", null);

        res.moveToFirst();
        return res;
    }

    @Override
    public List<Place> getLocations() {

        Cursor res;
        List<Place> places = new ArrayList<>();

        res = getBuilding();

        while(res.moveToNext()){
            Place place = new Place(
                    res.getString(res.getColumnIndex(AppDbHelper.B_ID)),
                    res.getString(res.getColumnIndex(AppDbHelper.B_NAME)),
                    res.getDouble(res.getColumnIndex(AppDbHelper.B_LAT)),
                    res.getDouble(res.getColumnIndex(AppDbHelper.B_LONG)),
                    Vertex.BUILDING,
                    res.getInt(res.getColumnIndex(AppDbHelper.B_KNOWN)),
                    res.getDouble(res.getColumnIndex(AppDbHelper.B_FAM)),
                    res.getInt(res.getColumnIndex(AppDbHelper.B_LANDMARK)),
                    0,
                    res.getString(res.getColumnIndex(AppDbHelper.B_CATEGORY)));
            places.add(place);
        }
        res=getRooms();

        while(res.moveToNext()){
            Place place = new Place(
                    res.getString(res.getColumnIndex(AppDbHelper.RT_ID)),
                    res.getString(res.getColumnIndex(AppDbHelper.RT_NAME)),
                    res.getDouble(res.getColumnIndex(AppDbHelper.RT_LAT)),
                    res.getDouble(res.getColumnIndex(AppDbHelper.RT_LONG)),
                    Vertex.ROOM,
                    res.getInt(res.getColumnIndex(AppDbHelper.RT_KNOWN)),
                    res.getDouble(res.getColumnIndex(AppDbHelper.RT_FAM)),
                    res.getInt(res.getColumnIndex(AppDbHelper.RT_LANDMARK)),
                    res.getInt(res.getColumnIndex(AppDbHelper.RT_FLOOR)),
                    res.getString(res.getColumnIndex(AppDbHelper.RT_CATEGORY)));
            places.add(place);
        }

        return places;
    }

    /*
     *  This method returns a list of rooms IDs and names.
     */
    @Override
    public ArrayList<String> roomList(){

        ArrayList<String> rooms = new ArrayList<String>() ;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor roomsData =  getRooms();//db.query(ROOM_TABLE, null, null, null, null, null, null);

        if(roomsData != null && roomsData.getCount()>0){
            //roomsData.moveToFirst();

            while (!roomsData.isAfterLast()){

                String room_id = roomsData.getString(roomsData.getColumnIndex(RT_ID));
                String room_name = roomsData.getString(roomsData.getColumnIndex(RT_NAME));


                rooms.add(room_id);
                rooms.add(room_name);
                roomsData.moveToNext();
            }
        }

        roomsData.close();
        return rooms;
    }



    @Override
    public ArrayList<String> buildingList(){

        ArrayList<String> buildings = new ArrayList<String>() ;


        Cursor buildingsData =  getBuilding();//db.query(ROOM_TABLE, null, null, null, null, null, null);

        if(buildingsData != null && buildingsData.getCount()>0){
            while (!buildingsData.isAfterLast()){

                //String building_id = buildingsData.getString(buildingsData.getColumnIndex(B_ID));
                String building_name = buildingsData.getString(buildingsData.getColumnIndex(B_NAME));


                //buildings.add(building_id);
                buildings.add(building_name);
                buildingsData.moveToNext();
            }
        }

        buildingsData.close();
        return buildings;
    }

    @Override
    public Cursor getEdges(){

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + EDGES_TABLE + " WHERE 1 ", null);

        res.moveToFirst();
        return res;

    }



    @Override
    public void updateRoom(String id, Integer known, double familiarity) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues rm = new ContentValues();


        rm.put(RT_KNOWN,known);
        rm.put(RT_FAM,familiarity);
        db.update(ROOM_TABLE,rm, RT_ID+"= ?", new String[]{id});

        rm.clear();
        db.close();
    }


    /**
     *   For debugging purposes to get database.
     */
    public void writeToSD(Context context) throws IOException {
        File sd = Environment.getExternalStorageDirectory();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DB_PATH = context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
        }
        else {
            DB_PATH = context.getFilesDir().getPath() + context.getPackageName() + "/databases/";
        }

        if (sd.canWrite()) {
            String currentDBPath = DATABASE_NAME;
            String backupDBPath = "backupname.db";
            File currentDB = new File(DB_PATH, currentDBPath);
            File backupDB = new File(sd, backupDBPath);

            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

    @Override
    public void insertLandmark(double lat, double lng, String name, String desc, String image_id) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues vertices = new ContentValues();

        //TODO add levels to landmarks


        vertices.put(V_ID,image_id);
        vertices.put(V_NAME,name);
        vertices.put(V_LAT,lat);
        vertices.put(V_LONG,lng);
        vertices.put(V_TYPE ,"Place" );
        vertices.put(V_LEVEL,0);

        db.insert(VERTICES_TABLE,null,vertices);
        vertices.clear();


    }
}//END of DB_HELPER
