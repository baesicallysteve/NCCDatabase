package edu.ncc.nccdepartmentdatabase;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class MainActivity extends ListActivity {

    private DepartmentInfoSource datasource;
    private ArrayAdapter<DepartmentEntry> adapter;
    private static final int QUERY_REQUEST = 1;
    private String resultQuery = "";
    private Intent customQueryIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        customQueryIntent = new Intent(this, CustomQueryActivity.class);
        datasource = new DepartmentInfoSource(this);
        datasource.open();

        List<DepartmentEntry> values = datasource.getAllDepartments();

        // add departments to the database if it is currently empty
        if (values.isEmpty())
        {
            new ParseURL().execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void startSecondActivity(){
        customQueryIntent = new Intent(getApplicationContext(),CustomQueryActivity.class);
        startActivityForResult(customQueryIntent,42);
    }
    public void onClick(View view) {
        DepartmentEntry dept;
        List<DepartmentEntry> values;
        switch (view.getId()) {
            case R.id.show:
                values = datasource.getAllDepartments();
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
                setListAdapter(adapter);
                break;
            case R.id.cluster:
                values = datasource.queryBy(new String[] {"%A Cluster%", "%Cluster A%", "%B Cluster%","%Cluster B%"
                        ,"%C Cluster%","%Cluster C%","%D Cluster%","%Cluster D%", "%Building A%","%A Building%","%Building B%", "%B Building%",
                        "%Building C%", "%C Building%", "%Building D%", "%D Building%"});
                Log.i("CLUSTERBUTTON***", values.toString());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
                break;
            case R.id.dean:
                values = datasource.queryBy(new String[] {"%Dean%"});
                Log.i("DEANBUTTON***", values.toString());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
                break;
            case R.id.center:
                values = datasource.queryBy(new String[] {"%Center%"});
                Log.i("CENTERBUTTON***", values.toString());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
                break;
            case R.id.tower:
                values = datasource.queryBy(new String[] {"%Tower%"});
                Log.i("TOWERBUTTON***", values.toString());
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
                break;
            case R.id.custom:
                startSecondActivity();
                Log.i("RESULT***", resultQuery);
                values = datasource.queryBy(new String[]{ "Result","%"+resultQuery+"%"});
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, values);
                break;
        }
        setListAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void onDestroy()
    {
        datasource.close();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data){
        if(requestCode == 42){
            if(resultCode == RESULT_OK){
                resultQuery = data.getStringExtra("DEPARTMENTQUERY");
            }
        }
    }

    private class ParseURL extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String str;
            String deptName;
            String deptPhone;
            String deptLocation;
            Document doc;
            int count = 0;

            try {
                // connect to the webpage
                doc = Jsoup.connect("http://www.ncc.edu/contactus/deptdirectory.shtml").get();

                // find the body of the webpage
                Elements tableEntries = doc.select("tbody");
                for (Element e : tableEntries)
                {
                    // look for a row in the table
                    Elements trs = e.getElementsByTag("tr");

                    // for each element in the row (there are 5)
                    for (Element e2 : trs)
                    {
                        // get the table descriptor
                        Elements tds = e2.getElementsByTag("td");

                        // ignore the first row
                        if (count > 0) {
                            // get the department name and remove the formatting tags
                            deptName = tds.get(0).text();

                            // get the department phone number
                            deptPhone = tds.get(1).text();

                            // get the department location
                            deptLocation = tds.get(4).text();

                            datasource.addDept(deptName, deptLocation, deptPhone);
                        }
                        count++;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            //if you had a ui element, you could display the title
            Log.d("PARSING", "async task has completed");
        }
    }
}