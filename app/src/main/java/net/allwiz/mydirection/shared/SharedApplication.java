package net.allwiz.mydirection.shared;

import android.app.Application;

import net.allwiz.mydirection.database.DirectionDb;

public class SharedApplication extends Application {
    private static final String TAG = SharedApplication.class.getSimpleName();

    private DirectionDb     mDirectionDb = null;

    @Override
    public void onCreate() {
        super.onCreate();

        createDatabase();
    }


    @Override
    public void onTerminate() {

        closeDatabase();
        super.onTerminate();
    }


    private void createDatabase() {
        if (mDirectionDb == null) {
            mDirectionDb = DirectionDb.getInstance(this);
        }

        // Insert default places such as home, work, starred for recent header
        mDirectionDb.insertDefaultDirection();
    }


    private void closeDatabase() {
        if (mDirectionDb != null) {
            mDirectionDb.close();
        }
    }


    public DirectionDb getDirectionDb() {
        return mDirectionDb;
    }
}
