package com.skiptirengu.dhice.ui.characterlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.ParcelableListLceViewState;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.fragments.OnMenuItemPressedListener;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.ui.base.RxMvpLceViewStateController;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharacterListController extends
        RxMvpLceViewStateController<FrameLayout, List<Character>, CharacterListContract.View, CharacterListContract.Presenter>
        implements CharacterListContract.View, OnMenuItemPressedListener {

    @BindView(R.id.contentView)
    protected FrameLayout mLayout;
    @BindView(R.id.loadingView)
    protected FrameLayout mProgress;
    @BindView(R.id.recylerView)
    protected RecyclerView mRecyclerView;

    private CharacterListAdapter mRecyclerAdapter;

    protected String getTitle() {
        return Objects.requireNonNull(getApplicationContext()).getString(R.string.title_characters);
    }

    @NonNull
    @Override
    public CharacterListContract.Presenter createPresenter() {
        return new CharacterListPresenter();
    }

    @NonNull
    @Override
    protected View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        View view = inflater.inflate(R.layout.characterlist, container, false);
        ButterKnife.bind(this, view);
        initListView(view.getContext());
        return view;
    }

    @Override
    public List<Character> getData() {
        return mRecyclerAdapter.getItems();
    }

    @Override
    public void setData(List<Character> data) {
        mRecyclerAdapter.setItems(data);
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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mRecyclerAdapter = new CharacterListAdapter();
        mRecyclerView.setAdapter(mRecyclerAdapter);
        disposeOnDetach(mRecyclerAdapter.click().subscribe(character -> onCharacterClicked(character)));
    }

    @Override
    public void onCharacterClicked(Character character) {
        Toast.makeText(Objects.requireNonNull(getView()).getContext(), "Clicked character", Toast.LENGTH_SHORT).show();
    }

    @Override
    @OnClick(R.id.fab_new_character)
    public void onNewCharacterClicked(View view) {
        Toast.makeText(view.getContext(), "Clicked fab", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMenuItemPressed(int menuItemId) {
        return menuItemId == R.id.navigation_characters;
    }
}
