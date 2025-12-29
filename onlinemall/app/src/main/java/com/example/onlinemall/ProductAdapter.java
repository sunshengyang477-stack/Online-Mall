package com.example.onlinemall;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onProductClick(Product product);
        void onAddToCartClick(Product product);
    }

    public ProductAdapter(List<Product> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public void updateProducts(List<Product> newProducts) {
        productList.clear();
        productList.addAll(newProducts);
        notifyDataSetChanged();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageViewProduct;
        private TextView textViewProductName;
        private TextView textViewProductPrice;
        private TextView textViewProductDescription;
        private Button buttonAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewProduct);
            textViewProductName = itemView.findViewById(R.id.textViewProductName);
            textViewProductPrice = itemView.findViewById(R.id.textViewProductPrice);
            textViewProductDescription = itemView.findViewById(R.id.textViewProductDescription);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
        }

        public void bind(final Product product, final OnProductClickListener listener) {
            textViewProductName.setText(product.getName());
            textViewProductPrice.setText(String.format("¥%.2f", product.getPrice()));
            textViewProductDescription.setText(product.getDescription());

            String imageName = product.getImageUrl();
            int resId = itemView.getContext().getResources()
                    .getIdentifier(imageName, "drawable", itemView.getContext().getPackageName());

            // 使用Glide加载图片
            Glide.with(itemView.getContext())
                    .load(resId)
                    .placeholder(R.drawable.placeholder_product)
                    .into(imageViewProduct);

            // 点击商品项
            itemView.setOnClickListener(v -> listener.onProductClick(product));

            // 在ProductAdapter中修改加入购物车逻辑
            buttonAddToCart.setOnClickListener(v -> {
                // 通过监听器回调到Activity
                listener.onAddToCartClick(product);

                // 也可以直接在这里处理（如果不需要Activity知道）
                // CartManager.getInstance(itemView.getContext()).addToCart(product);
                // Toast.makeText(itemView.getContext(), "已添加 " + product.getName() + " 到购物车", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
