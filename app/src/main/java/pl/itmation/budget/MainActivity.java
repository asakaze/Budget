package pl.itmation.budget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity
{
    private String currentUser = null;
    private static final String LOGTAG = MainActivity.class.getSimpleName();
    private ArrayList<BudgetEntry> entries = null;
    private ArrayAdapter<BudgetEntry> entryAdapter = null;
    private DatabaseHandler db = null;
    private int currentPosition = 0;

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
        setTitle(getString(R.string.welcom) + " " + currentUser);
        db = ((App) getApplication()).db;
        entries = db.getAllEntries();
        Collections.sort(entries);
        setupListView();
    }

    private void setupListView()
    {
        setupAdapter();
        ListView list = (ListView) findViewById(R.id.budget_list);
        list.setAdapter(entryAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId)
            {
                BudgetEntry currentEntry = entries.get(position);
                if(currentUser.equals(currentEntry.getOwner()))
                {
                    currentPosition = position;
                    Intent intent = new Intent(MainActivity.this, ManageEntryActivity.class);
                    intent.putExtra("request_code", App.MODIFY_ITEM_REQ);
                    intent.putExtra("editable_item", currentEntry);
                    Log.d(LOGTAG, "Sending intent to modify item on postition " + String.valueOf(position));
                    startActivityForResult(intent, App.MODIFY_ITEM_REQ);
                }
                else
                {
                    Toast.makeText(view.getContext(), getString(R.string.not_the_owner), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupAdapter()
    {
        entryAdapter = new ArrayAdapter<BudgetEntry>(this, 0, entries)
        {
            @Override
            public View getView(int position, View view, ViewGroup parent)
            {
                BudgetEntry currentEntry = entries.get(position);

                if(view == null || view.getTag() == null)
                {
                    view = initializeView();
                }

                if(!isVisible(currentEntry, view))
                {
                    view = getLayoutInflater().inflate(R.layout.entry_item_null, null);
                    return view;
                }

                EntryViewHolder viewHolder = recreateViewHolder(view);
                setFields(currentEntry, viewHolder);

                return view;
            }

            private View initializeView()
            {
                View view = getLayoutInflater().inflate(R.layout.entry_item, null, false);
                EntryViewHolder viewHolder = new EntryViewHolder();
                viewHolder.name = (TextView) view.findViewById(R.id.entry_item_name);
                viewHolder.value = (TextView) view.findViewById(R.id.entry_item_value);
                viewHolder.type = (TextView) view.findViewById(R.id.entry_item_type);
                viewHolder.comment = (TextView) view.findViewById(R.id.entry_item_comment);
                viewHolder.category = (TextView) view.findViewById(R.id.entry_item_category);
                viewHolder.date = (TextView) view.findViewById(R.id.entry_item_date);
                view.setTag(viewHolder);
                return view;
            }

            private boolean isVisible(BudgetEntry entry, View view)
            {
                if(entry.getType() == BudgetCategory.Type.EXPENSE && !App.showExpensesSelected)
                {
                    view.setVisibility(View.GONE);
                    return false;
                }
                else if(entry.getType() == BudgetCategory.Type.EXPENSE && App.showExpensesSelected)
                {
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                else if(entry.getType() == BudgetCategory.Type.INCOME && !App.showIncomeSelected)
                {
                    view.setVisibility(View.GONE);
                    return false;
                }
                else if(entry.getType() == BudgetCategory.Type.INCOME && App.showIncomeSelected)
                {
                    view.setVisibility(View.VISIBLE);
                    return true;
                }
                else
                {
                    return true;
                }
            }

            private EntryViewHolder recreateViewHolder(View view)
            {
                EntryViewHolder viewHolder = new EntryViewHolder();
                viewHolder.name = ((EntryViewHolder) view.getTag()).name;
                viewHolder.value = ((EntryViewHolder) view.getTag()).value;
                viewHolder.type = ((EntryViewHolder) view.getTag()).type;
                viewHolder.comment = ((EntryViewHolder) view.getTag()).comment;
                viewHolder.date = ((EntryViewHolder) view.getTag()).date;
                viewHolder.category = ((EntryViewHolder) view.getTag()).category;
                return viewHolder;
            }

            private void setFields(BudgetEntry currentEntry, EntryViewHolder viewHolder)
            {
                viewHolder.name.setText(currentEntry.getName());

                viewHolder.value.setText(getString(R.string.desc_value));
                viewHolder.value.append(String.valueOf(currentEntry.getValue()) + " " + getString(R.string.pln));

                viewHolder.type.setText(getString(R.string.desc_type));
                BudgetCategory.Type type = currentEntry.getType();
                String typeStr = (type == BudgetCategory.Type.EXPENSE)
                                 ? getString(R.string.expense)
                                 : getString(R.string.income);
                viewHolder.type.append(" " + typeStr);

                String comment = currentEntry.getComment();
                viewHolder.comment.setText(getString(R.string.desc_comment) + " ");
                if(TextUtils.isEmpty(comment))
                {
                    viewHolder.comment.append(getString(R.string.none));
                }
                else
                {
                    viewHolder.comment.append(comment);
                }

                viewHolder.category.setText(getString(R.string.desc_category) + " " + currentEntry.getCategory());

                viewHolder.date.setText(getString(R.string.desc_date) + " " +
                                        BudgetEntry.dateFormatter.format(currentEntry.getDate().getTime()));
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        if(!App.showIncomeSelected)
        {
            MenuItem item = menu.findItem(R.id.show_income);
            item.setChecked(false);
        }
        if(!App.showExpensesSelected)
        {
            MenuItem item = menu.findItem(R.id.show_expenses);
            item.setChecked(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.manage_category:
            {
                onMenageCategoryMenuItemSelected();
                return true;
            }
            case R.id.add_entry:
            {
                onAddItemMenuItemSelected();
                return true;
            }
            case R.id.logout:
            {
                onLogoutMenuItemSelected();
                return true;
            }
            case R.id.show_income:
            {
                onShowIncomeMenuItemSelected(item);
                return true;
            }
            case R.id.show_expenses:
            {
                onShowExpensesMenuItemSelected(item);
                return true;
            }
            case R.id.graphs:
            {
                onGraphsMenuItemSelected();
                return true;
            }
        }
        return false;
    }

    private void onMenageCategoryMenuItemSelected()
    {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        startActivity(intent);
    }

    private void onAddItemMenuItemSelected()
    {
        Intent intent = new Intent(MainActivity.this, ManageEntryActivity.class);
        intent.putExtra("request_code", App.CREATE_ITEM_REQ);
        startActivityForResult(intent, App.CREATE_ITEM_REQ);
    }

    private void onLogoutMenuItemSelected()
    {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void onShowIncomeMenuItemSelected(MenuItem item)
    {
        if(item.isChecked())
        {
            item.setChecked(false);
            App.showIncomeSelected = false;
            Log.d(LOGTAG, "Hiding income");
            entryAdapter.notifyDataSetChanged();
        }
        else
        {
            item.setChecked(true);
            App.showIncomeSelected = true;
            Log.d(LOGTAG, "Showing income");
            entryAdapter.notifyDataSetChanged();
        }
    }

    private void onShowExpensesMenuItemSelected(MenuItem item)
    {
        if(item.isChecked())
        {
            item.setChecked(false);
            App.showExpensesSelected = false;
            Log.d(LOGTAG, "Hiding expenses");
            entryAdapter.notifyDataSetChanged();
        }
        else
        {
            item.setChecked(true);
            App.showExpensesSelected = true;
            Log.d(LOGTAG, "Showing expenses");
            entryAdapter.notifyDataSetChanged();
        }
    }

    private void onGraphsMenuItemSelected()
    {
        Intent intent = new Intent(MainActivity.this, GraphMonthlyActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == App.CREATE_ITEM_REQ)
        {
            if(resultCode == App.CREATE_ITEM_RESP)
            {
                addNewEntry(data);
            }
            else
            {
                Log.w(LOGTAG, "Unknown respose " + String.valueOf(resultCode));
            }
        }
        else if(requestCode == App.MODIFY_ITEM_REQ)
        {
            if(resultCode == App.MODIFY_ITEM_RESP)
            {
                modifyEntry(data);
            }
            else if(resultCode == App.DELETE_ITEM_RESP)
            {
                deleteEntry(data);
            }
            else
            {
                Log.w(LOGTAG, "Unknown respose " + String.valueOf(resultCode));
            }
        }
        else
        {
            Log.w(LOGTAG, "Unknown request " + String.valueOf(requestCode));
        }
    }

    private void addNewEntry(Intent data)
    {
        BudgetEntry newEntry = data.getExtras().getParcelable("new_entry");
        long id = db.createEntry(newEntry);
        newEntry.setId(id);
        entries.add(newEntry);
        Log.d(LOGTAG, "Added entry " + newEntry.toString());
        Collections.sort(entries);
        entryAdapter.notifyDataSetChanged();
    }

    private void modifyEntry(Intent data)
    {
        BudgetEntry modifiedEntry = data.getExtras().getParcelable("modified_entry");
        Log.d(LOGTAG, "Modified entry at position " + currentPosition + "\n" + modifiedEntry.toString());
        db.updateEntry(modifiedEntry);
        entries.set(currentPosition, modifiedEntry);
        Collections.sort(entries);
        entryAdapter.notifyDataSetChanged();
    }

    private void deleteEntry(Intent data)
    {
        Log.d(LOGTAG, "Deleted entry at position " + currentPosition + "\n" + entries.get(currentPosition).toString());
        db.deleteEntry(entries.get(currentPosition).getId());
        entries.remove(currentPosition);
        entryAdapter.notifyDataSetChanged();
    }
}
