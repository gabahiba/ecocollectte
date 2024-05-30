package com.hb.ecocollectte;

import androidx.annotation.NonNull;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class home extends AppCompatActivity {
    private BottomNavigationView bottomnavigation;
    private FrameLayout framelayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().setStatusBarColor(ContextCompat.getColor(home.this, R.color.Green1));
        bottomnavigation=findViewById(R.id.bottomNavigationView);
        framelayout=findViewById(R.id.framelayout);
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.framelayout,new homeFragment()).commit();
        }
        bottomnavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId =item.getItemId();
                if(itemId==R.id.home){
                    loadFragment(new homeFragment(),false);
                }else if(itemId==R.id.table){
                    loadFragment(new tablefragment(),false);
                }else if(itemId==R.id.setting){
                    loadFragment(new SettingFragment(),false);
                }
                return true;
            }
        });
    }
    private void loadFragment(Fragment fragment, boolean isappinitialized){
        FragmentManager fragmentmanager =getSupportFragmentManager();
        FragmentTransaction fragmenttransaction =fragmentmanager.beginTransaction();
        if(isappinitialized){
            fragmenttransaction.add(R.id.framelayout,fragment);
        }
        else{
            fragmenttransaction.replace(R.id.framelayout,fragment);
        }
        fragmenttransaction.commit();
    }


}
