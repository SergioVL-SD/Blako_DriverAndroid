package com.blako.mensajero.accesibility;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;

import com.blako.mensajero.BkoDataMaganer;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by TRINIDAD on 8/8/16.
 */
public class CustomAccessibilityService extends AccessibilityService implements View.OnTouchListener {
    private View floatyView;
    static final String TAG = "RecorderService";
    private FrameLayout mOverlay;
    private WindowManager mWindowManager;
    private ArrayList<AccessibilityNodeInfo> textViewNodes = new ArrayList<>();
    private WindowManager windowManager;
    //  private AccesibilityPresenter presenter;

    private void onChange(final String source, final AccessibilityNodeInfo rootNode, final ArrayList<AccessibilityNodeInfo> textViewNodes, int state) {
        if (textViewNodes != null) {
            VWindowLocationService.lastNodes = textViewNodes;
            VWindowLocationService.root = rootNode;
            sendScrenData(source);
        }

        if (VAccesibilityActions.getIsOnScreen(source, textViewNodes, VAppData.FLAG_REQUEST)) {
            Map<String, Object> request = VAccesibilityActions.getRequest(this, textViewNodes, source, null, false);
            sendRequestNoFiltered(source, request, 0);
            VAccesibilityStatus.setCurrentAppOnService(CustomAccessibilityService.this, source);
        }

    }

    private void sendScrenData(String source) {
        Intent intent = new Intent(this, VWindowLocationService.class);
        Bundle bundle = new Bundle();
        bundle.putString("source", source);
        intent.putExtras(bundle);
        intent.setAction("foo");
        startService(intent);
    }


    private void sendRequestNoFiltered(String source, Map<String, Object> map, int connection) {
        try {
            VRequest vRequest = new VRequest();
            if (map != null) {
                VRequest lastRequest = VAccesibilityStatus.getLastRequestTimeSended(this);
                if (lastRequest != null) {
                    long dif = System.currentTimeMillis() - lastRequest.getLastTime();
                    if (dif < 30000) {
                        return;
                    }
                }

                if (connection == 0) {
                    VAccesibilityStatus.setLastRequestTimeSended(this, null);
                    VAccesibilityStatus.setLastRequestOnWaySended(this, false);
                    VAccesibilityStatus.setLastRequestArrivedSended(this, false);
                    VAccesibilityStatus.setLastRequestStartedSended(this, false);
                    VAccesibilityStatus.setLastRequestFinishedSended(this, false);
                }
                vRequest.setPackageName(source);
                vRequest.setLastTime(System.currentTimeMillis());
                VAccesibilityStatus.setLastRequestTimeSended(this, vRequest);
                //   presenter.sendNewRequestItem(map, VAppData.ROOM_SERVICE, vRequest, this);
            }
        } catch (Exception ignored) {

        }

    }


    private synchronized String getEventType(AccessibilityEvent event) {


        final AccessibilityNodeInfo ani = event.getSource();
        if (ani != null) {
            ani.refresh();
        } else {
            return "";
        }

        final String source = (String) event.getPackageName();

        if (!VAppData.appPackages.contains(source))
            return "";

        final AccessibilityNodeInfo rootNode = getRootInActiveWindow();

        switch (event.getEventType()) {
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                return "TYPE_VIEW_CLICKED";
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                textViewNodes = new ArrayList<>();
                findChildViews(rootNode, source.replace(".", ""));
                ArrayList<AccessibilityNodeInfo> nodes;
                nodes = textViewNodes;
                if (VAccesibilityStatus.getVictorStatus(this) == VAccesibilityStatus.BUSY)
                    onChange(source, ani, nodes, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);
                else
                    onChange(source, rootNode, nodes, AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED);

                return "TYPE_WINDOW_CONTENT_CHANGED";
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                textViewNodes = new ArrayList<>();
                findChildViews(rootNode, source.replace(".", ""));
                ArrayList<AccessibilityNodeInfo> _nodes;
                _nodes = textViewNodes;

                if (VAccesibilityStatus.getVictorStatus(this) == VAccesibilityStatus.BUSY)
                    onChange(source, ani, _nodes, AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                else
                    onChange(source, rootNode, _nodes, AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED);
                return "TYPE_WINDOW_STATE_CHANGED";

        }
        return String.format("unknown (%d)", event.getEventType());
    }


    private void findChildViews(AccessibilityNodeInfo parentView, String source) {
        if (parentView == null || parentView.getClassName() == null) {
            return;
        }
        int childCount = parentView.getChildCount();
        if (childCount == 0) {
            parentView.refresh();
            if (!(parentView.getViewIdResourceName() == null && parentView.getText() == null))
                textViewNodes.add(parentView);
        } else {
            for (int i = 0; i < childCount; i++) {
                findChildViews(parentView.getChild(i), source);
            }
        }
    }


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    @Override
    public void onInterrupt() {

    }


    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.v(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.packageNames = new String[]
                {"com.postmates.android.courier", "com.ubercab.driver"}; // , "com.rappi.storekeeper"
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
                | AccessibilityServiceInfo.FLAG_INCLUDE_NOT_IMPORTANT_VIEWS | AccessibilityServiceInfo.FLAG_REQUEST_TOUCH_EXPLORATION_MODE | AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        //presenter = new AccesibilityPresenter();
        windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        }
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        removeOverlay();
        return true;
    }

    private void removeOverlay() {
        if (floatyView != null) {
            windowManager.removeView(floatyView);
            floatyView = null;
        }
    }
}