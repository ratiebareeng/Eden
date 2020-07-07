package com.example.eden.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.eden.adapter.ItemViewHolder;
import com.example.eden.R;
import com.example.eden.model.Item;
import com.example.eden.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    /**
     * HomeFragmentDirections.ActionHomeFragmentToItemDetailsFragment homeToDetailsAction = HomeFragmentDirections.actionHomeFragmentToItemDetailsFragment(sweetCorn);
     * homeToDetailsAction.setToolbarTitle(toolbarTitleString);
     * navController.navigate(homeToDetailsAction);
     */


    private static final String TAG = "HomeFragment";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private RecyclerView itemsRecyclerView;
    private RecyclerView.LayoutManager gridLayoutManager;

    private String toolbarTitleString;
    private List<Item> itemList = new ArrayList<>();
    private FrameLayout drawerLayoutRoot;

    private Locale botswana;
    private NumberFormat pulaCurrency;

    private NavController navController;
    private HomeFragmentArgs homeFragmentArgs;

    private User user;

    private DatabaseReference itemsDatabaseReference;
    private FirebaseRecyclerAdapter<Item, ItemViewHolder> adapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getUserFromAction();
        initFirebase();

        botswana = new Locale("en", "Bw");
        pulaCurrency = NumberFormat.getCurrencyInstance(botswana);
    }

    private void getUserFromAction() {
        if (getArguments() != null) {
            homeFragmentArgs = HomeFragmentArgs.fromBundle(getArguments());
            user = homeFragmentArgs.getUser();
            if (user != null)
                Log.d(TAG, "getUserFromAction: " + user.getName());
        }
    }

    private void initFirebase() {
        // initItemsList();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        itemsDatabaseReference = firebaseDatabase.getReference().child("items");
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
        getData();
    }

    private void initLayoutViews(View view) {
        itemsRecyclerView = view.findViewById(R.id.items_recycler_view);
        itemsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
    }

    private void initNavgation(View view) {
        navController = Navigation.findNavController(view);
    }

    private void getData() {
        Query allItems = itemsDatabaseReference;

        FirebaseRecyclerOptions<Item> itemOptions =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(allItems, Item.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<Item, ItemViewHolder>(itemOptions) {
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
        itemsRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}