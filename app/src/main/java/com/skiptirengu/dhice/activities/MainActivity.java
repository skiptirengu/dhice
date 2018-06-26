package com.skiptirengu.dhice.activities;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.fragments.CharacterListFragment;
import com.skiptirengu.dhice.storage.Database;

public class MainActivity extends AppCompatActivity {
    private static final String SELECTED_MENU_ITEM = "last_fragment";
    private View mContainer;
    private Database mDatabase;
    private FragmentManager mFragmentManager;

    private OnNavigationItemSelectedListener mOnNavigationItemSelected = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_characters:
                setFragmentNonDuplicated(new CharacterListFragment());
                return true;
            case R.id.navigation_roll:
                return true;
        }
        return false;
    };

    public View getContainer() {
        return mContainer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = findViewById(R.id.main_activity_container);
        mFragmentManager = getSupportFragmentManager();
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelected);

        int itemId = R.id.navigation_characters;
        if (savedInstanceState != null) {
            navigation.setSelectedItemId(savedInstanceState.getInt(SELECTED_MENU_ITEM, itemId));
        } else {
            navigation.setSelectedItemId(itemId);
        }
    }

    private void setFragmentNonDuplicated(Fragment fragment) {
        Fragment topFragment = getTopFragment();
        if (topFragment == null || !fragment.getClass().equals(topFragment.getClass())) {
            setFragment(fragment);
        }
    }

    private Fragment getTopFragment() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            return null;
        } else {
            return mFragmentManager.getFragments().get(0);
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            mFragmentManager.popBackStack();
        }
    }

    public void setFragment(Fragment fragment) {
        setFragment(fragment, true);
    }

    public synchronized void setFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        transaction.replace(mContainer.getId(), fragment);
        if (addToBackStack) {
            transaction.addToBackStack(fragment.getClass().getCanonicalName());
        }
        transaction.commit();
    }

    public synchronized Database getDatabase() {
        if (mDatabase == null) {
            mDatabase = new Database(this);
        }
        return mDatabase;
    }
}
