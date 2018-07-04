package com.skiptirengu.dhice.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Toast;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.activities.MainActivity;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;
import com.skiptirengu.dhice.viewmodel.CharacterDataViewModel;
import com.transitionseverywhere.TransitionManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * {@link Fragment}
 */
public class CharacterDataFragment extends Fragment implements OnCheckedChangeListener, OnBackPressedListener {
    private static final String USE_SPELL = "spells";
    private static final String USE_ATTACK = "attacks";

    private RadioGroup mRadioGroup;
    private RadioButton mRadioSpell;
    private RadioButton mRadioAttack;
    private MainActivity mMainActivity;
    private View mProgress;
    private ViewGroup mMainLayout;
    private ViewGroup mLayoutBonus;
    private ScrollView mScrollView;
    private AppCompatEditText mEdtName;
    private AppCompatEditText mEdtRace;
    private Button mBtnSave;
    private Button mBtnAddBonus;

    private CharacterDataViewModel mViewModel;
    private Character mCharacter;

    public CharacterDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = Objects.requireNonNull((MainActivity) getActivity());
        mCharacter = new CharacterEntity();
        mViewModel = ViewModelProviders.of(this).get(CharacterDataViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_character_data, container, false);

        mMainLayout = inflate.findViewById(R.id.layout_character_data);
        mProgress = inflate.findViewById(R.id.progress_bar);
        mLayoutBonus = inflate.findViewById(R.id.layout_character_bonus);
        mScrollView = inflate.findViewById(R.id.character_data_scrollview);
        setLoading(true);

        mRadioGroup = inflate.findViewById(R.id.character_radio_group);
        mRadioSpell = inflate.findViewById(R.id.character_use_spells);
        mRadioAttack = inflate.findViewById(R.id.character_use_attacks);
        mEdtName = inflate.findViewById(R.id.character_name);
        mEdtRace = inflate.findViewById(R.id.character_race);
        mBtnSave = inflate.findViewById(R.id.save_character);
        mBtnAddBonus = inflate.findViewById(R.id.btn_add_bonus);

        mRadioGroup.setOnCheckedChangeListener(this);
        mBtnSave.setOnClickListener(view -> save());
        mBtnAddBonus.setOnClickListener(this::addBonus);
        unrwapArguments();

        return inflate;
    }

    @SuppressLint("CheckResult")
    private void unrwapArguments() {
        Bundle arguments = getArguments();

        if (arguments != null && arguments.getBoolean("update")) {
            mMainActivity
                    .getDatabase()
                    .findCharacterById(arguments.getInt("id"))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            character -> {
                                mMainActivity.setTitle(R.string.update_character);
                                mCharacter = character;
                                mEdtName.setText(mCharacter.getName());
                                mEdtRace.setText(mCharacter.getRace());
                                String preferredAttack = mCharacter.getPreferredAttack();
                                if (preferredAttack != null && preferredAttack.equals(USE_ATTACK)) {
                                    mRadioAttack.setChecked(true);
                                } else {
                                    mRadioSpell.setChecked(true);
                                }
                            },
                            Throwable::printStackTrace,
                            () -> setLoading(false)
                    );
        } else {
            mMainActivity.setTitle(R.string.fragment_title_create_character);
            setLoading(false);
        }
    }

    private void addBonus(View view) {
        setFadeInTransition(mScrollView);
        View layout = LayoutInflater.from(view.getContext()).inflate(R.layout.character_bonus, mLayoutBonus, false);
        mLayoutBonus.addView(layout);
        Observable
                .empty()
                .delay(300, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    mScrollView.fullScroll(View.FOCUS_DOWN);
                    layout.findViewById(R.id.character_bonus_description).requestFocus();
                })
                .subscribe();
    }

    private void setFadeInTransition(ViewGroup viewGroup) {
        TransitionManager.beginDelayedTransition(viewGroup);
    }

    @SuppressLint("CheckResult")
    private void save() {
        View error = null;

        if (mEdtRace.getText().toString().isEmpty()) {
            mEdtRace.setError(getString(R.string.race_required));
            error = mEdtRace;
        }
        if (mEdtName.getText().toString().isEmpty()) {
            mEdtName.setError(getString(R.string.name_required));
            error = mEdtName;
        }

        if (error != null) {
            error.requestFocus();
            return;
        }

        setLoading(true);
        mCharacter.setName(mEdtName.getText().toString());
        mCharacter.setRace(mEdtRace.getText().toString());

        ReactiveEntityStore<Persistable> dataStore = mMainActivity.getDatabase().getDataStore();

        dataStore.upsert(mCharacter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        val -> {
                            this.returnToList();
                            setLoading(false);
                        },
                        err -> {
                            err.printStackTrace();
                            setLoading(false);
                        }
                );
    }

    private void setLoading(boolean loading) {
        if (loading) {
            mMainLayout.setVisibility(View.GONE);
            mProgress.setVisibility(View.VISIBLE);
        } else {
            mMainLayout.setVisibility(View.VISIBLE);
            mProgress.setVisibility(View.GONE);
        }
    }

    private void returnToList() {
        Toast.makeText(mMainActivity, R.string.character_saved, Toast.LENGTH_SHORT).show();
        mMainActivity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.character_use_spells:
                mCharacter.setPreferredAttack(USE_SPELL);
                break;
            case R.id.character_use_attacks:
                mCharacter.setPreferredAttack(USE_ATTACK);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        mMainActivity.getSupportFragmentManager().popBackStack();
    }

    @Override
    public boolean onMenuItemPressed(int menuItemId) {
        boolean thisFragment = menuItemId == R.id.navigation_characters;
        if (thisFragment) {
            onBackPressed();
        }
        return thisFragment;
    }
}
