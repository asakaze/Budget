package pl.itmation.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class ManageCategoryActivity extends AppCompatActivity
{

    private enum Mode
    {
        CREATE, MODIFY
    }
    private Mode mode;
    private BudgetCategory category = null;
    private DatabaseHandler db = null;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = ((App)getApplication()).db;
        populateSpinner();

        Intent data = getIntent();
        if(data.getIntExtra("request_code", 0) == App.CREATE_ITEM_REQ)
        {
            mode = Mode.CREATE;
        }
        else if(data.getIntExtra("request_code", 0) == App.MODIFY_ITEM_REQ)
        {
            mode = Mode.MODIFY;
            category = data.getExtras().getParcelable("editable_category");
            populateFields();
        }

        setupButtons();
    }

    private void setupButtons()
    {
        Button manageButton = (Button) findViewById(R.id.category_menage_button);
        Button deleteButton = (Button) findViewById(R.id.category_delete_button);
        if(mode == Mode.CREATE) {
            deleteButton.setVisibility(View.GONE);
            manageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    createCategory();
                }
            });
        }
        else if(mode == Mode.MODIFY)
        {
            manageButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    editCategory();
                }
            });
            deleteButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    deleteCategory();
                }
            });
        }
    }

    private void deleteCategory()
    {
        Intent resultIntent = new Intent();
        setResult(App.DELETE_ITEM_RESP, resultIntent);
        finish();
    }

    private void createCategory()
    {
        String name = extractName();
        if (name == null)
        {
            return;
        }

        int value = extractValue();
        BudgetCategory.Type type = extractType();
        String comment = extractComment();
        category = new BudgetCategory.BudgetCategoryBuilder(name).defaultType(type).defaultValue(value).comment(comment).build();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_category", category);
        setResult(App.CREATE_ITEM_RESP, resultIntent);
        finish();
    }

    private void editCategory()
    {
        String name = extractName();
        if (name == null)
        {
            return;
        }
        category.setName(name);
        category.setDefaultType(extractType());
        category.setComment(extractComment());
        category.setDefaultValue(extractValue());
        Intent resultIntent = new Intent();
        resultIntent.putExtra("modified_category", category);
        setResult(App.MODIFY_ITEM_RESP, resultIntent);
        finish();
    }

    private void populateFields()
    {
        EditText name = (EditText) findViewById(R.id.name_input);
        name.setText(category.getName());

        Spinner type = (Spinner) findViewById(R.id.spinner_type);
        int position = 0;
        if(category.getDefaultType() == BudgetCategory.Type.INCOME)
        {
            position = 1;
        }
        else if(category.getDefaultType() == BudgetCategory.Type.EXPENSE)
        {
            position = 2;
        }

        type.setSelection(position);

        EditText value = (EditText) findViewById(R.id.value_input);
        value.setText(String.valueOf(category.getDefaultValue()));
        EditText comment = (EditText) findViewById(R.id.comment_input);
        comment.setText(category.getComment());

        Button button = (Button) findViewById(R.id.category_menage_button);
        button.setText(getString(R.string.button_modify));

    }

    private String extractName()
    {
        EditText nameInput = (EditText) findViewById(R.id.name_input);
        String name = nameInput.getText().toString();
        if(TextUtils.isEmpty(name))
        {
            nameInput.setError(getString(R.string.error_field_required));
            nameInput.requestFocus();
            return null;
        }
        else if(db.checkIfCategoryExists(name) && mode == Mode.CREATE)
        {
            nameInput.setError(getString(R.string.error_field_unique));
            nameInput.requestFocus();
            return null;
        }
        return nameInput.getText().toString();
    }

    private int extractValue()
    {
        EditText valueInput = (EditText) findViewById(R.id.value_input);
        if (valueInput.getText().toString().equals(""))
        {
            return 0;
        }
        return Integer.parseInt(valueInput.getText().toString());
    }

    private BudgetCategory.Type extractType()
    {
        BudgetCategory.Type retVal = null;
        Spinner typeInput = (Spinner) findViewById(R.id.spinner_type);
        switch (typeInput.getSelectedItemPosition())
        {
            case 1:
                retVal = BudgetCategory.Type.INCOME;
                break;
            case 2:
                retVal = BudgetCategory.Type.EXPENSE;
                break;
        }
        return retVal;
    }

    private String extractComment()
    {
        EditText commentInput = (EditText) findViewById(R.id.comment_input);
        if (commentInput.getText() == null) return null;
        return commentInput.getText().toString();
    }

    private void populateSpinner()
    {
        Spinner spinner = (Spinner) findViewById(R.id.spinner_type);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

}
