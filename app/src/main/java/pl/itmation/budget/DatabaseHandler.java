package pl.itmation.budget;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

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
    private static final String KEY_USER = "user";
    private static final String KEY_PASSWORD = "password";

    public DatabaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String CREATE_TABLE_CATEGORY = "CREATE TABLE " + TABLE_CATEGORY
                + "("
                + KEY_NAME + " TEXT PRIMARY KEY,"
                + KEY_TYPE + " TEXT,"
                + KEY_VALUE + " INTEGER,"
                + KEY_COMMENT + " TEXT"
                + ")";

        String CREATE_TABLE_ENTRY = "CREATE TABLE " + TABLE_ENTRY
                + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_TYPE + " TEXT,"
                + KEY_VALUE + " INTEGER,"
                + KEY_COMMENT + " TEXT"
                + KEY_OWNER + " TEXT"
                + KEY_DATE + " TEXT"
                + KEY_CATEGORY + " TEXT"
                + ")";

        String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_ENTRY
                + "("
                + KEY_USER + " TEXT PRIMARY KEY,"
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
        values.put(KEY_TYPE, category.getDefaultType().name());
        values.put(KEY_COMMENT, category.getComment());

        long category_id = db.insert(TABLE_CATEGORY, null, values);
        Log.d(LOGTAG, "Inserted into db: " + values.toString());
        return category_id;
    }

    public BudgetCategory getCategory(String name)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY + " WHERE "
                + KEY_NAME + " = " + name;
        Log.d(LOGTAG, selectQuery);

        Cursor cur = db.rawQuery(selectQuery, null);
        if(cur != null)
        {
            cur.moveToFirst();
        }

        BudgetCategory category = new BudgetCategory.BudgetCategoryBuilder(name).
                defaultType(BudgetCategory.Type.valueOf(cur.getString(cur.getColumnIndex(KEY_TYPE)))).
                defaultValue(cur.getInt(cur.getColumnIndex(KEY_VALUE))).
                comment(cur.getString(cur.getColumnIndex(KEY_COMMENT))).
                build();
        cur.close();
        return category;
    }

    public ArrayList<BudgetCategory> getAllCategories()
    {
        ArrayList<BudgetCategory> categories = new ArrayList<BudgetCategory>();
        String selectQuery = "SELECT * FROM " + TABLE_CATEGORY;
        Log.d(LOGTAG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery(selectQuery, null);

        if (cur.moveToFirst())
        {
            do
            {
                BudgetCategory category = new BudgetCategory.BudgetCategoryBuilder(cur.getString(cur.getColumnIndex(KEY_NAME))).
                        defaultType(BudgetCategory.Type.valueOf(cur.getString(cur.getColumnIndex(KEY_TYPE)))).
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
        values.put(KEY_TYPE, category.getDefaultType().name());
        values.put(KEY_COMMENT, category.getComment());

        return db.update(TABLE_CATEGORY, values, KEY_NAME + " = ?",
                new String[] { category.getName() });
    }


    public void deleteCategory(String name)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_CATEGORY, KEY_NAME + " = ?", new String[] { name });
    }

    public boolean checkIfCategoryExists(String name)
    {
        String query = "SELECT * FROM " + TABLE_CATEGORY + " WHERE " + KEY_NAME + " = " + name;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        int count = cursor.getCount();
        cursor.close();

        if(count == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
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