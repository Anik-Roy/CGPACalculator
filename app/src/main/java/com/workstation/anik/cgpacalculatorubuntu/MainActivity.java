package com.workstation.anik.cgpacalculatorubuntu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.support.v7.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // this is for action bar
    android.support.v7.app.ActionBar actionBar;
    // this is for share button on action bar
    ShareActionProvider mShareActionProvider;

    EditText creditInput, gpaInput;
    TextView cgpaOutput;
    ListView showResultList;

    ArrayList<resultInfo> showResult = new ArrayList<>();

    int flag = 1;
    Integer id = 0;
    Double totalCredit = 0D, totalGpa = 0D;
    Double changeCredit = 0D, changeGpa = 0D;

    MyDBHandler myDBHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // setting action bar
        actionBar = getSupportActionBar();
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        creditInput = (EditText) findViewById(R.id.creditInput);
        gpaInput = (EditText) findViewById(R.id.gpaInput);
        cgpaOutput = (TextView) findViewById(R.id.cgpaOutput);
        showResultList = (ListView) findViewById(R.id.showResult);

        myDBHandler  = new MyDBHandler(this, null, null, 1);

        SharedPreferences getPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String values = getPref.getString("list", "1");

        if(values.contentEquals("1")) {
            int col = Color.parseColor("#000000");
            View someView = findViewById(R.id.relativeLayout);// get Any child View
            someView.setBackgroundColor(col);

            callForPref1();

            flag = 1;
        }

        if(values.contentEquals("2")) {
            flag = 2;

            int col = Color.parseColor("#FFFFFF");
            View someView = findViewById(R.id.relativeLayout);// get Any child View
            someView.setBackgroundColor(col);

            callForPref2();
        }

        showResultList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                        TextView prevCredit = (TextView) view.findViewById(R.id.credit);
                        TextView prevGpa = (TextView) view.findViewById(R.id.gpa);

                        changeCredit = Double.parseDouble(prevCredit.getText().toString());
                        changeGpa = Double.parseDouble(prevGpa.getText().toString());

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.edit_input_popup);
                        int d = position+1;
                        String t = "Edit your entry number " + d;
                        dialog.setTitle(t);
                        final EditText creditInp = (EditText) dialog.findViewById(R.id.credit);
                        final EditText gpaInp = (EditText) dialog.findViewById(R.id.gpa);

                        creditInp.setText(changeCredit.toString());
                        gpaInp.setText(changeGpa.toString());

                        dialog.show();

                        Button b = (Button) dialog.findViewById(R.id.updateButton);
                        Button a = (Button) dialog.findViewById(R.id.cancelButton);

                        a.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if(creditInp.getText().toString().isEmpty() || gpaInp.getText().toString().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Please fill all the information!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String ci = creditInp.getText().toString();
                                String gi = gpaInp.getText().toString();

                                if(ci.length() == 1 && (ci.charAt(0) < '0' || ci.charAt(0) > '9')) {
                                    Toast.makeText(MainActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(gi.length() == 1 && (gi.charAt(0) < '0' || gi.charAt(0) > '9')) {
                                    Toast.makeText(MainActivity.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Double credit = Double.parseDouble(creditInp.getText().toString());
                                Double gpa = Double.parseDouble(gpaInp.getText().toString());

                                if(gpa > 4) {
                                    Toast.makeText(getApplicationContext(), "GPA cann't be greater than 4", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if(gpa < 0) {
                                    Toast.makeText(getApplicationContext(), "GPA cann't be less than 0", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                update(credit, gpa, view, position, 1);

                                dialog.dismiss();
                            }
                        });
                    }
                }
        );

        showResultList.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, final View view, final int position, long id) {
                        TextView prevCredit = (TextView) view.findViewById(R.id.credit);
                        TextView prevGpa = (TextView) view.findViewById(R.id.gpa);

                        changeCredit = Double.parseDouble(prevCredit.getText().toString());
                        changeGpa = Double.parseDouble(prevGpa.getText().toString());

                        final Dialog dialog = new Dialog(MainActivity.this);
                        dialog.setContentView(R.layout.delete_prev_input);
                        int d = position+1;

                        String t = "Delete your entry number " + d;

                        dialog.setTitle(t);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
                        LayoutInflater inflater = LayoutInflater.from(MainActivity.this);

                        final View dialogView = inflater.inflate(R.layout.custom_alart_title, null);
                        dialogBuilder.setView(dialogView);

                        final TextView creditInp = (TextView) dialog.findViewById(R.id.credit);
                        final TextView gpaInp = (TextView) dialog.findViewById(R.id.gpa);

                        creditInp.setText("credit: "+changeCredit.toString());
                        gpaInp.setText("gpa: "+changeGpa.toString());

                        dialog.show();

                        Button b = (Button) dialog.findViewById(R.id.deleteButton);
                        Button a = (Button) dialog.findViewById(R.id.cancelDeleteButton);

                        a.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });

                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Double credit = changeCredit;
                                Double gpa = changeGpa;

                                update(credit, gpa, view, position, 2);

                                dialog.dismiss();
                            }
                        });

                        return true;
                    }
                }
        );
    }

    public void addButtonClicked(View view) {

        String ci = creditInput.getText().toString();
        String gi = gpaInput.getText().toString();

        if(creditInput.getText().toString().isEmpty() || gpaInput.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all the information!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(ci.length() == 1 && (ci.charAt(0) < '0' || ci.charAt(0) > '9')) {
            Toast.makeText(this, "Invalid input!", Toast.LENGTH_SHORT).show();
            return;
        }

        if(gi.length() == 1 && (gi.charAt(0) < '0' || gi.charAt(0) > '9')) {
            Toast.makeText(this, "Invalid input!", Toast.LENGTH_SHORT).show();
            return;
        }

        Double credit = Double.parseDouble(creditInput.getText().toString());
        Double gpa = Double.parseDouble(gpaInput.getText().toString());

        if(gpa > 4) {
            Toast.makeText(this, "GPA cann't be greater than 4", Toast.LENGTH_SHORT).show();
            return;
        }

        if(gpa < 0) {
            Toast.makeText(this, "GPA cann't be less than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show();

        totalCredit += credit;

        totalGpa += (credit * gpa);

        Double cgpa = totalGpa / totalCredit;

        String s;

        s = "CGPA = " + String.format( Locale.getDefault(),"%.3f", cgpa);

        cgpaOutput.setText(s);

        gpaInput.setText("");

        id++;

        showResult.add(new resultInfo(id, credit, gpa, credit*gpa));

        CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);

        showResultList.setAdapter(aniksAdapter);
    }

    public void update(Double credit, Double gpa, final View view, final int position, int ser) {

        if(ser == 1) {
            totalCredit -= changeCredit;
            totalGpa -= (changeCredit * changeGpa);

            totalCredit += credit;
            totalGpa += (credit * gpa);

            Double cgpa = totalGpa / totalCredit;

            String s;

            s = "CGPA = " + String.format(Locale.getDefault(), "%.3f", cgpa);

            cgpaOutput.setText(s);

            resultInfo result = new resultInfo(position + 1, credit, gpa, credit * gpa);
            showResult.set(position, result);

            showResultList = (ListView) findViewById(R.id.showResult);

            CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);

            showResultList.setAdapter(aniksAdapter);

            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show();
        }

        else {
            totalCredit -= credit;
            totalGpa -= (credit * gpa);

            Double cgpa;

            if(totalCredit == 0)
                cgpa = 0.000;

            else
                cgpa = totalGpa / totalCredit;

            String s;

            s = "CGPA = " + String.format(Locale.getDefault(), "%.3f", cgpa);

            cgpaOutput.setText(s);

            showResult.remove(position);
            id--;

            int sz = showResult.size();

            for(int i = position; i < sz; i++) {
                resultInfo res = showResult.get(i);
                res.setId(res.getId()-1);
                showResult.set(i, res);
            }

            if(sz == 0) {
                id = 0;
            }
            showResultList = (ListView) findViewById(R.id.showResult);

            CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);

            showResultList.setAdapter(aniksAdapter);

            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
        }
    }

    public void saveButtonClicked(View view) {
        if(showResult.size() == 0) {
            Toast.makeText(MainActivity.this, "Nothing to save!", Toast.LENGTH_SHORT).show();
            return;
        }
        //myDBHandler.deleteDataBase();
        myDBHandler.addProduct(showResult);
        Toast.makeText(MainActivity.this, "Saved", Toast.LENGTH_SHORT).show();
    }

    public void clearButtonClicked(View view) {
        showResult.clear();
        id = 0;

        Double cgpa = 0.000;
        totalCredit = 0D;
        totalGpa = 0D;
        cgpaOutput = (TextView) findViewById(R.id.cgpaOutput);

        String s;

        s = "CGPA = " + String.format(Locale.getDefault(), "%.3f", cgpa);

        cgpaOutput.setText(s);

        showResultList = (ListView) findViewById(R.id.showResult);

        CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);

        showResultList.setAdapter(aniksAdapter);

        Toast.makeText(this, "All entry deleted!", Toast.LENGTH_SHORT).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater blowup = getMenuInflater();
        blowup.inflate(R.menu.cool_menu, menu);

        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        //create the sharing intent
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "https://play.google.com/store/apps/details?id=com.workstation.anik.cgpacalculatorubuntu";
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Subject");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        //then set the sharingIntent
        mShareActionProvider.setShareIntent(sharingIntent);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMe:
                try {
                    Class myClass = Class.forName("com.workstation.anik.cgpacalculatorubuntu.AboutMe");
                    Intent intent = new Intent(MainActivity.this, myClass);
                    startActivity(intent);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.preferences:
                try{
                    Class myClass = Class.forName("com.workstation.anik.cgpacalculatorubuntu.Pref");
                    Intent intent = new Intent(MainActivity.this, myClass);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.savedResult:
                if(myDBHandler.databaseToList().size() <= 0)
                    Toast.makeText(this, "No saved result found!", Toast.LENGTH_SHORT).show();

                else {
                    Intent intent = new Intent(MainActivity.this, SavedResult.class);
                    startActivity(intent);
                }
                break;

            case R.id.deleteSavedResult:
                myDBHandler.deleteDataBase();
                break;

            case R.id.exit:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void callForPref1() {
        TextView initial = (TextView) findViewById(R.id.textView2);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));

        initial = (TextView)findViewById(R.id.cgpaOutput);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));

        initial = (TextView) findViewById(R.id.serialName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));


        initial = (TextView) findViewById(R.id.creditName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));


        initial = (TextView) findViewById(R.id.gpaName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));


        initial = (TextView) findViewById(R.id.totalgpaName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));

    }

    public void callForPref2(){
        TextView initial = (TextView) findViewById(R.id.textView2);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        initial = (TextView)findViewById(R.id.cgpaOutput);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        initial = (TextView) findViewById(R.id.serialName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


        initial = (TextView) findViewById(R.id.creditName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


        initial = (TextView) findViewById(R.id.gpaName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


        initial = (TextView) findViewById(R.id.totalgpaName);
        initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences getPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String values = getPref.getString("list", "1");

        if(values.contentEquals("1")) {
            flag = 1;
            int col = Color.parseColor("#000000");
            View someView = findViewById(R.id.relativeLayout);// get Any child View
            someView.setBackgroundColor(col);

            showResultList = (ListView) findViewById(R.id.showResult);

            CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, 1);
            showResultList.setAdapter(aniksAdapter);

            TextView initial = (TextView) findViewById(R.id.textView2);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));

            initial = (TextView)findViewById(R.id.cgpaOutput);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));

            initial = (TextView) findViewById(R.id.serialName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));


            initial = (TextView) findViewById(R.id.creditName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));


            initial = (TextView) findViewById(R.id.gpaName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));


            initial = (TextView) findViewById(R.id.totalgpaName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.myColor));

        }

        else {
            flag = 2;
            int col = Color.parseColor("#FFFFFF");
            View someView = findViewById(R.id.relativeLayout);// get Any child View
            someView.setBackgroundColor(col);

            showResultList = (ListView) findViewById(R.id.showResult);

            CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, 2);
            showResultList.setAdapter(aniksAdapter);

            TextView initial = (TextView) findViewById(R.id.textView2);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

            initial = (TextView)findViewById(R.id.cgpaOutput);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

            initial = (TextView) findViewById(R.id.serialName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


            initial = (TextView) findViewById(R.id.creditName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


            initial = (TextView) findViewById(R.id.gpaName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));


            initial = (TextView) findViewById(R.id.totalgpaName);
            initial.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorAccent));

        }
    }
}