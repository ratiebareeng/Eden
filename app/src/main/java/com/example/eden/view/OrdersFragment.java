package com.example.eden.view;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eden.model.OrderRequest;
import com.example.eden.adapter.OrderRequestViewHolder;
import com.example.eden.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static com.example.eden.view.BasketFragment.ORDER_REQUESTS_ROOT;


public class OrdersFragment extends Fragment {
    private static final String TAG = "OrdersFragment";
    private RecyclerView ordersRecyclerView;
    private LinearLayout linearLayout;
    private FirebaseRecyclerAdapter<OrderRequest, OrderRequestViewHolder> customerOrdersAdapter;
    private Query getAllCustomerOrders;
    private ProgressBar progressBar;

    private Locale botswana = new Locale("en", "BW");
    private NumberFormat numberFormat = NumberFormat.getCurrencyInstance(botswana);
    private DateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy", botswana);

    private DatabaseReference orderRequestsDbRef;


    public OrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFirebase();
    }

    private void initFirebase() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        orderRequestsDbRef = firebaseDatabase.getReference().child(ORDER_REQUESTS_ROOT);
        String customerEmail = getResources().getString(R.string.user_email_placeholder_text);
        getAllCustomerOrders = orderRequestsDbRef.orderByChild("customerEmail").equalTo(customerEmail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);
        isOrdersEmpty();
        initRecyclerView(view);
        initAdapter();
        swipeToDeleteOrder();
        return view;
    }

    private void isOrdersEmpty() {
        getAllCustomerOrders.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ordersRecyclerView.setVisibility(View.VISIBLE);
                    linearLayout.setVisibility(View.GONE);
                } else {
                    ordersRecyclerView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "onCancelled: ", databaseError.toException());
                Log.d(TAG, "onCancelled: " + databaseError.getMessage());
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initRecyclerView(View view) {
        ordersRecyclerView = view.findViewById(R.id.customer_orders_recycler);
        ordersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        linearLayout = view.findViewById(R.id.no_orders_root);
    }

    private void initAdapter() {
        Log.d(TAG, "initAdapter: starting");
        FirebaseRecyclerOptions<OrderRequest> options = new FirebaseRecyclerOptions.Builder<OrderRequest>()
                .setQuery(getAllCustomerOrders, OrderRequest.class)
                .build();

        customerOrdersAdapter = new FirebaseRecyclerAdapter<OrderRequest, OrderRequestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderRequestViewHolder holder, int position, @NonNull OrderRequest model) {
                holder.orderId.setText(String.valueOf(model.getOrderRequestId()));
                holder.orderDate.setText(model.getOrderRequestDate());
                holder.orderStatus.setText(convertCodeToStatus(model.getOrderRequestStatus()));
                holder.orderTotal.setText(numberFormat.format(model.getOrderRequestTotal()));
                Log.d(TAG, "check data: " + model.getCustomerAddress());
            }

            @NonNull
            @Override
            public OrderRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_order_request, parent, false);
                return new OrderRequestViewHolder(view);
            }

            //public int get
        };
        ordersRecyclerView.setAdapter(customerOrdersAdapter);
        Log.d(TAG, "initAdapter: done");
    }

    private String convertCodeToStatus(String code) {
        switch (code) {
            case "0":
                return "Order Placed";
            case "1":
                return "Order Confirmed";
            case "2":
                return "Order Dispatched for Delivery";
            case "3":
                return "Order Completed";
            default:
                return "Order Placed";
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        customerOrdersAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        customerOrdersAdapter.stopListening();
    }

    private void swipeToDeleteOrder() {
        boolean deleteOrderRequest = true;
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                OrderRequest orderRequest = customerOrdersAdapter.getItem(position);
                progressBar.setVisibility(View.VISIBLE);
                orderRequestsDbRef.child(String.valueOf(orderRequest.getOrderRequestId())).removeValue()
                        .addOnFailureListener(e -> {
                            progressBar.setVisibility(View.GONE);
                            Log.e(TAG, "Error deleting order " + orderRequest.getOrderRequestId(), e);
                            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                    "Error Deleting Order " + orderRequest.getOrderRequestId(), Snackbar.LENGTH_LONG).show();
                        }).addOnSuccessListener(aVoid -> {
                            progressBar.setVisibility(View.GONE);
                            Log.d(TAG, "Order Deleted");
                            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                            "Order " + orderRequest.getOrderRequestId() + "Deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", v -> {
                                progressBar.setVisibility(View.VISIBLE);
                                orderRequestsDbRef.child(String.valueOf(orderRequest.getOrderRequestId())).setValue(orderRequest)
                                        .addOnFailureListener(e -> {
                                            progressBar.setVisibility(View.GONE);
                                            Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                    "Error Restoring Order " + orderRequest.getOrderRequestId(), Snackbar.LENGTH_LONG).show();
                                        })
                                        .addOnSuccessListener(aVoid1 -> {
                                            progressBar.setVisibility(View.GONE);
                                        });
                            }).show();
                });

                /*Snackbar.make(requireActivity().findViewById(android.R.id.content), orderEntityToDelete.getItemName() + " Deleted", Snackbar.LENGTH_LONG)
                        .setAction("UNDO", v -> {
                            orderViewModel.insertOrder(orderEntityToDelete);
                            basketItemsAdapter.notifyDataSetChanged();
                            basketItemsAdapter.notifyItemInserted(position);
                        }).setActionTextColor(getResources().getColor(R.color.secondaryColor))
                        .setBackgroundTint(getResources().getColor(R.color.primaryColor)).show();*/

               /* Snackbar.make(requireActivity().findViewById(android.R.id.content), "Delete Order " + orderRequest.getOrderRequestId(), Snackbar.LENGTH_LONG)
                        .setAction("Confirm", v -> {
                            orderRequestsDbRef.child(String.valueOf(orderRequest.getOrderRequestId())).removeValue()
                                    .addOnFailureListener(e -> {
                                        Log.e(TAG, "Error deleting " + orderRequest.getOrderRequestId(), e);
                                        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                                "Error Deleting " + orderRequest.getOrderRequestId(), Snackbar.LENGTH_LONG).show();
                                    }).addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Order Deleted");
                                Snackbar.make(requireActivity().findViewById(android.R.id.content),
                                        "Order " + orderRequest.getOrderRequestId() + "Deleted", Snackbar.LENGTH_LONG).show();
                            });

                        }).setActionTextColor(getResources().getColor(R.color.secondaryColor))
                        .setBackgroundTint(getResources().getColor(R.color.primaryColor)).show();*/

            }


            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                View itemView = viewHolder.itemView;

                Drawable deleteIcon = requireContext().getDrawable(R.drawable.ic_baseline_delete_sweep_24);
                assert deleteIcon != null;
                deleteIcon.setTint(getResources().getColor(R.color.primaryTextColor));
                ColorDrawable backgroundColor = new ColorDrawable(getResources().getColor(R.color.edenRed));

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
                    backgroundColor.setBounds(0, 0, 0, 0);
                }

                backgroundColor.draw(c);
                deleteIcon.draw(c);
            }
        }).attachToRecyclerView(ordersRecyclerView);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.orders_fragment_progress_indicator);
    }
}