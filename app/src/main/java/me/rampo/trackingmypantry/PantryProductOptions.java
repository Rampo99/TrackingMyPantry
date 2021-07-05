package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

import java.text.DateFormat;
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
        EditText t = view.findViewById(R.id.options_date);
        EditText maptext = view.findViewById(R.id.options_place);
        String productId = b.getString("productId");
        PantryProduct p = db.getProduct(productId);

        int position;
        switch (p.getCategoria()){
            case "Uova":
                position = 2;
                break;
            case "Carne":
                position = 1;
                break;
            case "Grassi da condimento":
                position = 6;
                break;
            case "Cereali":
                position = 4;
                break;
            case "Latte e derivati":
                position = 3;
                break;
            case "Ortaggi e frutta":
                position = 5;
                break;
            default:
                position = 0;
        }
        Date date = p.getDate();
        String place = p.getPlace();
        if(date != null){
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String output = dateFormat.format(date);
            t.setText(output);
        }
        if(place != null){
            maptext.setText(place);
        }
        Spinner spinner = view.findViewById(R.id.options_spinner);
        ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(context, R.array.options, android.R.layout.simple_spinner_item);

        staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(staticAdapter);
        spinner.setSelection(position);
        view.findViewById(R.id.options_confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String category = spinner.getSelectedItem().toString();
                String date = t.getText().toString();
                String place = maptext.getText().toString();
                if(isValidDate(date)){
                    db.addDate(productId, t.getText().toString());
                    db.addCategory(productId,category);
                    db.addPlace(productId,place);
                    NavHostFragment.findNavController(PantryProductOptions.this).navigate(R.id.action_PantryOptions_Pantry,b);
                } else {
                    Toast.makeText(context, "Digita una data corretta!",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });
        view.findViewById(R.id.options_map).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + maptext.getText().toString());
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);

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
