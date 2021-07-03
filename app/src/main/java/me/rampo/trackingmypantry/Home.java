package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    Context context;
    String accessToken;

    String insertedproducts;
    Bundle b;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        context = this.getContext();
        super.onViewCreated(view, savedInstanceState);
        b = getArguments();
        accessToken = b.getString("accessToken");
        insertedproducts = b.getString("products");
        if(insertedproducts != null){
            //print my products
            //tbd
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Vuoi uscire dal tuo account?").setCancelable(true);
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Login);
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);

        view.findViewById(R.id.home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText barcode = view.findViewById(R.id.home_barcode);

                getProducts(barcode.getText().toString());


            }
        });


    }

    void getProducts(String barcode){
        RequestQueue queue = Volley.newRequestQueue(context);
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, "https://lam21.modron.network/products?barcode="+barcode, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String products = response.getString("products");
                            String token = response.getString("token");
                            List<Product> productList = new Gson().fromJson(products,new TypeToken<List<Product>>(){}.getType());
                            if(productList.size() == 0) {
                                Log.d("test","non trovati");
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Non é stato trovato nessun elemento con questo barcode, vuoi aggiungerlo?").setCancelable(true);
                                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        b.putString("barcode",barcode);
                                        b.putString("token",token);
                                        NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Add,b);
                                    }
                                });
                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                Log.d("test","trovati");
                                //print products here
                                //tbd
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("ErrorBarcodeSearch", error.toString());
                Toast.makeText(context, "Unexpected error",
                        Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();
                params.put("Authorization", "Bearer " + accessToken);
                return params;
            }
        };
        queue.add(request);
    }

}