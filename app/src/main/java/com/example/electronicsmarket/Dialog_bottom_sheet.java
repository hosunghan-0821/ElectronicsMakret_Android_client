package com.example.electronicsmarket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class Dialog_bottom_sheet  extends BottomSheetDialogFragment {
    private View view;

    private BottomSheetListener mListener;
    private TextView selling,reservation,soldOut;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view =inflater.inflate(R.layout.bottom_sheet_layout,container,false);
        mListener=(BottomSheetListener) getContext();

        selling=view.findViewById(R.id.selling);
        reservation=view.findViewById(R.id.reservation);
        soldOut=view.findViewById(R.id.sold_out);

        selling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("판매중");
                dismiss();
            }
        });
        reservation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("예약중");
                dismiss();
            }
        });
        soldOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onButtonClicked("판매완료");
                dismiss();
            }
        });


        return view;
    }

    public interface BottomSheetListener{
        void onButtonClicked(String text);
    }
}
