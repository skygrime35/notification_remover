package com.tjysdsg.notification_remover;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.color.MaterialColors;

public class MainActivity extends NotificationListenerActivity implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeRefreshLayout;
    RecyclerView notificationList;
    NotificationListAdapter notificationListAdapter;
    NotificationListener notificationListener;
    BottomSheet notificationServicePermissionBottomSheet;
    TextView noItemsPromptView;
    int notificationIconColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        notificationServicePermissionBottomSheet = new BottomSheet();
        notificationServicePermissionBottomSheet.show(getSupportFragmentManager(), BottomSheet.TAG);
        notificationServicePermissionBottomSheet.hide(true);

        // pull down to refresh
        swipeRefreshLayout = findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        notificationList = findViewById(R.id.notification_list);

        noItemsPromptView = findViewById(R.id.no_items_prompt);

        notificationIconColor = MaterialColors.getColor(
                this, com.google.android.material.R.attr.colorPrimary, Color.BLACK
        );
    }

    // Swipe down to refresh
    @Override
    public void onRefresh() {
        if (notificationListener == null) return;
        notificationListener.retrieveCurrentStatusBarNotifications();
        updateMainScreen();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    protected void askForNotificationServicePermission(boolean needPermission) {
        if (needPermission) {
            notificationServicePermissionBottomSheet.hide(false);
            notificationServicePermissionBottomSheet.setOnClickListener(
                    (View v) -> jumpToNotificationServicePermissionSettingPage()
            );
        } else {
            notificationServicePermissionBottomSheet.hide(true);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onNotificationListenerServiceStarted(NotificationListener listener) {
        this.notificationListener = listener;

        listener.retrieveCurrentStatusBarNotifications();

        notificationListAdapter = new NotificationListAdapter(notificationListener, notificationIconColor);
        notificationList.setAdapter(notificationListAdapter);
        notificationList.setLayoutManager(new LinearLayoutManager(this));
        notificationListener.registerListenerCallback(
                (l) -> updateMainScreen()
        );
        updateMainScreen();
    }

    @Override
    public void onNotificationListenerServiceStopped() {
        notificationListener = null;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateMainScreen() {
        notificationListAdapter.notifyDataSetChanged();

        // show a text prompt if no notification is in the list
        if (notificationListAdapter.getItemCount() == 0) {
            swipeRefreshLayout.setVisibility(View.GONE);
            noItemsPromptView.setVisibility(View.VISIBLE);
        } else {
            swipeRefreshLayout.setVisibility(View.VISIBLE);
            noItemsPromptView.setVisibility(View.GONE);
        }
    }

}