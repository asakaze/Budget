package pl.itmation.budget;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
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
    private SharedPreferences session = null;
    private boolean valueProvidedByUser = false;
    private boolean typeProvidedByUser = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        session = getApplicationContext().getSharedPreferences(LoginActivity.SESSION, MODE_PRIVATE);
        db = ((App) getApplication()).db;
        Intent data = getIntent();
        setMode(data);
        loadFieldsFromResources();
        populateSpinners();
        setListeners();

        if(mode == Mode.MODIFY)
        {
            loadedItem = data.getExtras().getParcelable("editable_item");
            populateFields(loadedItem);
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

    private void setListeners()
    {
        setListenerOnType();
        setListenerOnValue();
        setListenerOnCategory();
    }

    private void setListenerOnType()
    {
        inputFields.type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String name = inputFields.category.getSelectedItem().toString();
                if(!TextUtils.isEmpty(name))
                {
                    typeProvidedByUser = true;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                return;
            }
        });
    }

    private void setListenerOnValue()
    {
        inputFields.value.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                valueProvidedByUser = true;
            }
        });
    }

    private void setListenerOnCategory()
    {
        inputFields.category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                String name = inputFields.category.getSelectedItem().toString();
                if(!TextUtils.isEmpty(name))
                {
                    fillDefaultValues(name);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                return;
            }

            private void fillDefaultValues(String name)
            {
                BudgetCategory category = db.getCategory(name);
                if((category.getDefaultType() != null) && !typeProvidedByUser)
                {
                    int position = category.getDefaultType().ordinal() + 1;
                    inputFields.type.setSelection(position);
                }
                if((category.getDefaultValue() > 0) && !valueProvidedByUser)
                {
                    inputFields.value.setText(String.valueOf(category.getDefaultValue()));
                }
            }
        });
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
        category_names.add("");

        for(BudgetCategory cat : categories)
        {
            category_names.add(cat.getName());
        }

        categorySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, category_names);
        categorySpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputFields.category.setAdapter(categorySpinnerAdapter);
    }

    private void populateFields(BudgetEntry item)
    {
        inputFields.name.setText(item.getName());
        int positionType = item.getType().ordinal() + 1;
        inputFields.type.setSelection(positionType);
        int positionCategory = categorySpinnerAdapter.getPosition(item.getCategory());
        inputFields.category.setSelection(positionCategory);
        inputFields.value.setText(String.valueOf(item.getValue()));
        inputFields.comment.setText(item.getComment());
        inputFields.date.setText(BudgetEntry.dateFormatter.format(item.getDate().getTime()));
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
                    addNewEntry();
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

    private void addNewEntry()
    {
        BudgetEntry entry = createEntryFromInputs();
        if(entry != null)
        {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("new_entry", entry);
            setResult(App.CREATE_ITEM_RESP, resultIntent);
            finish();
        }
    }

    private void editEntry()
    {
        BudgetEntry entry = createEntryFromInputs();
        if(entry != null)
        {
            entry.setId(loadedItem.getId());
            Intent resultIntent = new Intent();
            resultIntent.putExtra("modified_entry", entry);
            setResult(App.MODIFY_ITEM_RESP, resultIntent);
            finish();
        }
    }

    private BudgetEntry createEntryFromInputs()
    {
        String name = inputFields.name.getText().toString();
        if(!validateName(name))
        {
            return null;
        }

        BudgetCategory.Type type = extractType();
        if(!validateType(type))
        {
            return null;
        }

        String valueStr = inputFields.value.getText().toString();
        int value = 0;
        if(!validateValue(valueStr))
        {
            return null;
        }
        else
        {
            value = Integer.parseInt(valueStr);
        }

        String owner = session.getString(LoginActivity.SESSION_LOGIN, "");

        String dateStr = inputFields.date.getText().toString();
        Calendar date = Calendar.getInstance();
        if(!validateDate(dateStr))
        {
            return null;
        }
        else
        {
            try
            {
                date.setTime(BudgetEntry.dateFormatter.parse(dateStr));
            }
            catch(ParseException e)
            {
                return null;
            }
        }
        String category = inputFields.category.getSelectedItem().toString();
        if(!validateCategory(category))
        {
            return null;
        }
        String comment = inputFields.comment.getText().toString();

        BudgetEntry entry = new BudgetEntry(name, category, type, value, date, owner, comment);
        return entry;
    }

    private boolean validateName(String name)
    {
        if(TextUtils.isEmpty(name))
        {
            inputFields.name.setError(getString(R.string.error_field_required));
            inputFields.name.requestFocus();
            inputFields.name.selectAll();
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean validateType(BudgetCategory.Type type)
    {
        if(type == null)
        {
            Toast.makeText(this, getString(R.string.type_required), Toast.LENGTH_LONG).show();
            inputFields.type.requestFocus();
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean validateValue(String valueStr)
    {
        if(TextUtils.isEmpty(valueStr))
        {
            inputFields.value.setError(getString(R.string.error_field_required));
            inputFields.value.requestFocus();
            inputFields.value.selectAll();
            return false;
        }

        int value = 0;
        try
        {
            value = Integer.valueOf(valueStr);
        }
        catch(NumberFormatException e)
        {
            inputFields.value.setError(getString(R.string.error_cannot_parse));
            inputFields.value.requestFocus();
            inputFields.value.selectAll();
            return false;
        }

        if(value <= 0)
        {
            inputFields.value.setError(getString(R.string.error_wrong_value));
            inputFields.value.requestFocus();
            inputFields.value.selectAll();
            return false;
        }
        else
        {
            return true;
        }
    }

    private BudgetCategory.Type extractType()
    {
        int position = inputFields.type.getSelectedItemPosition();
        if(position == 0)
        {
            return null;
        }
        else
        {
            BudgetCategory.Type type = BudgetCategory.Type.values()[position - 1];
            return type;
        }
    }

    private boolean validateCategory(String category)
    {
        if(TextUtils.isEmpty(category))
        {
            Toast.makeText(this, getString(R.string.category_required), Toast.LENGTH_LONG).show();
            inputFields.category.requestFocus();
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean validateDate(String dateStr)
    {
        Calendar date = Calendar.getInstance();
        try
        {
            date.setTime(BudgetEntry.dateFormatter.parse(dateStr));
        }
        catch(ParseException e)
        {
            String msg = getString(R.string.error_wrong_date) + " " + BudgetEntry.dateFormat;
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            inputFields.date.requestFocus();
            return false;
        }
        return true;
    }
}
