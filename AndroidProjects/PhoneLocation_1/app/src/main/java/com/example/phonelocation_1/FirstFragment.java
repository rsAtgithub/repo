package com.example.phonelocation_1;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.phonelocation_1.databinding.FragmentFirstBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;

public class FirstFragment extends Fragment {

private FragmentFirstBinding binding;

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
                PeriodicWorkRequest.Builder wifiWorkBuilder =
                        new PeriodicWorkRequest.Builder(myWork.class, 1,
                                TimeUnit.MINUTES)
                                .addTag(BgTaskManager.BG_TASK_TAG)
                                .setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build());
                PeriodicWorkRequest wifiWork = wifiWorkBuilder.build();
                WorkManager.getInstance(binding.getRoot().getContext()).enqueueUniquePeriodicWork(BgTaskManager.BG_TASK_TAG, ExistingPeriodicWorkPolicy.REPLACE, wifiWork);
            }
        });

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WorkManager.getInstance(binding.getRoot().getContext()).cancelAllWorkByTag(BgTaskManager.BG_TASK_TAG);

            }
        });
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}