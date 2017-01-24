package pl.itmation.budget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphShareActivity extends AppCompatActivity
{
    static class ViewHolder
    {
        Spinner shareSpinner;
        PieChart shareChart;
    }

    private static final String LOGTAG = GraphMonthlyActivity.class.getSimpleName();
    private ArrayList<BudgetEntry> entries = null;
    private DatabaseHandler db = null;
    private ArrayAdapter<CharSequence> typeAdapter = null;
    private ViewHolder viewHolder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_share);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = ((App) getApplication()).db;
        entries = db.getAllEntries();
        loadFieldsFromResources();
        populateSpinners();
        addListeners();
        createShareGraph();
    }

    private void loadFieldsFromResources()
    {
        viewHolder = new ViewHolder();
        viewHolder.shareSpinner = (Spinner) findViewById(R.id.content_graph_type_selection_spinner);
        viewHolder.shareChart = (PieChart) findViewById(R.id.chart_share);
    }

    private void populateSpinners()
    {
        populateShareSpinner();
    }

    private void populateShareSpinner()
    {
        typeAdapter =
                ArrayAdapter.createFromResource(this, R.array.type_array_no_null, android.R.layout.simple_spinner_item);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.shareSpinner.setAdapter(typeAdapter);
    }

    private void addListeners()
    {
        viewHolder.shareSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                createShareGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                return;
            }
        });
    }

    private void createShareGraph()
    {
        viewHolder.shareChart.setTouchEnabled(true);
        viewHolder.shareChart.setUsePercentValues(true);
        //List<BarEntry> graphEntries = new ArrayList<BarEntry>();
        HashMap<String, Integer> shares = getShares();
        Log.d(LOGTAG, "Shares: " + shares.toString());


        Set<String> categories = shares.keySet();
        ArrayList<PieEntry> values = new ArrayList<PieEntry>();

        int i = 0;
        for(String category : categories)
        {
            values.add(new PieEntry(shares.get(category), category));
            i++;
        }

        PieDataSet dataSet = new PieDataSet(values, "");
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS) colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS) colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS) colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS) colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        viewHolder.shareChart.setData(data);
        viewHolder.shareChart.setEntryLabelColor(Color.BLACK);
        viewHolder.shareChart.invalidate();
    }

    private HashMap<String, Integer> getShares()
    {
        BudgetCategory.Type
                selectedType =
                BudgetCategory.Type.values()[viewHolder.shareSpinner.getSelectedItemPosition()];

        Log.d(LOGTAG, "Selected type = " + selectedType.name());
        HashMap<String, Integer> cummulativeForCategory = new HashMap<>();

        for(BudgetEntry entry : entries)
        {
            if(selectedType.equals(entry.getType()))
            {
                cummulativeForCategory.put(entry.getCategory(), 0);
            }
        }
        Log.d(LOGTAG, "Clear map = " + cummulativeForCategory.toString());
        for(BudgetEntry entry : entries)
        {
            if(selectedType.equals(entry.getType()))
            {
                Integer current = cummulativeForCategory.get(entry.getCategory());
                current += entry.getValue();
                cummulativeForCategory.put(entry.getCategory(), current);
            }
        }

        return cummulativeForCategory;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_graph, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId())
        {
            case R.id.open_monthly_graph:
            {
                Intent intent = new Intent(GraphShareActivity.this, GraphMonthlyActivity.class);
                startActivity(intent);
                finish();
                return true;
            }
            case R.id.open_share_graph:
            {
                return true;
            }
        }
        return false;
    }
}
