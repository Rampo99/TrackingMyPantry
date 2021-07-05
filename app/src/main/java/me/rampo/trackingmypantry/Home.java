package me.rampo.trackingmypantry;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Home extends Fragment {
    Context context;
    String accessToken = null;
    ListView productsview;
    ArrayList<String> insertedproducts = null;
    String cameraBarcode = null;
    List<Product> productList;
    RequestQueue queue;
    Bundle b;
    String token;
    DBHelper db;
    EditText barcode;
    OnBackPressedCallback callback;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.home, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        context = this.getContext();
        queue = Volley.newRequestQueue(context);
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        b = getArguments();
        db = new DBHelper(context);
        try {
            accessToken = b.getString("accessToken");
            insertedproducts = b.getStringArrayList("products");
            cameraBarcode = b.getString("qrCode");
        }catch (Exception ignored){}


        productsview = view.findViewById(R.id.home_products);
        if(insertedproducts != null){
            //print my products
            productList = new Gson().fromJson(insertedproducts.toString(),new TypeToken<List<Product>>(){}.getType());
            ArrayAdapter<Product> productArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,productList);
            productsview.setAdapter(productArrayAdapter);
            setlistener();
        }
        if(cameraBarcode != null){
            barcode = view.findViewById(R.id.home_barcode);
            barcode.setText(cameraBarcode);
            getProducts(cameraBarcode);
        }
        callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                OnBackPressedCallback cb = this;
                // Handle the back button event
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Vuoi uscire dal tuo account?").setCancelable(true);
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        cb.remove();
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

        view.findViewById(R.id.home_pantry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.remove();
                NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Pantry,b);
            }
        });
        view.findViewById(R.id.home_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcode = view.findViewById(R.id.home_barcode);
                getProducts(barcode.getText().toString());


            }
        });
        view.findViewById(R.id.home_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.remove();
                NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Qrcode,b);

            }
        });

    }
    void setlistener(){
        productsview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Product p = productList.get(position);


                //rating part

                JSONObject body = new JSONObject();
                try {
                    body.put("token",token);
                    body.put("rating",1);
                    body.put("productId",p.getId());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                RequestQueue queue = Volley.newRequestQueue(context);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://lam21.modron.network/votes", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Toast.makeText(context, "Aggiunto rating!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorRating", error.toString());
                        Toast.makeText(context, "Hai giá selezionato questo prodotto!",
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return body.toString().getBytes(StandardCharsets.UTF_8);
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8";
                    }

                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("Authorization", "Bearer " + accessToken);
                        return params;
                    }
                };
                queue.add(request);

                //add to db here
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Vuoi aggiungere questo prodotto alla tua dispensa?").setCancelable(true);
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       //aggiungi prodotto a lista.
                        db.addProduct(p);
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
        });
    }
    void getProducts(String barcode){
        JsonObjectRequest request = new JsonObjectRequest(com.android.volley.Request.Method.GET, "https://lam21.modron.network/products?barcode="+barcode, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String products = response.getString("products");
                            token = response.getString("token");
                            productList = new Gson().fromJson(products,new TypeToken<List<Product>>(){}.getType());
                            if(productList.size() == 0) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Non é stato trovato nessun elemento con questo barcode, vuoi aggiungerlo?").setCancelable(true);
                                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        callback.remove();
                                        b.putString("accessToken",accessToken);
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
                                //print products here
                                ArrayAdapter<Product> productArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,productList);
                                productsview.setAdapter(productArrayAdapter);
                                setlistener();
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