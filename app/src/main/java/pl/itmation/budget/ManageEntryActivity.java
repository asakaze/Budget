package pl.itmation.budget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Calendar;

public class ManageEntryActivity extends AppCompatActivity
{
    private enum Mode
    {
        CREATE, MODIFY
    }
    private Mode mode;
    private BudgetEntry entry = null;
    private DatabaseHandler db = null;
    private ArrayAdapter<String> categoryAdapter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = ((App)getApplication()).db;
        populateSpinners();

        Intent data = getIntent();
        if(data.getIntExtra("request_code", 0) == MainActivity.CREATE_ENTRY)
        {
            mode = Mode.CREATE;
        }
        else if(data.getIntExtra("request_code", 0) == MainActivity.MODIFY_ENTRY)
        {
            mode = Mode.MODIFY;
            entry = data.getExtras().getParcelable("editable_entry");
            populateFields();
        }

        setupButtons();
    }

    private void setupButtons()
    {
        Button menageButton = (Button) findViewById(R.id.category_menage_button);
        Button deleteButton = (Button) findViewById(R.id.category_delete_button);
        if(mode == Mode.CREATE) {
            deleteButton.setVisibility(View.GONE);
            menageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    createEntry();
                }
            });
        }
        else if(mode == Mode.MODIFY)
        {
            menageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editEntry();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteEntry();
                }
            });
        }
    }

    private void deleteEntry()
    {
        Intent resultIntent = new Intent();
        setResult(MainActivity.DELETE_ENTRY_RESP, resultIntent);
        finish();
    }

    private void createEntry()
    {
        String name = extractName();
        if(name == null)
        {
            return;
        }

        int value = extractValue();
        if(value == 0)
        {
            return;
        }
        BudgetCategory.Type type = extractType();
        if(type == null)
        {
            return;
        }
        SharedPreferences session = getApplicationContext().getSharedPreferences(LoginActivity.SESSION, MODE_PRIVATE);
        String owner = session.getString(LoginActivity.SESSION_LOGIN, "");

        Calendar date = extractDate();
        String category = extractCategory();
        String comment = extractComment();
        BudgetEntry entry = new BudgetEntry(name, category, type, value, date, owner, comment);

        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_entry", entry);
        setResult(MainActivity.CREATE_ENTRY_RESP, resultIntent);
        finish();
    }

    private void editEntry()
    {
        String name = extractName();
        if (name == null)
        {
            return;
        }
        entry.setName(name);
        entry.setType(extractType());
        entry.setComment(extractComment());
        entry.setValue(extractValue());
        entry.setOwner(entry.getOwner());
        entry.setDate(extractDate());
        entry.setCategory(extractCategory());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("modified_entry", entry);
        setResult(MainActivity.MODIFY_ENTRY_RESP, resultIntent);
        finish();
    }

    private void populateFields()
    {
        EditText name = (EditText) findViewById(R.id.entry_name_input);
        name.setText(entry.getName());

        Spinner type = (Spinner) findViewById(R.id.entry_spinner_type);
        int position = categoryAdapter.getPosition(entry.getCategory());
        type.setSelection(position);

        EditText value = (EditText) findViewById(R.id.entry_value_input);
        value.setText(String.valueOf(entry.getValue()));
        EditText comment = (EditText) findViewById(R.id.entry_comment_input);
        comment.setText(entry.getComment());

        Button button = (Button) findViewById(R.id.entry_menage_button);
        button.setText(getString(R.string.button_modify));

    }

    private String extractName()
    {
        EditText nameInput = (EditText) findViewById(R.id.entry_name_input);
        String name = nameInput.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            nameInput.setError(getString(R.string.error_field_required));
            nameInput.requestFocus();
            return null;
        }

        return nameInput.getText().toString();
    }

    private int extractValue()
    {
        EditText valueInput = (EditText) findViewById(R.id.entry_value_input);
        if (valueInput.getText().toString().equals(""))
        {
            valueInput.setError(getString(R.string.error_field_required));
            valueInput.requestFocus();
            return 0;
        }
        return Integer.parseInt(valueInput.getText().toString());
    }

    private BudgetCategory.Type extractType()
    {
        BudgetCategory.Type retVal = null;
        Spinner typeInput = (Spinner) findViewById(R.id.entry_spinner_type);
        switch (typeInput.getSelectedItemPosition())
        {
            case 1:
                retVal = BudgetCategory.Type.INCOME;
                break;
            case 2:
                retVal = BudgetCategory.Type.EXPENSE;
                break;
        }

        if(retVal == null)
        {
            typeInput.requestFocus();
        }
        return retVal;
    }

    private String extractCategory()
    {
        Spinner categoryInput = (Spinner) findViewById(R.id.entry_spinner_category);
        return categoryInput.getSelectedItem().toString();
    }

    private Calendar extractDate()
    {
        return null;
    }

    private String extractComment()
    {
        EditText commentInput = (EditText) findViewById(R.id.entry_comment_input);
        if (commentInput.getText() == null) return null;
        return commentInput.getText().toString();
    }

    private void populateSpinners()
    {
        populateTypeSpinner();
        populateCategorySpinner();
    }
    private void populateTypeSpinner()
    {
        Spinner spinner = (Spinner) findViewById(R.id.entry_spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void populateCategorySpinner()
    {
        ArrayList<String> category_names = new ArrayList<String>();
        ArrayList<BudgetCategory> categories = db.getAllCategories();
        for (BudgetCategory cat : categories)
        {
            category_names.add(cat.getName());
        }
        Spinner spinner = (Spinner) findViewById(R.id.entry_spinner_category);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, category_names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}
