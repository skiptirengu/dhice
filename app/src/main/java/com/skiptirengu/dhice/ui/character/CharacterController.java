package com.skiptirengu.dhice.ui.character;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.ParcelableDataLceViewState;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.databinding.CharacterBinding;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.ui.base.RxMvpLceViewStateController;

public class CharacterController extends
        RxMvpLceViewStateController<FrameLayout, Character, CharacterContract.View, CharacterContract.Presenter>
        implements CharacterContract.View {

    public static final String INTENT_CHARACTER_ID = "characterId";

    private Character mCharacter;
    private CharacterBinding mBinding;

    {
        setHasEmptyState(false);
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
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.character, container, false);
        return mBinding.getRoot();
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
        presenter.loadCharacter(getArgs().getInt(INTENT_CHARACTER_ID));
    }
}
