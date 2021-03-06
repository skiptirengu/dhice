package com.skiptirengu.dhice.ui.characterlist;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby3.mvp.viewstate.lce.data.ParcelableListLceViewState;
import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.exception.EmptyResultException;
import com.skiptirengu.dhice.fragments.OnMenuItemPressedListener;
import com.skiptirengu.dhice.storage.Character;
import com.skiptirengu.dhice.ui.base.RxMvpLceViewStateController;
import com.skiptirengu.dhice.ui.character.CharacterController;

import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CharacterListController extends
        RxMvpLceViewStateController<FrameLayout, List<Character>, CharacterListContract.View, CharacterListContract.Presenter>
        implements CharacterListContract.View, OnMenuItemPressedListener {

    @BindView(R.id.recylerView)
    protected RecyclerView mRecyclerView;
    private CharacterListAdapter mRecyclerAdapter;

    {
        setHasEmptyState(true);
    }

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
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        errorView.setClickable(false);
    }

    @Override
    public List<Character> getData() {
        return mRecyclerAdapter.getItems();
    }

    @Override
    public void setData(List<Character> data) {
        mRecyclerAdapter.setItems(data);
    }

    @Override
    protected void onRestoreViewState(@NonNull View view, @NonNull Bundle savedViewState) {
        super.onRestoreViewState(view, savedViewState);
    }

    @NonNull
    @Override
    public LceViewState<List<Character>, CharacterListContract.View> createViewState() {
        return new ParcelableListLceViewState<>();
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        Context context = Objects.requireNonNull(getApplicationContext());
        if (e instanceof EmptyResultException) {
            return context.getString(R.string.error_nothing_to_see);
        } else {
            return context.getString(R.string.error_occurred);
        }
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadCharacters();
    }

    private void initListView(Context context) {
        mRecyclerAdapter = new CharacterListAdapter();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        disposeOnDetach(mRecyclerAdapter.click().subscribe(character -> onCharacterClicked(character)));
    }

    @Override
    public void onCharacterClicked(Character character) {
        goToCharacter(character.getId());
    }

    @Override
    @OnClick(R.id.fab_new_character)
    public void onNewCharacterClicked(View view) {
        goToCharacter(0);
    }

    @Override
    public boolean onMenuItemPressed(int menuItemId) {
        return menuItemId == R.id.navigation_characters;
    }

    @Override
    protected int getEmptyDrawable() {
        return R.drawable.ic_group_black_128dp;
    }

    private void goToCharacter(int id) {
        CharacterController controller = new CharacterController();
        controller.getArgs().putInt(CharacterController.INTENT_CHARACTER_ID, id);
        getRouter().pushController(
                RouterTransaction.with(controller)
                        .pushChangeHandler(new HorizontalChangeHandler())
                        .popChangeHandler(new HorizontalChangeHandler())
        );
    }
}
