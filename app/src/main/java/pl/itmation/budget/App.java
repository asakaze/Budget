package pl.itmation.budget;

import android.app.Application;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

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

    private void seedUsers()
    {
        int count = db.getUserCount();
        Log.d(LOGTAG, "User count " + count);
        if(count > 0)
        {
            return;
        }

        db.createUser(new User("jan", "naj"));
        db.createUser(new User("ania", "aina"));
    }

    private void seedCategories()
    {
        if(db.getCategoryCount() > 0)
        {
            return;
        }

        BudgetCategory category = new BudgetCategory.BudgetCategoryBuilder("czynsz").
                defaultType(BudgetCategory.Type.EXPENSE).
                defaultValue(600).
                comment("płatne do 25").
                build();
        db.createCategory(category);

        category = new BudgetCategory.BudgetCategoryBuilder("pensja").
                defaultType(BudgetCategory.Type.EXPENSE).
                defaultValue(4000).
                build();
        db.createCategory(category);

        category = new BudgetCategory.BudgetCategoryBuilder("odsetki").
                defaultType(BudgetCategory.Type.INCOME).
                build();
        db.createCategory(category);

        category = new BudgetCategory.BudgetCategoryBuilder("zakupy").
                defaultType(BudgetCategory.Type.EXPENSE).
                build();
        db.createCategory(category);

        category = new BudgetCategory.BudgetCategoryBuilder("loteria").
                build();
        db.createCategory(category);
    }

    private void seedEntries()
    {
        int count = db.getEntryCount();
        Log.d(LOGTAG, "Entry count " + count);
        if(count > 0)
        {
            return;
        }

        Calendar date = GregorianCalendar.getInstance();
        date.set(2017, Calendar.JUNE, 2);
        BudgetEntry entry = new BudgetEntry("czynsz za maj", "czynsz", BudgetCategory.Type.EXPENSE, 640, date, "jan", "tydzień przed czasem");
        db.createEntry(entry);
    }

}
