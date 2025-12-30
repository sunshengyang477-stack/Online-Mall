package com.example.onlinemall;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view, dateFormat); // 将 dateFormat 传递给 ViewHolder
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewOrderId;
        private TextView textViewOrderDate;
        private TextView textViewOrderAmount;
        private TextView textViewOrderPoints;
        private SimpleDateFormat dateFormat; // 在 ViewHolder 中保存 dateFormat 实例

        public OrderViewHolder(@NonNull View itemView, SimpleDateFormat dateFormat) {
            super(itemView);
            this.dateFormat = dateFormat; // 初始化 dateFormat
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDate);
            textViewOrderAmount = itemView.findViewById(R.id.textViewOrderAmount);
            textViewOrderPoints = itemView.findViewById(R.id.textViewOrderPoints);
        }

        public void bind(Order order) {
            textViewOrderId.setText(String.format("订单号: %s", order.getOrderId().substring(0, 8)));
            textViewOrderDate.setText(dateFormat.format(order.getOrderDate())); // 使用传递进来的 dateFormat
            textViewOrderAmount.setText(String.format("金额: ¥%.2f", order.getTotalAmount()));
            textViewOrderPoints.setText(String.format("获得积分: %d", order.getEarnedPoints()));
        }
    }
}
