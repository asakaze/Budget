package pl.itmation.budget;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphActivity extends AppCompatActivity
{
    static class ViewHolder
    {
        Spinner yearSelection;
        BarChart monthlyChart;
    }

    private static final String LOGTAG = GraphActivity.class.getSimpleName();
    private ArrayList<BudgetEntry> entries = null;
    private DatabaseHandler db = null;
    private ArrayAdapter<String> yearAdapter = null;
    private ViewHolder viewHolder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = ((App) getApplication()).db;
        entries = db.getAllEntries();
        loadFieldsFromResources();
        populateSpinners();
        addListeners();
        createMonthlyGraph();
    }

    private void loadFieldsFromResources()
    {
        viewHolder = new ViewHolder();
        viewHolder.monthlyChart = (BarChart) findViewById(R.id.chart_monthly);
        viewHolder.yearSelection = (Spinner) findViewById(R.id.content_graph_year_selection_spinner);
    }

    private void populateSpinners()
    {
        ArrayList<String> years = new ArrayList<>();
        Set<String> uniqueYears = new HashSet<>();
        for(BudgetEntry entry : entries)
        {
            String year = String.valueOf(entry.getDate().get(Calendar.YEAR));
            uniqueYears.add(year);
        }

        years.addAll(uniqueYears);
        Collections.sort(years, Collections.reverseOrder());

        yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewHolder.yearSelection.setAdapter(yearAdapter);
    }

    private void addListeners()
    {
        viewHolder.yearSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id)
            {
                createMonthlyGraph();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView)
            {
                return;
            }
        });
    }

    private void createMonthlyGraph()
    {
        viewHolder.monthlyChart.setDragEnabled(true);
        List<BarEntry> graphEntries = new ArrayList<BarEntry>();
        HashMap<Integer, Integer> monthBalance = getMonthlyBalance();

        for(Integer month = 1; month <= 12; month++)
        {
            graphEntries.add(new BarEntry(month, monthBalance.get(month)));
        }

        BarDataSet dataSet = new BarDataSet(graphEntries, "Miesięczny balans");
        BarData lineData = new BarData(dataSet);
        viewHolder.monthlyChart.setData(lineData);
        viewHolder.monthlyChart.invalidate();
    }

    private HashMap<Integer, Integer> getMonthlyBalance()
    {
        String selectedYear = viewHolder.yearSelection.getSelectedItem().toString();
        Log.d(LOGTAG, "Selected year = " + selectedYear);
        HashMap<Integer, Integer> monthBalance = new HashMap<>();

        for(Integer month = 1; month <= 12; month++)
        {
            monthBalance.put(month, 0);
        }
        Log.v(LOGTAG, "Monthly balance = " + monthBalance.toString());
        for(BudgetEntry entry : entries)
        {
            String year = String.valueOf(entry.getDate().get(Calendar.YEAR));
            if(year.equals(selectedYear))
            {
                Log.v(LOGTAG, "Entry for year " + year);
                Integer month = entry.getDate().get(Calendar.MONTH) + 1;
                Log.v(LOGTAG, "Entry for month " + String.valueOf(month));
                Integer currentBalance = monthBalance.get(month);
                Log.v(LOGTAG, "Balance before " + currentBalance.toString());

                if(entry.getType() == BudgetCategory.Type.EXPENSE)
                {
                    currentBalance -= entry.getValue();
                }
                else
                {
                    currentBalance += entry.getValue();
                }
                Log.v(LOGTAG, "Current balance " + currentBalance.toString());
                monthBalance.put(month, currentBalance);
            }
        }
        Log.d(LOGTAG, "Monthly balance = " + monthBalance.toString());

        return monthBalance;
    }
}
