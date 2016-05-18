package com.mehrnaz.shoplist;
import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.lang.Integer.parseInt;

public class ShowList extends Activity {
    private DBAdapter db;
    TextView report;
    TableLayout table_layout;
    private String[] headings = {"Id#", "Item", "Store","Cost"}; //headings for the table
    private boolean locked;
    Button bLocked;
    Button bDelete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list);
        table_layout = (TableLayout) findViewById(R.id.tableLayout1);
        report = (TextView) findViewById(R.id.showSavingMonth);
        bLocked = (Button) findViewById(R.id.buttonLock);
        bDelete = (Button) findViewById(R.id.buttonDeleteAll);
        locked = false;
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
                CopyDB(getBaseContext().getAssets().open("shopdb"),
                        new FileOutputStream(destPath + "/ShopDB"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        report.setText("Shopping List:\n\n");
        setTable();
        //populate the table view with data retrieved from database for specific month

    }

    private void setTable() {
        db.open();
        Cursor c = db.getList();
        int counter = 0;
        //set the headings
        TableRow hRow = new TableRow(this);
        hRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT));
        for (int i = 0; i < 4; ++i) {
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT));
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(18);
            tv.setPadding(0, 5, 0, 5);
            tv.setBackgroundResource(R.drawable.header_shape);
            tv.setText(headings[i]);

            hRow.addView(tv);
        }
        table_layout.addView(hRow);
        if (c.moveToFirst()) {

            do {
                //set the item rows
                TableRow row = new TableRow(this);
                row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                        LayoutParams.WRAP_CONTENT));
                row.setClickable(true);
                row.setId(100 + counter++);
                row.setOnClickListener(
                        new View.OnClickListener() {
                            public void onClick(View view) {
                                //if clicked and unlocked, then delete the row
                                if (!locked) {
                                    TableRow t = (TableRow) view;
                                    TextView firstTextView = (TextView) t.getChildAt(0);
                                    String firstText = firstTextView.getText().toString();
                                    deleteItem(firstText);
                                }
                            }
                        });
                for (int i = 0; i < 4; ++i) {
                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT));
                    tv.setGravity(Gravity.CENTER);
                    tv.setTextSize(18);
                    tv.setPadding(0, 5, 0, 5);
                    tv.setBackgroundResource(R.drawable.cell_shape);
                    tv.setText(c.getString(i));

                    row.addView(tv);
                }
                table_layout.addView(row);
            } while (c.moveToNext());
        }
        db.close();
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
        getMenuInflater().inflate(R.menu.menu_show_list, menu);
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

    public void deleteItem(String s) {
        //delete a particular item based on the id passed in
        db.open();
        db.deleteItem(parseInt(s));
        db.close();
        //refresh the table
        table_layout.removeAllViews();
        setTable();
    }


    public void onListButtonClick(View view) {
        Cursor c;
        switch(view.getId())
        {
            case R.id.buttonLock:
                //---locks/unlocks the table and the delete all button to prevent/allow deletion
                if(bLocked.getText().toString().equals("Lock")) {
                    locked = true;
                    bLocked.setText("Unlock");
                    bDelete.setEnabled(false);
                }
                else if(bLocked.getText().toString().equals("Unlock")) {
                    locked = false;
                    bLocked.setText("Lock");
                    bDelete.setEnabled(true);
                }
                break;
            //deletes all the items
            case R.id.buttonDeleteAll:
                db.open();
                db.deleteAll();
                db.close();
                table_layout.removeAllViews();
                setTable();
                break;

        }
    }
}
