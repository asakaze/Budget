package pl.itmation.budget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ManageEntryActivity extends AppCompatActivity
{
    private enum Mode
    {
        CREATE, MODIFY
    }

    static class InputFields
    {
        EditText name;
        EditText value;
        Spinner type;
        EditText comment;
        Spinner category;
        EditText date;
    }

    private static final String LOGTAG = ManageEntryActivity.class.getSimpleName();
    private Mode mode = null;
    private BudgetEntry loadedItem = null;
    private DatabaseHandler db = null;
    private ArrayAdapter<String> categorySpinnerAdapter = null;
    private ArrayAdapter<CharSequence> typeSpinnerAdapter = null;
    private InputFields inputFields = null;
    SimpleDateFormat dateFormatter = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_entry);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/

        db = ((App) getApplication()).db;
        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");
        Intent data = getIntent();
        int requestCode = data.getIntExtra("request_code", 69);
        loadedItem = data.getExtras().getParcelable("editable_item");
        Log.w(LOGTAG, "Name = " + loadedItem.getName());
        Log.w(LOGTAG, "Unknown mode, request code = " + String.valueOf(requestCode));
        setMode(data);
        loadFieldsFromResources();
        populateSpinners();
        if(mode == Mode.MODIFY)
        {
            loadedItem = data.getExtras().getParcelable("editable_item");
            populateFields();
        }

        setupButtons();
    }

    private void setMode(Intent data)
    {
        int requestCode = data.getIntExtra("request_code", 0);
        if(requestCode == App.CREATE_ITEM_REQ)
        {
            mode = Mode.CREATE;
            Log.d(LOGTAG, "Create mode");
        }
        else if(requestCode == App.MODIFY_ITEM_REQ)
        {
            mode = Mode.MODIFY;
            Log.w(LOGTAG, "Modify mode");
        }
        else
        {
            Log.w(LOGTAG, "Unknown mode, request code = " + String.valueOf(requestCode));
        }
    }

    private void loadFieldsFromResources()
    {
        inputFields = new InputFields();
        inputFields.name = (EditText) findViewById(R.id.content_manage_entry_name_input);
        inputFields.type = (Spinner) findViewById(R.id.content_manage_entry_spinner_type);
        inputFields.category = (Spinner) findViewById(R.id.content_manage_entry_spinner_category);
        inputFields.value = (EditText) findViewById(R.id.content_manage_entry_value_input);
        inputFields.comment = (EditText) findViewById(R.id.content_manage_entry_comment_input);
        inputFields.date = (EditText) findViewById(R.id.content_manage_entry_date_input);
    }

    private void populateSpinners()
    {
        populateTypeSpinner();
        populateCategorySpinner();
    }

    private void populateTypeSpinner()
    {
        typeSpinnerAdapter =
                ArrayAdapter.createFromResource(this, R.array.type_array, android.R.layout.simple_spinner_item);
        typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputFields.type.setAdapter(typeSpinnerAdapter);
    }

    private void populateCategorySpinner()
    {
        ArrayList<String> category_names = new ArrayList<>();
        ArrayList<BudgetCategory> categories = db.getAllCategories();

        for(BudgetCategory cat : categories)
        {
            category_names.add(cat.getName());
        }

        categorySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, category_names);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputFields.category.setAdapter(categorySpinnerAdapter);
    }

    private void populateFields()
    {
        inputFields.name.setText(loadedItem.getName());
        int positionType = typeSpinnerAdapter.getPosition(loadedItem.getType().name());
        inputFields.type.setSelection(positionType);
        int positionCategory = typeSpinnerAdapter.getPosition(loadedItem.getCategory());
        inputFields.category.setSelection(positionCategory);
        inputFields.value.setText(String.valueOf(loadedItem.getValue()));
        inputFields.comment.setText(loadedItem.getComment());
        inputFields.date.setText(dateFormatter.format(loadedItem.getDate().getTime()));
    }

    private void setupButtons()
    {
        Button menageButton = (Button) findViewById(R.id.content_manage_entry_menage_button);
        Button deleteButton = (Button) findViewById(R.id.content_manage_entry_delete_button);

        if(mode == Mode.CREATE)
        {
            deleteButton.setVisibility(View.GONE);
            menageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    createEntry();
                }
            });
        }
        else if(mode == Mode.MODIFY)
        {
            menageButton.setText(getString(R.string.button_modify));
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
        setResult(App.DELETE_ITEM_RESP, resultIntent);
        finish();
    }

    private void createEntry()
    {
        String name = extractName();
        if(name == null)
        {
            return;
        }

        BudgetCategory.Type type = extractType();
        if(type == null)
        {
            return;
        }

        int value = extractValue();
        if(value == 0)
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
        setResult(App.CREATE_ITEM_RESP, resultIntent);
        finish();
    }

    private void editEntry()
    {
        String name = extractName();
        if(name == null)
        {
            return;
        }
        loadedItem.setName(name);
        loadedItem.setType(extractType());
        loadedItem.setComment(extractComment());
        loadedItem.setValue(extractValue());
        loadedItem.setOwner(loadedItem.getOwner());
        loadedItem.setDate(extractDate());
        loadedItem.setCategory(extractCategory());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("modified_entry", loadedItem);
        setResult(App.MODIFY_ITEM_RESP, resultIntent);
        finish();
    }

    private String extractName()
    {
        EditText nameInput = (EditText) findViewById(R.id.content_manage_entry_name_input);
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
        EditText valueInput = (EditText) findViewById(R.id.content_manage_entry_value_input);
        if(valueInput.getText().toString().equals(""))
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
        Spinner typeInput = (Spinner) findViewById(R.id.content_manage_entry_spinner_type);
        switch(typeInput.getSelectedItemPosition())
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
        Spinner categoryInput = (Spinner) findViewById(R.id.content_manage_entry_spinner_category);
        return categoryInput.getSelectedItem().toString();
    }

    private Calendar extractDate()
    {
        return null;
    }

    private String extractComment()
    {
        EditText commentInput = (EditText) findViewById(R.id.content_manage_entry_comment_input);
        if(commentInput.getText() == null)
        {
            return null;
        }
        return commentInput.getText().toString();
    }
}
