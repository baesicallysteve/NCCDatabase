package edu.ncc.nccdepartmentdatabase;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class CustomQueryActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText customquery;
    private  Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_query);
        customquery = (EditText) findViewById(R.id.textView);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.enter_button:
                if(customquery.toString() == ""){
                    Toast toast = Toast.makeText(getApplicationContext(), "Must Enter a Department!!!!", Toast.LENGTH_LONG);
                    toast.show();
                }
                else{
                    intent = new Intent();
                    intent.putExtra("DEPARTMENTQUERY", customquery.getText().toString());
                    setResult(RESULT_OK, intent);
                    finish();
          }
                break;


        }
    }
}
