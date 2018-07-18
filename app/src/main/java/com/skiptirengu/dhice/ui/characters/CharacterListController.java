package com.skiptirengu.dhice.ui.characters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.conductor.viewstate.lce.MvpLceViewStateController;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.ParcelableListLceViewState;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.fragments.CharacterListAdapter;
import com.skiptirengu.dhice.storage.Character;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharacterListController extends MvpLceViewStateController<FrameLayout, List<Character>, CharacterListContract.View, CharacterListContract.Presenter>
        implements CharacterListContract.View {

    @BindView(R.id.contentView)
    protected FrameLayout mLayout;
    @BindView(R.id.loadingView)
    protected FrameLayout mProgress;
    @BindView(R.id.listview_characters)
    protected ListView mListView;

    private CharacterListAdapter mAdapter;

    protected String getTitle() {
        return getApplicationContext().getString(R.string.title_characters);
    }

    @NonNull
    @Override
    public CharacterListContract.Presenter createPresenter() {
        return new CharacterListPresenter();
    }

    public void showLoading(boolean pullToRefresh) {
        mProgress.setVisibility(View.VISIBLE);
        mLayout.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.character_list_fragment, container, false);
        ButterKnife.bind(this, view);
        initListView(view.getContext());
        return view;
    }

    @Override
    public List<Character> getData() {
        return mAdapter.getItems();
    }

    @Override
    public void setData(List<Character> data) {
        mAdapter.clear();
        mAdapter.addAll(data);
    }

    @NonNull
    @Override
    public LceViewState<List<Character>, CharacterListContract.View> createViewState() {
        return new ParcelableListLceViewState<>();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return "Ur mom gay";
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadCharacters();
    }

    private void initListView(Context context) {
        mAdapter = new CharacterListAdapter(context, new ArrayList<>());
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener((parent, view, position, id) -> onCharacterClicked(mAdapter.getItem(position)));
    }

    @Override
    public void onCharacterClicked(Character character) {
        Toast.makeText(getView().getContext(), "Clicked character", Toast.LENGTH_SHORT).show();
    }

    @Override
    @OnClick(R.id.fab_new_character)
    public void onNewCharacterClicked(View view) {
        Toast.makeText(view.getContext(), "Clicked fab", Toast.LENGTH_SHORT).show();
    }
}
