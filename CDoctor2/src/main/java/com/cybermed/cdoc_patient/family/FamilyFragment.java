package com.cybermed.cdoc_patient.family;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.WebServiceID;
import com.cdfortis.datainterface.soap.model.Represented_Patient;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.login.ProceedLogin;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.cybermed.cdoc_patient.main.HomeFragment;
import com.cybermed.cdoc_patient.me.MeFragment;
import com.cybermed.cdoc_patient.util.ErrorMessage;
import com.cybermed.cdoc_patient.view.MyAlertDialog;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.List;


public class FamilyFragment extends BaseFragment implements View.OnClickListener, FamilyCallback, SwipeRefreshLayout.OnRefreshListener, MeFragment.OnInnerFragmentStatusChange {

    private View view;
    private FragmentMainActivity fragMain;
    private boolean fromUserFragment;
    private AuthRepAdapter authRepAdapter;
    private EditText mFamilyEmailInput;
    private TextView mErrorRelationship, mErrorEmail,toolbar_title;
    private SwipeRefreshLayout swipeContainer;
    Button btnSwitchToCurrentAcc;
    String originalAccount;
    LinearLayout emptyView;
    Button btnAddFamilyMember;


    public FamilyFragment() {
    }

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        view = inflater.inflate(R.layout.fragment_family, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        fragMain = (FragmentMainActivity) getActivity();

        emptyView = view.findViewById(R.id.emptyView);
        btnAddFamilyMember = view.findViewById(R.id.add_family_member);
        swipeContainer = view.findViewById(R.id.swipeRefreshLayout);
        toolbar_title = view.findViewById(R.id.toolbar_title);
        swipeContainer.setOnRefreshListener(this);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        authRepAdapter = new AuthRepAdapter(getContext());
        recyclerView.setAdapter(authRepAdapter);

        fromUserFragment = getArguments().getBoolean("TYPE");

        initView();
        GetAuthRep();

        if (fromUserFragment) {
            ImageView backbtn = view.findViewById(R.id.back_btn);
            backbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MeFragment) getParentFragment() != null)) {
                        ((MeFragment) getParentFragment()).openUserActivityFragment();
                    }
                }
            });
        }
    }


    private void initView() {
        originalAccount = CDoctor2Application.getLoginInfo().getOriginalAccount();
        btnSwitchToCurrentAcc = view.findViewById(R.id.btn_current);
        ImageView mFamilyAddBtn = view.findViewById(R.id.btn_add_family_dialog);
        LinearLayout mTabletBtnLayout = view.findViewById(R.id.btn_layout);
        ImageView mTabletFamilyAddBtn = view.findViewById(R.id.btn_add_family);
        ImageView mTabletHomeBtn = view.findViewById(R.id.btn_back_home);
        mFamilyAddBtn.setOnClickListener(this);
        mTabletHomeBtn.setOnClickListener(this);
        mTabletFamilyAddBtn.setOnClickListener(this);

        if (!fromUserFragment) {
            mTabletBtnLayout.setVisibility(View.VISIBLE);
            mFamilyAddBtn.setVisibility(View.GONE);
        }
        btnSwitchToCurrentAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountSwitch(originalAccount, null);
            }
        });
    }

    private void GetAuthRep() {
        showProgress();
        if (!originalAccount.equals(CDoctor2Application.getLoginInfo().getAccount())) {
            btnSwitchToCurrentAcc.setVisibility(View.VISIBLE);
        } else btnSwitchToCurrentAcc.setVisibility(View.GONE);
        OnPostExecute ope = result -> {
            hideProgress();
            swipeContainer.setRefreshing(false);

            List<Represented_Patient> patientList = new ArrayList<>(new SoapObjectVector<>(Represented_Patient.class, (SoapObject) result));
            if (patientList.size() > 0 && patientList.get(0).errorMsg != null) {
                Log.e("WS:get_rep_patients", patientList.get(0).errorMsg);
                ErrorMessage.alertDialog(getContext(), getString(R.string.server_error), getString(R.string.retrieve_auth_list_fail), () -> {
                    HomeFragment.getInstance().openTabletMainFragment();
                });
            } else {
                if (patientList.size() == 0) {
                    emptyView(true);
                    toolbar_title.setText(R.string.add_family_member);
                } else {
                    toolbar_title.setText(R.string.family_member_title);
                    emptyView(false);
                    authRepAdapter.appendList(patientList);
                    //TODO: set callback when adapter is created
                    authRepAdapter.setOnPatientSelected(represented_patient -> {
                        accountSwitch(originalAccount, represented_patient);
                    });
                    authRepAdapter.setOnPatientDeleted(represented_patient -> {
                        if (fragMain.getLoginInfo2().getAccount().equals(represented_patient.user_id)) {
                            ProceedLogin.logout(getActivity());
                            login(originalAccount, originalAccount);
                        }
                        onRefresh();
                    });
                }
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.get_rep_patients, ope, originalAccount);
    }

    void emptyView(boolean isVisible) {
        if (isVisible) {
            emptyView.setVisibility(View.VISIBLE);
        } else emptyView.setVisibility(View.GONE);
        btnAddFamilyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFamily();
            }
        });
    }

    private void accountSwitch(String originalAccount, Represented_Patient represented_patient) {
        String username = null;
        if (represented_patient != null) {
            username = represented_patient.first_name + " " + represented_patient.last_name;
        }
        MyAlertDialog dialog = new MyAlertDialog(getActivity());
        dialog.show();
        dialog.setDialogTitle(getString(R.string.use_other_account));
        if (represented_patient == null) {
            dialog.setDialogContent(getString(R.string.confirm_switch_back_own_account_msg));
        } else {
            String dialogMsg = getString(R.string.confirm_switch_account_message, username);
            CharSequence styledDialogMsg = Html.fromHtml(dialogMsg);
            dialog.setDialogContent(styledDialogMsg.toString());
        }
        dialog.setLeftClickListener(getString(R.string.btn_cancel), new MyAlertDialog.LeftClickListener() {
            @Override
            public void onLeftClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.setRightClickListener(getString(R.string.btn_ok), view -> {
            dialog.dismiss();
            ProceedLogin.logout(getActivity());
            if (represented_patient != null) {
                login(originalAccount, represented_patient.user_id);
            } else login(originalAccount, originalAccount);
        });

    }

    private void login(String originId, String userId) {
        ProceedLogin.loginAsRep(getActivity(), originId, userId, CDoctor2Application.getLoginInfo().getPwd(), () -> {
                    String username = CDoctor2Application.getLoginInfo().getUserInfo().getFirstName() + " " + CDoctor2Application.getLoginInfo().getUserInfo().getLastname();
                    String switchSuccessMsg = getString(R.string.switch_success_message, username);
                    CharSequence styledSwitchSuccessMsg = Html.fromHtml(switchSuccessMsg);
                    ErrorMessage.alertDialog(getContext(), getString(R.string.switch_success_title), styledSwitchSuccessMsg, () -> {
                        authRepAdapter.notifyDataSetChanged();
                        if (!originalAccount.equals(CDoctor2Application.getLoginInfo().getAccount())) {
                            btnSwitchToCurrentAcc.setVisibility(View.VISIBLE);
                        } else btnSwitchToCurrentAcc.setVisibility(View.GONE);
                        fragMain.homefragment.openMainFragment();
                    });

                }, () -> ErrorMessage.alertDialog(getContext(), getString(R.string.server_error), getString(R.string.switch_fail_message), null)
        );
    }


    private void addAuthRep(String userId, String pwd, Runnable onAddSuccess) {
        OnPostExecute ope = result -> {
            String message = result.toString();
            if (message.equals("1")) {
                if (onAddSuccess != null) {
                    onAddSuccess.run();
                }
                ErrorMessage.alertDialog(getContext(), getString(R.string.success_dialog_title), getString(R.string.add_representive_success), null);
                onRefresh();
            } else if (message.equals("Invalid user id")) {
                mFamilyEmailInput.setError(getString(R.string.family_member_email_error));
                mFamilyEmailInput.requestFocus();
            } else if (message.equals("Relation already exist")) {
                ErrorMessage.alertDialog(getContext(), getString(R.string.notice_title), getString(R.string.relation_already_exist), null);
            } else if (message.equals("Incorrect password")) {
                ErrorMessage.alertDialog(getContext(), getString(R.string.error_dialog_title), getString(R.string.incorrect_password), null);
            } else if (message.equalsIgnoreCase("Can't add yourself")) {
                ErrorMessage.alertDialog(getContext(), getString(R.string.error_dialog_title), "Cannot add yourself as a family member", null);
            } else {
                Toast.makeText(getContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
            }
        };
        WebService.webServiceAsyncTask(WebServiceID.create_auth_link, ope, fragMain.getLoginInfo2().getOriginalAccount(), userId, pwd);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.btn_add_family_dialog:
            case R.id.btn_add_family:
                addFamily();
                break;

            case R.id.btn_back_home:
                HomeFragment.getInstance().openTabletMainFragment();
                break;

        }
    }

    private void addFamily() {

        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_new_family, null);

        dialogView.findViewById(R.id.btn_personal_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                addByPatientInfoDialog();
            }
        });
        dialogView.findViewById(R.id.btn_email).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                addByEmailDialog();
            }
        });
        ImageView imgClose = dialogView.findViewById(R.id.imgclose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void addByPatientInfoDialog() {
        PatientInfoDialog dialog = new PatientInfoDialog(getActivity());
        dialog.setCallBack(new PatientInfoDialog.ButtonCallBack() {
            @Override
            public void onPatientExist(String userId, String fullname) {
                dialog.dismiss();

                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.dialog_add_family_no_id, null);
                EditText email = dialogView.findViewById(R.id.email_txt);
                EditText pwdInput = dialogView.findViewById(R.id.edit_password);
                Button addBtn = dialogView.findViewById(R.id.btn_add_family);
                ImageView password_hide_button = dialogView.findViewById(R.id.password_hide_button);
                LinearLayout lin_email = dialogView.findViewById(R.id.lin_email2);
                CardView card_user = dialogView.findViewById(R.id.card_user);
                TextView rep_name = dialogView.findViewById(R.id.rep_name);
                TextView rep_account = dialogView.findViewById(R.id.rep_account);
                email.setText(getString(R.string.add_family_member_no_id, userId));
                lin_email.setVisibility(View.GONE);
                card_user.setVisibility(View.VISIBLE);
                rep_name.setText(fullname);
                rep_account.setText(userId);
                addBtn.setOnClickListener(v -> {
                    addAuthRep(userId, pwdInput.getText().toString(), () -> alertDialog.dismiss());
                });
                password_hide_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        pwdInput.setTypeface(Typeface.DEFAULT);
                        pwdInput.setTransformationMethod(new PasswordTransformationMethod());
                        if (view.getTag() == "0") {
                            view.setTag("1");
                            pwdInput.setTransformationMethod(null);
                            pwdInput.setSelection(pwdInput.length());
                            password_hide_button.setImageResource(R.drawable.pass_show);
                        } else {
                            view.setTag("0");
                            pwdInput.setTransformationMethod(new PasswordTransformationMethod());
                            pwdInput.setSelection(pwdInput.length());
                            password_hide_button.setImageResource(R.drawable.pass_hide);
                        }
                    }
                });
                alertDialog.setView(dialogView);
                alertDialog.show();
            }

            @Override
            public void onPatientNotExist(String firstName, String lastName, String gender, String dob, String zip_code) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle(getString(R.string.register_new_family_title));
                alertDialog.setMessage(getString(R.string.register_new_family));

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, getString(R.string.btn_ok), (dialog1, which) -> {
                    alertDialog.dismiss();
                    RegisterFamilyMember registerDialog = RegisterFamilyMember.newInstance(getActivity(), (user_id, password) -> {
                        addAuthRep(user_id, password, null);
                        dialog.dismiss();
                    });
                    registerDialog.setBasicInfo(firstName, lastName, gender, dob, zip_code);
                    registerDialog.show();
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getString(R.string.btn_cancel), (dialog2, which) -> {
                    alertDialog.dismiss();
                });
                alertDialog.show();
            }
        });
    }


    private void addByEmailDialog() {
        final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_family_no_id, null);
        mFamilyEmailInput = dialogView.findViewById(R.id.email_txt);
        EditText mFamilyPwdInput = dialogView.findViewById(R.id.edit_password);
        ImageView password_hide_button = dialogView.findViewById(R.id.password_hide_button);
        mErrorEmail = dialogView.findViewById(R.id.error_select_email);
        mErrorRelationship = dialogView.findViewById(R.id.error_select_relationship);
        password_hide_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFamilyPwdInput.setTypeface(Typeface.DEFAULT);
                mFamilyPwdInput.setTransformationMethod(new PasswordTransformationMethod());
                if (view.getTag() == "0") {
                    view.setTag("1");
                    mFamilyPwdInput.setTransformationMethod(null);
                    mFamilyPwdInput.setSelection(mFamilyPwdInput.length());
                    password_hide_button.setImageResource(R.drawable.pass_show);
                } else {
                    view.setTag("0");
                    mFamilyPwdInput.setTransformationMethod(new PasswordTransformationMethod());
                    mFamilyPwdInput.setSelection(mFamilyPwdInput.length());
                    password_hide_button.setImageResource(R.drawable.pass_hide);
                }
            }
        });
        mFamilyPwdInput.setOnEditorActionListener((v, actionId, event) -> {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                mErrorEmail.setVisibility(View.GONE);
                mErrorRelationship.setVisibility(View.GONE);
                if (inputCheck()) {
                    addAuthRep(mFamilyEmailInput.getText().toString().trim(), mFamilyPwdInput.getText().toString(), () -> dialogBuilder.dismiss());
                }

            }
            return false;
        });
        Button saveBtn = dialogView.findViewById(R.id.btn_add_family);

        saveBtn.setOnClickListener(view -> {
            mErrorEmail.setVisibility(View.GONE);
            mErrorRelationship.setVisibility(View.GONE);
            if (inputCheck()) {
                addAuthRep(mFamilyEmailInput.getText().toString().trim(), mFamilyPwdInput.getText().toString(), () -> dialogBuilder.dismiss());
            }
        });
        ImageView imgClose = dialogView.findViewById(R.id.imgClose);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBuilder.dismiss();
            }
        });
        dialogBuilder.setView(dialogView);
        dialogBuilder.show();
    }

    private boolean inputCheck() {

        if (TextUtils.isEmpty(mFamilyEmailInput.getText().toString())) {
            mFamilyEmailInput.setError(getString(R.string.family_member_email_empty));
            mFamilyEmailInput.requestFocus();
            return false;
        }

        if (!isEmailValid(mFamilyEmailInput.getText().toString().trim())) {
            mFamilyEmailInput.setError(getString(R.string.family_member_email_validation));
            mFamilyEmailInput.requestFocus();
            return false;
        }

        return true;
    }

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    @Override
    public void removeFamily(String familyMemberUserId) {

    }


    @Override
    public void onRefresh() {
        GetAuthRep();
    }

    @Override
    public void onMyResume() {
        GetAuthRep();
    }

    @Override
    public void onMyStop() {

    }

}
