package me.rampo.trackingmypantry;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Register extends Fragment {
    Context context;
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.register, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        context = this.getContext();
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.register_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Register.this)
                        .navigate(R.id.action_Register_Login);
            }
        });

        view.findViewById(R.id.register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    EditText mail = view.findViewById(R.id.register_mail);
                    EditText name = view.findViewById(R.id.register_name);
                    EditText psw = view.findViewById(R.id.register_psw);
                    RequestQueue queue = Volley.newRequestQueue(context);
                    JSONObject body = new JSONObject();
                    body.put("username", name.getText().toString());
                    body.put("email", mail.getText().toString());
                    body.put("password", psw.getText().toString());
                    String requestBody = body.toString();
                    JsonObjectRequest request = new JsonObjectRequest(
                            com.android.volley.Request.Method.POST, "https://lam21.modron.network/users", null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.d("Success", response.toString());
                                    Toast.makeText(context, "Registrazione avvenuta!",
                                            Toast.LENGTH_LONG).show(); //SOSTITUIRE CON DIALOG
                                    //finish
                                    NavHostFragment.findNavController(Register.this).navigate(R.id.action_Register_Login);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ErrorRegister", error.toString());
                            Toast.makeText(context, "Account giá esistente!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        public String getBodyContentType() {
                            return "application/json; charset=utf-8";
                        }
                        @Override
                        public byte[] getBody() {
                            return requestBody.getBytes(StandardCharsets.UTF_8);
                        }
                    };
                    queue.add(request);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}