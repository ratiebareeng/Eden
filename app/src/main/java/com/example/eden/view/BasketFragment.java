package com.example.eden.view;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eden.adapter.BasketItemsAdapter;
import com.example.eden.ConnectionStateMonitor;
import com.example.eden.model.OrderRequest;
import com.example.eden.viewmodel.OrderViewModel;
import com.example.eden.R;
import com.example.eden.model.OrderEntity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BasketFragment extends Fragment {
    private static final String TAG = "BasketFragment";
    public static final String ORDER_REQUESTS_ROOT = "order_requests";
    private OrderViewModel orderViewModel;
    private BasketItemsAdapter basketItemsAdapter;

    private CoordinatorLayout basketCoordinatorLayout;
    private LinearLayout emptyBasketLinearLayout;
    private LinearLayout liveBasketLinearLayout;
    private RecyclerView basketItemsRecyclerView;
    private TextView basketItemsTotal;
    private Button placeOrderButton;
    private MaterialCardView totalCardView;
    private ImageView emptyBasketImageView;
    private TextView emptyBasketTextView;


    private double basketTotal;
    private List<OrderEntity> basketItemsList;
    private NumberFormat pulaNumberFormat;
    private OrderRequest orderRequest;
    private long currentTimestamp;

    private DatabaseReference orderRequestsDbRef;
    private DateFormat simpleDateFormat;

    private boolean isConnected;
    private ConnectionStateMonitor connectionStateMonitor;
    private int primaryColorResource;
    private int secondaryColorResource;
    private int edenRedColorResource;

    public BasketFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initResources();
        initDateCurrencyFormat();
        initFirebase();
    }

    private void initResources() {
        primaryColorResource = getResources().getColor(R.color.primaryColor);
        secondaryColorResource = getResources().getColor(R.color.secondaryColor);
        edenRedColorResource = getResources().getColor(R.color.edenRed);
    }

    private void initDateCurrencyFormat() {
        Locale botswana = new Locale("en", "BW");
        pulaNumberFormat = NumberFormat.getCurrencyInstance(botswana);
        simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", botswana);
    }

    private void initFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        orderRequestsDbRef = firebaseDatabase.getReference().child(ORDER_REQUESTS_ROOT);
    }

    private void isConnectedToNet() {
        connectionStateMonitor = new ConnectionStateMonitor(requireContext());
        connectionStateMonitor.observe(getViewLifecycleOwner(), isConnected -> {
            this.isConnected = isConnected;
        });
    }

    private void setupViewModel() {
        orderViewModel = new ViewModelProvider(requireActivity()).get(OrderViewModel.class);
        orderViewModel.getAllOrderItems().observe(getViewLifecycleOwner(), orderEntities -> {
            basketItemsList = orderEntities;
            basketItemsAdapter.setOrderEntities(orderEntities);
        });

        orderViewModel.getItemTotals().observe(getViewLifecycleOwner(), this::updateTotal);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setupViewModel();
        return inflater.inflate(R.layout.fragment_basket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLayoutViews(view);
        isConnectedToNet();
        initOnclickListeners();
        setupRecyclerViewAdapter();
        swipeToDeleteItemFromBasket();
    }

    private void initLayoutViews(View view) {
        basketCoordinatorLayout = view.findViewById(R.id.business_basket_coordinator_layout);
        basketItemsTotal = view.findViewById(R.id.user_orders_total);
        basketItemsRecyclerView = view.findViewById(R.id.user_orders_recycler_view);
        placeOrderButton = view.findViewById(R.id.basket_checkout_place_order);
        totalCardView = view.findViewById(R.id.order_total_card_view);
        emptyBasketLinearLayout = view.findViewById(R.id.empty_basket_root);
        liveBasketLinearLayout = view.findViewById(R.id.live_basket_root);
    }

    private void initOnclickListeners() {
        placeOrderButton.setOnClickListener(v -> {
            if (!isConnected) {
                Log.d(TAG, "initOnclickListeners: " + isConnected);
                final Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Oops... You need an internet connection to place orders.", Snackbar.LENGTH_INDEFINITE);
                snackbar.setBackgroundTint(primaryColorResource).setActionTextColor(secondaryColorResource);
                snackbar.setAction("OK", confirmView -> snackbar.dismiss());
                snackbar.show();
            } else {
                submitOrderRequest();
                Log.d(TAG, "order submitted" );
            }
            /*connectionStateMonitor.observe(getViewLifecycleOwner(), isConnected -> {
                if (!isConnected) {
                    Log.d(TAG, "initOnclickListeners: " + isConnected);
                    final Snackbar snackbar = Snackbar.make(requireActivity().findViewById(android.R.id.content), "Oops... You need an internet connection to place orders.", Snackbar.LENGTH_INDEFINITE);
                    snackbar.setBackgroundTint(primaryColorResource).setActionTextColor(secondaryColorResource);
                    snackbar.setAction("OK", confirmView -> snackbar.dismiss());
                    snackbar.show();
                } else {
                    //submitOrderRequest();
                    Log.d(TAG, "order submitted" );
                }
            });*/
        });
    }

    private void submitOrderRequest() {
        initOrderRequest();
        orderRequestsDbRef.child(String.valueOf(orderRequest.getOrderRequestId())).setValue(orderRequest)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getActivity(), "Thank you Naomi. Your order has been placed!", Toast.LENGTH_LONG).show();
                    orderViewModel.deleteAllOrders();
                })
                .addOnFailureListener(e -> Toast.makeText(getActivity(), "Error placing order: " + e.getMessage(), Toast.LENGTH_LONG).show());
    }

    private void initOrderRequest() {
        // TODO: GET CURRENT APP USER FOR SUBMITTING ORDER
        // hardcode user details
        String customerName = "Naomi Molemi";
        String customerEmail = getResources().getString(R.string.user_email_placeholder_text);
        String customerContact = "+267 7600 3532";
        String customerAddress = "Plot 11481 Lefoko Road, Gaborone";
        currentTimestamp = System.currentTimeMillis();
        String currentDate = simpleDateFormat.format(currentTimestamp);

        orderRequest = new OrderRequest(currentTimestamp, customerName, customerEmail, customerContact, customerAddress,
                currentDate, basketItemsList, basketTotal);
    }

    private void setupRecyclerViewAdapter() {
        basketItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        basketItemsAdapter = new BasketItemsAdapter();

        basketItemsRecyclerView.setAdapter(basketItemsAdapter);
        basketItemsAdapter.setOnItemClickListener(orderEntity ->
                Toast.makeText(getActivity(), orderEntity.getItemName() + " clicked", Toast.LENGTH_SHORT).show());
    }

    private void swipeToDeleteItemFromBasket() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                OrderEntity orderEntityToDelete = basketItemsAdapter.getOrderItemAt(position);
                orderViewModel.deleteOrder(orderEntityToDelete);
                Snackbar.make(requireActivity().findViewById(android.R.id.content), orderEntityToDelete.getItemName() + " Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> {
                            orderViewModel.insertOrder(orderEntityToDelete);
                            basketItemsAdapter.notifyDataSetChanged();
                            basketItemsAdapter.notifyItemInserted(position);
                        }).setActionTextColor(secondaryColorResource)
                        .setBackgroundTint(primaryColorResource).show();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                Drawable deleteIcon = requireContext().getDrawable(R.drawable.ic_baseline_delete_sweep_24);
                assert deleteIcon != null;
                deleteIcon.setTint(getResources().getColor(R.color.primaryTextColor));
                ColorDrawable backgroundColor  = new ColorDrawable(edenRedColorResource);

                int iconMargin = (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconTopMargin = itemView.getTop() + (itemView.getHeight() - deleteIcon.getIntrinsicHeight()) / 2;
                int iconBottomMargin = iconTopMargin + deleteIcon.getIntrinsicHeight();

                if (dX > 0) { // Swiping right
                    int iconLeft = itemView.getLeft() + iconMargin;
                    int iconRight = iconLeft + deleteIcon.getIntrinsicWidth();
                    deleteIcon.setBounds(iconLeft, iconTopMargin, iconRight, iconBottomMargin);

                    backgroundColor.setBounds(itemView.getLeft(), itemView.getTop(),
                            itemView.getLeft() + (int) dX, itemView.getBottom());
                } else { // view is unswiped
                    backgroundColor.setBounds(0,0,0,0);
                }

                backgroundColor.draw(c);
                deleteIcon.draw(c);
            }
        }).attachToRecyclerView(basketItemsRecyclerView);
    }

    private void updateTotal(List<Double> doubles) {
        if (doubles.size() == 0) {
            emptyBasketLinearLayout.setVisibility(View.VISIBLE);
            liveBasketLinearLayout.setVisibility(View.GONE);
        } else {
            liveBasketLinearLayout.setVisibility(View.VISIBLE);
            emptyBasketLinearLayout.setVisibility(View.GONE);
        }
        basketTotal = 0;
        for (Double itemTotal : doubles)
            basketTotal += itemTotal;
        basketItemsTotal.setText(pulaNumberFormat.format(basketTotal));
        Log.d(TAG, "list size: " + doubles.size());
    }

    public void onBasketDecreaseQuantity(View view) {
        basketItemsAdapter.decreaseItemQuantity(1);
    }
}