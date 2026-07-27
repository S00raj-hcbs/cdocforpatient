package com.cybermed.cdoc_patient.payment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.webapi.model.response.SquareCard;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qinwe on 2017/5/3.
 */

public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;
    private List<SquareCard> list;
    private boolean isShowFocus;
    private boolean[] mChecked;
    private int mSelectedItem = -1;
    private int TAG_UNSELECTED = 0;
    private int TAG_SELECTED = 1;
    private LinearLayout creditAdapterLayout;
    // private CheckBox paymentCheck;
    private boolean checkBoxVisiblity;

    private ICreditCardCallback ICreditCardCallback;
    int selectedPosition = -1;

    public interface ICreditCardCallback {
        void delete(SquareCard squareCard);

        void itemSelect(SquareCard squareCard);
    }

    public CreditCardAdapter(Context context, boolean isShowFoucs, boolean checkBoxVisibility, ICreditCardCallback ICreditCardCallback) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        list = new ArrayList<>();
        this.isShowFocus = isShowFoucs;
        this.checkBoxVisiblity = checkBoxVisibility;
        this.ICreditCardCallback = ICreditCardCallback;
    }

    public void appendList(List<SquareCard> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCardType;
        TextView cardNum, nameTxt;
        LinearLayout creditAdapterLayout;
        RadioButton radioItemSelect;
        ImageView deleteBtn;
        LinearLayout viewParent;

        public ViewHolder(View v) {
            super(v);
            imgCardType = (ImageView) v.findViewById(R.id.imgCardType);
            cardNum = (TextView) v.findViewById(R.id.cardNum);
            //nameTxt = (TextView) v.findViewById(R.id.nameTxt);
            radioItemSelect = v.findViewById(R.id.radio_card);
            viewParent = v.findViewById(R.id.view_parent);
            //  creditAdapterLayout = (LinearLayout) v.findViewById(R.id.creditAdapterLayout);
            deleteBtn = v.findViewById(R.id.deleteBtn);
            viewParent.setOnClickListener(v1 -> {
                selectedPosition = getAdapterPosition();
                ICreditCardCallback.itemSelect(list.get(getAdapterPosition()));
                notifyDataSetChanged();
            });
            radioItemSelect.setOnClickListener(v1 -> {
                selectedPosition = getAdapterPosition();
                ICreditCardCallback.itemSelect(list.get(getAdapterPosition()));
                notifyDataSetChanged();
            });
            deleteBtn.setOnClickListener(view -> {
                AlertDialog alertDialog = new AlertDialog.Builder(context).create();
                alertDialog.setTitle(context.getString(R.string.btn_confirm));
                alertDialog.setMessage(context.getString(R.string.delete_card));

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, context.getString(R.string.btn_ok), (dialog1, which) -> {
                    alertDialog.dismiss();
                    ICreditCardCallback.delete(list.get(getAdapterPosition()));
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, context.getString(R.string.btn_cancel), (dialog2, which) -> {
                    alertDialog.dismiss();
                });
                alertDialog.show();
            });
            if (checkBoxVisiblity) {
                radioItemSelect.setVisibility(View.VISIBLE);
            } else {
                radioItemSelect.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_payment_card_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SquareCard cardInfo = list.get(position);

        int type = getItemViewType(position);
        Log.d("check", String.valueOf(type));

        String cardNumber = cardInfo.getLast_4();
        holder.cardNum.setText(context.getString(R.string.card_end, cardNumber));


        switch (cardInfo.getCard_brand().toLowerCase()) {
            case "visa":
                holder.imgCardType.setImageResource(R.drawable.visa);
                break;
            case "mastercard":
                holder.imgCardType.setImageResource(R.drawable.mastercard);
                break;
            case "discover":
                holder.imgCardType.setImageResource(R.drawable.discover);
                break;
            case "american_express":
                holder.imgCardType.setImageResource(R.drawable.amex);
                break;

        }
        if (checkBoxVisiblity) {
            if (selectedPosition == position) {
                holder.radioItemSelect.setChecked(true);
               //// holder.viewParent.setBackgroundColor(ContextCompat.getColor(context, R.color.color_ecf6fb));
            } else {
                holder.radioItemSelect.setChecked(false);
            //    holder.viewParent.setBackgroundColor(ContextCompat.getColor(context, R.color.white_0_2));
            }
        } else {
           // holder.viewParent.setBackgroundColor(ContextCompat.getColor(context, R.color.color_ecf6fb));
        }



    }


    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position == mSelectedItem ? TAG_SELECTED : TAG_UNSELECTED;
    }


    public void unSelectItem() {
        mSelectedItem = -1;
        notifyDataSetChanged();
    }


}
