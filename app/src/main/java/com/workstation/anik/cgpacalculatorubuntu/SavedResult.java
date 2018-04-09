package com.workstation.anik.cgpacalculatorubuntu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by anik on 1/6/18.
 */

public class SavedResult extends AppCompatActivity {

    android.support.v7.app.ActionBar actionBar;

    MyDBHandler myDBHandler;
    ArrayList<resultInfo> showResult = new ArrayList<>();
    ListView showResultList;
    TextView cgpaOutput;
    EditText creditInput, gpaInput;

    Double totalGpa = 0D, totalCredit = 0D;
    int flag = 1, id = 0;
    Double changeCredit = 0D, changeGpa = 0D;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saved_result);

        actionBar = getSupportActionBar();

        actionBar.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        actionBar.setTitle("Your Saved Result");
        SharedPreferences getPref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String values = getPref.getString("list", "1");

        if (values.contentEquals("1")) {
            int col = Color.parseColor("#000000");
            View someView = findViewById(R.id.relativeLayout);// get Any child View
            someView.setBackgroundColor(col);
            flag = 1;
            callForPref1();
        }

        if (values.contentEquals("2")) {
            flag = 2;

            int col = Color.parseColor("#FFFFFF");
            View someView = findViewById(R.id.relativeLayout);// get Any child View
            someView.setBackgroundColor(col);
            callForPref2();
        }

        creditInput = (EditText) findViewById(R.id.creditInput);
        gpaInput = (EditText) findViewById(R.id.gpaInput);

        cgpaOutput = (TextView) findViewById(R.id.cgpaOutput);
        showResultList = (ListView) findViewById(R.id.showResult);
        myDBHandler = new MyDBHandler(this, null, null, 1);

        ArrayList<resultInfo> mySavedResult = myDBHandler.databaseToList();

        //clearing current listview
        showResult.clear();
        CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);
        showResultList.setAdapter(aniksAdapter);
        totalGpa = 0D;
        totalCredit = 0D;

        // showing saved result
        showResultList = (ListView) findViewById(R.id.showResult);
        aniksAdapter = new CustomAdapter(this, mySavedResult, flag);
        showResultList.setAdapter(aniksAdapter);

        showResult = mySavedResult;

        int sz = mySavedResult.size();

        Double tc = 0D, tg = 0D;

        for (int i = 0; i < sz; i++) {
            tc += mySavedResult.get(i).getCredit();
            tg += mySavedResult.get(i).getTotalGpa();
        }

        totalCredit = tc;
        totalGpa = tg;
        id = sz;

        Double cg;

        if (tc == 0)
            cg = 0D;

        else
            cg = tg / tc;

        String s = "CGPA = " + String.format(Locale.getDefault(), "%.3f", cg);
        cgpaOutput.setText(s);

        showResultList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                        TextView prevCredit = (TextView) view.findViewById(R.id.credit);
                        TextView prevGpa = (TextView) view.findViewById(R.id.gpa);

                        changeCredit = Double.parseDouble(prevCredit.getText().toString());
                        changeGpa = Double.parseDouble(prevGpa.getText().toString());

                        final Dialog dialog = new Dialog(SavedResult.this);
                        dialog.setContentView(R.layout.edit_input_popup);
                        int d = position + 1;
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

                                if (creditInp.getText().toString().isEmpty() || gpaInp.getText().toString().isEmpty()) {
                                    Toast.makeText(getApplicationContext(), "Please fill all the information!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                String ci = creditInp.getText().toString();
                                String gi = gpaInp.getText().toString();

                                if (ci.length() == 1 && (ci.charAt(0) < '0' || ci.charAt(0) > '9')) {
                                    Toast.makeText(SavedResult.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (gi.length() == 1 && (gi.charAt(0) < '0' || gi.charAt(0) > '9')) {
                                    Toast.makeText(SavedResult.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                Double credit = Double.parseDouble(creditInp.getText().toString());
                                Double gpa = Double.parseDouble(gpaInp.getText().toString());

                                if (gpa > 4) {
                                    Toast.makeText(getApplicationContext(), "GPA cann't be greater than 4", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                if (gpa < 0) {
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

                        final Dialog dialog = new Dialog(SavedResult.this);
                        dialog.setContentView(R.layout.delete_prev_input);
                        int d = position+1;

                        String t = "Delete your entry number " + d;


                        //int myColor = getResources().getColor(R.color.myTitleColor);
                        //dialog.getWindow().setTitleColor(myColor);

                        dialog.setTitle(t);

                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(SavedResult.this);
                        LayoutInflater inflater = LayoutInflater.from(SavedResult.this);

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

    public void update(Double credit, Double gpa, final View view, final int position, int ser) {

        if (ser == 1) {
            totalCredit -= changeCredit;
            totalGpa -= (changeCredit * changeGpa);

            totalCredit += credit;
            totalGpa += (credit * gpa);

            Double cgpa = totalGpa / totalCredit;

            //cgpaOutput = (TextView) findViewById(R.id.cgpaOutput);

            String s;

            s = "CGPA = " + String.format(Locale.getDefault(), "%.3f", cgpa);

            cgpaOutput.setText(s);

            resultInfo result = new resultInfo(position + 1, credit, gpa, credit * gpa);
            showResult.set(position, result);

            showResultList = (ListView) findViewById(R.id.showResult);

            CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);

            showResultList.setAdapter(aniksAdapter);

            myDBHandler.deleteDataBase();
            myDBHandler.addProduct(showResult);

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

            //cgpaOutput = (TextView) findViewById(R.id.cgpaOutput);

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
            myDBHandler.deleteDataBase();

            if(sz > 0)
                myDBHandler.addProduct(showResult);

            Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
        }
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

        //cgpaOutput = (TextView) findViewById(R.id.cgpaOutput);

        String s;

        s = "CGPA = " + String.format( Locale.getDefault(),"%.3f", cgpa);

        cgpaOutput.setText(s);

        gpaInput.setText("");

        id++;

        showResult.add(new resultInfo(id, credit, gpa, credit*gpa));

        CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);

        showResultList.setAdapter(aniksAdapter);

        myDBHandler.deleteDataBase();
        myDBHandler.addProduct(showResult);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater blowup = getMenuInflater();
        blowup.inflate(R.menu.cool_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.aboutMe:
                try {
                    Class myClass = Class.forName("com.workstation.anik.cgpacalculatorubuntu.AboutMe");
                    Intent intent = new Intent(this, myClass);
                    startActivity(intent);

                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.preferences:
                try{
                    Class myClass = Class.forName("com.workstation.anik.cgpacalculatorubuntu.Pref");
                    Intent intent = new Intent(this, myClass);
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.savedResult:
                if(showResult.size() <= 0)
                    Toast.makeText(this, "No saved result found!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Saved result are displaying!", Toast.LENGTH_SHORT).show();

                break;

            case R.id.deleteSavedResult:
                showResult.clear();
                myDBHandler.deleteDataBase();

                CustomAdapter aniksAdapter = new CustomAdapter(this, showResult, flag);
                showResultList.setAdapter(aniksAdapter);

                cgpaOutput.setText("CGPA: 0.000");
                break;

            case R.id.exit:
                finish();
                break;
        }

        return false;
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
