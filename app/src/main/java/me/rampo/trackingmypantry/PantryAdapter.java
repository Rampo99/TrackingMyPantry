package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.PantryHolder> {
    private List<Product> productList;
    private ProductDao productDao;
    private Fragment fragment;
    private Bundle b;
    private Executor mExecutor = Executors.newSingleThreadExecutor();
    private Context context;
    private OnBackPressedCallback callback;
    public PantryAdapter(List<Product> productList, ProductDao productDao, Fragment fragment, Bundle b, OnBackPressedCallback callback, Context context){
        this.productList = productList;
        this.productDao = productDao;
        this.fragment = fragment;
        this.b = b;
        this.callback = callback;
        this.context = context;
    }
    public class PantryHolder extends RecyclerView.ViewHolder{
        private TextView productname;
        private TextView productdesc;
        private TextView productid;
        private TextView productbarcode;
        private TextView productplace;
        private TextView productquantity;
        private TextView productcategory;
        private TextView productdate;
        private ImageView plusButton;
        private ImageView minusButton;
        private ImageView editButton;
        private ImageView mapButton;
        public PantryHolder(final View view){
            super(view);
            productname = view.findViewById(R.id.productname1);
            productdesc = view.findViewById(R.id.productdesc1);
            productid = view.findViewById(R.id.productid1);
            productbarcode = view.findViewById(R.id.productbarcode1);
            productplace = view.findViewById(R.id.productplace1);
            productquantity = view.findViewById(R.id.productquantity1);
            productcategory = view.findViewById(R.id.productcat1);
            productdate = view.findViewById(R.id.productdate1);
            plusButton = view.findViewById(R.id.plusbutton);
            minusButton = view.findViewById(R.id.minusbotton);
            editButton = view.findViewById(R.id.editbutton);
            mapButton = view.findViewById(R.id.mapbutton);
        }
    }
    @NonNull
    @Override
    public PantryAdapter.PantryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.productitem,parent,false);
        return new PantryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PantryAdapter.PantryHolder holder, int position) {
        Product p = productList.get(position);
        holder.productname.setText(p.getName());
        holder.productbarcode.setText(p.getBarcode());
        holder.productdesc.setText(p.getDescription());
        holder.productid.setText(p.getId());
        holder.productcategory.setText(p.getCategoria());
        holder.productquantity.setText(String.valueOf(p.getQuantity()));
        holder.productplace.setText(p.getPlace());
        holder.productdate.setText(p.getDateoutput());


        holder.plusButton.setOnClickListener(v -> {
            int q = p.getQuantity()+1;
            p.setQuantity(q);
            holder.productquantity.setText(String.valueOf(q));
            mExecutor.execute(() -> {
                productDao.updateProduct(p.id,q);
                new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
            });

        });
        holder.minusButton.setOnClickListener(v -> {

            int q = p.getQuantity()-1;
            if(q == 0){
                remove(p,position);
            } else {
                p.setQuantity(q);
                holder.productquantity.setText(String.valueOf(q));
                mExecutor.execute(() -> {
                    productDao.updateProduct(p.id,q);
                    new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
                });
            }

        });
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.remove();
                b.putString("productId",p.getId());
                NavHostFragment.findNavController(fragment).navigate(R.id.action_Pantry_PantryOptions,b);
            }
        });

        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(p.getPlace() == null){
                    Toast.makeText(context, "Devi prima inserire il luogo!",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + p.getPlace());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    fragment.startActivity(mapIntent);
                }

            }
        });


    }
    private void remove(Product p,int pos){
        mExecutor.execute(()->{
            productDao.delete(p);
            productList.remove(p);
            new Handler(Looper.getMainLooper()).post(() ->  {
                this.notifyItemRemoved(pos);
                this.notifyItemRangeChanged(pos,productList.size());
            });

        });

    }
    @Override
    public int getItemCount() {
        return productList.size();
    }


}
