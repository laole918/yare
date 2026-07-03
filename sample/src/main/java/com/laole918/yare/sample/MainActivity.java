package com.laole918.yare.sample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.laole918.yare.sample.test.Test;
import com.laole918.yare.sample.test.TestItem;
import com.laole918.yare.sample.test.Tests;

public final class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    private TextView output;

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.test_output);
        output.setText(String.format("Android %s (API %d); ABI %s; no test executed",
                Build.VERSION.RELEASE, Build.VERSION.SDK_INT, Build.CPU_ABI));

        ListView listView = findViewById(R.id.test_list);
        String[] names = new String[Tests.ALL.length];
        for (int i = 0; i < Tests.ALL.length; i++) {
            names[i] = Tests.ALL[i].name;
        }
        listView.setAdapter(new ArrayAdapter<>(this, R.layout.test_item, R.id.test_item_name, names));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TestItem item = Tests.ALL[position];
        int result = item.run();
        output.setText(item.name + ": " + resultToText(result));
    }

    private static String resultToText(int result) {
        if (result == Test.SUCCESS) {
            return "success";
        }
        if (result == Test.FAILED) {
            return "failed";
        }
        if (result == Test.IGNORED) {
            return "ignored";
        }
        return "unknown(" + result + ")";
    }
}
