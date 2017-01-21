package pl.itmation.budget;

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

    private BudgetCategory category = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        populateSpinner();
        createListeners();
    }

    private void createListeners() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, category.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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