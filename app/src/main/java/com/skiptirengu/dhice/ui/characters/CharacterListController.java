package com.skiptirengu.dhice.ui.characters;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.ControllerChangeType;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.ui.base.BaseController;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CharacterListController extends BaseController<CharacterListContract.View, CharacterListContract.Presenter>
        implements CharacterListContract.View {

    @BindView(R.id.layout_character_list)
    protected FrameLayout mLayout;
    @BindView(R.id.progress_bar)
    protected FrameLayout mProgress;

    @Override
    protected String getTitle() {
        return getString(R.string.title_characters);
    }

    @NonNull
    @Override
    public CharacterListContract.Presenter createPresenter() {
        return new CharacterListPresenter();
    }

    @Override
    public void showLoading() {
        mProgress.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mProgress.setVisibility(View.GONE);
        mLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCharactersResult(List<Character> characterList) {
        //
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.character_list_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onChangeStarted(@NonNull ControllerChangeHandler changeHandler, @NonNull ControllerChangeType changeType) {
        if (changeType.isEnter) {
            presenter.loadCharacters();
        }
        super.onChangeStarted(changeHandler, changeType);
    }
}
