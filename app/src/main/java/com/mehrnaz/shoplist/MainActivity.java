package com.mehrnaz.shoplist;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private DBAdapter db;
    private EditText nameText;
    private Spinner storeSpinner;
    private EditText costText;
    private EditText saveText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        nameText = (EditText) findViewById(R.id.nametext);
        storeSpinner = (Spinner) findViewById(R.id.storespinner);
        costText = (EditText) findViewById(R.id.costText);
        saveText = (EditText) findViewById(R.id.savText);
        db = new DBAdapter(this);
        // get the existing database file or from assets folder if doesn't exist
        try {
            String destPath = "/data/data/" + getPackageName() +
                    "/databases";
            File f = new File(destPath);
            if (!f.exists()) {
                f.mkdirs();
                f.createNewFile();

                //---copy the db from the assets folder into
                // the databases folder---
                CopyDB(getBaseContext().getAssets().open("savingdb"),
                        new FileOutputStream(destPath + "/SavingDB"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void CopyDB(InputStream inputStream,
                       OutputStream outputStream) throws IOException {
        //---copy 1K bytes at a time---
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onButtonClick(View view) {
        Cursor c;
        switch(view.getId())
        {
            case R.id.buttonAdd:
                //---add an item---
                String addName ="";
                String addStore ="";
                double addCost = 0.0;
                double addSaving = 0.0;

                addName =nameText.getText().toString();
                addStore =storeSpinner.getSelectedItem().toString();
                addCost = Double.parseDouble(costText.getText().toString());
                addSaving = Double.parseDouble(saveText.getText().toString());


                db.open();
                long id = db.insertItem(addName, addStore, addCost, addSaving);
                Toast.makeText(this,
                        "Item " + addName +" added to list!",
                        Toast.LENGTH_LONG).show();
                db.close();
                costText.setText("");
                saveText.setText("");
                break;
            case R.id.buttonShowAll:
                //---opens another page to show the shopping list---
                Intent i=new Intent(this, ShowList.class);
                startActivity(i);
                break;

        }
    }
}
