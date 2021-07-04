package me.rampo.trackingmypantry;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

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

        view.findViewById(R.id.filter_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PantryFilters.this).navigate(R.id.action_PantryFilters_Pantry,b);

            }
        });

        view.findViewById(R.id.filters_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton btn1 = view.findViewById(R.id.filter1);
                RadioButton btn2 = view.findViewById(R.id.filter2);
                RadioButton btn3 = view.findViewById(R.id.filter3);
                RadioButton btn4 = view.findViewById(R.id.filter4);
                RadioButton btn5 = view.findViewById(R.id.filter5);
                RadioButton btn6 = view.findViewById(R.id.filter6);
                RadioButton btn7 = view.findViewById(R.id.filter7);

                NavHostFragment.findNavController(PantryFilters.this).navigate(R.id.action_PantryFilters_Pantry,b);

            }
        });
    }
}
