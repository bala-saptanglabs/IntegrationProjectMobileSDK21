package com.example.integrationprojectapi21;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.SigningInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.integrationprojectapi21.util.CustomAdapter;
import com.example.mysdklib.Excal;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * MainActivity Class is an Activity that contains single input box,
 * By entering an URL for GET request (<a href="https://dog.ceo/api/breeds/image/random">...</a>) and pressing the button
 * will displays you json result in cardview
 */
public class MainActivity extends AppCompatActivity {

    CustomAdapter customAdapterObj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        // set a LinearLayoutManager with default vertical orientation
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayList<String> LoadingKeys = new ArrayList<>();
        LoadingKeys.add("No data");


        ArrayList<String> Loadingvals = new ArrayList<>();
        Loadingvals.add("...");



        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        customAdapterObj = new CustomAdapter(MainActivity.this, LoadingKeys, Loadingvals);
        recyclerView.setAdapter(customAdapterObj); // set the Adapter to RecyclerView


    }

    /**
     *clickButtonListener method is listening for button presses in the MainActivity
     * make a GET request to the URL and update the UI to show json response in cardview
     * and a toast of api21 app clicked will be appeared
     */
    public void clickButtonListener(View view) {
        Toast.makeText(this, "Api 21  " +
                "app  clicked", Toast.LENGTH_SHORT).show();
        EditText simpleEditText = (EditText) findViewById(R.id.simpleEditText);
        String editTextValue = simpleEditText.getText().toString();

        NetworkRequests.sendRequest(editTextValue,this);
    }

    public void execButtonListener(View view){
        Toast.makeText(this, "Api 21 command  " +
                "app  clicked", Toast.LENGTH_SHORT).show();
        EditText simpleEditText = (EditText) findViewById(R.id.simpleEditText);
        String editTextValue = simpleEditText.getText().toString();
        String output = executeCommand(editTextValue);
        updateListCommandExecution(editTextValue,output);

    }

    public static String executeCommand(String command) {
        StringBuilder output = new StringBuilder();

        try {
            // Execute the command using Runtime
            Process process = Runtime.getRuntime().exec(command);

            // Read the output from the command
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Read the error stream (if any)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // Wait for the process to complete
            process.waitFor();
        } catch (Exception e) {
            return e.toString();
        }

        return output.toString();
    }

    /**
     *Update the View as CardView with json result from GET request
     */
    public void updateList(String jsonData){
        try{
            ArrayList<String> keys_list = new ArrayList<>();
            ArrayList<String> values_list = new ArrayList<>();
            JSONObject obj = new JSONObject(jsonData);
            for (Iterator<String> it = obj.keys(); it.hasNext(); ) {
                String key = it.next();
                String val = obj.getString(key);

                keys_list.add(key);
                values_list.add(val);

//                    Log.d("JSON","key = "+key+"val = "+val);
            }

            runOnUiThread(()->{
                this.customAdapterObj.onLoadedList(keys_list,values_list);
            });

        }catch (Exception e){
            Log.d("json",e.toString());
        }
    }
    public void updateListCommandExecution(String cmd,String out){

        ArrayList<String> keys_list = new ArrayList<>();
        ArrayList<String> values_list = new ArrayList<>();
       keys_list.add(cmd);
       values_list.add(out);

        runOnUiThread(()->{
            this.customAdapterObj.onLoadedList(keys_list,values_list);
        });
    }

}