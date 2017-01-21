package pl.itmation.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import static android.widget.AdapterView.INVALID_POSITION;

public class ManageCategoryActivity extends AppCompatActivity {

    private enum Mode{
        CREATE, MODIFY
    }
    private Mode mode;
    private BudgetCategory category = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();
        if(data.getIntExtra("request_code", 0) == CategoryActivity.CREATE_CATEGORY)
        {
            mode = Mode.CREATE;
        }
        else if(data.getIntExtra("request_code", 0) == CategoryActivity.MODIFY_CATEGORY)
        {
            mode = Mode.MODIFY;
            BudgetCategory editableCategory = data.getExtras().getParcelable("editable_category");
            populateFields(editableCategory);
        }

        populateSpinner();
        createListeners();
    }

    private void createListeners() {

        Button button = (Button) findViewById(R.id.manage_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCategory();
            }
        });
    }

    private void createCategory() {
        String name = extractName();
        if (name == null)
        {
            return;
        }

        Integer value = extractValue();
        BudgetCategory.Type type = extractType();
        String comment = extractComment();
        category = new BudgetCategory.BudgetCategoryBuilder(name).defaultType(type).defaultValue(value).comment(comment).build();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("new_category", category);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void populateFields(BudgetCategory current)
    {
        EditText name = (EditText) findViewById(R.id.name_input);
        name.setText(current.getName());

        Spinner type = (Spinner) findViewById(R.id.spinner_type);
        int position = 0;
        if(current.getDefaultType() == BudgetCategory.Type.INCOME)
        {
            position = 1;
        }
        else if(current.getDefaultType() == BudgetCategory.Type.EXPENSE)
        {
            position = 2;
        }

        type.setSelection(position);

        EditText value = (EditText) findViewById(R.id.value_input);
        value.setText(current.getDefaultValue().toString());
        EditText comment = (EditText) findViewById(R.id.comment_input);
        comment.setText(current.getComment());

        Button button = (Button) findViewById(R.id.manage_button);
        button.setText(getString(R.string.button_modify));

    }

    private String extractName()
    {
        EditText nameInput = (EditText) findViewById(R.id.name_input);
        String name = nameInput.getText().toString();
        if (TextUtils.isEmpty(name))
        {
            nameInput.setError(getString(R.string.error_field_required));
            nameInput.requestFocus();
            return null;
        }
        return nameInput.getText().toString();
    }

    private Integer extractValue()
    {
        EditText valueInput = (EditText) findViewById(R.id.value_input);
        if (valueInput.getText() == null) return null;
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
