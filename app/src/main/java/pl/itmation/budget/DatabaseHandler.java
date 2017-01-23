package pl.itmation.budget;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String LOGTAG = DatabaseHandler.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "budget.db";

    private static final String TABLE_CATEGORY = "categories";
    private static final String TABLE_ENTRY = "entries";
    private static final String TABLE_USER = "users";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_TYPE = "type";
    private static final String KEY_VALUE = "value";
    private static final String KEY_COMMENT = "comment";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_OWNER = "owner";
    private static final String KEY_DATE = "date";
    private static final String KEY_LOGIN = "login";
    private static final String KEY_PASSWORD = "password";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE_CATEGORY = "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORY
                + "("
                + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_TYPE + " TEXT,"
                + KEY_VALUE + " INTEGER,"
                + KEY_COMMENT + " TEXT"
                + ")";

        String CREATE_TABLE_ENTRY = "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRY
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_VALUE + " INTEGER,"
                + KEY_COMMENT + " TEXT,"
                + KEY_OWNER + " TEXT,"
                + KEY_DATE + " INTEGER,"
                + KEY_CATEGORY + " TEXT"
                + ")";

        String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " + TABLE_USER
                + "("
                + KEY_LOGIN + " TEXT PRIMARY KEY,"
                + KEY_PASSWORD + " TEXT"
                + ")";

        db.execSQL(CREATE_TABLE_CATEGORY);
        Log.d(LOGTAG, "Query: " + CREATE_TABLE_CATEGORY);
        db.execSQL(CREATE_TABLE_ENTRY);
        Log.d(LOGTAG, "Query: " + CREATE_TABLE_ENTRY);
        db.execSQL(CREATE_TABLE_USER);
        Log.d(LOGTAG, "Query: " + CREATE_TABLE_USER);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENTRY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }

    public long createCategory(BudgetCategory category)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, category.getName());
        values.put(KEY_VALUE, category.getDefaultValue());
        BudgetCategory.Type type = category.getDefaultType();
        if(type != null)
        {
            values.put(KEY_TYPE, category.getDefaultType().name());
        }
        else
        {
            values.put(KEY_TYPE, "");
        }
        values.put(KEY_COMMENT, category.getComment());

        long id = db.insert(TABLE_CATEGORY, null, values);
        Log.d(LOGTAG, "Inserted into db: " + values.toString());
        return id;
    }

    public long createUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_LOGIN, user.getLogin());
        values.put(KEY_PASSWORD, user.getPassword());

        long id = db.insert(TABLE_USER, null, values);
        Log.d(LOGTAG, "Inserted into db: " + values.toString());
        return id;
    }

    public long createEntry(BudgetEntry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, entry.getName());
        values.put(KEY_VALUE, entry.getValue());
        values.put(KEY_TYPE, entry.getType().name());
        values.put(KEY_COMMENT, entry.getComment());
        values.put(KEY_OWNER, entry.getOwner());
        values.put(KEY_DATE, entry.getDate().getTime());
        values.put(KEY_CATEGORY, entry.getCategory());

        long id = db.insert(TABLE_ENTRY, null, values);
        Log.d(LOGTAG, "Inserted into db: " + values.toString());
        return id;
    }

    public User getUser(String login)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;
        try
        {
            cur = db.rawQuery("SELECT * FROM " + TABLE_USER + " WHERE " + KEY_LOGIN + " = ? ", new String[]{login});
        }
        catch (Exception e)
        {
            Log.d(LOGTAG, "DB error: " + e.toString());
            return null;
        }

        if(cur != null && cur.moveToFirst())
        {
            User user = new User(login, cur.getString(cur.getColumnIndex(KEY_PASSWORD)));
            Log.d(LOGTAG, "Found user: " + user.getLogin());
            cur.close();
            return user;
        }
        else
        {
            cur.close();
            return null;
        }
    }

    public ArrayList<BudgetEntry> getAllEntries()
    {
        ArrayList<BudgetEntry> entries = new ArrayList<BudgetEntry>();
        String query = "SELECT * FROM " + TABLE_ENTRY;
        Log.d(LOGTAG, "Query: " + query);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;
        try
        {
            cur = db.rawQuery(query, null);
        }
        catch(Exception e)
        {
            Log.d(LOGTAG, "DB error: " + e.toString());
            return null;
        }


        if (cur.moveToFirst())
        {
            do
            {
                long id = cur.getLong(cur.getColumnIndex(KEY_ID));
                String name = cur.getString(cur.getColumnIndex(KEY_NAME));
                String category = cur.getString(cur.getColumnIndex(KEY_CATEGORY));
                BudgetCategory.Type type = BudgetCategory.Type.valueOf(cur.getString(cur.getColumnIndex(KEY_TYPE)));
                int value = cur.getInt(cur.getColumnIndex(KEY_VALUE));
                Date date = new Date(cur.getLong(cur.getColumnIndex(KEY_DATE)));
                String owner = cur.getString(cur.getColumnIndex(KEY_OWNER));
                String comment = cur.getString(cur.getColumnIndex(KEY_COMMENT));
                BudgetEntry entry = new BudgetEntry(id, name, category, type, value, date, owner, comment);
                entries.add(entry);
            }
            while (cur.moveToNext());
        }

        cur.close();
        return entries;
    }

    public ArrayList<BudgetCategory> getAllCategories()
    {
        ArrayList<BudgetCategory> categories = new ArrayList<BudgetCategory>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY;
        Log.d(LOGTAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;
        try
        {
            cur = db.rawQuery(selectQuery, null);
        }
        catch(Exception e)
        {
            Log.d(LOGTAG, "DB error: " + e.toString());
            return null;
        }

        if (cur.moveToFirst())
        {
            do
            {
                BudgetCategory category = new BudgetCategory.BudgetCategoryBuilder(cur.getString(cur.getColumnIndex(KEY_NAME))).
                        defaultType(cur.getString(cur.getColumnIndex(KEY_TYPE))).
                        defaultValue(cur.getInt(cur.getColumnIndex(KEY_VALUE))).
                        comment(cur.getString(cur.getColumnIndex(KEY_COMMENT))).
                        build();
                categories.add(category);
            }
            while (cur.moveToNext());
        }

        cur.close();
        return categories;
    }


    public int updateCategory(BudgetCategory category)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, category.getName());
        values.put(KEY_VALUE, category.getDefaultValue());
        BudgetCategory.Type type = category.getDefaultType();
        if(type != null)
        {
            values.put(KEY_TYPE, category.getDefaultType().name());
        }
        else
        {
            values.put(KEY_TYPE, "");
        }
        values.put(KEY_COMMENT, category.getComment());

        return db.update(TABLE_CATEGORY, values, KEY_NAME + " = ?",
                new String[] { category.getName() });
    }

    public int updateEntry(BudgetEntry entry)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, entry.getName());
        values.put(KEY_VALUE, entry.getValue());
        values.put(KEY_TYPE, entry.getType().name());
        values.put(KEY_COMMENT, entry.getComment());
        values.put(KEY_OWNER, entry.getOwner());
        values.put(KEY_DATE, entry.getDate().getTime());
        values.put(KEY_CATEGORY, entry.getCategory());

        return db.update(TABLE_ENTRY, values, KEY_NAME + " = ?",
                new String[] { String.valueOf(entry.getId()) });
    }


    public void deleteCategory(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.delete(TABLE_CATEGORY, KEY_NAME + " = ?", new String[] { name });
        }
        catch(Exception e)
        {
            Log.d(LOGTAG, "DB error: " + e.toString());
        }
    }

    public void deleteEntry(long id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        try
        {
            db.delete(TABLE_ENTRY, KEY_ID + " = ?", new String[] { String.valueOf(id) });
        }
        catch(Exception e)
        {
            Log.d(LOGTAG, "DB error: " + e.toString());
        }
    }

    public boolean checkIfCategoryExists(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = null;
        try
        {
            cur = db.rawQuery("SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME + " = ? ", new String[]{name});
        }
        catch (Exception e)
        {
            Log.d(LOGTAG, "DB error: " + e.toString());
            return false;
        }
        int count = cur.getCount();
        cur.close();

        if(count == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public int getCategoryCount()
    {
        return getTableCount(TABLE_CATEGORY);
    }

    public int getEntryCount()
    {
        return getTableCount(TABLE_ENTRY);
    }

    public int getUserCount()
    {
        return getTableCount(TABLE_USER);
    }

    public int getTableCount(String table)
    {
        String countQuery = "SELECT * FROM " + table;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void closeDB()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        if(db != null && db.isOpen())
        {
            db.close();
        }
    }

}