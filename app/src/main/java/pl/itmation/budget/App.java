package pl.itmation.budget;

import android.app.Application;
import android.util.Log;

public class App extends Application
{
    public DatabaseHandler db = null;

    private static final String LOGTAG = App.class.getSimpleName();
    @Override
    public void onCreate()
    {
        super.onCreate();
        db = new DatabaseHandler(getApplicationContext());
        Log.d(LOGTAG, "Created db " + db.getDatabaseName());
        seed();
    }

    @Override
    public void onTerminate()
    {
        this.db.close();
        super.onTerminate();
    }

    private void seed()
    {
        seedUsers();
        seedCategories();
        seedEntries();

    }


}
