package com.wesllei.ruufmt.about;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wesllei.ruufmt.R;

import java.util.ArrayList;


public class AboutActivity extends AppCompatActivity {

    private String apache20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        configLicenseTypes();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.mit_licence);
        builder.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog dialog = builder.create();

        findViewById(R.id.mit_licence).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        ArrayList<LibLicenseItem> items = getLibItems();
        ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        LinearLayout licenseList = (LinearLayout) findViewById(R.id.third_list);
        for (int i = 0; i < items.size(); i++) {
            View child = getLayoutInflater().inflate(R.layout.license_list_item, viewGroup, false);
            ((TextView) child.findViewById(R.id.lib_title)).setText(items.get(i).getLibTitle());
            ((TextView) child.findViewById(R.id.license_title)).setText(items.get(i).getLicenseTitle());
            ((TextView) child.findViewById(R.id.license_body)).setText(items.get(i).getLicenseBody());
            licenseList.addView(child);
        }

    }

    private ArrayList<LibLicenseItem> getLibItems() {
        ArrayList<LibLicenseItem> items = new ArrayList<>();
        items.add(new LibLicenseItem("Android SwipeableRecyclerView", "Copyright 2015 Dmytro Tarianyk", apache20));
        items.add(new LibLicenseItem("FloatingActionButton", "Copyright 2015 @brnunes", apache20));
        return items;
    }

    private void configLicenseTypes() {
        apache20 = "Licensed under the Apache License, Version 2.0 (the \"License\"); you may not use this file except in compliance with the License. You may obtain a copy of the License at\n\n" +
                "http://www.apache.org/licenses/LICENSE-2.0\n\n" +
                "Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an \"AS IS\" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the Licens";
    }
}

