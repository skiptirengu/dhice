package com.skiptirengu.dhice.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Toast;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.activities.MainActivity;
import com.skiptirengu.dhice.databinding.CharacterDataFragmentBinding;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterEntity;
import com.skiptirengu.dhice.viewmodel.CharacterDataViewModel;
import com.skiptirengu.dhice.viewmodel.ViewModelResponse;
import com.transitionseverywhere.TransitionManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.progress_bar)
    protected View mProgress;
    @BindView(R.id.layout_character_data)
    protected ViewGroup mMainLayout;
    @BindView(R.id.layout_character_bonus)
    protected ViewGroup mLayoutBonus;
    @BindView(R.id.character_data_scrollview)
    protected ScrollView mScrollView;
    @BindView(R.id.character_name)
    protected AppCompatEditText mEdtName;
    @BindView(R.id.character_race)
    protected AppCompatEditText mEdtRace;
    @BindView(R.id.save_character)
    protected Button mBtnSave;
    @BindView(R.id.btn_add_bonus)
    protected Button mBtnAddBonus;

    private MainActivity mMainActivity;
    private CharacterDataFragmentBinding mBinding;
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
        mBinding = DataBindingUtil.inflate(inflater, R.layout.character_data_fragment, container, false);
        ButterKnife.bind(this, mBinding.getRoot());

        Bundle arguments = getArguments();
        mViewModel.setCharacterId(arguments != null ? arguments.getInt("id") : 0);
        mMainActivity.setTitle(mViewModel.getTitle());
        mViewModel.response().observe(this, this::processResponse);
        mViewModel.fetchCharacter();

        mBtnSave.setOnClickListener(view -> save());
        mBtnAddBonus.setOnClickListener(view -> this.addBonus());

        return mBinding.getRoot();
    }

    private void processResponse(ViewModelResponse<Character> response) {
        switch (response.getStatus()) {
            case LOADING:
                setLoading(true);
                break;
            case SUCCESS:
                mBinding.setCharacter(response.getData());
                setLoading(false);
                break;
            case ERRORED:
                Toast.makeText(mMainActivity, R.string.load_error, Toast.LENGTH_LONG).show();
                onBackPressed();
        }
    }

    private void addBonus() {
        setFadeInTransition(mScrollView);
        View layout = getLayoutInflater().inflate(R.layout.character_bonus, mLayoutBonus, false);
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
