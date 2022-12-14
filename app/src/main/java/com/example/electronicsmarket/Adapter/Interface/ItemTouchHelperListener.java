package com.example.electronicsmarket.Adapter.Interface;

public interface ItemTouchHelperListener {
    boolean onItemMove(int from_position,int to_position);
    void onItemSwipe(int position);
}
