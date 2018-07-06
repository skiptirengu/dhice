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
import com.skiptirengu.dhice.databinding.CharacterBonusBinding;
import com.skiptirengu.dhice.databinding.CharacterDataFragmentBinding;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.viewmodel.CharacterDataViewModel;
import com.skiptirengu.dhice.viewmodel.ViewModelResponse;
import com.transitionseverywhere.TransitionManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

    public CharacterDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = Objects.requireNonNull((MainActivity) getActivity());
        mViewModel = ViewModelProviders.of(this).get(CharacterDataViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.character_data_fragment, container, false);
        ButterKnife.bind(this, mBinding.getRoot());

        Bundle arguments = getArguments();
        mViewModel.setCharacterId(arguments != null ? arguments.getInt("id") : 0);
        mMainActivity.setTitle(mViewModel.getTitle());

        mViewModel.bonus().observe(this, this::proccessBonusResponse);
        mViewModel.character().observe(this, this::processCharacterResponse);
        mViewModel.delete().observe(this, this::processRemoveBonusResponse);
        mViewModel.save().observe(this, this::proccessSaveResponse);
        mViewModel.fetchCharacter();

        mBtnSave.setOnClickListener(view -> save());
        mBtnAddBonus.setOnClickListener(view -> this.addBonus());

        return mBinding.getRoot();
    }

    private void processRemoveBonusResponse(Integer index) {
        TransitionManager.beginDelayedTransition(mLayoutBonus);
        mLayoutBonus.removeViewAt(index);
        updateDeleteListeners();
    }

    private void updateDeleteListeners() {
        for (int index = 0; index < mLayoutBonus.getChildCount(); index++) {
            int finalIndex = index;
            mLayoutBonus
                    .getChildAt(index)
                    .findViewById(R.id.btn_delete_bonus)
                    .setOnClickListener(view -> mViewModel.removeBonus(finalIndex));
        }
    }

    private void proccessBonusResponse(CharacterDataViewModel.CharacterBonusResponse data) {
        CharacterBonusBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.character_bonus, mLayoutBonus, false);

        final View child = binding.getRoot();
        final int index = data.getIndex();
        binding.setBonus(data.getBonus());

        child.findViewById(R.id.btn_delete_bonus).setOnClickListener(view -> mViewModel.removeBonus(index));

        if (data.isNew()) {
            addFadeTransition(mScrollView);
            Completable.timer(300, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnComplete(() -> focusBonus(child))
                    .subscribe();
        }

        mLayoutBonus.addView(child);
    }

    private void focusBonus(final View parent) {
        mScrollView.fullScroll(View.FOCUS_DOWN);
        parent.requestFocus();
    }

    private void processCharacterResponse(ViewModelResponse<Character> response) {
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

    private void proccessSaveResponse(ViewModelResponse<String> response) {
        switch (response.getStatus()) {
            case LOADING:
                setLoading(true);
                break;
            case SUCCESS:
                onBackPressed();
                Toast.makeText(mMainActivity, response.getData(), Toast.LENGTH_SHORT).show();
                break;
            case ERRORED:
                Toast.makeText(mMainActivity, R.string.character_save_error, Toast.LENGTH_LONG).show();
                setLoading(false);
                break;
        }
    }

    private void addBonus() {
        mViewModel.addBonus();
    }

    private void addFadeTransition(ViewGroup viewGroup) {
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

        mViewModel.saveCharacter();
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

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {
            case R.id.character_use_spells:
                break;
            case R.id.character_use_attacks:
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
