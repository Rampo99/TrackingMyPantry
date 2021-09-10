package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeHolder> {
    private List<WebProduct> webProductList;
    private String token;
    private String accessToken;
    private Context context;
    private ProductDao products;
    private Executor mExecutor = Executors.newSingleThreadExecutor();

    public HomeAdapter(List<WebProduct> webProductList, String token,String accessToken, ProductDao products, Context context){
        this.webProductList = webProductList;
        this.token = token;
        this.accessToken = accessToken;
        this.products = products;
        this.context = context;
    }
    public class HomeHolder extends RecyclerView.ViewHolder{

        private TextView webProductname;
        private TextView webProductdesc;
        private TextView webProductid;
        private TextView webProductbarcode;
        private ImageView addButton;
        private ImageView likeButton;
        public HomeHolder(final View view){
            super(view);

            webProductname = view.findViewById(R.id.webproductname1);
            webProductdesc = view.findViewById(R.id.webproductdesc1);
            webProductid = view.findViewById(R.id.webproductid1);
            webProductbarcode = view.findViewById(R.id.webproductbarcode1);
            addButton = view.findViewById(R.id.addButton);
            likeButton = view.findViewById(R.id.likeButton);
        }
    }
    @NonNull
    @Override
    public HomeAdapter.HomeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.webproductitem,parent,false);
        return new HomeHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull HomeAdapter.HomeHolder holder, int position) {

        WebProduct p = webProductList.get(position);
        holder.webProductname.setText(p.getName());
        holder.webProductbarcode.setText(p.getBarcode());
        holder.webProductdesc.setText(p.getDescription());
        holder.webProductid.setText(p.getId());

        holder.addButton.setOnClickListener(v -> {
            //aggiungi prodotto a lista.
            Toast.makeText(context, "Prodotto aggiunto alla tua dispensa!",Toast.LENGTH_SHORT).show();
            mExecutor.execute(() -> {
                Product temp = products.getById(p.id);
                if (temp == null) {
                    products.insert(p.toProduct());
                } else {
                    products.updateProduct(temp.id, temp.getQuantity() + 1);
                }
            });
        });

        holder.likeButton.setOnClickListener(v -> {
            v.setClickable(false);
            holder.likeButton.setColorFilter(Color.GRAY);
            JSONObject body = new JSONObject();
            try {
                body.put("token", token);
                body.put("rating", 1);
                body.put("productId", p.getId());
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
                    Toast.makeText(context, "Hai giá aggiunto il rating a questo prodotto!",
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
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + accessToken);
                    return params;
                }
            };
            queue.add(request);

        });

    }

    @Override
    public int getItemCount() {
        return webProductList.size();
    }


    public void refreshList(List<WebProduct> webProductList){
        this.webProductList = webProductList;
    }
}
