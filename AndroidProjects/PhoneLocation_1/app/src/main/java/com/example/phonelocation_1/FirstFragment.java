package com.example.phonelocation_1;

import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.phonelocation_1.databinding.FragmentFirstBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class FirstFragment extends Fragment {

private FragmentFirstBinding binding;

    private WorkManager mwM;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

      binding = FragmentFirstBinding.inflate(inflater, container, false);
      return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //NavHostFragment.findNavController(FirstFragment.this)
                //        .navigate(R.id.action_FirstFragment_to_SecondFragment);
                //String s = ((TextInputLayout)binding.getRoot().findViewById(R.id.input_box_000)).getEditText().getText().toString();
                //TextView t = binding.getRoot().findViewById(R.id.textview_first);
                //t.setText(s);

                mwM = WorkManager.getInstance(getActivity().getApplicationContext());

                emptyTheWorkManagerQueue();

                PeriodicWorkRequest.Builder wifiWorkBuilder =
                        new PeriodicWorkRequest.Builder(myWork.class, 1,
                                TimeUnit.MINUTES)
                                .addTag(BgTaskManager.BG_TASK_TAG)
                                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build());

                mwM.enqueue(wifiWorkBuilder.build());
                Log.d("RVS_001", "Queue Start");
            }
        });



        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyTheWorkManagerQueue();
                Log.d("RVS_001", "Queue End Finish");
            }
        });
    }

    private void emptyTheWorkManagerQueue() {
        int maxRetry = 0;

        Log.d("RVS_001", "WorkManager queue clearing starts...");
        while (maxRetry != 5) {
            if(isWorkScheduled(BgTaskManager.BG_TASK_TAG) > 0) {
                maxRetry++;
                Log.d("RVS_001", String.format("WorkManager queue is not empty, clear Attempt[%d]", maxRetry));
                // Already something in queue. Let's clear it.
                WorkManager.getInstance(getActivity().getApplicationContext()).cancelAllWorkByTag(BgTaskManager.BG_TASK_TAG);
            } else {
                Log.d("RVS_001", String.format("WorkManager queue clear in Attempts[%d]", maxRetry));
                return;
            }
        }
        Log.d("RVS_001", "WorkManager queue clearing FAILED!!!");
    }

    /** ref: https://stackoverflow.com/a/51613101
     *
     * @param tag
     * @return
     */
    private int isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance(getActivity().getApplicationContext());
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            int running = 0;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                if (state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED) {
                    running++;
                }
                Log.d("RVS_001", workInfo.toString());
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return -1;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return -2;
        }
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}