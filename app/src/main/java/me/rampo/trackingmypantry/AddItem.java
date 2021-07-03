package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItem extends Fragment {
    Context context;
    String accessToken;
    Bundle b;
    String token;
    String barcode;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.add_item, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        context = this.getContext();
        super.onViewCreated(view, savedInstanceState);
        b = getArguments();
        barcode = b.getString("barcode");
        token = b.getString("token");
        accessToken = b.getString("accessToken");
        ArrayList<String> insertedproducts = new ArrayList<>();

        EditText add_barcode = view.findViewById(R.id.add_item_barcode);
        add_barcode.setText(barcode);

        view.findViewById(R.id.add_item_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = view.findViewById(R.id.add_item_name);
                EditText desc = view.findViewById(R.id.add_item_desc);
                JSONObject body = new JSONObject();

                try {
                    body.put("token", token);
                    body.put("name", name.getText().toString());
                    body.put("description", desc.getText().toString());
                    body.put("barcode", add_barcode.getText().toString());
                    body.put("test",false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String requestBody = body.toString();
                insertedproducts.add(requestBody);
                RequestQueue queue = Volley.newRequestQueue(context);
                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, "https://lam21.modron.network/products", null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage("Elemento aggiunto! Vuoi aggiungerne altri?").setCancelable(true);
                                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        name.setText("");
                                        desc.setText("");
                                        add_barcode.setText("");
                                        dialog.dismiss();
                                    }
                                });
                                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        b.putStringArrayList("products",insertedproducts);
                                        dialog.dismiss();
                                        NavHostFragment.findNavController(AddItem.this).navigate(R.id.action_Add_Home,b);
                                    }
                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ErrorAddItem", error.toString());
                        Toast.makeText(context, "Unexpected error",
                                Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public byte[] getBody() {
                        return requestBody.getBytes(StandardCharsets.UTF_8);
                    }
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
        });
    }
}
