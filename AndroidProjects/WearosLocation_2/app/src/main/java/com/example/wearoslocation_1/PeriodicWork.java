package com.example.wearoslocation_1;

import static com.example.wearoslocation_1.MainActivity.sendEmail;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class PeriodicWork extends Worker {
    public PeriodicWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        sendEmail();
        return null;
    }
}
