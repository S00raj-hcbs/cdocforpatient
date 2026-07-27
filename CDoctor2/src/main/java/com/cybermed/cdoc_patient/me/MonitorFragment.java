package com.cybermed.cdoc_patient.me;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import static com.cdfortis.datainterface.soap.WebService.WSInstance;
import static com.cdfortis.datainterface.soap.WebServiceID.get_patient_health_records_v2;
import static java.lang.Math.*;

import com.annimon.stream.IntStream;
import com.cdfortis.datainterface.annotation.DataField;
import com.cdfortis.datainterface.data.MostRecentMonitorData;
import com.cdfortis.datainterface.data.RequestMonitorRefresh;
import com.cdfortis.datainterface.data.MonitorRefreshCompletedMessage;
import com.cdfortis.datainterface.soap.UserInfo;
import com.cdfortis.datainterface.soap.model.Monitor_BO;
import com.cdfortis.datainterface.soap.model.Monitor_BP;
import com.cdfortis.datainterface.soap.model.Monitor_Glucose;
import com.cdfortis.datainterface.soap.model.Monitor_HR;
import com.cdfortis.datainterface.soap.model.Monitor_STEMO;
import com.cdfortis.datainterface.soap.model.Monitor_Weight;
import com.cdfortis.datainterface.soap.model.SoapObjectData;
import com.cdfortis.datainterface.soap.model.SoapObjectVector;
import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.annotation.Factory;
import com.cybermed.cdoc_patient.common.BaseFragment;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.FragmentMonitorBinding;
import com.cybermed.cdoc_patient.main.FragmentMainActivity;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.ksoap2.serialization.SoapObject;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.cybermed.cdoc_patient.me.MeFragment.USERINFOKEY;


public class MonitorFragment extends BaseFragment {

    private FragmentMainActivity fragMain;
    public static final String MONITOR_HIDE_TOOLBAR = "MONITOR_HIDE_TOOLBAR";

    @Factory
    public static MonitorFragment newInstance(UserInfo userInfo, boolean hideToolBar) {
        MonitorFragment fragment = new MonitorFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putSerializable(USERINFOKEY, userInfo);
        args.putBoolean(MONITOR_HIDE_TOOLBAR, hideToolBar);
        fragment.setArguments(args);

        return fragment;
    }

    private static final String HR = "1";
    private static final String WEIGHT = "2";
    private static final String BLOOD_PRESSURE = "3";
    public static final String BLOOD_OXYGEN = "5";
    public static final String GLUCOSE = "6";
    public static final String STEMOSCOPE = "8";
    private static final String BP_POSTFIX = "(mmHg)";
    private static final String BO_POSTFIX = "(%)";
    private static final String HR_POSTFIX = "(bpm)";
    private static final String WEIGHT_POSTFIX = "(lbs)";
    private static final String GLUCOSE_POSTFIX = "(mg/dl)";

    private static final int RADIUS = 10;
    private static final int THICKNESS = 4;

    private static final int WEEKAGO = 695520000;
    private static final int BPL = 1;
    private static final int BPH = 2;


    private Vector<Monitor_BO> monitor_boVector;
    private Vector<Monitor_BP> monitor_bpVector;
    private Vector<Monitor_Glucose> monitor_glucoseVector;
    private Vector<Monitor_Weight> monitor_weightVector;
    private Vector<Monitor_HR> monitor_hrVector;
    private Vector<Monitor_STEMO> stemoscope_Vector;

    FragmentMonitorBinding binding;


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.unbind();
    }

    UserInfo userInfo;

    @Override
    protected View getContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_monitor,container,false);

        return binding.getRoot();
    }

    @Override
    protected void initLayout(View view) {

        if (getArguments() != null) {
            userInfo = (UserInfo) getArguments().getSerializable(USERINFOKEY);
        } else {
            userInfo = CDoctor2Application.getLoginInfo().getUserInfo();

            AppCompatActivity activity = ((AppCompatActivity) getActivity());
            activity.setSupportActionBar(binding.toolbar);
            activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icon_back_row));

        }

        binding.monitorRefreshLayout.setOnRefreshListener(() -> {
            EventBus.getDefault().post(new RequestMonitorRefresh());
        });

        initToolBar();
        clickListener();
    }


    private void initToolBar() {

        //the following code is to hide toolbar if necessary
        if (getArguments() != null) {
            boolean hideToolBar = getArguments().getBoolean(MONITOR_HIDE_TOOLBAR);
            if (hideToolBar)
                binding.toolbar.setVisibility(View.GONE);
        }

        if (getActivity() instanceof FragmentMainActivity)
            fragMain = (FragmentMainActivity) getActivity();

        if (fragMain == null)
            return;

        fragMain.setSupportActionBar(binding.toolbar);
        fragMain.getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.icon_back_row));

        binding.toolbar.setNavigationOnClickListener(v -> {
            if(((MeFragment)getParentFragment()!=null)){
                ((MeFragment)getParentFragment()).openUserActivityFragment();
            }
        });
    }

    Disposable refresh_disposable;

    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(RequestMonitorRefresh messageEvent) {

        if (refresh_disposable != null) {
            refresh_disposable.dispose();
        }

        final String user_account = userInfo.getEmail();

        refresh_disposable = Observable.just(HR, WEIGHT, BLOOD_OXYGEN, BLOOD_PRESSURE, GLUCOSE, STEMOSCOPE)
                .flatMap(record_type ->
                        Observable.fromCallable(() ->
                                WSInstance().RxCallingWebservice(get_patient_health_records_v2, record_type, "", user_account))
                                .subscribeOn(Schedulers.io())
                                .map(soapObject -> {
                                    switch (record_type) {
                                        case HR:
                                            monitor_hrVector = new SoapObjectVector<>(Monitor_HR.class, (SoapObject) soapObject);
                                            break;
                                        case WEIGHT:
                                            monitor_weightVector = new SoapObjectVector<>(Monitor_Weight.class, (SoapObject) soapObject);
                                            break;
                                        case BLOOD_OXYGEN:
                                            monitor_boVector = new SoapObjectVector<>(Monitor_BO.class, (SoapObject) soapObject);
                                            break;
                                        case BLOOD_PRESSURE:
                                            monitor_bpVector = new SoapObjectVector<>(Monitor_BP.class, (SoapObject) soapObject);
                                            break;
                                        case GLUCOSE:
                                            monitor_glucoseVector = new SoapObjectVector<>(Monitor_Glucose.class, (SoapObject) soapObject);
                                            break;
                                        case STEMOSCOPE:
                                            stemoscope_Vector = new SoapObjectVector<>(Monitor_STEMO.class, (SoapObject) soapObject);
                                            break;
                                        default:
                                            throw new Exception("Invalid Patient Health Records");
                                    }
                                    return true;
                                })
                ).subscribe(result -> {
                }, error -> {
                    binding.monitorRefreshLayout.setRefreshing(false);
                    Toast.makeText(getContext(), getString(R.string.refresh_error), Toast.LENGTH_SHORT).show();
                }, /*Complete*/() -> {
                    EventBus.getDefault().post(new MonitorRefreshCompletedMessage());
                });
    }

    // Called in a separate thread
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshPage(MonitorRefreshCompletedMessage messageEvent) {

        binding.monitorRefreshLayout.setRefreshing(false);
        EventBus.getDefault().post(new MostRecentMonitorData(monitor_hrVector, monitor_weightVector, monitor_boVector, monitor_bpVector, monitor_glucoseVector, stemoscope_Vector));

        init_Weight_Graph();
        init_Blood_Oxygen_Graph();
        init_Heart_Rate_Graph();
        init_Blood_Pressure_Graph();
        init_Glucose_Graph();
    }

    public static String dateFormatter(String timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa MM/dd/yyyy", Locale.getDefault());
        return sdf.format(new Date(Long.valueOf(timestamp) * 1000));
    }


    private DataPoint soapObjectData_To_DataPoint(int index, Monitor_BP monitor_bp, int BP) {
        try {
            long timeStamp = Long.valueOf(monitor_bp.BP_timestamp);
            double data = 0;

            switch (BP) {
                case BPH:
                    data = Double.valueOf(monitor_bp.BPH);
                    break;
                case BPL:
                    data = Double.valueOf(monitor_bp.BPL);
                    break;
            }

            if (data != 0) {
                return new MyDataPoint(index, data, new Date(timeStamp * 1000));

            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    private DataPoint soapObjectData_To_DataPoint(int index, SoapObjectData soapObjectData) {

        Field[] fields = soapObjectData.getClass().getDeclaredFields();

        Long timeStamp = null;
        Double data = null;

        try {
            for (Field field : fields) {
                /*if no annotation*/
                if (field.getAnnotation(DataField.class) == null)
                    continue;

                if (field.getName().contains("stamp"))
                    timeStamp = Long.valueOf(field.get(soapObjectData).toString());
                else
                    data = Double.valueOf(field.get(soapObjectData).toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (timeStamp != null && data != null)
            return new MyDataPoint(index, data, new Date(timeStamp * 1000));

        else
            return null;

    }

    private void init_Blood_Pressure_Graph() {
        DataPoint[] bph_dataPoints = IntStream.range(0, monitor_bpVector.size())
                .mapToObj(i -> soapObjectData_To_DataPoint(i, monitor_bpVector.get(i), BPH))
                .toArray(MyDataPoint[]::new);

        DataPoint[] bpl_dataPoints = IntStream.range(0, monitor_bpVector.size())
                .mapToObj(i -> soapObjectData_To_DataPoint(i, monitor_bpVector.get(i), BPL))
                .toArray(MyDataPoint[]::new);

        LineGraphSeries<DataPoint> bph_series = new LineGraphSeries<>(bph_dataPoints);
        LineGraphSeries<DataPoint> bpl_series = new LineGraphSeries<>(bpl_dataPoints);

        bph_series.setOnDataPointTapListener(createOnTapListener((MyDataPoint[]) bph_dataPoints));
        bpl_series.setOnDataPointTapListener(createOnTapListener((MyDataPoint[]) bpl_dataPoints));

        bpl_series.setColor(Color.BLUE);
        bpl_series.setDrawDataPoints(true);
        bpl_series.setDataPointsRadius(RADIUS);
        bpl_series.setThickness(THICKNESS);

        initGraph(binding.bloodPressureGraph, bph_series, (MyDataPoint[]) bph_dataPoints);
        binding.bloodPressureGraph.addSeries(bpl_series);
        binding.bloodPressureGraph.getGridLabelRenderer().setLabelFormatter(customizedDefaultLabelFormatter(BP_POSTFIX));
    }


    private void init_Blood_Oxygen_Graph() {
        DataPoint[] dataPoints = vectorToDataPoints(monitor_boVector);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        initGraph(binding.bloodOxygenGraph, series, (MyDataPoint[]) dataPoints);
        binding.bloodOxygenGraph.getGridLabelRenderer().setLabelFormatter(customizedDefaultLabelFormatter(BO_POSTFIX));
    }

    private void init_Glucose_Graph() {
        DataPoint[] dataPoints = vectorToDataPoints(monitor_glucoseVector);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        initGraph(binding.glucoseGraph, series, (MyDataPoint[]) dataPoints);
        binding.glucoseGraph.getGridLabelRenderer().setLabelFormatter(customizedDefaultLabelFormatter(GLUCOSE_POSTFIX));
    }


    private void init_Heart_Rate_Graph() {
        DataPoint[] dataPoints = vectorToDataPoints(monitor_hrVector);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        initGraph(binding.heartRateGraph, series, (MyDataPoint[]) dataPoints);
        binding.heartRateGraph.getGridLabelRenderer().setLabelFormatter(customizedDefaultLabelFormatter(HR_POSTFIX));
    }

    private void init_Weight_Graph() {
        DataPoint[] dataPoints = vectorToDataPoints(monitor_weightVector);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
        initGraph(binding.weightGraph, series, (MyDataPoint[]) dataPoints);
        binding.weightGraph.getGridLabelRenderer().setLabelFormatter(customizedDefaultLabelFormatter(WEIGHT_POSTFIX));
    }

    private DataPoint[] vectorToDataPoints(Vector<? extends SoapObjectData> vector) {
        DataPoint[] dataPoints = IntStream.range(0, vector.size())
                .mapToObj(i -> soapObjectData_To_DataPoint(i, vector.get(i)))
                .toArray(MyDataPoint[]::new);

        return dataPoints;
    }

    private void initGraph(GraphView graphView, LineGraphSeries<DataPoint> lineGraphSeries, MyDataPoint[] dataPoints) {

        int length = dataPoints.length;
        if (length == 0)
            return;

        lineGraphSeries.setColor(Color.RED);
        lineGraphSeries.setDrawDataPoints(true);
        lineGraphSeries.setDataPointsRadius(RADIUS);
        lineGraphSeries.setThickness(THICKNESS);
        lineGraphSeries.setOnDataPointTapListener(createOnTapListener(dataPoints));

        graphView.getGridLabelRenderer().setNumHorizontalLabels(3); // only 3 because of the space
        graphView.getViewport().setScalable(true); // enables horizontal zooming and scrolling

        int start, end;
        start = (length - 10) >= 0 ? length - 10 : 0;
        end = length - 1;

        graphView.getViewport().setMinX(dataPoints[start].getX());
        graphView.getViewport().setMaxX(dataPoints[end].getX());

        graphView.addSeries(lineGraphSeries);
    }


    private OnDataPointTapListener createOnTapListener(MyDataPoint[] dataPoints) {
        return (series, dataPoint) -> {
            MyDataPoint myDataPoint = null;
            try {
                myDataPoint = dataPoints[(int) dataPoint.getX()];
            } catch (Exception e) {
            }

            if (myDataPoint == null)
                return;

            Toast.makeText(getContext(),
                    String.format(Locale.US, "%.2f\n%s", myDataPoint.getY(), dateFormatter(String.valueOf(myDataPoint.getTimeStamp() / 1000)))
                    , Toast.LENGTH_SHORT).show();
        };
    }

    private void clickListener(){
        binding.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new RequestMonitorRefresh());
            }
        });
    }



    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new RequestMonitorRefresh());
    }


    private DefaultLabelFormatter customizedDefaultLabelFormatter(String postFix) {

        return new DateAsXAxisLabelFormatter(getContext()) {
            @Override
            public String formatLabel(double index, boolean isValueX) {

                if (isValueX) {
                    /*Get timeStamp*/
                    double value = 0;
                    int i = (int) round(index);
                    String timeStr = "";

                    switch (postFix) {
                        case BP_POSTFIX:
                            if (!inRange(i, monitor_bpVector)) return timeStr;
                            timeStr = monitor_bpVector.get(i).BP_timestamp;
                            break;
                        case BO_POSTFIX:
                            if (!inRange(i, monitor_boVector)) return timeStr;
                            timeStr = monitor_boVector.get(i).BO_timestamp;
                            break;
                        case HR_POSTFIX:
                            if (!inRange(i, monitor_hrVector)) return timeStr;
                            timeStr = monitor_hrVector.get(i).HR_timestamp;
                            break;
                        case WEIGHT_POSTFIX:
                            if (!inRange(i, monitor_weightVector)) return timeStr;
                            timeStr = monitor_weightVector.get(i).weight_timestamp;
                            break;
                        case GLUCOSE_POSTFIX:
                            if (!inRange(i, monitor_glucoseVector)) return timeStr;
                            timeStr = monitor_glucoseVector.get(i).Glucose_timestamp;
                            break;
                    }

                    if (!timeStr.isEmpty())
                        value = new Date(Long.valueOf(timeStr) * 1000).getTime();


                    String date = super.formatLabel(value, true);
                    return date.substring(0, date.lastIndexOf("/"));
                } else {
                    return super.formatLabel(index, false) + " " + postFix;
                }
            }
        };
    }

    boolean inRange(int index, Vector<?> vector) {
        return vector.size() != 0 && index >= 0 && index < vector.size();
    }

    private class MyDataPoint extends DataPoint {
        private long timeStamp;

        public MyDataPoint(double x, double y, Date date) {
            super(x, y);
            this.timeStamp = date.getTime();
        }

        public long getTimeStamp() {
            return timeStamp;
        }
    }
}
