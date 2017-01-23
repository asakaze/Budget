package pl.itmation.budget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import java.util.ArrayList;
import java.util.Collections;


public class CategoryActivity extends AppCompatActivity
{
    public static final int CREATE_CATEGORY = 1;
    public static final int CREATE_CATEGORY_RESP = 11;
    public static final int MODIFY_CATEGORY = 2;
    public static final int MODIFY_CATEGORY_RESP = 12;
    public static final int DELETE_CATEGORY_RESP = 13;

    private static final String LOGTAG = CategoryActivity.class.getSimpleName();
    private ArrayList<BudgetCategory> categories = null;
    private int currentPosition = 0;
    private ArrayAdapter<BudgetCategory> categoryAdapter = null;
    private DatabaseHandler db = null;

    static class CategoryViewHolder
    {
        TextView name;
        TextView value;
        TextView type;
        TextView comment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = ((App)getApplication()).db;
        categories = db.getAllCategories();
        Collections.sort(categories);
        categoryAdapter = new ArrayAdapter<BudgetCategory>(this, 0, categories)
            {
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    BudgetCategory currentCategory = categories.get(position);

                    if(convertView == null)
                    {
                        convertView = getLayoutInflater().inflate(R.layout.category_item, null, false);
                        CategoryViewHolder viewHolder = new CategoryViewHolder();
                        viewHolder.name = (TextView)convertView.findViewById(R.id.category_item_name);
                        viewHolder.value = (TextView)convertView.findViewById(R.id.category_item_value);
                        viewHolder.type = (TextView)convertView.findViewById(R.id.category_item_type);
                        viewHolder.comment = (TextView)convertView.findViewById(R.id.category_item_comment);
                        convertView.setTag(viewHolder);
                    }

                    TextView categoryName = ((CategoryViewHolder)convertView.getTag()).name;
                    TextView categoryValue = ((CategoryViewHolder)convertView.getTag()).value;
                    TextView categoryType = ((CategoryViewHolder)convertView.getTag()).type;
                    TextView categoryComment = ((CategoryViewHolder)convertView.getTag()).comment;

                    categoryName.setText(currentCategory.getName());
                    int value = currentCategory.getDefaultValue();
                    categoryValue.setText(getString(R.string.desc_value));
                    if(value != 0)
                    {
                        categoryValue.append(String.valueOf(currentCategory.getDefaultValue())
                                + " " + getString(R.string.pln));
                    }
                    else
                    {
                        categoryValue.append(getString(R.string.none));
                    }

                    categoryType.setText(getString(R.string.desc_type));
                    BudgetCategory.Type type = currentCategory.getDefaultType();
                    if(type != null)
                    {
                        if(type == BudgetCategory.Type.EXPENSE)
                        {
                            categoryType.append(" " + getString(R.string.expense));
                        }
                        else if(type == BudgetCategory.Type.INCOME)
                        {
                            categoryType.append(" " + getString(R.string.income));
                        }
                    }
                    else
                    {
                        categoryType.append(" " + getString(R.string.none));
                    }

                    String comment = currentCategory.getComment();
                    categoryComment.setText(getString(R.string.desc_comment) + " " );
                    if(comment == null || comment.equals(""))
                    {
                        categoryComment.append("Brak");
                    }
                    else
                    {
                        categoryComment.append(comment);
                    }
                    return convertView;
                }
            };
        ListView list = (ListView) findViewById(R.id.category_list);
        list.setAdapter(categoryAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long rowId)
            {
                BudgetCategory currentCategory = categories.get(position);
                currentPosition = position;
                Intent intent = new Intent(CategoryActivity.this, ManageCategoryActivity.class);
                intent.putExtra("request_code", MODIFY_CATEGORY);
                intent.putExtra("editable_category", currentCategory);
                startActivityForResult(intent, MODIFY_CATEGORY);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.add_category:
                Intent intent = new Intent(CategoryActivity.this, ManageCategoryActivity.class);
                intent.putExtra("request_code", CREATE_CATEGORY);
                startActivityForResult(intent, CREATE_CATEGORY);
                return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == CREATE_CATEGORY)
        {
            if(resultCode == CREATE_CATEGORY_RESP)
            {
                BudgetCategory newCategory = data.getExtras().getParcelable("new_category");
                Log.d(LOGTAG, "Added item " +  newCategory.toString());
                db.createCategory(newCategory);
                categories.add(newCategory);
                Collections.sort(categories);
                categoryAdapter.notifyDataSetChanged();
            }
        }
        else if(requestCode == MODIFY_CATEGORY)
        {
            if(resultCode == MODIFY_CATEGORY_RESP)
            {
                Log.d(LOGTAG, "Modified position " + currentPosition);
                BudgetCategory modifiedCategory = data.getExtras().getParcelable("modified_category");
                db.updateCategory(modifiedCategory);
                categories.set(currentPosition, modifiedCategory);
                Collections.sort(categories);
                categoryAdapter.notifyDataSetChanged();
            }
            else if(resultCode == DELETE_CATEGORY_RESP)
            {
                Log.d(LOGTAG, "Deleted position " + currentPosition);
                db.deleteCategory(categories.get(currentPosition).getName());
                categories.remove(currentPosition);
                categoryAdapter.notifyDataSetChanged();
            }
        }
    }
}
