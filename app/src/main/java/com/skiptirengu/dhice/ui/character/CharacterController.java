package com.skiptirengu.dhice.ui.character;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.ParcelableDataLceViewState;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.databinding.CharacterBinding;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.storage.CharacterBonus;
import com.skiptirengu.dhice.storage.CharacterBonusEntity;
import com.skiptirengu.dhice.ui.base.RxMvpLceViewStateController;
import com.skiptirengu.dhice.ui.characterlist.CharacterListController;
import com.skiptirengu.dhice.util.Conv;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharacterController extends
        RxMvpLceViewStateController<FrameLayout, Character, CharacterContract.View, CharacterContract.Presenter>
        implements CharacterContract.View {

    public static final String INTENT_CHARACTER_ID = "characterId";

    @BindView(R.id.recylerView)
    protected RecyclerView mRecyclerView;
    @BindView(R.id.txt_layout_character_name)
    protected TextInputLayout mCharacterNameLayout;
    @BindView(R.id.contentView)
    protected NestedScrollView mScroll;

    private CharacterBonusAdapter mAdapter;
    private Character mCharacter;
    private CharacterBinding mBinding;

    {
        setHasEmptyState(false);
        setHasOptionsMenu(true);
    }

    @Override
    protected String getTitle() {
        return null;
    }

    @Override
    public Character getData() {
        return mCharacter;
    }

    @Override
    public void setData(Character data) {
        mCharacter = data;
        mBinding.setCharacter(mCharacter);
        mAdapter.setList(data.getBonuses());
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_save, menu);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.character, container, false);
        ButterKnife.bind(this, mBinding.getRoot());
        setupRecyclerView(mBinding.getRoot().getContext());
        return mBinding.getRoot();
    }

    private void setupRecyclerView(Context context) {
        mAdapter = new CharacterBonusAdapter();
        disposeOnDetach(mAdapter.delete().subscribe(tag -> onRemoveBonus(tag)));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @NonNull
    @Override
    public LceViewState<Character, CharacterContract.View> createViewState() {
        return new ParcelableDataLceViewState<>();
    }

    @NonNull
    @Override
    public CharacterContract.Presenter createPresenter() {
        return new CharacterPresenter();
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        getPresenter().loadCharacter(getArgs().getInt(INTENT_CHARACTER_ID));
    }

    @Override
    @OnClick(R.id.btn_add_bonus)
    public void onAddBonus(View view) {
        CharacterBonus bonus = new CharacterBonusEntity();
        view.setTag(bonus.uniqueId());
        mAdapter.addData(bonus);
    }

    @Override
    public void onRemoveBonus(String position) {
        mAdapter.removeItem(position);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save_btn) {
            onSaveClick();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveClick() {
        if (validate()) {
            getPresenter().saveCharacter(mCharacter);
        }
    }

    private boolean validate() {
        View focusView = null;

        if (Conv.nullOrEmpty(mCharacter.getName())) {
            mCharacterNameLayout.setError(getString(R.string.validation_name_required));
            focusView = mCharacterNameLayout;
        } else {
            mCharacterNameLayout.setError(null);
        }

        if (focusView != null) {
            focusView.requestFocus();
            mScroll.smoothScrollTo(focusView.getScrollX(), focusView.getScrollY());
        }

        return focusView == null;
    }

    private String getString(int res) {
        return Objects.requireNonNull(getResources()).getString(res);
    }

    @Override
    public void onCharacterSaved() {
        getRouter().setRoot(
                RouterTransaction.with(new CharacterListController())
                        .popChangeHandler(new HorizontalChangeHandler())
                        .pushChangeHandler(new HorizontalChangeHandler())
        );
    }
}
