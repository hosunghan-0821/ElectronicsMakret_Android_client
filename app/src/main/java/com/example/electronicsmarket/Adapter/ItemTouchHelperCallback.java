package com.example.electronicsmarket.Adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.electronicsmarket.Adapter.Interface.ItemTouchHelperListener;

//public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
//
//    private ItemTouchHelperListener listener;
//    public ItemTouchHelperCallback(ItemTouchHelperListener listener) {
//        this.listener=listener;
//    }
//
//    @Override
//    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//        int drag_flags=ItemTouchHelper.LEFT|ItemTouchHelper.RIGHT;
//        int swipe_flags=ItemTouchHelper.START|ItemTouchHelper.END;
//
//        return makeMovementFlags(drag_flags,swipe_flags);
//    }
//
//    @Override
//    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//       return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//    }
//
//    @Override
//    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//    }
//
//
//}
public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperListener listener;
    private Adapter_post_image adapter;

    public ItemTouchHelperCallback(Adapter_post_image adapter,ItemTouchHelperListener listener){
        this.adapter =adapter;
        this.listener = listener;
    }
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        Log.e("123"," getMovementFlags");
        int drag_flags = 0;
        int swipe_flags = ItemTouchHelper.START|ItemTouchHelper.END;
        return makeMovementFlags(swipe_flags,drag_flags);
        //return makeMovementFlags(drag_flags, swipe_flags);
    }

    @Override
    public boolean isLongPressDragEnabled() {
        Log.e("123","롱클릭");
        return super.isLongPressDragEnabled();
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        Log.e("123"," 아이템 움직임 ");
        return listener.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        Log.e("123"," 스와이프 ");
        listener.onItemSwipe(viewHolder.getAdapterPosition());

    }
}
