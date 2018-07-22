package com.skiptirengu.dhice.ui.character;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.skiptirengu.dhice.R;
import com.skiptirengu.dhice.databinding.CharacterBonusBinding;
import com.skiptirengu.dhice.storage.CharacterBonus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class CharacterBonusAdapter extends RecyclerView.Adapter<CharacterBonusAdapter.CharacterBonusViewHolder> {

    private final Object mListLock = new Object();
    private List<CharacterBonus> mList;
    private PublishSubject<Integer> mOnClickSubject = PublishSubject.create();

    CharacterBonusAdapter() {
        mList = new ArrayList<>();
    }

    @NonNull
    @Override
    public CharacterBonusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CharacterBonusBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), R.layout.character_bonus, parent, false
        );
        return new CharacterBonusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CharacterBonusViewHolder holder, int position) {
        final int finalPosition = position;
        final CharacterBonus bonus = mList.get(position);
        holder.bind(bonus);
        holder.itemView.findViewById(R.id.btn_delete_bonus).setOnClickListener(v -> mOnClickSubject.onNext(finalPosition));
    }

    @Override
    public int getItemCount() {
        synchronized (mListLock) {
            return mList.size();
        }
    }

    public void setList(List<CharacterBonus> list) {
        synchronized (mListLock) {
            mList = list;
        }
    }

    public Observable<Integer> delete() {
        return mOnClickSubject;
    }

    public CharacterBonusAdapter setData(List<CharacterBonus> list) {
        synchronized (mListLock) {
            mList.clear();
            mList.addAll(list);
            notifyDataSetChanged();
            return this;
        }
    }

    public void removeItem(int position) {
        synchronized (mListLock) {
            mList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void addData(CharacterBonus bonus) {
        synchronized (mListLock) {
            int index = 0;
            mList.add(index, bonus);
            notifyItemInserted(index);
        }
    }

    class CharacterBonusViewHolder extends RecyclerView.ViewHolder {
        final CharacterBonusBinding mBinding;

        private CharacterBonusViewHolder(CharacterBonusBinding binding) {
            super(binding.getRoot());
            mBinding = binding;
        }

        void bind(CharacterBonus bonus) {
            mBinding.setBonus(bonus);
            mBinding.executePendingBindings();
        }
    }
}
