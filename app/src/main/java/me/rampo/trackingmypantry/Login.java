package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;


public class Login extends Fragment {
    Context context;
    SharedPreferences preferences;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        preferences = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        String date = preferences.getString("loginDate",null);
        if(date != null){
            try {

                Date d = new SimpleDateFormat("dd/MM/yyyy").parse(date);

                Date today = Calendar.getInstance().getTime();
                long diffInMillies = Math.abs(today.getTime() - d.getTime());
                long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                if(diff <= 7) {
                    Bundle b = new Bundle();
                    String token = preferences.getString("loginToken",null);
                    b.putString("accessToken",token);
                    NavHostFragment.findNavController(Login.this).navigate(R.id.action_Login_Home,b);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        context = this.getContext();


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
                    Bundle b = new Bundle();
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
                                    try {
                                        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                        String token = response.get("accessToken").toString();
                                        b.putString("accessToken",token);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("loginToken",token);
                                        editor.putString("loginDate",dateFormat.format(Calendar.getInstance().getTime()));
                                        editor.apply();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(context, "Login avvenuto!",
                                            Toast.LENGTH_LONG).show();
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