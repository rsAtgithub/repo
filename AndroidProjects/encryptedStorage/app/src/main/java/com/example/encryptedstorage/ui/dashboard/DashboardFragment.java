package com.example.encryptedstorage.ui.dashboard;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.encryptedstorage.R;
import com.example.encryptedstorage.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    private int getId(int row, int column){
        int id = (row * 10) + column;
        return id;
    }

    public void onButtonShowPopupWindowClick(View view) {

        // inflate the layout of the popup window
        LayoutInflater inflater = (LayoutInflater)
                getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);

        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true; // lets taps outside the popup also dismiss it
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        // dismiss the popup window when touched
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    private void ct2() {
        try {
            TableLayout tl;
            tl = (TableLayout) binding.getRoot().findViewById(R.id.fragment1_tlayout);
            //int matrix[][] = new int[73][3];

            for (int i = 0; i < 73; i++) {
                int runningColumn = 0;
                //matrix[i][runningColumn] = (i << 8) + runningColumn;

                TableRow tr = new TableRow(getActivity());

                tr.setId(i);
                //tr.setBackgroundResource(0xFFFF00);
                tr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


                /*CheckBox cb = new CheckBox(getActivity());
                cb.setId(getId(i, runningColumn));
                tr.addView(cb);
                runningColumn++;*/

                TextView tv1 = new TextView(getActivity());
                tv1.setText("TEST NUMBER");
                tv1.setId(getId(i, runningColumn));
                tv1.setTextColor(Color.WHITE);
                tv1.setTextSize(20);
                tv1.setPadding(5, 5, 5, 5);
                tr.addView(tv1);

                tv1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundColor(Color.GREEN);
                        int rowNumber = view.getId();
                        Toast.makeText(getActivity(), "Cell:" + rowNumber + " selected", Toast.LENGTH_SHORT).show();
                    }
                });

            /*tr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    view.setBackgroundColor(Color.GREEN);
                    int rowNumber = view.getId();
                    Toast.makeText(getActivity(), "Row:" + rowNumber + " selected", Toast.LENGTH_SHORT).show();
                }
            });*/

                runningColumn++;
                //matrix[i][runningColumn] = (i << 8) + runningColumn;
                TextView tv2 = new TextView(getActivity());

                tv2.setText("L : " + i);
                tv2.setId(getId(i, runningColumn));
                tv2.setTextColor(Color.WHITE);
                tv1.setTextSize(20);
                tv2.setPadding(5, 5, 5, 5);
                tr.addView(tv2);

                //tl.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                tv2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundColor(Color.YELLOW);
                        int rowNumber = view.getId();
                        Toast.makeText(getActivity(), "Cell:" + rowNumber + " selected", Toast.LENGTH_SHORT).show();
                        onButtonShowPopupWindowClick(view);
                    }
                });

                runningColumn++;
                //matrix[i][runningColumn] = (i << 8) + runningColumn;
                //TextView tv3 = new TextView(getActivity());
                EditText tv3 = new EditText(getActivity());

                tv3.setText("p: " + i);
                tv3.setId(getId(i, runningColumn));
                tv3.setTextColor(Color.WHITE);
                tv3.setTextSize(20);
                tv3.setPadding(5, 5, 5, 5);
                tr.addView(tv3);



                tv3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        view.setBackgroundColor(Color.BLUE);
                        int rowNumber = view.getId();
                        Toast.makeText(getActivity(), "Cell:" + rowNumber + " selected", Toast.LENGTH_SHORT).show();
                    }
                });
                /*
                tv3.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        Toast.makeText(getActivity(), "Here:" + keyEvent + " selected", Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });*/

                tl.addView(tr, new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            }
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //createTable();
        //ct2();
        //setContentView(new TableMainLayout(this.getContext()));
        //new TableMainLayout(this.getContext());

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        ct2();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}