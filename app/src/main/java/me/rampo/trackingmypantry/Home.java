package me.rampo.trackingmypantry;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Home extends Fragment {
    Context context;
    String accessToken = null;
    RecyclerView productsview;
    ArrayList<String> insertedproducts = null;
    String cameraBarcode = null;
    List<WebProduct> productList;
    RequestQueue queue;
    Bundle b;
    String token;
    AppDatabase db;
    ProductDao products;
    EditText barcode;
    OnBackPressedCallback callback;
    HomeAdapter adapter;
    LifecycleOwner lifecycleOwner;
    SharedPreferences preferences;

    ActivityResultLauncher<String[]> activityResultLauncher;
    public Home(){
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
            Log.e("activityResultLauncher", ""+result.toString());
            boolean areAllGranted = true;
            for(boolean b : result.values()) {
                areAllGranted = areAllGranted && b;
            }

            if(areAllGranted) {
                callback.remove();
                NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Qrcode,b);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        lifecycleOwner = getViewLifecycleOwner();
        return inflater.inflate(R.layout.home, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        context = this.getContext();
        queue = Volley.newRequestQueue(context);
        preferences = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        b = getArguments();
        db = Room.databaseBuilder(this.getActivity().getApplicationContext(), AppDatabase.class, "pantry-db").build();
        products = db.productDao();
        try {
            accessToken = b.getString("accessToken");
            insertedproducts = b.getStringArrayList("products");
            cameraBarcode = b.getString("qrCode");
        }catch (Exception ignored){}


        productsview = view.findViewById(R.id.home_products);
        if(insertedproducts != null){
            b.remove("products");
            //print my products
            productList = new Gson().fromJson(insertedproducts.toString(),new TypeToken<List<WebProduct>>(){}.getType());
            setAdapter();
        }
        if(cameraBarcode != null){
            b.remove("qrCode");
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
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove("loginToken");
                        editor.remove("loginDate");
                        editor.apply();
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
        view.findViewById(R.id.home_camera_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == 0) {
                    callback.remove();
                    NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Qrcode,b);
                } else {
                    activityResultLauncher.launch(new String[]{Manifest.permission.CAMERA});
                }


            }
        });
        view.findViewById(R.id.home_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.remove();
                NavHostFragment.findNavController(Home.this).navigate(R.id.action_Home_Add,b);

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
                            boolean check = productList == null;
                            productList = new Gson().fromJson(products,new TypeToken<List<WebProduct>>(){}.getType());
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
                                if(check) setAdapter();
                                else {
                                    adapter.refreshList(productList);
                                    adapter.notifyDataSetChanged();
                                }
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
    private void setAdapter(){

        adapter = new HomeAdapter(productList,token,accessToken,products,context);
        GridLayoutManager layoutManager = new GridLayoutManager(context,1);
        productsview.setHasFixedSize(true);
        productsview.setLayoutManager(layoutManager);
        productsview.setItemAnimator(new DefaultItemAnimator());
        productsview.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
        productsview.setAdapter(adapter);


    }

}