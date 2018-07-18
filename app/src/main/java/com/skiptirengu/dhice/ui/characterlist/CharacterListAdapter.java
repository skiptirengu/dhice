package com.skiptirengu.dhice.ui.characterlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.storage.Character;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class CharacterListAdapter extends RecyclerView.Adapter<CharacterListAdapter.CharacterListViewHolder> {

    private final PublishSubject<Character> mOnClickSubject = PublishSubject.create();
    private List<Character> mItems = new ArrayList<>();

    @NonNull
    @Override
    public CharacterListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CharacterListViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.characterlist_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterListViewHolder holder, int position) {
        final Character character = mItems.get(position);
        holder.txvCharacterName.setText(getDisplayText(character));
        holder.itemView.setOnClickListener(v -> mOnClickSubject.onNext(character));
    }

    @NonNull
    private String getDisplayText(@NonNull Character character) {
        return String.format("%s - %s", character.getName(), character.getRace());
    }

    @NonNull
    public Observable<Character> click() {
        return mOnClickSubject;
    }

    @NonNull
    public List<Character> getItems() {
        return mItems;
    }

    public void setItems(List<Character> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class CharacterListViewHolder extends RecyclerView.ViewHolder {

        TextView txvCharacterName;

        CharacterListViewHolder(View itemView) {
            super(itemView);
            txvCharacterName = itemView.findViewById(R.id.txv_character_name);
        }
    }
}
