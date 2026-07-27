package com.cybermed.cdoc_patient.main.chat;


import static com.google.android.material.color.MaterialColors.isColorLight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cybermed.cdoc_patient.R;
import com.cybermed.cdoc_patient.common.CDoctor2Application;
import com.cybermed.cdoc_patient.databinding.ChatActivityLayoutBinding;
import com.cybermed.cdoc_patient.databinding.ItemChatRecieveDataLayoutBinding;
import com.cybermed.cdoc_patient.databinding.ItemChatSendDataLayoutBinding;
import com.cybermed.cdoc_patient.databinding.ItemDateHeaderLayoutBinding;
import com.cybermed.cdoc_patient.main.chat.model.ChatItem;
import com.cybermed.cdoc_patient.main.chat.model.DateHeaderModel;
import com.cybermed.cdoc_patient.main.chat.model.MessageModel;
import com.cybermed.cdoc_patient.signalr.ChatManager;
import com.cybermed.cdoc_patient.webapi.IResponseReceiver;
import com.cybermed.cdoc_patient.webapi.manager.HomeApiManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class ChatActivity extends AppCompatActivity {
    ChatActivityLayoutBinding binding;
    String name="";
    String appt_id="";
    String ProviderID="";
    String provider_id="";
    String org_Id="";
    private ChatManager chatManager;
    private MessageListingAdapter messageAdapter;
    private List<ChatItem> chatItems = new ArrayList<>();

    private static Timer sGetAllMessageTimer;
    String isFirst="true";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.chat_activity_layout);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            OnBackInvokedCallback callback = () -> {
                // Handle the back action
                binding.toolbar.performClick();
            };

            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(
                    OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    callback
            );
        }
        messageAdapter = new MessageListingAdapter(chatItems);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(messageAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            /*WindowCompat.setDecorFitsSystemWindows(getWindow(), true);*/

            Window window = getWindow();
            if (window != null) {
                WindowCompat.setDecorFitsSystemWindows(window, true);
                WindowInsetsControllerCompat controller =
                        WindowCompat.getInsetsController(window, window.getDecorView());
                controller.setAppearanceLightStatusBars(true); // Use dark icons on light bg
                controller.setAppearanceLightNavigationBars(true);
            }
            ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), (v, insets) -> {
                Insets systemBars = insets.getInsets(  WindowInsetsCompat.Type.systemBars()
                        | WindowInsetsCompat.Type.displayCutout());
                v.setBackgroundColor(ContextCompat.getColor(ChatActivity.this,R.color.white_0_2));

                Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
                Insets systemBars2 = insets.getInsets(WindowInsetsCompat.Type.systemBars());

                int bottomInset = Math.max(imeInsets.bottom, systemBars2.bottom);
                v.setPadding(systemBars.left, systemBars.top, systemBars.right,bottomInset);
                if (isColorLight(ContextCompat.getColor(ChatActivity.this, R.color.white_0_2))) {
                   // WindowInsetsController controller = getWindow().getInsetsController();
                    WindowInsetsControllerCompat controller =
                            WindowCompat.getInsetsController(window, window.getDecorView());
                    controller.setAppearanceLightStatusBars(true); // Use dark icons on light bg
                    controller.setAppearanceLightNavigationBars(true);
                    if (controller != null) {
                        // For light text (dark backgrounds)
                        // 1. Make content go edge-to-edge
                        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);

                        // 2. Set transparent system bars
                        /*getWindow().setStatusBarColor(Color.TRANSPARENT);
                        getWindow().setNavigationBarColor(Color.WHITE);*/
                    }
                } else {
                    // Dark background - use light text
                    v.setSystemUiVisibility(v.getSystemUiVisibility() & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                }
                return insets;
            });

            WindowManager.LayoutParams params = getWindow().getAttributes();

            params.setFitInsetsTypes(0); // prevent insets from blocking resize

            getWindow().setAttributes(params);

        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            boolean isKeyboardVisible = imeInsets.bottom > 0;

            if (isKeyboardVisible) {
                assert binding.recyclerView.getAdapter() != null;
                if (binding.recyclerView.getAdapter().getItemCount()>0){
                    binding.recyclerView.smoothScrollToPosition(chatItems.size() - 1);
                }
            } else {
                assert binding.recyclerView.getAdapter() != null;
                if (binding.recyclerView.getAdapter().getItemCount()>0){
                    binding.recyclerView.smoothScrollToPosition(chatItems.size() - 1);
                }
            }

            return insets;
        });
        binding.sendButton.setOnClickListener(v -> {
            hideKeyboard();
            String messageText = binding.edChatMessage.getText().toString().trim();
            String patientId = CDoctor2Application.getLoginInfo().getAccount();
            String providerId = org_Id+"&"+provider_id;


            if ( !messageText.isEmpty()) {
                Date now2 = new Date(); // current date & time
                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH);
                String formatted = sdf.format(now2);
                Date now = null;
                try {
                    now = sdf.parse(formatted);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                boolean shouldAddDateHeader = true;

                if (!chatItems.isEmpty()) {
                    for (int i = chatItems.size() - 1; i >= 0; i--) {
                        ChatItem item = chatItems.get(i);
                        if (item instanceof DateHeaderModel) {
                            Date lastHeaderDate = ((DateHeaderModel) item).getDate();
                            if (isSameDateTimeToMinute(lastHeaderDate, now)) {
                                shouldAddDateHeader = false;
                            }
                            break; // found the latest DateHeaderModel, no need to check further
                        }
                    }
                }
                if (shouldAddDateHeader) {
                    chatItems.add(new DateHeaderModel(now));
                    messageAdapter.notifyItemInserted(chatItems.size() - 1);
                }

// Add message
                MessageModel message = new MessageModel("1",patientId,"patient",providerId,"provider","PatientToProvider",messageText, formatted, "");
                chatItems.add(message);
                messageAdapter.notifyItemInserted(chatItems.size() - 1);

// Scroll to bottom
                binding.recyclerView.scrollToPosition(chatItems.size() - 1);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyMessage.setVisibility(View.GONE);
                chatManager.sendChatMessage( patientId,providerId, "PatientToProvider", messageText);
                binding.edChatMessage.setText("");
            }
        });
        /*WindowCompat.getInsetsController(getWindow(), binding.edChatMessage).show(WindowInsetsCompat.Type.ime());*/

    }

    private void initData() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        appt_id = intent.getStringExtra("appt_id");
        ProviderID = intent.getStringExtra("ProviderID");
        provider_id = intent.getStringExtra("provider_code");
        org_Id = intent.getStringExtra("org_code");
        binding.toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView textView=binding.toolbar.findViewById(R.id.toolbar_title);
        textView.setText(name);
        sGetAllMessageTimer=new Timer();
        sGetAllMessageTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                updateAllMessageAppointment(CDoctor2Application.getLoginInfo().getUserInfo().getService_code(),CDoctor2Application.getLoginInfo().getAccount(),ProviderID,appt_id);
            }
        }, 0, 2000);
    }



    private void updateAllMessageAppointment(String orgCode, String patientId, String providerId, String appt_id) {
        new HomeApiManager(new IResponseReceiver<List<MessageModel>>() {
            @Override
            public void onSuccess(List<MessageModel> data) {
                if (data == null || data.isEmpty()) {
                    /*binding.emptyMessage.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);*/
                    return;
                }
                String oldLastMsgId = null;
                if (!chatItems.isEmpty() && chatItems.get(chatItems.size() - 1) instanceof MessageModel) {
                    oldLastMsgId = ((MessageModel) chatItems.get(chatItems.size() - 1)).getMsg_id(); // or use timestamp
                }
                chatItems.clear();
                SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH);
                Date lastHeaderDate = null;
                for (MessageModel msg : data) {

                    Date messageDate = null;
                    try {
                        messageDate = sdf.parse(msg.getMsg_date());
                    } catch (ParseException e) {
                        e.printStackTrace();
                        continue; // skip this message if date is invalid
                    }


                    if (lastHeaderDate == null || !isSameDateTimeToMinute(lastHeaderDate, messageDate)) {
                        chatItems.add(new DateHeaderModel(messageDate));
                        lastHeaderDate = messageDate;
                    }

                    // Add the actual message
                    chatItems.add(msg);
                }
                String newLastMsgId = null;
                if (!chatItems.isEmpty() && chatItems.get(chatItems.size() - 1) instanceof MessageModel) {
                    newLastMsgId = ((MessageModel) chatItems.get(chatItems.size() - 1)).getMsg_id();
                }
                if (isFirst.equals("true")){
                    isFirst="false";
                    messageAdapter = new MessageListingAdapter(chatItems);
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                    binding.recyclerView.setAdapter(messageAdapter);
                    binding.recyclerView.scrollToPosition(chatItems.size() - 1);
                }else {
                    LinearLayoutManager layoutManager = (LinearLayoutManager) binding.recyclerView.getLayoutManager();
                    int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                    boolean isAtBottom = lastVisiblePosition >= chatItems.size() - 2;
                    messageAdapter.notifyDataSetChanged();

                    /*if (isAtBottom) {
                        binding.recyclerView.scrollToPosition(chatItems.size() - 1);
                    }*/
                    if (isAtBottom || (newLastMsgId != null && !newLastMsgId.equals(oldLastMsgId))) {
                        binding.recyclerView.scrollToPosition(chatItems.size() - 1);
                    }
                }



                if (!chatItems.isEmpty()){
                    binding.emptyMessage.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                }else {
                    binding.emptyMessage.setVisibility(View.VISIBLE);
                    binding.recyclerView.setVisibility(View.GONE);
                }


            }

            @Override
            public void onFailure(@NonNull String errorResponse) {
                if (sGetAllMessageTimer != null) {
                    sGetAllMessageTimer.cancel();
                }
            }
        },ChatActivity.this).getAllMessagesAppointment(orgCode,patientId,providerId, appt_id);
    }

    private String getCurrentTime() {
        return new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(new Date());
    }

    public class MessageListingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ChatItem> chatItems;
        private static final int TYPE_MESSAGE_SENT = 1;
        private static final int TYPE_MESSAGE_RECEIVED = 2;
        private static final int TYPE_DATE_HEADER = 3;

        public MessageListingAdapter(List<ChatItem> messageLists) {
            this.chatItems = messageLists;
        }

        @Override
        public int getItemViewType(int position) {
            ChatItem item = chatItems.get(position);
            if (item instanceof DateHeaderModel) return TYPE_DATE_HEADER;
            MessageModel msg = (MessageModel) item;
            return msg.getSender_type().equals("patient") ? TYPE_MESSAGE_SENT : TYPE_MESSAGE_RECEIVED;
        }
        // inflates the row layout from xml when needed
        @Override
        @NonNull
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_DATE_HEADER) {
                ItemDateHeaderLayoutBinding itemDateHeaderLayoutBinding = ItemDateHeaderLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new MyViewHolder(itemDateHeaderLayoutBinding);
            } else if (viewType == TYPE_MESSAGE_SENT){
                ItemChatSendDataLayoutBinding itemChatSendDataLayoutBinding = ItemChatSendDataLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new SenderViewHolder(itemChatSendDataLayoutBinding);
            }else {
                ItemChatRecieveDataLayoutBinding itemChatRecieveDataLayoutBinding = ItemChatRecieveDataLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
                return new ReceiveViewHolder(itemChatRecieveDataLayoutBinding);
            }
        }

        @SuppressLint({"SetTextI18n", "QueryPermissionsNeeded", "NotifyDataSetChanged"})
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChatItem item = chatItems.get(position);
            if (holder instanceof MyViewHolder) {
                Date date = ((DateHeaderModel) item).getDate();
                String formattedDate = formatChatDateTime(date); // we'll define this below
                ((MyViewHolder) holder).binding.textDate.setText(formattedDate);


            }else if (holder instanceof SenderViewHolder){
                MessageModel msg = (MessageModel) item;
                ((SenderViewHolder) holder).binding.txtName.setText(CDoctor2Application.getLoginInfo().getUserInfo().getFirstName()+" "+CDoctor2Application.getLoginInfo().getUserInfo().getLastname());
                ((SenderViewHolder) holder).binding.txtMessage.setText(msg.getMsg_detail());
                ((SenderViewHolder) holder).binding.txtTime.setText(parseAndFormatTime(msg.getMsg_date()));
                ((SenderViewHolder) holder).binding.textNameImage.setText(giveName(CDoctor2Application.getLoginInfo().getUserInfo().getFirstName()+" "+CDoctor2Application.getLoginInfo().getUserInfo().getLastname()));
            }else {
                MessageModel msg = (MessageModel) item;
                ((ReceiveViewHolder) holder).binding.txtName.setText(name);
                ((ReceiveViewHolder) holder).binding.txtMessage.setText(msg.getMsg_detail());
                ((ReceiveViewHolder) holder).binding.txtTime.setText(parseAndFormatTime(msg.getMsg_date()));
                ((ReceiveViewHolder) holder).binding.textNameImage.setText(giveName(name));
            }

        }

        @Override
        public int getItemCount() {
            return chatItems.size();
        }

        // stores and recycles views as they are scrolled off screen
        static class MyViewHolder extends RecyclerView.ViewHolder {

            ItemDateHeaderLayoutBinding binding = null;
            MyViewHolder(ItemDateHeaderLayoutBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }

        }
        // stores and recycles views as they are scrolled off screen
        static class SenderViewHolder extends RecyclerView.ViewHolder {

            ItemChatSendDataLayoutBinding binding = null;
            SenderViewHolder(ItemChatSendDataLayoutBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }

        }
        // stores and recycles views as they are scrolled off screen
        static class ReceiveViewHolder extends RecyclerView.ViewHolder {

            ItemChatRecieveDataLayoutBinding binding = null;
            ReceiveViewHolder(ItemChatRecieveDataLayoutBinding itemView) {
                super(itemView.getRoot());
                this.binding = itemView;
            }
        }

    }
    public String giveName(String input){
        String strgname="";
        if (input.contains(" ")){
            String[] seprate=input.split(" ");
            if (seprate[0].length()>=1){
                String str_name1=seprate[0];
                String name1 =str_name1.substring(0,1);
                String strnamenew=name1;
                if (seprate[seprate.length-1].length()>1){
                    String strname2=seprate[seprate.length-1];
                    String name2= strname2.substring(0,1);
                    strnamenew=name1+name2;
                }
                strgname=strnamenew.toUpperCase();
            }else {
                String str_name1=seprate[0];
                String strgname3=str_name1.substring(0,1);
                strgname=strgname3.toUpperCase();
            }
        }else {
            String strgname3=input.substring(0,1);
            strgname=strgname3.toUpperCase();
        }
        return strgname;
    }

    public static String formatChatDateTime(Date messageDate) {
        Date now = new Date();

        // Check for Today
        if (isSameDay(messageDate, now)) {
            return "Today, " + formatTime(messageDate);
        }

        // Check for Yesterday
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        if (isSameDay(messageDate, cal.getTime())) {
            return "Yesterday, " + formatTime(messageDate);
        }

        // Else: show full date
        return new SimpleDateFormat("d MMM, yyyy, h:mm a", Locale.getDefault()).format(messageDate);
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static boolean isSameDateTimeToMinute(Date d1, Date d2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(d1);
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(d2);

        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH) &&
                cal1.get(Calendar.HOUR_OF_DAY) == cal2.get(Calendar.HOUR_OF_DAY) &&
                cal1.get(Calendar.MINUTE) == cal2.get(Calendar.MINUTE);
    }


    private static String formatTime(Date date) {
        return new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(date);
    }
    private static String parseAndFormatTime(String dateString) {
        try {
            // Parse from your string format
            SimpleDateFormat parser = new SimpleDateFormat("M/d/yyyy h:mm:ss a", Locale.ENGLISH);
            Date date = parser.parse(dateString);

            // Format to just "h:mm a"
            return formatTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return ""; // or handle error
        }
    }
    private void hideSystemBars() {
        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (insetsController != null) {
            insetsController.hide(WindowInsetsCompat.Type.systemBars());
            insetsController.setSystemBarsBehavior(
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }

    private void showSystemBars() {
        WindowInsetsControllerCompat insetsController =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (insetsController != null) {
            insetsController.show(WindowInsetsCompat.Type.systemBars());
        }
    }

    private void hideKeyboard() {
        ((InputMethodManager) (getSystemService(Context.INPUT_METHOD_SERVICE))).
                hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);
    }
    @Override
    protected void onResume() {
        super.onResume();
        chatManager = new ChatManager();
        if (sGetAllMessageTimer != null) {
            sGetAllMessageTimer.cancel();
        }
        isFirst="true";
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (sGetAllMessageTimer != null) {
            sGetAllMessageTimer.cancel();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (sGetAllMessageTimer != null) {
            sGetAllMessageTimer.cancel();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        binding.toolbar.performClick();
    }

}
 /*        binding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                Rect r = new Rect();
                binding.relBottomView.getWindowVisibleDisplayFrame(r);

                int screenHeight = binding.getRoot().getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                // Get navigation bar height (if present)
                int navBarHeight = 0;
                int resourceId = binding.getRoot().getResources().getIdentifier(
                        "navigation_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    navBarHeight = binding.getRoot().getResources().getDimensionPixelSize(resourceId);
                }

                if (keypadHeight > screenHeight * 0.15) {
                    // Move just enough so relBottomView touches keyboard
                    binding.relBottomView.setTranslationY(-(keypadHeight - navBarHeight));
                    binding.recyclerView.post(() ->
                            binding.recyclerView.scrollToPosition(chatItems.size() - 1)
                    );
                } else {
                    binding.relBottomView.setTranslationY(0);
                }
            });*/