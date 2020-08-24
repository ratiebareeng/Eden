package com.example.eden.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.eden.ItemClickListener;
import com.example.eden.adapter.ItemViewHolder;
import com.example.eden.R;
import com.example.eden.model.Combo;
import com.example.eden.model.Item;
import com.example.eden.model.User;
import com.example.eden.viewmodel.ComboViewModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.example.eden.Utils.pulaCurrency;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private RecyclerView itemsRecyclerView, combosRecyclerView;
    private RecyclerView.LayoutManager gridLayoutManager;

    private List<Item> itemList = new ArrayList<>();


    private NavController navController;
    private HomeFragmentArgs homeFragmentArgs;

    private User user;

    private FirebaseDatabase firebaseDatabase;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> itemsAdapter;
    private FirebaseRecyclerAdapter<Combo, ComboViewModel> comboAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseDatabase = FirebaseDatabase.getInstance();
        getUserFromAction();

    }

    private void getUserFromAction() {
        if (getArguments() != null) {
            homeFragmentArgs = HomeFragmentArgs.fromBundle(getArguments());
            user = homeFragmentArgs.getUser();
            if (user != null)
                Log.d(TAG, "getUserFromAction: " + user.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLayoutViews(view);
        initNavgation(view);
        getCombosFromFirebase();
        getItemsFromFirebase();
    }

    private void initLayoutViews(View view) {
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view);
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        combosRecyclerView = view.findViewById(R.id.combo_recycler_view);
        combosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void initNavgation(View view) {
        navController = Navigation.findNavController(view);
    }

    private void getCombosFromFirebase() {
        Query combosQuery = firebaseDatabase.getReference("combos");

        FirebaseRecyclerOptions<Combo> comboOptions = new FirebaseRecyclerOptions.Builder<Combo>()
                .setQuery(combosQuery, Combo.class)
                .build();

        comboAdapter = new FirebaseRecyclerAdapter<Combo, ComboViewModel>(comboOptions) {
            @Override
            protected void onBindViewHolder(@NonNull ComboViewModel holder, int position, @NonNull Combo model) {
                Glide.with(HomeFragment.this)
                        .load(model.getiUrl())
                        .thumbnail(0.25f)
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .into(holder.comboImage);

                Log.d(TAG, "onBindViewHolder: test");

                holder.name.setText(model.getComboName());
                holder.price.setText(pulaCurrency.format(model.getComboPrice()));
                StringBuilder itemListBuilder = new StringBuilder();

                /*for (String itemName : model.getComboItems()) {
                    itemListBuilder.append(itemName).append(" ");
                }
                holder.itemList.setText(itemListBuilder);*/

                holder.setItemClickListener((view, position1, isLongClick) -> {
                    Toast.makeText(getContext(), model.getComboName(), Toast.LENGTH_SHORT).show();
                });

            }

            @NonNull
            @Override
            public ComboViewModel onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = getLayoutInflater().inflate(R.layout.combo_card, parent, false);
                return new ComboViewModel(view);
            }
        };

        combosRecyclerView.setAdapter(comboAdapter);
    }

    private void getItemsFromFirebase() {
        Query allItems = firebaseDatabase.getReference("items");

        FirebaseRecyclerOptions<Item> itemOptions =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(allItems, Item.class)
                        .build();

        itemsAdapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(itemOptions) {
            @Override
            protected void onBindViewHolder(@NonNull final ItemViewHolder holder, int position, @NonNull final Item model) {

                Glide.with(requireActivity()).load(model.getImageUrl()).into(holder.itemImage);
                holder.itemName.setText(model.getName());
                holder.itemPrice.setText(pulaCurrency.format(model.getPrice()));

                holder.setItemClickListener((view, position1, isLongClick) -> {
                    String itemName = model.getName();
                    Toast.makeText(getActivity(), model.getName(), Toast.LENGTH_SHORT).show();

                    HomeFragmentDirections.ActionHomeFragmentToItemDetailsFragment homeToDetails = HomeFragmentDirections.actionHomeFragmentToItemDetailsFragment();
                    homeToDetails.setItemName(itemName);
                    navController.navigate(homeToDetails);
                });
            }

            @NonNull
            @Override
            public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_card,
                        parent, false);
                return new ItemViewHolder(view);
            }
        };
        itemsRecyclerView.setAdapter(itemsAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        itemsAdapter.startListening();
        comboAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        itemsAdapter.stopListening();
        comboAdapter.stopListening();
    }
}