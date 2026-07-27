package com.cybermed.cdoc_patient.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cybermed.cdoc_patient.R;

/**
 * Created by qinwe on 2017/5/8.
 */

public class MyAlertDialog extends Dialog implements View.OnClickListener {
    private Context context;
    private TextView tvTitle,tvContent;
    private Button leftBtn,rightBtn;
    private View viewLine;
    private LeftClickListener leftClickListener;
    private RightClickListener rightClickListener;

    public MyAlertDialog(Context context) {
        super(context);
        this.context = context;
        this.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
    }

    public MyAlertDialog(Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_my_alert);
        initView();
    }

    private void initView(){
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);
        leftBtn = findViewById(R.id.tv_left);
        rightBtn = findViewById(R.id.tv_right);
       // viewLine = findViewById(R.id.view_line);
        leftBtn.setOnClickListener(this);
        rightBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_left:
                leftClickListener.onLeftClick(v);
                dismiss();
                break;
            case R.id.tv_right:
                rightClickListener.onRightClick(v);
                dismiss();
                break;
        }
    }

    public void setDialogTitle(String title){
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
    }

    public void setDialogContent(String content){
        tvContent.setText(content);
    }

     public interface LeftClickListener{
        void onLeftClick(View view);
    }

    public interface RightClickListener{
        void onRightClick(View view);
    }

    public void setLeftClickListener(String btnStr,LeftClickListener leftClickListener){
        this.leftClickListener = leftClickListener;
        leftBtn.setVisibility(View.VISIBLE);
      //  viewLine.setVisibility(View.VISIBLE);
        leftBtn.setText(btnStr);
    }

    public void setRightClickListener(String btnStr,RightClickListener rightClickListener){
        this.rightClickListener = rightClickListener;
        rightBtn.setVisibility(View.VISIBLE);
        rightBtn.setText(btnStr);
    }

}
