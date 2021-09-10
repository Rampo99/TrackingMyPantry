package me.rampo.trackingmypantry;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class Pantry extends Fragment {
    Context context;
    RecyclerView productsview;
    Bundle b;
    SharedPreferences preferences;
    List<Product> products = null;
    ImageView[] filtersimages;
    ImageView[] categoriesimages;
    ProductDao productDao;
    PantryAdapter adapter;
    OnBackPressedCallback callback;
    Executor mExecutor = Executors.newSingleThreadExecutor();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.pantry, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        b = getArguments();
        context = this.getContext();
        AppDatabase db = Room.databaseBuilder(this.getActivity().getApplicationContext(), AppDatabase.class, "pantry-db").build();
        productDao = db.productDao();
        productsview = view.findViewById(R.id.pantry_elements);
        EditText text = view.findViewById(R.id.pantry_search);
        preferences = this.getActivity().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Set<String> tempfilters = preferences.getStringSet("filters",null);
        Set<String> tempcategories = preferences.getStringSet("categories",null);
        Set<String> filters = new HashSet<>();
        Set<String> categories = new HashSet<>();
        if(tempfilters != null) filters.addAll(tempfilters);
        if(tempcategories != null) categories.addAll(tempcategories);
        ImageView pesce =view.findViewById(R.id.pesce);
        ImageView carne = view.findViewById(R.id.carne);
        ImageView uova = view.findViewById(R.id.uova);
        ImageView grassi = view.findViewById(R.id.grassi);
        ImageView latte = view.findViewById(R.id.latte);
        ImageView frutta =view.findViewById(R.id.frutta);
        ImageView cereali = view.findViewById(R.id.cereali);

        callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                this.remove();
                NavHostFragment.findNavController(Pantry.this).navigate(R.id.action_Pantry_Home,b);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(callback);
        filtersimages = new ImageView[]{pesce,carne,uova,latte,grassi,frutta,cereali};

        ImageView pesce2 =view.findViewById(R.id.pesce2);
        ImageView carne2 = view.findViewById(R.id.carne2);
        ImageView uova2 = view.findViewById(R.id.uova2);
        ImageView grassi2 = view.findViewById(R.id.grassi2);
        ImageView latte2 = view.findViewById(R.id.latte2);
        ImageView frutta2 =view.findViewById(R.id.frutta2);
        ImageView cereali2 = view.findViewById(R.id.cereali2);

        categoriesimages = new ImageView[]{pesce2,carne2,uova2,latte2,grassi2,frutta2,cereali2};

        if(filters.size() == 0) {
            mExecutor.execute(()->{
                products = productDao.get();
                new Handler(Looper.getMainLooper()).post(this::setAdapter);
            });

        } else {
            mExecutor.execute(()->{
                products = productDao.getByFilters(filters);
                for(String s: filters){
                    for(ImageView imageView:filtersimages){
                        String content = (String) imageView.getContentDescription();
                        if(content.equals(s)) imageView.setBackgroundColor(Color.GREEN);
                    }
                }
                new Handler(Looper.getMainLooper()).post(this::setAdapter);
            });
        }
        if(categories.size() > 0){
            for(String s: categories){
                for(ImageView imageView:categoriesimages){
                    String content = (String) imageView.getContentDescription();
                    if(content.equals(s)) imageView.setBackgroundColor(Color.GREEN);
                }
            }
        }

        for(ImageView imageView : filtersimages){
            String content = (String) imageView.getContentDescription();
            imageView.setOnClickListener(v -> {
                if(v.equals(imageView)){
                    int green = Color.GREEN;
                    int red = Color.RED;
                    Drawable background = imageView.getBackground();
                    if (background instanceof ColorDrawable){
                        int color = ((ColorDrawable) background).getColor();
                        if(color == green){
                            filters.remove(content);
                            imageView.setBackgroundColor(red);
                        } else {
                            filters.add(content);
                            imageView.setBackgroundColor(green);
                        }
                        if(filters.size() > 0){
                            mExecutor.execute(()->{
                                if(text.getText().toString().equals("")) products = productDao.getByFilters(filters);
                                else products = productDao.getByNameandFilter("%"+text.getText().toString()+"%",filters);
                                new Handler(Looper.getMainLooper()).post(()->setAdapter());
                            });
                        } else {
                            mExecutor.execute(()->{
                                if(text.getText().toString().equals("")) products = productDao.get();
                                else products = productDao.getByName("%"+text.getText().toString()+"%");
                                new Handler(Looper.getMainLooper()).post(()->setAdapter());
                            });
                        }

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet("filters",filters);
                        editor.apply();

                    }

                }
            });
        }
        for(ImageView imageView : categoriesimages){
            String content = (String) imageView.getContentDescription();
            imageView.setOnClickListener(v -> {
                if(v.equals(imageView)){
                    int green = Color.GREEN;
                    int red = Color.RED;
                    Drawable background = imageView.getBackground();
                    if (background instanceof ColorDrawable){
                        int color = ((ColorDrawable) background).getColor();
                        if(color == green){
                            categories.remove(content);
                            imageView.setBackgroundColor(red);
                        } else {
                            categories.add(content);
                            imageView.setBackgroundColor(green);
                        }
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet("categories",categories);
                        editor.apply();
                    }

                }
            });
        }
        text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() > 0){
                    if(filters.size() > 0){
                        mExecutor.execute(()-> {
                            products = productDao.getByNameandFilter("%" + s.toString() + "%", filters);
                            new Handler(Looper.getMainLooper()).post(()->setAdapter());
                        });
                    } else {
                        mExecutor.execute(()-> {
                            products = productDao.getByName("%" + s.toString() + "%");
                            new Handler(Looper.getMainLooper()).post(()->setAdapter());
                        });
                    }
                } else {
                    if(filters.size() > 0){
                        mExecutor.execute(()-> {
                            products = productDao.getByFilters(filters);
                            new Handler(Looper.getMainLooper()).post(()->setAdapter());
                        });
                    } else {
                        mExecutor.execute(()-> {
                            products = productDao.get();
                            new Handler(Looper.getMainLooper()).post(()->setAdapter());
                        });
                    }
                }



            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        view.findViewById(R.id.pantry_home_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.remove();
                NavHostFragment.findNavController(Pantry.this).navigate(R.id.action_Pantry_Home,b);
            }
        });
    }
    private void setAdapter(){
        adapter = new PantryAdapter(products,productDao,Pantry.this,b,callback);
        GridLayoutManager layoutManager = new GridLayoutManager(context,1);
        productsview.setLayoutManager(layoutManager);
        productsview.setItemAnimator(new DefaultItemAnimator());
        productsview.addItemDecoration(new DividerItemDecoration(context,
                DividerItemDecoration.VERTICAL));
        productsview.setAdapter(adapter);

    }
}
