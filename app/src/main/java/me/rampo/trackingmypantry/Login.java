package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.Intent;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;


public class Login extends Fragment {
    Context context;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        context = this.getContext();
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.login_to_register_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(Login.this)
                        .navigate(R.id.action_Login_Register);
            }
        });
        view.findViewById(R.id.login_btn).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                EditText mail = view.findViewById(R.id.login_mail);
                EditText psw = view.findViewById(R.id.login_psw);
                try {
                    RequestQueue queueLogin = Volley.newRequestQueue(context);
                    JSONObject body = new JSONObject();
                    body.put("email", mail.getText().toString());
                    body.put("password", psw.getText().toString());
                    String requestBody = body.toString();
                    JsonObjectRequest request = new JsonObjectRequest(
                            com.android.volley.Request.Method.POST, "https://lam21.modron.network/auth/login", null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Bundle b = new Bundle();
                                    try {
                                        String token = response.get("accessToken").toString();

                                        b.putString("accessToken",token);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Log.d("Success", response.toString());
                                    Toast.makeText(context, "Login avvenuto!",
                                            Toast.LENGTH_LONG).show(); //SOSTITUIRE CON DIALOG
                                    //finish
                                    NavHostFragment.findNavController(Login.this).navigate(R.id.action_Login_Home,b);
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d("ErrorLogin", error.toString());
                            Toast.makeText(context, "Credenziali errate!",
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
                    queueLogin.add(request);




                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}