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
        if(db.getEntryCount() > 0)
        {
            return;
        }
    }

}
