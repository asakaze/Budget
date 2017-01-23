package pl.itmation.budget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private String currentUser = null;
    private static final String LOGTAG = MainActivity.class.getSimpleName();
    private ArrayList<BudgetEntry> entries = null;
    private ArrayAdapter<BudgetEntry> entryAdapter = null;
    private DatabaseHandler db = null;

    static class EntryViewHolder
    {
        TextView name;
        TextView value;
        TextView type;
        TextView comment;
        TextView date;
        TextView category;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences session = getApplicationContext().getSharedPreferences(LoginActivity.SESSION, MODE_PRIVATE);
        currentUser = session.getString(LoginActivity.SESSION_LOGIN, "");
        Log.d(LOGTAG, "Logged as user: " + currentUser);
        db = ((App)getApplication()).db;
        entries = db.getAllEntries();

        entryAdapter = new ArrayAdapter<BudgetEntry>(this, 0, entries)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent)
            {
                BudgetEntry currentEntry = entries.get(position);

                if(convertView == null)
                {
                    convertView = getLayoutInflater().inflate(R.layout.entry_item, null, false);
                    MainActivity.EntryViewHolder viewHolder = new MainActivity.EntryViewHolder();
                    viewHolder.name = (TextView)convertView.findViewById(R.id.entry_item_name);
                    viewHolder.value = (TextView)convertView.findViewById(R.id.entry_item_value);
                    viewHolder.type = (TextView)convertView.findViewById(R.id.entry_item_type);
                    viewHolder.comment = (TextView)convertView.findViewById(R.id.entry_item_comment);
                    viewHolder.category = (TextView)convertView.findViewById(R.id.entry_item_category);
                    viewHolder.date = (TextView)convertView.findViewById(R.id.entry_item_date);
                    convertView.setTag(viewHolder);
                }

                TextView entryName = ((MainActivity.EntryViewHolder)convertView.getTag()).name;
                TextView entryValue = ((MainActivity.EntryViewHolder)convertView.getTag()).value;
                TextView entryType = ((MainActivity.EntryViewHolder)convertView.getTag()).type;
                TextView entryComment = ((MainActivity.EntryViewHolder)convertView.getTag()).comment;
                TextView entryDate = ((MainActivity.EntryViewHolder)convertView.getTag()).date;
                TextView entryCategory = ((MainActivity.EntryViewHolder)convertView.getTag()).category;

                entryName.setText(currentEntry.getName());
                entryValue.setText(getString(R.string.desc_value));
                entryValue.append(String.valueOf(currentEntry.getValue()) + " " + getString(R.string.pln));
                entryType.setText(getString(R.string.desc_type));
                BudgetCategory.Type type = currentEntry.getType();
                if(type != null)
                {
                    if(type == BudgetCategory.Type.EXPENSE)
                    {
                        entryType.append(" " + getString(R.string.expense));
                    }
                    else if(type == BudgetCategory.Type.INCOME)
                    {
                        entryType.append(" " + getString(R.string.income));
                    }
                }
                else
                {
                    entryType.append(" " + getString(R.string.none));
                }

                String comment = currentEntry.getComment();
                entryComment.setText(getString(R.string.desc_comment) + " " );
                if(comment == null || comment.equals(""))
                {
                    entryComment.append("Brak");
                }
                else
                {
                    entryComment.append(comment);
                }
                entryCategory.setText(getString(R.string.desc_category) + " " + currentEntry.getCategory());
                SimpleDateFormat date = new SimpleDateFormat("dd-MM-yyyy");
                entryDate.setText(getString(R.string.desc_date) + " " + date.format(currentEntry.getDate().getTime()));
                return convertView;
            }
        };
        ListView list = (ListView) findViewById(R.id.budget_list);
        list.setAdapter(entryAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.manage_category:
            {
                Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
                startActivity(intent);
                return true;
            }
        }
        return false;
    }
}
