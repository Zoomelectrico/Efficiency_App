package co.avilatek.efficiencyapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.avilatek.efficiencyapp.CycleTimeFragment.OnListFragmentInteractionListener;
import co.avilatek.efficiencyapp.helpers.CycleTimeModel;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CycleTimeModel} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyCycleTimeRecyclerViewAdapter extends RecyclerView.Adapter<MyCycleTimeRecyclerViewAdapter.ViewHolder> {

    private final List<CycleTimeModel> values;
    private final OnListFragmentInteractionListener mListener;

    public MyCycleTimeRecyclerViewAdapter(List<CycleTimeModel> items, OnListFragmentInteractionListener listener) {
        values = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_cycletime, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = values.get(position);
        holder.mIdView.setText(String.valueOf(values.get(position).getCycle()));
        holder.mContentView.setText(values.get(position).getTime());
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @NonNull
        public final View mView;
        public final TextView mIdView;
        public final TextView mContentView;
        public CycleTimeModel mItem;

        public ViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mIdView = view.findViewById(R.id.item_number);
            mContentView = view.findViewById(R.id.content);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
