package pl.itmation.budget;

import android.app.Application;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class App extends Application
{
    public DatabaseHandler db = null;
    public static final int CREATE_ITEM_REQ = 10;
    public static final int CREATE_ITEM_RESP = 11;
    public static final int MODIFY_ITEM_REQ = 20;
    public static final int MODIFY_ITEM_RESP = 21;
    public static final int DELETE_ITEM_RESP = 22;

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
        this.db.closeDB();
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
        date.set(2016, Calendar.JUNE, 2);
        BudgetEntry entry = new BudgetEntry("czynsz za maj", "czynsz", BudgetCategory.Type.EXPENSE, 640, date, "jan", "tydzień przed czasem");
        db.createEntry(entry);

        date.set(2016, Calendar.MAY, 12);
        entry = new BudgetEntry("czynsz za kwiecień", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.APRIL, 25);
        entry = new BudgetEntry("czynsz za marzec", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.APRIL, 13);
        entry = new BudgetEntry("jedzenie", "zakupy", BudgetCategory.Type.EXPENSE, 1649, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.APRIL, 11);
        entry = new BudgetEntry("lotto", "loteria", BudgetCategory.Type.INCOME, 106, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.APRIL, 12);
        entry = new BudgetEntry("poker", "loteria", BudgetCategory.Type.EXPENSE, 1560, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.MARCH, 7);
        entry = new BudgetEntry("czynsz za luty", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "janusz");
        db.createEntry(entry);

        date.set(2016, Calendar.FEBRUARY, 7);
        entry = new BudgetEntry("czynsz za styczeń", "czynsz", BudgetCategory.Type.EXPENSE, 566, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.JANUARY, 7);
        entry = new BudgetEntry("czynsz za grudzień", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.DECEMBER, 7);
        entry = new BudgetEntry("czynsz za listopad", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.NOVEMBER, 7);
        entry = new BudgetEntry("czynsz za październik", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.OCTOBER, 7);
        entry = new BudgetEntry("czynsz", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.SEPTEMBER, 7);
        entry = new BudgetEntry("czynsz", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.AUGUST, 7);
        entry = new BudgetEntry("czynsz", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.JULY, 7);
        entry = new BudgetEntry("czynsz", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.JUNE, 7);
        entry = new BudgetEntry("czynsz", "czynsz", BudgetCategory.Type.EXPENSE, 649, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.JUNE, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.JULY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.AUGUST, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.AUGUST, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.SEPTEMBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.SEPTEMBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.OCTOBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.OCTOBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.NOVEMBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.NOVEMBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2015, Calendar.DECEMBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2015, Calendar.DECEMBER, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.JANUARY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.JANUARY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.FEBRUARY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.FEBRUARY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.MARCH, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.MARCH, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.APRIL, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.APRIL, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");
        db.createEntry(entry);

        date.set(2016, Calendar.MAY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4800, date, "ania");
        db.createEntry(entry);

        date.set(2016, Calendar.MAY, 7);
        entry = new BudgetEntry("wypłata", "pensja", BudgetCategory.Type.INCOME, 4000, date, "jan");

        db.createEntry(entry);
    }
}
