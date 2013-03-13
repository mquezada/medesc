package org.mem.medesc;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

/**
 * @author Danny Remington - MacroSolve
 * 
 *         Activity for demonstrating how to use a sqlite database.
 */
public class Database extends Activity {
     /** Called when the activity is first created. */
     @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        DatabaseHelper myDbHelper;
        SQLiteDatabase myDb = null;

        myDbHelper = new DatabaseHelper(this);
        /*
         * Database must be initialized before it can be used. This will ensure
         * that the database exists and is the current version.
         */
         myDbHelper.initializeDataBase();

         try {
            // A reference to the database can be obtained after initialization.
            myDb = myDbHelper.getWritableDatabase();
            /*
             * Place code to use database here.
             */
         } catch (Exception ex) {
            ex.printStackTrace();
         } finally {
            try {
                myDbHelper.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                myDb.close();
            }
        }

    }
}