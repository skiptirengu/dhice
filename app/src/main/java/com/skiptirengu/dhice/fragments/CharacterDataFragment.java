package com.skiptirengu.dhice.fragments;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.ScrollView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.skiptirengu.dhice.MainActivity;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.databinding.CharacterBinding;
import com.skiptirengu.dhice.databinding.CharacterBonusBinding;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.util.Animations;
import com.skiptirengu.dhice.viewmodel.CharacterDataViewModel;
import com.skiptirengu.dhice.viewmodel.ViewModelResponse;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * {@link Fragment}
 */
public class CharacterDataFragment extends Fragment implements OnCheckedChangeListener, OnMenuItemPressedListener {
    private static final String USE_SPELL = "spells";
    private static final String USE_ATTACK = "attacks";

    @BindView(R.id.loadingView)
    protected View mProgress;
    @BindView(R.id.layout_character_data)
    protected ViewGroup mMainLayout;
    @BindView(R.id.layout_character_bonus)
    protected ViewGroup mLayoutBonus;
    @BindView(R.id.contentView)
    protected ScrollView mScrollView;
    @BindView(R.id.character_name)
    protected TextInputEditText mEdtName;
    @BindView(R.id.character_race)
    protected TextInputEditText mEdtRace;
    @BindView(R.id.btn_add_bonus)
    protected Button mBtnAddBonus;

    private MainActivity mMainActivity;
    private CharacterBinding mBinding;
    private CharacterDataViewModel mViewModel;

    public CharacterDataFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMainActivity = Objects.requireNonNull((MainActivity) getActivity());
        mViewModel = ViewModelProviders.of(this).get(CharacterDataViewModel.class);
        mViewModel.bonus().observe(this, this::proccessBonusResponse);
        mViewModel.character().observe(this, this::processCharacterResponse);
        mViewModel.delete().observe(this, this::processRemoveBonusResponse);
        mViewModel.save().observe(this, this::proccessSaveResponse);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.character, container, false);
        ButterKnife.bind(this, mBinding.getRoot());

        Bundle arguments = getArguments();
        mViewModel.setCharacterId(arguments != null ? arguments.getInt("id") : 0);
        mMainActivity.setTitle(mViewModel.getTitle());
        mBtnAddBonus.setOnClickListener(view -> this.addBonus());
        mViewModel.fetchCharacter();

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save_btn:
                save();
                return true;
            default:
                return false;
        }
    }

    private void processRemoveBonusResponse(String tag) {
        final View childAt = (View) mLayoutBonus.findViewWithTag(tag).getParent();
        Animations.delayed(Techniques.SlideOutRight, childAt, animator -> mLayoutBonus.removeView(childAt));
    }

    private void proccessBonusResponse(CharacterDataViewModel.CharacterBonusResponse data) {
        CharacterBonusBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.character_bonus, mLayoutBonus, false);

        final View child = binding.getRoot();
        final View deleteBtn = child.findViewById(R.id.btn_delete_bonus);

        deleteBtn.setTag(data.getTag());
        binding.setBonus(data.getBonus());
        deleteBtn.setOnClickListener(view -> mViewModel.removeBonus((String) view.getTag()));
        mLayoutBonus.addView(child, 0);

        if (data.isNew()) {
            Animations.delayed(Techniques.SlideInLeft, child, animator -> child.requestFocus());
        }
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
                Toast.makeText(mMainActivity, R.string.error_load_character, Toast.LENGTH_LONG).show();
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
                Toast.makeText(mMainActivity, R.string.error_save_character, Toast.LENGTH_LONG).show();
                setLoading(false);
                break;
        }
    }

    private void addBonus() {
        mViewModel.addBonus();
    }

    @SuppressLint("CheckResult")
    private void save() {
        View error = null;

        if (mEdtRace.getText().toString().isEmpty()) {
            mEdtRace.setError(getString(R.string.validation_name_required));
            error = mEdtRace;
        }
        if (mEdtName.getText().toString().isEmpty()) {
            mEdtName.setError(getString(R.string.validation_name_required));
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
