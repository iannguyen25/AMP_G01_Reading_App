package com.example.amp_g01_reading_app.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ServiceReadyReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceReadyReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        // Kiểm tra xem intent có đúng với action mà chúng ta đang lắng nghe không
        if ("com.example.amp_g01_reading_app.SERVICE_READY".equals(intent.getAction())) {
            Log.d(TAG, "Service is ready");
            // Bạn có thể thêm logic để thông báo cho ứng dụng khi dịch vụ đã sẵn sàng.
        }
    }
}
