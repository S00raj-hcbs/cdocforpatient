package com.cybermed.cdoc_patient.me.vitalcheck;

import static com.cybermed.cdoc_patient.util.AppConstant.KEY_BMI;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_HEIGHT;
import static com.cybermed.cdoc_patient.util.AppConstant.KEY_WEIGHT;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.DialogFragment;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.databinding.ActivityBmigraphBinding;
import com.github.anastr.speedviewlib.components.Section;

import java.util.ArrayList;
import java.util.Arrays;

import kotlin.jvm.functions.Function2;

public class BMIGraphActivity extends DialogFragment {

    ActivityBmigraphBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.activity_bmigraph, container, false);
        binding.speedView.clearSections();
        binding.speedView.addSections(new Section(0f, 0.2f, Color.parseColor("#34c85a"), 80f));  // Green: Underweight (0 to 18.5)
        binding.speedView.addSections(new Section(0.2f, 0.4f, Color.parseColor("#ffcc00"), 80f));  // Yellow: Normal (18.5 to 25)
        binding.speedView.addSections(new Section(0.4f, 0.6f, Color.parseColor("#ff9501"), 80f));  // Orange: Overweight (25 to 30)
        binding.speedView.addSections(new Section(0.6f, 1f, Color.parseColor("#ff3b2f"), 80f));  // Red: Obesity (30 to 50)
        binding.speedView.setTickNumber(4);
        binding.speedView.setTicks(new ArrayList<>(Arrays.asList(0.2f, 0.4f, 0.6f, 1f)));
        binding.toolBar.txtTittle.setText(R.string.bmi_graph);
        binding.toolBar.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        binding.speedView.setOnPrintTickLabel(new Function2<Integer, Float, CharSequence>() {
            @Override
            public CharSequence invoke(Integer integer, Float aFloat) {
                switch (integer) {
                    case 0:
                        return "18.5";  // Corresponds to 0.37f
                    case 1:
                        return "25";  // Corresponds to 0.5f
                    case 2:
                        return "30";  // Corresponds to 0.6f
                    case 3:
                        return "50";  // Corresponds to 1f
                    default:
                        return "";  // No label for other ticks
                }
            }
        });
        //setBmiAndUpdateSpeed(28.0f);


        binding.speedView.setTextColor(Color.BLACK);  // Set text color for tick labels
        initLayout();

        return binding.getRoot();
    }

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }*/

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialog);
    }
    protected void initLayout() {
        Bundle data = getArguments();
        if (data != null) {
            String height = data.getString(KEY_HEIGHT);
            String weight = data.getString(KEY_WEIGHT);
            String BMI = data.getString(KEY_BMI);
            float bmi = (float) convertToKg(weight) / ((float) convertFtInToMeters(height) * (float) convertFtInToMeters(height));
            binding.textValue.setText("BMI = "+BMI);
            binding.speedView.speedTo(normalizeBmiValue(Float.parseFloat(BMI)));
            binding.textWeightRange.setText(calculateHealthyWeightRange(convertFtInToMeters(height)));
            binding.textBMIPrime.setText(calculateBMIPrime(Float.parseFloat(BMI)));
            binding.textPonderalIndex.setText(calculatePonderalIndex(convertToKg(weight),convertFtInToMeters(height)));
            binding.textWeightLossGain.setText(calculateWeightChange(convertToKg(weight), convertFtInToMeters(height), 25f));
        }
    }


    public float normalizeBmiValue(float bmiValue) {
        if (bmiValue <= 18.5f) {
            return mapValue(bmiValue,  0,  18.5f,  0,  20); // Below 0 maps to 0f
        } else if (bmiValue <= 25) {
            return  mapValue(bmiValue,  18.5f,  25,  20,  40);// Normalize between 0 and 18.5 to 0f and 0.2f
        } else if (bmiValue <= 30) {
            return mapValue(bmiValue,  25,  30,  40,  60); // Normalize 18.5 to 25 to 0.2f to 0.4f
        } else if (bmiValue <= 50) {
            return  mapValue(bmiValue,  30,  50,  60,  100); // Normalize 25 to 30 to 0.4f to 0.6f
        }  else {
            return 100; // Above 50 maps to 1f
        }
    }

    public static float mapValue(float sourceValue, float sourceMin, float sourceMax, float destinationMin, float destinationMax) {
        return destinationMin + (sourceValue - sourceMin) * (destinationMax - destinationMin) / (sourceMax - sourceMin);
    }

    public static double convertToKg(String weightString) {
        // Convert ounces to pounds
        if (weightString.contains("lbs") && weightString.contains("oz")){

            String[] parts = weightString.split(" ");
            double pounds = Double.parseDouble(parts[0].replace("lbs","")); // First part is pounds
            double ounces = Double.parseDouble(parts[1].replace("oz","")); // Third part is ounces
            double totalPounds = pounds + (ounces / 16);

            // Convert pounds to kilograms
            return totalPounds * 0.453592;
        }else {

            String parts = weightString.replace("lbs","");
            double pounds = Double.parseDouble(parts); // First part is pounds

            // Convert pounds to kilograms
            return pounds * 0.453592;
        }
    }
    public static double convertFtInToMeters(String heightFtIn) {
        // Split the string by space
        if (heightFtIn.contains("ft")){
            String[] parts = heightFtIn.split(" ");

            // Extract feet and inches
            int feet = Integer.parseInt(parts[0].replace("ft","")); // First part is feet
            int inches = Integer.parseInt(parts[1].replace("in","")); // Third part is inches

            // Convert feet to meters and inches to meters
            double metersFromFeet = feet * 0.3048;
            double metersFromInches = inches * 0.0254;

            // Return total height in meters
            return metersFromFeet + metersFromInches;
        }else {
            String parts = heightFtIn.replace("in","");

            // Extract feet and inches
            int feet = Integer.parseInt(parts); // First part is feet

            // Convert feet to meters and inches to meters
            double metersFromFeet = feet * 0.3048;

            // Return total height in meters
            return metersFromFeet;
        }

    }

    public String calculateHealthyWeightRange(double heightFeet) {
        // Step 1: Convert height from feet to meters
        //double heightMeters = heightFeet * 0.3048;

        // Step 2: Define the BMI range for healthy weight (18.5 to 25)
        double minBMI = 18.5;
        double maxBMI = 25;

        // Step 3: Calculate the minimum and maximum healthy weight in kilograms
        double minWeightKg = minBMI * Math.pow(heightFeet, 2);
        double maxWeightKg = maxBMI * Math.pow(heightFeet, 2);

        // Step 4: Convert the weight from kilograms to pounds
        double minWeightLb = minWeightKg * 2.20462;
        double maxWeightLb = maxWeightKg * 2.20462;


        return String.format(getString(R.string.healthy_weight_range_for_height_1f_lbs_1f_lbs),
                 minWeightLb, maxWeightLb);
        // Print the healthy weight range in pounds
    }
    // Method to calculate BMI prime
    public String calculateBMIPrime(double bmi) {
        final double BMI_UPPER_LIMIT = 25;
        double prime= bmi / BMI_UPPER_LIMIT;
        return String.format(getString(R.string.bmi_prime_2f),
                prime);
    }

    // Method to calculate Ponderal Index
    public String calculatePonderalIndex(double weightKg, double heightMeters) {
        double Ponderal_Index=  weightKg / Math.pow(heightMeters, 3);
        return String.format(getString(R.string.ponderal_index_2f_kg_m),
                Ponderal_Index);
    }

    // Method to calculate the weight change in pounds
    public String calculateWeightChange(double currentWeightKg, double heightMeters, double targetBMI) {
        // Calculate the target weight in kilograms for the desired BMI
        double targetWeightKg = targetBMI * Math.pow(heightMeters, 2);

        // Calculate the difference between current weight and target weight
        double weightChangeKg = targetWeightKg - currentWeightKg;

        // Convert the weight change to pounds
        double weightChangeLb = weightChangeKg * 2.20462;

        return displayWeightChangeResult(weightChangeLb, targetBMI);
    }

    // Method to display whether the weight change is a gain or loss
    public String displayWeightChangeResult(double weightChangeLb, double targetBMI) {
        if (weightChangeLb < 0) {
            return String.format(getString(R.string.lose_1f_lbs_to_reach_a_bmi_of_25_kg_m),  Math.abs(weightChangeLb));
        } else if (weightChangeLb > 0) {
            return String.format(getString(R.string.gain_1f_lbs_to_reach_a_bmi_of_25_kg_m),  Math.abs(weightChangeLb));
        } else {
            return String.format(getString(R.string.you_are_at_the_ideal_bmi_of_1f_kg_m), targetBMI);
        }
    }
}