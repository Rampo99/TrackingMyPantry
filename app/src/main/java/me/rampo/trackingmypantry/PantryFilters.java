package me.rampo.trackingmypantry;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.List;

public class PantryFilters extends Fragment {
    Bundle b;
    Context context;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pantryfilters, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        b = getArguments();
        context = this.getContext();
        DBHelper db = new DBHelper(context);
        List<String> filters = db.getFilters();
        CheckBox btn1 = view.findViewById(R.id.filter1);
        CheckBox btn2 = view.findViewById(R.id.filter2);
        CheckBox btn3 = view.findViewById(R.id.filter3);
        CheckBox btn4 = view.findViewById(R.id.filter4);
        CheckBox btn5 = view.findViewById(R.id.filter5);
        CheckBox btn6 = view.findViewById(R.id.filter6);
        CheckBox btn7 = view.findViewById(R.id.filter7);
        for(String f: filters){
            switch (f){
                case "Uova":
                    btn1.setChecked(true);
                    break;
                case "Carne":
                    btn4.setChecked(true);
                    break;
                case "Grassi da condimento":
                    btn7.setChecked(true);
                    break;
                case "Cereali":
                    btn5.setChecked(true);
                    break;
                case "Latte e derivati":
                    btn2.setChecked(true);
                    break;
                case "Pesce":
                    btn3.setChecked(true);
                    break;
                case "Ortaggi e frutta":
                    btn6.setChecked(true);
                    break;
            }
        }

        view.findViewById(R.id.filter_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PantryFilters.this).navigate(R.id.action_PantryFilters_Pantry,b);

            }
        });

        view.findViewById(R.id.filters_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CheckBox[] btns = {btn1,btn2,btn3,btn4,btn5,btn6,btn7};
                List<String> strings = new ArrayList<>();
                for(CheckBox b: btns) {
                    if(b.isChecked()) strings.add(b.getText().toString());
                }
                db.addFilter(strings);
                NavHostFragment.findNavController(PantryFilters.this).navigate(R.id.action_PantryFilters_Pantry,b);

            }
        });
    }
}
