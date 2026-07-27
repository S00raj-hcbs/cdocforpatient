package com.cybermed.cdoc_patient.me;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.cdfortis.datainterface.soap.OnPostExecute;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.MyVitals;
import com.cybermed.cdoc_patient.MultiSpinner.MultiSelectionSpinner;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.UnitLocale;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cdfortis.datainterface.soap.WebServiceID.save_pat_vitals;

public class MyVitalsFragment extends BaseFragment implements AdapterView.OnItemSelectedListener, MultiSelectionSpinner.OnMultipleItemsSelectedListener, View.OnClickListener {


    private EditText mWeight;
    private TextView mHeight;
    private EditText mBloodPressureHigh;
    private EditText mBloodPressureLow;
    private EditText mPulse;
    //private EditText mTemperature;
    private EditText mAllergies;
    private CountDownTimer cdTimer;
    private MultiSelectionSpinner allergiesSpinner;
    private Spinner smokeStatusSpinner;
    private TextView txtFt;
    private TextView txtCm;

    private TextView txtKg;
    private TextView txtLb;
    private String heightUnit = "ft";
    private String weightUnit = "lb";
    //private String temperatureUnit = "f";
    private String mWeightTemp = "";
    private String mWeightTemp1 = "";
    //private String mTemperatureTemp = "";
    //private String mTemperatureTemp1 = "";
    private String mHeightTemp = "";
    private String mHeightTemp1 = "";

    private List<String> allergies;

    private MyVitals patientVitals;
    private MeFragment meFragment;

    private View view;
    private FragmentMainActivity fragMain;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_my_vitals, null);
        return view;
    }

    @Override
    protected void initLayout(View view) {
        fragMain = (FragmentMainActivity) getActivity();
        fragMain.checkDataConnectionAndVersion(0);

        meFragment = (MeFragment) getParentFragment();

        initToolBar();

        Bundle mBundle = getArguments();
        if (mBundle != null) {
            Log.d("myvitalsactivity", "bundle");
            patientVitals = (MyVitals) mBundle.getSerializable("myvitals");
        }
        initView();
        initUnit();
        initSpinners();
        initPicker();
        initPatVitals();
    }



    private void initToolBar() {
        Toolbar toolbar = initFragToolBar(view, getString(R.string.my_vitals_header));
        Button updateBtn = (Button) toolbar.findViewById(R.id.updateBtn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                meFragment.openMeActivity(true);
            }
        });

    }

    private void initPicker() {

//        mHeight.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mWeight.clearFocus();
//                mHeight.requestFocus();
//                heightPicker = new OptionsPickerView(getActivity());
//                final ArrayList<ArrayList<ArrayList<String>>> combineOption3 = new ArrayList<>();
//                ;
//                final ArrayList<ArrayList<String>> combineOption2 = new ArrayList<>();
//                ;
//                final ArrayList<String> twoItemsOptions1 = new ArrayList<String>();
//                ;
//
//                if (heightUnit.equals("ft")) {
//                    String[] heightFtArray = getResources().getStringArray(R.array.heightFt);
//                    Collections.addAll(twoItemsOptions1, heightFtArray);
//
//                    final ArrayList<String> twoItemsOptions2 = new ArrayList<String>();
//                    String[] heightInArray = getResources().getStringArray(R.array.heightIn);
//                    Collections.addAll(twoItemsOptions2, heightInArray);
//
//                    combineOption2.add(twoItemsOptions2);
//
//                    final ArrayList<String> twoItemsOptions3 = new ArrayList<String>();
//                    twoItemsOptions3.add("ft/in");
//                    final ArrayList<ArrayList<String>> combineOption3temp = new ArrayList<>();
//                    combineOption3temp.add(twoItemsOptions3);
//                    combineOption3.add(combineOption3temp);
//
//
//                    heightPicker.setPicker(twoItemsOptions1, combineOption2, combineOption3, false);
//                    heightPicker.setTitle("Height");
//                    heightPicker.setCyclic(false, false, false);
//                    heightPicker.setSelectOptions(0, 0, 0);
//                    heightPicker.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//                        @Override
//                        public void onOptionsSelect(int options1, int option2, int options3) {
//                            mHeight.setText(twoItemsOptions1.get(options1) + "' " + twoItemsOptions2.get(option2) + "\"");
//                        }
//                    });
//                    heightPicker.show();
//                } else {
//                    String[] heightCmArray = getResources().getStringArray(R.array.heightCm);
//                    Collections.addAll(twoItemsOptions1, heightCmArray);
//
//                    final ArrayList<String> twoItemsOptions2 = new ArrayList<String>();
//                    twoItemsOptions2.add("cm");
//
//                    combineOption2.add(twoItemsOptions2);
//
//
//                    heightPicker.setPicker(twoItemsOptions1, combineOption2, false);
//                    heightPicker.setTitle("Height");
//                    heightPicker.setCyclic(false, false, false);
//                    heightPicker.setSelectOptions(0, 0);
//                    heightPicker.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//                        @Override
//                        public void onOptionsSelect(int options1, int option2, int options3) {
//                            mHeight.setText(twoItemsOptions1.get(options1));
//                        }
//                    });
//                    heightPicker.show();
//                }
//            }
//
//        });
    }

    private void initSpinners() {
        //Smoke status drop down elements
        String[] smokeStatusArray = getResources().getStringArray(R.array.smokeStatus);
        List<String> smokeStatus = new ArrayList<>();
        Collections.addAll(smokeStatus, smokeStatusArray);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, smokeStatus);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        smokeStatusSpinner.setAdapter(dataAdapter);

        //Smoke status drop down elements
        String[] allergiesArray = getResources().getStringArray(R.array.allergies);
        allergies = new ArrayList<>();
        Collections.addAll(allergies, allergiesArray);

        allergiesSpinner.setEnabled(false);

//        allergiesSpinner.hasNoneOption(true);
//        allergiesSpinner.setAllergy(true);
//        allergiesSpinner.setItems(allergies);
//        allergiesSpinner.setListener(this);
    }

    private void initView() {


        mWeight = (EditText) view.findViewById(R.id.weightEdit);
        mHeight = (TextView) view.findViewById(R.id.heightEdit);
        mBloodPressureHigh = (EditText) view.findViewById(R.id.bloodPressureHighEdit);
        mBloodPressureLow = (EditText) view.findViewById(R.id.bloodPressureLowEdit);
        mPulse = (EditText) view.findViewById(R.id.pulseEdit);

        mAllergies = (EditText) view.findViewById(R.id.allergiesEdit);

        allergiesSpinner = (MultiSelectionSpinner) view.findViewById(R.id.allergiesSpinner);
        smokeStatusSpinner = (Spinner) view.findViewById(R.id.smokeStatusSpinner);

        txtFt = (TextView) view.findViewById(R.id.txtFt);
        txtCm = (TextView) view.findViewById(R.id.txtCm);
        txtLb = (TextView) view.findViewById(R.id.txtLb);
        txtKg = (TextView) view.findViewById(R.id.txtKg);

//        txtFt.setOnClickListener(this);
//        txtCm.setOnClickListener(this);
//        txtLb.setOnClickListener(this);
//        txtKg.setOnClickListener(this);

    }

    private void initUnit() {
        //Change default units depending on System Locale
        if (UnitLocale.getDefault() == UnitLocale.Imperial) {
            setSelected(txtFt);
            setSelected(txtLb);

        } else {
            setSelected(txtCm);
            setSelected(txtKg);

            heightUnit = "cm";
            weightUnit = "kg";
        }
    }


    private void initPatVitals() {

        if (patientVitals != null) {
            if (patientVitals.weight != null && !patientVitals.weight.equals("NULL")) {
                mWeight.setText(patientVitals.weight);
            }

            if (patientVitals.height != null && !patientVitals.height.equals("NULL")) {
                int feet = (Integer.valueOf(patientVitals.height) / 12);
                int inches = (Integer.valueOf(patientVitals.height) % 12);
                Log.d("myvitals", "feet" + String.valueOf(feet));
                Log.d("myvitals", "inches" + String.valueOf(inches));
                mHeight.setText(feet + "\' " + inches + "\"");
            }
            Log.d("heightToken1", mHeight.getText().toString());

            if (patientVitals.smoke_status_code != null && !patientVitals.smoke_status_code.equals("NULL")) {
                if (patientVitals.smoke_status_code.equals("9")) {
                    smokeStatusSpinner.setSelection(8);

                } else {
                    smokeStatusSpinner.setSelection(Integer.valueOf(patientVitals.smoke_status_code));

                }
            }

            if (patientVitals.allergies != null && !patientVitals.allergies.equals("NULL")) {
                String[] getAllergiesArray = patientVitals.allergies.split(",");
                int[] allergiesSelectionList = new int[12];
                String otherAllergies = "";
                for (int i = 0; i < getAllergiesArray.length; i++) {
                    String s = getAllergiesArray[i];
                    s = s.trim();
                    if (allergies.contains(s)) {
                        int allergiesIndex = allergies.indexOf(s);
                        allergiesSelectionList[i] = allergiesIndex;
                    } else {
                        if (otherAllergies.equals("")) {
                            otherAllergies = s;
                        } else {
                            otherAllergies = otherAllergies + ", " + s;
                        }
                    }
                }
                mAllergies.setText(otherAllergies);
                Log.d("allergiestest", String.valueOf(allergiesSelectionList.length));
                if (getAllergiesArray.length != 0 && !getAllergiesArray[0].equals("No Known Allergies ")) {
                    Log.d("allergiestest", "no known allergies");
                    allergiesSpinner.setSelection(allergiesSelectionList, false);
                    allergiesSpinner.removeFirstSelection();
                } else {
                    allergiesSpinner.setSelection(allergiesSelectionList, true);
                }
            }

            if (patientVitals.bph != null && !patientVitals.bph.equals("NULL")) {
                mBloodPressureHigh.setText(patientVitals.bph);
            }
            if (patientVitals.bpl != null && !patientVitals.bpl.equals("NULL")) {
                mBloodPressureLow.setText(patientVitals.bpl);
            }
            if (patientVitals.pulse != null && !patientVitals.pulse.equals("NULL")) {
                mPulse.setText(patientVitals.pulse);
            }
        }
    }

    public void updateInfo() {
        final FragmentMainActivity fragMain = (FragmentMainActivity) getActivity();

        String weight, height, totalHeight, bPH, bPL, temperature, pulse, allergies, smokeStatus;

        totalHeight = "";
        weight = mWeight.getText().toString().trim();
        height = mHeight.getText().toString();
        if (smokeStatusSpinner.getSelectedItemPosition() == 0) {
            smokeStatus = "";
        } else {
            if (smokeStatusSpinner.getSelectedItemPosition() == 8) {
                smokeStatus = String.valueOf(9);

            } else {
                smokeStatus = String.valueOf(smokeStatusSpinner.getSelectedItemPosition());

            }
        }

        String allergiesList = allergiesSpinner.getSelectedItemsAsString().toString();
        String allergiesInput = mAllergies.getText().toString().trim();
        allergies = allergiesList + ", " + allergiesInput;

        if (heightUnit.equals("ft") && !height.equals("")) {
            String[] heightTokens = height.split("'|\"");
            Log.d("heightTokenheight", height);
            Log.d("heightToken", heightTokens[0]);
            Log.d("heightToken", heightTokens[1]);
            totalHeight = String.valueOf(Integer.valueOf(heightTokens[0].trim()) * 12 + Integer.valueOf(heightTokens[1].trim()));
            Log.d("pick", totalHeight);
        } else if (heightUnit.equals("cm")) {
            totalHeight = String.valueOf((int) (0.393701 * Integer.valueOf(height)));
            Log.d("pick", "cm");
        }

        if (weightUnit.equals("kg") && !weight.equals("")) {
            if (Double.valueOf(weight) > 635) {
                mWeight.setError(getString(R.string.input_error));
                mWeight.requestFocus();
                return;
            }

            DecimalFormat df = new DecimalFormat("###.#");
            weight = df.format(Double.valueOf(weight));

            //weight = df.format(Integer.valueOf(weight) * 2.20462);
            Log.d("pick", "kg");
        } else if (!weight.equals("")) {
            if (Double.valueOf(weight) > 1400) {
                mWeight.setError(getString(R.string.input_error));
                mWeight.requestFocus();
                return;
            }
            DecimalFormat df = new DecimalFormat("###.#");
            weight = df.format(Double.valueOf(weight));
        }

        if (weight.equals("0")) {
            mWeight.setError("Weight cannot be 0");
            mWeight.requestFocus();
            return;
        }

        String smokeDescription = "";


        bPH = mBloodPressureHigh.getText().toString();
        bPL = mBloodPressureLow.getText().toString();

        if (bPH.equals("") && !bPL.equals("")) {
            mBloodPressureHigh.setError(getString(R.string.vitals_bph_error));
            mBloodPressureHigh.requestFocus();
            return;
        }

        if (!bPH.equals("") && bPL.equals("")) {
            mBloodPressureLow.setError(getString(R.string.vitals_bpl_error));
            mBloodPressureLow.requestFocus();
            return;
        }

        if (!bPH.equals("") && !bPL.equals("")) {
            if (Integer.valueOf(bPH) < Integer.valueOf(bPL)) {
                mBloodPressureLow.setError(getString(R.string.bpl));
                mBloodPressureLow.requestFocus();
                return;
            }
        }

        pulse = mPulse.getText().toString();

        SavePatVitals(fragMain.getLoginInfo2().getAccount(), "", "", "", "", "", "", allergies, smokeStatus, smokeDescription, "", pulse, weight, totalHeight, bPH, bPL, "");

    }

    private void SavePatVitals(final String userId, final String entry_user_id, final String org_code, final String account, final String chief_complaint, final String medHx, final String socialHx,
                                    final String allergies, final String smoke_status_code, final String smokeDescription, final String temperature, final String pulse, final String weight,
                                    final String height, final String BPH, final String BPL, final String spo2) {

        OnPostExecute ope = result -> {
            int integer = Integer.valueOf(result.toString());
            if (integer == 1) {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(getString(R.string.update_succeed_title));
                alertDialog.setMessage(getString(R.string.update_succeed_msg));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();

            } else {
                AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle(getString(R.string.update_failed_title));
                alertDialog.setMessage(getString(R.string.update_failed_msg));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.btn_ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        };

        WebService.webServiceAsyncTask(save_pat_vitals, ope, userId, entry_user_id, org_code, account, chief_complaint, medHx, socialHx, allergies, temperature, pulse, weight, height, BPH, BPL, spo2);


    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void selectedIndices(List<Integer> indices) {

    }

    @Override
    public void selectedStrings(List<String> strings) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.txtFt:
//                setSelected(txtFt);
//                setUnSelected(txtCm);
//                if (heightUnit.equals("cm") && !mHeight.getText().toString().equals("")) {
//                    if (mHeightTemp.equals("") || !mHeightTemp1.equals(mHeight.getText().toString())) {
//                        mHeightTemp = mHeight.getText().toString().trim();
//                        String height = convertCentimeterToImperial(Double.parseDouble(mHeight.getText().toString()));
//                        mHeight.setText(height);
//                        mHeightTemp1 = height;
//                    } else {
//                        String mHeightTemp2 = mHeightTemp;
//                        mHeightTemp = mHeight.getText().toString().trim();
//                        mHeight.setText(mHeightTemp2);
//                        mHeightTemp1 = mHeightTemp2;
//                    }
//                }
//                heightUnit = "ft";
//                break;
//            case R.id.txtCm:
//                setSelected(txtCm);
//                setUnSelected(txtFt);
//                if (heightUnit.equals("ft") && !mHeight.getText().toString().equals("")) {
//                    if (mHeightTemp.equals("") || !mHeightTemp1.equals(mHeight.getText().toString())) {
//                        mHeightTemp = mHeight.getText().toString().trim();
//                        String height = convertImperialtoCentimeter(mHeight.getText().toString());
//                        mHeight.setText(height);
//                        mHeightTemp1 = height;
//                    } else {
//                        String mHeightTemp2 = mHeightTemp;
//                        mHeightTemp = mHeight.getText().toString().trim();
//                        mHeight.setText(mHeightTemp2);
//                        mHeightTemp1 = mHeightTemp2;
//                    }
//                }
//                heightUnit = "cm";
//                break;
//            case R.id.txtLb:
//                setSelected(txtLb);
//                setUnSelected(txtKg);
//                if (weightUnit.equals("kg") && !mWeight.getText().toString().equals("")) {
//                    if (mWeightTemp.equals("") || !mWeightTemp1.equals(mWeight.getText().toString())) {
//                        mWeightTemp = mWeight.getText().toString().trim();
//                        DecimalFormat df = new DecimalFormat("###.#");
//                        String weightDouble = df.format((Double.parseDouble(mWeight.getText().toString()) * 2.20462));
//                        if (weightDouble.length() > 5) {
//
//                            mWeight.setText(weightDouble.substring(0, 4));
//                            mWeightTemp1 = weightDouble.substring(0, 4);
//                        } else {
//
//                            mWeight.setText(weightDouble);
//                            mWeightTemp1 = weightDouble;
//                        }
//
//                    } else {
//
//                        String mWeightTemp2 = mWeightTemp;
//                        mWeightTemp = mWeight.getText().toString().trim();
//                        mWeight.setText(mWeightTemp2);
//                        mWeightTemp1 = mWeightTemp2;
//
//                    }
//
//                }
//                weightUnit = "lb";
//                break;
//            case R.id.txtKg:
//                setSelected(txtKg);
//                setUnSelected(txtLb);
//                if (weightUnit.equals("lb") && !mWeight.getText().toString().equals("")) {
//                    if (mWeightTemp.equals("") || !mWeightTemp1.equals(mWeight.getText().toString())) {
//                        mWeightTemp = mWeight.getText().toString().trim();
//                        DecimalFormat df = new DecimalFormat("###.#");
//                        String weightDouble = df.format((Double.parseDouble(mWeight.getText().toString()) * 0.453592));
//                        mWeight.setText(weightDouble);
//                        mWeightTemp1 = weightDouble;
//                    } else {
//                        String mWeightTemp2 = mWeightTemp;
//                        mWeightTemp = mWeight.getText().toString().trim();
//                        mWeight.setText(mWeightTemp2);
//                        mWeightTemp1 = mWeightTemp2;
//                    }
//                }
//                weightUnit = "kg";
//                break;
        }
    }

    public static String convertImperialtoCentimeter(String str) {
        str = str.replaceAll("\\s", "");
        System.out.println(str);
        String[] splitString = str.split("'");
        String firstItem = splitString[0];
        double heightInFeet = 0;
        double heightInInches = 0;
        try {
            int feet = Integer.parseInt(firstItem);
            String secondPart = splitString[1].substring(0, splitString[1].length() - 1);
            int inches = Integer.parseInt(secondPart);
            heightInFeet = feet;
            heightInInches = inches;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "Error Formatting";
        }
        return Integer.toString((int) ((heightInFeet * 30.48) + (heightInInches * 2.54)));
    }

    public static String convertCentimeterToImperial(double d) {
        int feetPart = 0;
        int inchesPart = 0;
        if (String.valueOf(d) != null && String.valueOf(d).trim().length() != 0) {
            feetPart = (int) Math.floor((d / 2.54) / 12);
            inchesPart = (int) Math.floor((d / 2.54) - (feetPart * 12));
        }
        return String.format("%d' %d\"", feetPart, inchesPart);
    }

    private void setSelected(TextView tv) {
        tv.setSelected(true);
        tv.setTextColor(getResources().getColor(R.color.white_0_2));
    }

    private void setUnSelected(TextView tv) {
        tv.setSelected(false);
        tv.setTextColor(getResources().getColor(R.color.gray_1_4));
    }

}
