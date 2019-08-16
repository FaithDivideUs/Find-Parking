package com.example.findparking;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AreaAdapter extends  RecyclerView.Adapter<AreaAdapter.AreaAdapterViewHolder> {

    private String[] mData;
    private String attention = "Not available parking";
    /**
     * An on-click handler that we've defined to make it easy for an Activity to interface with
     * our RecyclerView*/
    private final AreaAdapterOnClickHandler mClickHandler;

    // constructor
    public AreaAdapter(String[] mData, AreaAdapterOnClickHandler mClickHandler){
        this.mData = mData;
        this.mClickHandler = mClickHandler;
    }
    /** The interface that receives onClick messages. */
    public interface AreaAdapterOnClickHandler {
        void onClick(String specific);
    }
    /** Creates a AreaAdapter. */
    public AreaAdapter(AreaAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }
    /** Cache of the children views for a new list item. */
    public class AreaAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTextView;
        public final TextView mTextView2;
        public final TextView mTextView3;

        public AreaAdapterViewHolder(View view) {
            super(view);
            mTextView =  view.findViewById(R.id.parking_data1);
            mTextView2 =  view.findViewById(R.id.parking_data2);
            mTextView3 =  view.findViewById(R.id.parking_data3);
            view.setOnClickListener(this);
        }
        /** This gets called by the child views during a click. */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String specific = mData[adapterPosition];
            mClickHandler.onClick(specific);
        }
    }
    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  If your RecyclerView has more than one type of item (which ours doesn't) you
     *                  can use this viewType integer to provide a different layout.
     * @return A new AreaAdapterViewHolder that holds the View for each list item
     */
    @Override
    public AreaAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.search_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;
        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new AreaAdapterViewHolder(view);
    }
    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. In this method, we update the contents of the ViewHolder to display the news
     * details for this particular position, using the "position" argument that is conveniently
     * passed into us.
     * @param areaAdapterViewHolder The ViewHolder which should be updated to represent the
     *                                  contents of the item at the given position in the data set.
     * @param position                  The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull AreaAdapterViewHolder areaAdapterViewHolder, int position) {
        String specific = mData[position];
        if(specific.equals(attention)){ // if specific field is empty.

            areaAdapterViewHolder.mTextView3.setVisibility(View.VISIBLE);
            areaAdapterViewHolder.mTextView2.setVisibility(View.GONE);
            areaAdapterViewHolder.mTextView.setVisibility(View.GONE);

        }else {
            String[] bits = specific.split("~~yHashUi3~~");
            specific = bits[0]; // get the first item and display it. This is for name
            String add_code = bits[1]; // This is for address code
            areaAdapterViewHolder.mTextView.setText(add_code);
            areaAdapterViewHolder.mTextView2.setVisibility(View.VISIBLE); // activate the second view for name
            areaAdapterViewHolder.mTextView2.setText(specific);
        }

    }
    //This method simply returns the number of items to display. It is used behind the scenes
    // to help layout our Views and for animations.
    @Override
    public int getItemCount() {
        if (null == mData) return 0;
        return mData.length;
    }
    /**
     * This method is used to set the news on a MewsAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new NewsAdapter to display it.
     * @param areaData The new area data to be displayed.
     */
    public void setAreaData(String[] areaData) {
        mData = areaData;
        notifyDataSetChanged();
    }
}
