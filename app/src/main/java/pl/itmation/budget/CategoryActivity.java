package pl.itmation.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

public class CategoryActivity extends AppCompatActivity {

    public static final int CREATE_CATEGORY = 1;
    public static final int MODIFY_CATEGORY = 1;
    private ArrayList<BudgetCategory> categories;

    static class CategoryViewHolder{
        TextView name;
        TextView value;
        TextView type;
        TextView comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categories = new ArrayList<BudgetCategory>();
        ArrayAdapter<BudgetCategory> categoryAdapter =
                new ArrayAdapter<BudgetCategory>(this, 0, categories) {
                    @Override
                    public View getView(int position,
                                        View convertView,
                                        ViewGroup parent) {
                        BudgetCategory currentCategory = categories.get(position);

                        if(convertView == null) {
                            convertView = getLayoutInflater()
                                    .inflate(R.layout.category_item, null, false);
                            CategoryViewHolder viewHolder = new CategoryViewHolder();
                            viewHolder.name =
                                    (TextView)convertView.findViewById(R.id.category_item_name);
                            viewHolder.value =
                                    (TextView)convertView.findViewById(R.id.category_item_value);
                            viewHolder.type =
                                    (TextView)convertView.findViewById(R.id.category_item_type);
                            viewHolder.comment =
                                    (TextView)convertView.findViewById(R.id.category_item_comment);

                            convertView.setTag(viewHolder);
                        }

                        TextView categoryName =
                                ((CategoryViewHolder)convertView.getTag()).name;
                        TextView categoryValue =
                                ((CategoryViewHolder)convertView.getTag()).value;
                        TextView categoryType =
                                ((CategoryViewHolder)convertView.getTag()).type;
                        TextView categoryComment =
                                ((CategoryViewHolder)convertView.getTag()).comment;

                        categoryName.setText(currentCategory.getName());
                        categoryValue.setText(currentCategory.getDefaultValue().toString());
                        categoryType.setText(currentCategory.getDefaultType().toString());
                        categoryComment.setText(currentCategory.getComment());
                        return convertView;
                    }
                };
        ListView list = (ListView) findViewById(R.id.category_list);
        list.setAdapter(categoryAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,
                                    View view, int position, long rowId) {
                BudgetCategory currentCategory = categories.get(position);
                Intent intent = new Intent(CategoryActivity.this, ManageCategoryActivity.class);
                intent.putExtra("request_code", MODIFY_CATEGORY);
                intent.putExtra("editable_category", currentCategory);
                startActivityForResult(intent, MODIFY_CATEGORY);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.add_category:
                Intent intent = new Intent(CategoryActivity.this, ManageCategoryActivity.class);
                intent.putExtra("request_code", CREATE_CATEGORY);
                startActivityForResult(intent, CREATE_CATEGORY);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CREATE_CATEGORY) {
            if (resultCode == RESULT_OK) {
                BudgetCategory newCategory = data.getExtras().getParcelable("new_category");
                Toast.makeText(this, newCategory.toString(),
                        Toast.LENGTH_LONG).show();
                categories.add(newCategory);
            }
        }
    }
}
