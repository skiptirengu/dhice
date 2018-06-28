package com.skiptirengu.dhice.fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.activities.MainActivity;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;

import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.requery.Persistable;
import io.requery.reactivex.ReactiveEntityStore;

/**
 * {@link Fragment}
 */
public class CharacterDataFragment extends Fragment implements OnCheckedChangeListener, View.OnClickListener {
    private static final String USE_SPELL = "spells";
    private static final String USE_ATTACK = "attacks";

    private RadioGroup mRadioGroup;
    private RadioButton mRadioSpell;
    private RadioButton mRadioAttack;
    private MainActivity mMainActivity;
    private View mProgress;
    private View mMainLayout;
    private AppCompatEditText mEdtName;
    private AppCompatEditText mEdtRace;
    private Button mBtnSave;

    private Character mCharacter;
    private boolean mUpdate;

    public CharacterDataFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_character_data, container, false);

        mCharacter = new CharacterEntity();
        mMainActivity = Objects.requireNonNull((MainActivity) getActivity());

        mMainLayout = inflate.findViewById(R.id.layout_character_data);
        mProgress = inflate.findViewById(R.id.progress_bar);
        setLoading(true);

        mRadioGroup = inflate.findViewById(R.id.character_radio_group);
        mRadioSpell = inflate.findViewById(R.id.character_use_spells);
        mRadioAttack = inflate.findViewById(R.id.character_use_attacks);
        mEdtName = inflate.findViewById(R.id.character_name);
        mEdtRace = inflate.findViewById(R.id.character_race);
        mBtnSave = inflate.findViewById(R.id.save_character);

        mRadioGroup.setOnCheckedChangeListener(this);
        mBtnSave.setOnClickListener(this);
        unrwapArguments();

        return inflate;
    }

    @SuppressLint("CheckResult")
    private void unrwapArguments() {
        Bundle arguments = getArguments();

        if (arguments != null && (mUpdate = arguments.getBoolean("update"))) {
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
        Single<Character> insertOrUpdate;

        if (mUpdate) {
            insertOrUpdate = dataStore.update(mCharacter);
        } else {
            insertOrUpdate = dataStore.insert(mCharacter);
        }

        insertOrUpdate
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
    public void onClick(View view) {
        save();
    }
}
