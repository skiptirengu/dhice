package com.skiptirengu.dhice;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomNavigationView.OnNavigationItemSelectedListener;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.bluelinelabs.conductor.Conductor;
import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.Router;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.skiptirengu.dhice.fragments.OnMenuItemPressedListener;
import com.skiptirengu.dhice.ui.characterlist.CharacterListController;
import com.skiptirengu.dhice.ui.roll.DamageRollController;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.navigation)
    protected BottomNavigationView mNavigation;
    private Router mRouter;

    private OnNavigationItemSelectedListener mOnNavigationItemSelected = item -> {
        int itemId = item.getItemId();

        Controller topController = getTopController();
        if (topController != null
                && (topController instanceof OnMenuItemPressedListener)
                && ((OnMenuItemPressedListener) topController).onMenuItemPressed(itemId)) {
            return true;
        }

        switch (itemId) {
            case R.id.navigation_characters:
                setRoot(new CharacterListController(), itemId);
                return true;
            case R.id.navigation_roll:
                setRoot(new DamageRollController(), itemId);
                return true;
        }

        return false;
    };

    private Controller getTopController() {
        if (mRouter.getBackstackSize() == 0) {
            return null;
        } else {
            return mRouter.getBackstack().get(mRouter.getBackstackSize() - 1).controller();
        }
    }

    private void setRoot(Controller controller, int id) {
        mRouter.setRoot(
                RouterTransaction
                        .with(controller)
                        .pushChangeHandler(new FadeChangeHandler())
                        .popChangeHandler(new FadeChangeHandler())
                        .tag(String.valueOf(id))
        );
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(findViewById(R.id.app_toolbar));
        mNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelected);

        ViewGroup viewGroup = findViewById(R.id.main_activity_container);
        mRouter = Conductor.attachRouter(this, viewGroup, savedInstanceState);
        if (!mRouter.hasRootController()) {
            mNavigation.setSelectedItemId(R.id.navigation_characters);
        }
    }


    @Override
    public void onBackPressed() {
        if (!mRouter.handleBack()) {
            super.onBackPressed();
        }
    }
}
