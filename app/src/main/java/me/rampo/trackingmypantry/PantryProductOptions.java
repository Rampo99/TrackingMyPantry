package me.rampo.trackingmypantry;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PantryProductOptions extends Fragment {
    Context context;
    Bundle b;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.pantryoptions, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {

        context = this.getContext();
        DBHelper db = new DBHelper(context);
        b = getArguments();
        Spinner spinner = view.findViewById(R.id.options_spinner);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(context, R.array.options, android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(staticAdapter);

        view.findViewById(R.id.options_confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String category = spinner.getSelectedItem().toString();
                EditText t = view.findViewById(R.id.options_date);
                String date = t.getText().toString();




                String productId = b.getString("productId");

                if(isValidDate(date)){
                    db.addDate(productId, t.getText().toString());
                    db.addCategory(productId,category);
                    NavHostFragment.findNavController(PantryProductOptions.this).navigate(R.id.action_PantryOptions_Pantry,b);
                } else {
                    Toast.makeText(context, "Digita una data corretta!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        view.findViewById(R.id.options_back).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(PantryProductOptions.this).navigate(R.id.action_PantryOptions_Pantry,b);
            }
        });
    }
    private boolean isValidDate(String s){
        if(!s.equals("")){
            try {
                Date d = new SimpleDateFormat("dd/MM/yyyy").parse(s);
                return true;
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
