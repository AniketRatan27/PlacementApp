package com.example.placementapp;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {UserProfile.class},version = 6)
public abstract class Appdatabase extends RoomDatabase {
    private static Appdatabase instance;

    public abstract UserProfileDao userProfileDao();

    public static synchronized Appdatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    Appdatabase.class,"app_databse")
                    .addMigrations(MIGRATION_5_6)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }

    // Define migration strategy
    static final Migration MIGRATION_5_6 = new Migration(5,6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase supportSQLiteDatabase) {
           // supportSQLiteDatabase.execSQL("ALTER TABLE user_profile ADD COLUMN profileImageUrl TEXT");
          //  supportSQLiteDatabase.execSQL("ALTER TABLE user_profile ADD COLUMN resumeUrl TEXT");
            supportSQLiteDatabase.execSQL("ALTER TABLE user_profile ADD COLUMN tenthMarksheetUrl TEXT");
            supportSQLiteDatabase.execSQL("ALTER TABLE user_profile ADD COLUMN twelfthMarksheetUrl TEXT");
            supportSQLiteDatabase.execSQL("ALTER TABLE user_profile ADD COLUMN phoneNumber TEXT");
            supportSQLiteDatabase.execSQL("ALTER TABLE user_profile ADD COLUMN uucmsNo TEXT");
        }
    };
}