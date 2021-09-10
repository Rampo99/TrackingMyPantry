package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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
        AppDatabase db = Room.databaseBuilder(this.getActivity().getApplicationContext(), AppDatabase.class, "pantry-db").build();
        ProductDao products = db.productDao();
        b = getArguments();
        EditText t = view.findViewById(R.id.options_date);
        EditText maptext = view.findViewById(R.id.options_place);
        TextView productName = view.findViewById(R.id.pname);
        String productId = b.getString("productId");
        Spinner spinner = view.findViewById(R.id.options_spinner);
        Executor mExecutor = Executors.newSingleThreadExecutor();
        mExecutor.execute(()->{
            Product p = products.getById(productId);
            String name = p.getName();
            if(name != null) productName.setText(name);
            int position;
            if(p.getCategoria() == null){
                position = 0;
            } else {

                switch (p.getCategoria()) {
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

            ArrayAdapter<CharSequence> staticAdapter = ArrayAdapter.createFromResource(context, R.array.options, android.R.layout.simple_spinner_item);

            staticAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(staticAdapter);
            spinner.setSelection(position);
        });

        view.findViewById(R.id.options_confirm).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String category = spinner.getSelectedItem().toString();
                String date = t.getText().toString();
                String place = maptext.getText().toString();
                if(!date.equals("")) {
                    if (!place.equals("")) {
                        if (isValidDate(date)) {
                            mExecutor.execute(() -> {
                                products.addDate(productId, date);
                                products.addCategory(productId, category);
                                products.addPlace(productId, place);
                                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Prodotto aggiornato correttamente",Toast.LENGTH_SHORT).show());

                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Inserisci una data corretta!",Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        if (isValidDate(date)) {
                            mExecutor.execute(() -> {
                                products.addDate(productId, date);
                                products.addCategory(productId, category);
                                new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Prodotto aggiornato correttamente",Toast.LENGTH_SHORT).show());
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Inserisci una data corretta!",Toast.LENGTH_SHORT).show());
                        }
                    }
                } else {
                    if(!place.equals("")){
                        mExecutor.execute(() -> {
                            products.addCategory(productId, category);
                            products.addPlace(productId, place);
                            new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Prodotto aggiornato correttamente",Toast.LENGTH_SHORT).show());
                        });
                    } else {
                        mExecutor.execute(() -> {
                            products.addCategory(productId, category);
                        });
                        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(context, "Prodotto aggiornato correttamente",Toast.LENGTH_SHORT).show());
                    }

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
