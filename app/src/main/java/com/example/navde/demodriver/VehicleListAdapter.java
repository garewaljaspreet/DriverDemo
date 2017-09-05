package com.example.navde.demodriver;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;


/**
 * Created by navdeepg on 11/10/2015.
 */
public class VehicleListAdapter extends RecyclerView.Adapter<VehicleListAdapter.SimpleViewHolder> {

    private final Context mContext;
    ISelectAddress mActivity;
    ArrayList<BeansPrediction> responseVehicle;
    public void notifyData(ArrayList<BeansPrediction> responseVehicle){
        this.responseVehicle=responseVehicle;
        notifyDataSetChanged();
    }

    public void clear() {
        int size = this.responseVehicle.size();
        this.responseVehicle.clear();
        notifyItemRangeRemoved(0, size);
    }

    public class SimpleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final CustomTextView txtPrimary,txtSecondry;
        public final RelativeLayout rlMain;
        public SimpleViewHolder(View view) {
            super(view);
            txtPrimary = (CustomTextView) view.findViewById(R.id.txtPrimary);
            txtSecondry = (CustomTextView) view.findViewById(R.id.txtSecondry);
            rlMain = (RelativeLayout) view.findViewById(R.id.rlMain);

            rlMain.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mActivity.itemSelected(getLayoutPosition());
        }
    }

    public VehicleListAdapter(Context context, ISelectAddress activity, ArrayList<BeansPrediction> responseVehicle, boolean IS_INIT_SETTINGS) {
        mContext = context;
        mActivity=activity;
        this.responseVehicle=responseVehicle;
    }

    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.vehicle_list_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {

        holder.txtPrimary.setText(responseVehicle.get(position).getStrFullTxt());

        holder.txtSecondry.setText(responseVehicle.get(position).getStrPrimaryTxt());
    }

    @Override
    public int getItemCount() {
        return responseVehicle.size();
    }


}
