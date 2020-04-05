package com.example.adspractical;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;

public class MainActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    HorizontalScrollView horizontalScrollView;
    ArrayList<Integer> list = new ArrayList<>();
    ArrayList<Integer> bins = new ArrayList<>();
    ArrayList<Integer> myList = new ArrayList<>();
    ArrayList<Integer> myBins = new ArrayList<>();
    Hashtable<Integer, Integer> dataSearchTable = new Hashtable<>();
    Hashtable<Integer, ArrayList<Integer>> binSearchTable = new Hashtable<>();
    TextView FFNormal, FFDesc, Details, DetailsTopic;
    ImageView Search, Settings;
    EditText SearchKey;
    int currentSelected = -1;
    boolean applied = false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    int capacity = 1500, dataSize = 500, binNo = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearLayout = (LinearLayout)findViewById(R.id.llbinmain);
        FFNormal = (TextView)findViewById(R.id.tvffnormal);
        FFDesc = (TextView)findViewById(R.id.tvffdesc);
        Details = (TextView)findViewById(R.id.tvdetails);
        DetailsTopic = (TextView)findViewById(R.id.tvdetailstopic);
        Search = (ImageView)findViewById(R.id.ivsearch);
        Settings = (ImageView)findViewById(R.id.ivsettings);
        SearchKey = (EditText)findViewById(R.id.etsearch);
        horizontalScrollView = (HorizontalScrollView)findViewById(R.id.hsvbinsmain);

        sharedPreferences = getSharedPreferences("ADSPreferences",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        if (sharedPreferences.contains("DataSize")){
            capacity = sharedPreferences.getInt("DataSize", 1500);
            dataSize = sharedPreferences.getInt("DataSize", 500);
            binNo = sharedPreferences.getInt("BinNo", 100);
        }

        for(int i=1 ; i<=dataSize ; i++){
            list.add(i);
            if(i<=binNo){ bins.add(capacity); }
        }

        Details.setText("Data: 1,2,3..."+dataSize+"\nBins: 1,2,3..."+binNo+"\nCapacity: "+capacity);

        Collections.shuffle(list);

        myList = (ArrayList<Integer>)list.clone();
        myBins = (ArrayList<Integer>)bins.clone();
        refreshBins();

        FFNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horizontalScrollView.smoothScrollTo(0, 0);
                myList = (ArrayList<Integer>)list.clone();
                myBins = (ArrayList<Integer>)bins.clone();
                Fit(1);
                applied = true;
                refreshBins();
            }
        });

        FFDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                horizontalScrollView.smoothScrollTo(0, 0);
                myList = (ArrayList<Integer>)list.clone();
                myBins = (ArrayList<Integer>)bins.clone();
                Fit(2);
                applied = true;
                refreshBins();
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchData();
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog dialog = new Dialog(MainActivity.this, R.style.myDialod);
                View vieww = getLayoutInflater().inflate(R.layout.my_settings, null);
                final EditText DataSize = (EditText)vieww.findViewById(R.id.etsdatasize);
                final EditText BinNo = (EditText)vieww.findViewById(R.id.etsdatasize);
                final EditText Capacity = (EditText)vieww.findViewById(R.id.etsdatasize);
                Button Apply = (Button)vieww.findViewById(R.id.btnapply);

                Apply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        editor.putInt("DataSize",Integer.parseInt(DataSize.getText().toString()));
                        editor.putInt("BinNo",Integer.parseInt(BinNo.getText().toString()));
                        editor.putInt("Capacity",Integer.parseInt(Capacity.getText().toString()));
                        editor.apply();
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                });

                dialog.setContentView(vieww);
                dialog.show();
            }
        });
    }

    public void SearchData(){
        if (SearchKey.getText().toString().equals("")){
            Toast.makeText(this, "Please enter the data to be searched", Toast.LENGTH_SHORT).show();
            return;
        }
        int key = Integer.parseInt(SearchKey.getText().toString());
        if (!applied){
            Toast.makeText(this, "Please select an algorithm first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (key>dataSize || key<1){
            Toast.makeText(this, "Please enter data between 1 to "+dataSize, Toast.LENGTH_SHORT).show();
            return;
        }
        int bin = dataSearchTable.get(key);

        if(bin == 0){
            horizontalScrollView.smoothScrollTo(0, 0);
        }
        else{
            horizontalScrollView.smoothScrollTo(360*(bin-1), 0);
        }

        if (currentSelected != -1 && currentSelected!=bin){
            LinearLayout ll = (LinearLayout) linearLayout.getChildAt(currentSelected);
            TextView t = (TextView) ll.getChildAt(1);
            t.setTextColor(Color.parseColor("#000000"));
            ll.setBackgroundResource(0);
        }

        if (currentSelected != bin){
            currentSelected = bin;

            LinearLayout ll = (LinearLayout) linearLayout.getChildAt(currentSelected);
            TextView t = (TextView) ll.getChildAt(1);
            t.setTextColor(Color.parseColor("#ffffff"));
            ll.setBackgroundResource(R.drawable.highlighted_rounded_corners);
        }
    }

    public void refreshBins(){
        linearLayout.removeAllViews();

        for(int i=0 ; i<binNo ; i++){
            View view = getLayoutInflater().inflate(R.layout.bin_layout, null);
            final TextView textView = (TextView)view.findViewById(R.id.tvbinmain);
            final TextView stats = (TextView)view.findViewById(R.id.tvbinstats);
            final LinearLayout BinLayout = (LinearLayout)view.findViewById(R.id.llblmain);
            textView.setText(""+(i+1));
            stats.setText(""+myBins.get(i)+"/"+capacity);

            BinLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (currentSelected != -1 || currentSelected==linearLayout.indexOfChild(BinLayout)){
                        LinearLayout ll = (LinearLayout) linearLayout.getChildAt(currentSelected);
                        TextView t = (TextView) ll.getChildAt(1);
                        t.setTextColor(Color.parseColor("#000000"));
                        ll.setBackgroundResource(0);
                    }
                    if(currentSelected!=linearLayout.indexOfChild(BinLayout)){
                        currentSelected = linearLayout.indexOfChild(BinLayout);
                        BinLayout.setBackgroundResource(R.drawable.highlighted_rounded_corners);
                        stats.setTextColor(Color.parseColor("#ffffff"));
                    }
                }
            });

            BinLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Toast toast = new Toast(getApplicationContext());
                    View vieww = getLayoutInflater().inflate(R.layout.bin_contents, null);
                    TextView BinNo = (TextView)vieww.findViewById(R.id.tvbinno);
                    TextView BinCont = (TextView)vieww.findViewById(R.id.tvbincont);
                    BinNo.setText("Bin "+(linearLayout.indexOfChild(BinLayout)+1));
                    BinCont.setText(""+binSearchTable.get(linearLayout.indexOfChild(BinLayout)));
                    toast.setView(vieww);
                    toast.setGravity(Gravity.BOTTOM, 0, 500);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.show();
                    return true;
                }
            });

            linearLayout.addView(view);
        }
    }

    public void Fit(int method) {
        if (method == 2){
            Collections.sort(myList, Collections.reverseOrder());
        }
        dataSearchTable = new Hashtable<>();
        binSearchTable = new Hashtable<>();

        for (int i = 0; i < myList.size(); i++) {
            for (int j = 0; j < myBins.size(); j++) {
                int remaining = myBins.get(j) - myList.get(i);
                if (remaining >= 0) {
                    dataSearchTable.put(myList.get(i), j);
                    ArrayList<Integer> addList = binSearchTable.getOrDefault(j, new ArrayList<Integer>());
                    addList.add(myList.get(i));
                    binSearchTable.put(j, addList);
                    myBins.set(j, remaining);
                    break;
                }
            }
        }

        int memoryWastage = 0, fullCapacityBins = 0;

        for(Integer i : myBins){
            if (i==capacity){ fullCapacityBins++; }
            else{ memoryWastage+=i; }
        }

        if (method == 1){ DetailsTopic.setText("First Fit Analysis"); }
        else{ DetailsTopic.setText("First Fit (Desc) Analysis"); }

        Details.setText("Memory Wastage:  "+memoryWastage+"\nFull Capacity Bins:  "+fullCapacityBins);
    }
}
