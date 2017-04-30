package me.lensevents.lensevents;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.lensevents.lensevents.CategoryFragment.OnListFragmentInteractionListener;
import me.lensevents.model.Category;

import java.util.List;

public class CategoryRecyclerViewAdapter extends RecyclerView.Adapter<CategoryRecyclerViewAdapter.ViewHolder> {

    private final List<String> mCategoriesAsString;
    private final List<Category> mCategories;
    private final List<Drawable> mImages;
    private final OnListFragmentInteractionListener mListener;

    public CategoryRecyclerViewAdapter(List<String> categoriesAsString, List<Category> categories, List<Drawable> images, OnListFragmentInteractionListener listener) {
        mCategoriesAsString = categoriesAsString;
        mImages = images;
        mCategories = categories;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCategory = mCategories.get(position);
        holder.mImageView.setImageDrawable(mImages.get(position));
        holder.mNameView.setText(mCategoriesAsString.get(position));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mCategory);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCategoriesAsString.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView mImageView;
        public final TextView mNameView;
        public Category mCategory;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.group_image);
            mNameView = (TextView) view.findViewById(R.id.card_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
