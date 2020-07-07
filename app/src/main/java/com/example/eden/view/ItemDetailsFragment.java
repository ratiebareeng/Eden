package com.example.eden.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eden.adapter.ItemViewHolder;
import com.example.eden.viewmodel.OrderViewModel;
import com.example.eden.R;
import com.example.eden.model.Item;
import com.example.eden.model.OrderEntity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.Locale;

public class ItemDetailsFragment extends Fragment {
    private static final String TAG = "ItemDetailsFragment";
    private static final String ITEMS_DB_ROOT = "items";
    private String itemName;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference itemDatabaseReference;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> firebaseItemsAdapter;
    private Item item;


    private static final int MIN_ITEM_QUANTITY = 1;
    private static final int MAX_ITEM_QUANTITY = 20;
    private int itemQuantity = 1;
    private double orderTotal;
    private TextView itemNameTextView, itemDescriptionTextView, itemPriceTextView, itemQuantityTextView;
    private ImageView itemImage;
    private Button decreaseQuantity;
    private Button increaseQuantity;
    private Button addToCart;

    private Locale botswana;
    private NumberFormat pulaCurrencyFormat;
    private OrderViewModel orderViewModel;
    private NavController navController;

    private ItemDetailsFragmentArgs detailsFragmentArgs;

    public ItemDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setLocaleCurrency();
        getItemNameArg();
        initFirebase();
        getItemDetailsFromFirebase();
        initViewModel();
    }

    private void setLocaleCurrency() {
        botswana = new Locale("en", "BW");
        pulaCurrencyFormat = NumberFormat.getCurrencyInstance(botswana);
    }

    private void getItemNameArg() {
        if (getArguments() != null) {
            detailsFragmentArgs = ItemDetailsFragmentArgs.fromBundle(getArguments());
            itemName = detailsFragmentArgs.getItemName();
        }
    }

    private void initFirebase() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        itemDatabaseReference = firebaseDatabase.getReference(ITEMS_DB_ROOT).child(itemName);
    }

    private void getItemDetailsFromFirebase() {
        itemDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    item = dataSnapshot.getValue(Item.class);
                    if (item != null) {
                        Glide.with(requireContext()).load(item.getImageUrl()).into(itemImage);
                        itemNameTextView.setText(item.getName());
                        itemDescriptionTextView.setText(String.format("Fresh, organic %s harvested from our Gaborone farm", itemName.toLowerCase()));
                        itemPriceTextView.setText(pulaCurrencyFormat.format(item.getPrice()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_details, container, false);
        initLayoutViews(view);
        initOnClickListeners();
        return view;
    }

    private void initLayoutViews(View view) {
        itemNameTextView = view.findViewById(R.id.item_name);
        itemDescriptionTextView = view.findViewById(R.id.item_description);
        itemPriceTextView = view.findViewById(R.id.item_price);
        itemQuantityTextView = view.findViewById(R.id.item_quantity);
        itemImage = view.findViewById(R.id.item_image);
        increaseQuantity = view.findViewById(R.id.increase_quantity);
        decreaseQuantity = view.findViewById(R.id.decrease_quantity);
        addToCart = view.findViewById(R.id.add_to_cart);
    }

    private void initOnClickListeners() {
        decreaseQuantity.setOnClickListener(v -> decreaseItemQuantity());
        increaseQuantity.setOnClickListener(v -> increaseItemQuantity());
        addToCart.setOnClickListener(v -> addItemToCart());
    }

    private void increaseItemQuantity() {
        if (itemQuantity < MAX_ITEM_QUANTITY){
            itemQuantity++;
            itemQuantityTextView.setText(String.valueOf(itemQuantity));
            updatePriceForQuantity();
        }
    }

    private void decreaseItemQuantity() {
        if (itemQuantity > 1){
            itemQuantity--;
            itemQuantityTextView.setText(String.valueOf(itemQuantity));
            updatePriceForQuantity();
        }
    }

    private void updatePriceForQuantity() {
        orderTotal = item.getPrice() * itemQuantity;
        itemPriceTextView.setText(pulaCurrencyFormat.format(orderTotal));
        Log.d(TAG, "updatePriceForQuantity: item quantity " + itemQuantity);
    }

    private void addItemToCart() {
        updatePriceForQuantity();
        OrderEntity orderEntity = new OrderEntity(itemName, item.getPrice(), itemQuantity, orderTotal);
        orderEntity.setItemImageUrl(item.getImageUrl());
        orderViewModel.insertOrder(orderEntity);
        Log.d(TAG, "item added to cart");
        Toast.makeText(getContext(), itemName + " added to cart", Toast.LENGTH_SHORT).show();
    }

    private void initViewModel() {
        orderViewModel = new ViewModelProvider(this).get(OrderViewModel.class);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initNavController(view);
    }

    private void initNavController(View view) {
        navController = Navigation.findNavController(view);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.toolbar_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) ||
                super.onOptionsItemSelected(item);
    }
}