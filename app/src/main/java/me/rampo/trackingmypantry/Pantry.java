package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Pantry extends Fragment {
    Context context;
    ListView productsview;
    Bundle b;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pantry, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        b = getArguments();
        context = this.getContext();
        DBHelper db = new DBHelper(context);
        productsview = view.findViewById(R.id.pantry_elements);
        EditText text = view.findViewById(R.id.pantry_search);
        List<PantryProduct> earlyproducts = db.getProducts();
        List<PantryProduct> products = new ArrayList<>();
        List<String> filters = db.getFilters();
        if(filters.size() == 0) products.addAll(earlyproducts);
        else {
            for (PantryProduct p : earlyproducts) {
                for (String filter : filters) {
                    if (p.getCategoria().equals(filter)) {
                        products.add(p);
                        break;
                    }
                }
            }
        }


        ArrayAdapter<PantryProduct> productArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,products);
        productsview.setAdapter(productArrayAdapter);
        productsview.setTextFilterEnabled(true);
        productsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Scegli azione").setCancelable(true);
                PantryProduct p = products.get(position);
                int productquantity = p.getQuantity();
                builder.setPositiveButton("Aggiungi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        products.get(position).setQuantity(productquantity+1);
                        db.addProduct(p);
                        ArrayAdapter<PantryProduct> productArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,products);
                        productsview.setAdapter(productArrayAdapter);
                    }
                });
                builder.setNeutralButton("Opzioni", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        b.putString("productId",p.getId());
                        NavHostFragment.findNavController(Pantry.this).navigate(R.id.action_Pantry_PantryOptions,b);
                    }
                });
                builder.setNegativeButton("Rimuovi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(productquantity == 1){
                            products.remove(p);
                        } else {
                            products.get(position).setQuantity(productquantity-1);
                        }
                        db.removeProduct(p);
                        ArrayAdapter<PantryProduct> productArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,products);
                        productsview.setAdapter(productArrayAdapter);
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        });
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                productArrayAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        view.findViewById(R.id.pantry_filters).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Pantry.this).navigate(R.id.action_Pantry_PantryFilters,b);
            }
        });
        view.findViewById(R.id.pantry_important).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                List<String> categories = db.getCategories();
                String[] items = getResources().getStringArray(R.array.options);
                boolean[] checkedItems = {false, false, false,false, false,false,false};
                for(String s : categories)
                switch (s){
                    case "Uova":
                        checkedItems[2] = true;
                        break;
                    case "Carne":
                        checkedItems[1] = true;
                        break;
                    case "Grassi da condimento":
                        checkedItems[6] = true;
                        break;
                    case "Cereali":
                        checkedItems[4] = true;
                        break;
                    case "Latte e derivati":
                        checkedItems[3] = true;
                        break;
                    case "Ortaggi e frutta":
                        checkedItems[5] = true;
                        break;
                    default:
                        checkedItems[0] = true;
                }

                builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    }
                });
                builder.setPositiveButton("Conferma", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String> finalcategories = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            if (checkedItems[i]) {
                                finalcategories.add(items[i]);
                            }
                        }
                        db.addCategories(finalcategories);
                    }
                });

                builder.setNegativeButton("Annulla", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        view.findViewById(R.id.pantry_home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavHostFragment.findNavController(Pantry.this).navigate(R.id.action_Pantry_Home,b);
            }
        });
    }
}
