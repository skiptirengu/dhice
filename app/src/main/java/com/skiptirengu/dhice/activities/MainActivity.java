package com.skiptirengu.dhice.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.fragments.CharacterListFragment;

public class MainActivity extends AppCompatActivity {
    private final String SELECTED_MENU_ITEM = "last_fragment";
    private View mContainer;

    private OnNavigationItemSelectedListener mOnNavigationItemSelected = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_characters:
                setFragment(new CharacterListFragment());
                return true;
            case R.id.navigation_roll:
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = findViewById(R.id.main_activity_container);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelected);

        int itemId = R.id.navigation_characters;
        if (savedInstanceState != null) {
            navigation.setSelectedItemId(savedInstanceState.getInt(SELECTED_MENU_ITEM, itemId));
        } else {
            navigation.setSelectedItemId(itemId);
        }
    }

    public void setFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(mContainer.getId(), fragment);
        transaction.addToBackStack(fragment.getClass().getCanonicalName());
        transaction.commit();
    }
}
