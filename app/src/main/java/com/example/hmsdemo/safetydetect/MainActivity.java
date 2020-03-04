/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.

 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.example.hmsdemo.safetydetect;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.hmsdemo.R;

/**
 * A simple launcher activity containing a summary sample description
 * and a few action bar buttons at bottom of view.
 */
public class MainActivity extends Activity implements View.OnClickListener {

    public static final String TAG = "MainActivity";

    // UI Object
    private TextView txt_topbar;
    private TextView txt_appscheck;
    private TextView txt_sysintegrity;
    private TextView txt_urlcheck;
    private TextView txt_userdetect;

    // Fragment Object
    private Fragment fg1;
    private Fragment fg2;
    private Fragment fg3;
    private Fragment fg4;
    private FragmentManager fManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_safetydetect);

        fManager = getFragmentManager();
        bindViews();
        txt_sysintegrity.performClick();
    }

    private void bindViews() {
        txt_topbar = findViewById(R.id.txt_topbar);
        txt_appscheck = findViewById(R.id.txt_appscheck);
        txt_sysintegrity = findViewById(R.id.txt_sysintegrity);
        txt_urlcheck = findViewById(R.id.txt_urlcheck);
        txt_userdetect = findViewById(R.id.txt_userdetect);

        txt_appscheck.setOnClickListener(this);
        txt_sysintegrity.setOnClickListener(this);
        txt_urlcheck.setOnClickListener(this);
        txt_userdetect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        android.app.FragmentTransaction fTransaction = fManager.beginTransaction();
        hideAllFragment(fTransaction);
        int id = v.getId();
        if (id == R.id.txt_appscheck) {
            setSelected();
            txt_appscheck.setSelected(true);
            txt_topbar.setText(R.string.title_activity_apps_check);
            if (fg1 == null) {
                fg1 = new SafetyDetectAppsCheckAPIFragment();
                fTransaction.add(R.id.ly_content, fg1);
            } else {
                fTransaction.show(fg1);
            }
        } else if (id == R.id.txt_sysintegrity) {
            setSelected();
            txt_sysintegrity.setSelected(true);
            txt_topbar.setText(R.string.title_activity_sys_integrity);
            if (fg2 == null) {
                fg2 = new SafetyDetectSysIntegrityAPIFragment();
                fTransaction.add(R.id.ly_content, fg2);
            } else {
                fTransaction.show(fg2);
            }
        } else if (id == R.id.txt_urlcheck) {
            setSelected();
            txt_urlcheck.setSelected(true);
            txt_topbar.setText(R.string.title_url_check_entry);
            if (fg3 == null) {
                fg3 = new SafetyDetectUrlCheckAPIFragment();
                fTransaction.add(R.id.ly_content, fg3);
            } else {
                fTransaction.show(fg3);
            }
        } else if (id == R.id.txt_userdetect) {
            setSelected();
            txt_userdetect.setSelected(true);
            txt_topbar.setText(R.string.title_user_detect_entry);
            if (fg4 == null) {
                fg4 = new SafetyDetectUserDetectAPIFragment();
                fTransaction.add(R.id.ly_content, fg4);
            } else {
                fTransaction.show(fg4);
            }
        }
        fTransaction.commit();
    }

    private void setSelected() {
        txt_appscheck.setSelected(false);
        txt_sysintegrity.setSelected(false);
        txt_urlcheck.setSelected(false);
        txt_userdetect.setSelected(false);
    }

    private void hideAllFragment(android.app.FragmentTransaction fragmentTransaction) {
        if (fg1 != null) {
            fragmentTransaction.hide(fg1);
        }
        if (fg2 != null) {
            fragmentTransaction.hide(fg2);
        }
        if (fg3 != null) {
            fragmentTransaction.hide(fg3);
        }
        if (fg4 != null) {
            fragmentTransaction.hide(fg4);
        }
    }
}