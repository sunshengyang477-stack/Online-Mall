package com.example.onlinemall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(Product product, int quantity);
        void onItemRemoved(Product product);
    }

    public CartAdapter(List<CartItem> cartItems, CartItemListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = cartItems.get(position);
        holder.bind(cartItem, listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        cartItems.clear();
        cartItems.addAll(newCartItems);
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProduct;
        private TextView textViewProductName;
        private TextView textViewProductPrice;
        private TextView textViewQuantity;
        private TextView textViewItemTotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewCartProduct);
            textViewProductName = itemView.findViewById(R.id.textViewCartProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewCartProductPrice);
            textViewQuantity = itemView.findViewById(R.id.textViewCartQuantity);
            textViewItemTotal = itemView.findViewById(R.id.textViewCartItemTotal);
        }

        public void bind(CartItem cartItem, CartItemListener listener) {
            Product product = cartItem.getProduct();
            int quantity = cartItem.getQuantity();

            // 设置文本信息
            textViewProductName.setText(product.getName());
            textViewProductPrice.setText(String.format("¥%.2f", product.getPrice()));
            textViewQuantity.setText(String.valueOf(quantity));
            textViewItemTotal.setText(String.format("¥%.2f", cartItem.getTotalPrice()));

            // 动态加载图片（关键修改点）
            String imageName = product.getImageUrl(); // 获取图片名称（如 "smartphone"）
            int resId = itemView.getContext().getResources()
                    .getIdentifier(imageName, "drawable", itemView.getContext().getPackageName());

            if (resId != 0) {
                // 如果是有效的资源ID，加载本地图片
                Glide.with(itemView.getContext())
                        .load(resId)
                        .placeholder(R.drawable.placeholder_product)
                        .into(imageViewProduct);
            } else {
                // 如果不是资源ID，尝试作为URL加载（兼容网络图片）
                Glide.with(itemView.getContext())
                        .load(imageName)
                        .placeholder(R.drawable.placeholder_product)
                        .into(imageViewProduct);
            }

            // 绑定按钮事件
            itemView.findViewById(R.id.buttonIncrease).setOnClickListener(v -> {
                listener.onQuantityChanged(product, quantity + 1);
            });

            itemView.findViewById(R.id.buttonDecrease).setOnClickListener(v -> {
                if (quantity <= 1) {
                    listener.onItemRemoved(product);
                } else {
                    listener.onQuantityChanged(product, quantity - 1);
                }
            });

            itemView.findViewById(R.id.buttonRemove).setOnClickListener(v -> {
                listener.onItemRemoved(product);
            });
        }
    }
}