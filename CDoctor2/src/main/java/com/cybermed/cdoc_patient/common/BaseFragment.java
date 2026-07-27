package com.cybermed.cdoc_patient.common;


import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;


import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.me.securemessages.Filterable;
import com.cybermed.cdoc_patient.me.securemessages.adapter.SearchAdapter;
import com.cybermed.cdoc_patient.view.CdocProgressBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Joshua and Daniel on 2018/08/02.
 */

public abstract class BaseFragment extends Fragment implements FragmentInterface {

    public static final int MAINFRAGMENTTYPE = 0;
    public static final int USERFRAGMENTTYPE = 1;
    public static final String PASTAPPT = "0";
    public static final String FUTUREAPPT = "1";
    private AsyncTask getPatientApptHistoryTask;
    private CdocProgressBar mLoader;

    public Toolbar initFragToolBar(View view, String title) {
        final FragmentMainActivity fragMain = (FragmentMainActivity) getActivity();
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        fragMain.setSupportActionBar(toolbar);
        fragMain.getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_arrow));
        toolbar.setTitleTextColor(Color.WHITE);
        TextView mTitle = toolbar.findViewById(R.id.toolbar_title);
        mTitle.setText(title);

        return toolbar;
    }

    /**
     * abstract method to fetch data from child fragments to make default implementation in the parent fragment
     *
     * @param inflater           : {@link LayoutInflater} for inflating the view of the child fragment
     * @param container          : {@link ViewGroup} containing the fragment
     * @param savedInstanceState : {@link Bundle} for the savedState
     * @return {@link View} inflated using inflater
     */
    protected abstract View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    /**
     * Use this calllback in child fargment for UI related tasks, as it is called after View is created for the fragment
     *
     * @param view : inflated {@link View}
     */
    protected abstract void initLayout(View view);

    @Override
    public final View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getContentView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public final void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayout(view);
    }

    public String getDate() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.US);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public void fragmentOpened(int data) {

    }

    @Override
    public void refreshFragment(boolean isRefresh) {

    }


    public void toastShortInfo(String value) {
        Toast.makeText(getActivity(), value, Toast.LENGTH_SHORT).show();
    }

    public void toastLongInfo(String value) {
        Toast.makeText(getActivity(), value, Toast.LENGTH_LONG).show();
    }

    public void showProgress() {
        if (mLoader == null) {
            mLoader = new CdocProgressBar(getActivity());
            mLoader.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            //mLoader.setCancelable(false);
        }

        if (mLoader != null && !mLoader.isShowing()) {
            mLoader.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgress();
            }
        }, 20000);
    }

    public void hideProgress() {
        if ((getActivity() != null && !getActivity().isFinishing()) && mLoader != null && mLoader.isShowing())
            mLoader.dismiss();
    }

    protected Dialog showSearchDialog(SearchAdapter<? extends Filterable> searchAdapter, SearchAdapter.CancelListener cancelListener) {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).
                setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.search_dialog);
        final WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        // Dialog searchDialog = AppUtility.setDialogProperty(getActivity(), R.layout.search_dialog);
        dialog.setCancelable(true);
        RecyclerView recycleView = dialog.findViewById(R.id.recycler_view);
        recycleView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recycleView.setHasFixedSize(true);
        recycleView.setAdapter(searchAdapter);
        AppCompatEditText inputView = dialog.findViewById(R.id.input_search);
//        inputView.setOnTouchListener((view, motionEvent) -> {
//            checkForCustomKeyboard(inputView);
//            return false;
//        });
//        ImageView ivDelete = dialog.findViewById(R.id.iv_delete);
        inputView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
//                if (!checkForCustomKeyboard(inputView)) {
          //      ivDelete.setVisibility(TextUtils.isEmpty(editable.toString()) ? View.GONE : View.VISIBLE);
                searchAdapter.getFilter().filter(editable.toString());
//                }

            }
        });

//        dialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
//            dialog.cancel();
//            cancelListener.onCancel();
//        });
        dialog.setOnCancelListener(dialogInterface -> {
            // ((BaseActivity) getActivity()).hideKeyboard();
        });
        //ivDelete.setOnClickListener(view -> inputView.setText(""));

        dialog.show();
        return dialog;
    }


}
