package com.irfanvarren.myapplication.Adapter;

import android.app.Application;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;





import com.google.gson.Gson;

import com.irfanvarren.myapplication.Database.DistributorRepository;
import com.irfanvarren.myapplication.Model.Distributor;
import com.irfanvarren.myapplication.Model.Debt;
import com.irfanvarren.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;  
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DebtListAdapter extends ListAdapter<Debt,DebtListAdapter.ViewHolder> {
    private Context mContext;
    private OnDebtListener mDebtListener;
    private Application mApplication;


    public DebtListAdapter(DiffUtil.ItemCallback<Debt> diffCallback, OnDebtListener debtListener, Application application) {
        super(diffCallback);
        this.mDebtListener = debtListener;
        this.mApplication = application;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context mContext;
        private View rootView;
        private Debt mDebt;
        private TextView txtName,txtDueDate,txtStatus,txtAmount;
        private OnDebtListener mDebtListener;
        private Application mApplication;
        public ViewHolder(View view,OnDebtListener debtListener, Application application){
            super(view);
            mDebtListener = debtListener;
            mContext = view.getContext();
            mApplication = application;
            txtName = view.findViewById(R.id.name);
            txtDueDate = view.findViewById(R.id.dueDate);
            txtStatus = view.findViewById(R.id.status);
            txtAmount = view.findViewById(R.id.amount);
            rootView = view;
        }
        public void bind(Debt debt){
            mDebt = debt;
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
            Integer distributorId = debt.getDistributorId();
            if(distributorId == null){
                distributorId = 0;
            }
            DistributorRepository dRepository = new DistributorRepository(mApplication);
            Distributor distributor = dRepository.findById(distributorId);
            if(distributor != null){
                txtName.setText(distributor.getName());
            }

            Double debtAmount = debt.getAmount();
            if(debtAmount == null){
                debtAmount = 0.0;
            }

            Double debtAmountPaid = debt.getAmountPaid();
            if(debtAmountPaid == null){
                debtAmountPaid = 0.0;
            }
      
            if(debt.getStatus()){
                txtStatus.setText("Lunas");
                txtAmount.setTextColor(mContext.getResources().getColor(R.color.primary));
                txtAmount.setText("Rp. "+nf.format(debtAmountPaid));
            }else{
                txtStatus.setText("Belum Lunas");
                txtAmount.setTextColor(mContext.getResources().getColor(R.color.red));
                txtAmount.setText("Rp. "+nf.format(debtAmount - debtAmountPaid));
            }  

           
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
            txtDueDate.setText("JT: "+dateFormat.format(debt.getDueDate()));

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   
                    mDebtListener.DebtClick(getAdapterPosition(),mDebt);
                   
                }
            });

            if (getItemViewType() == 1) {
           
                            float dip = 90f;
                            Resources r = rootView.getResources();
                            int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
                
                            ViewGroup.MarginLayoutParams rootLayoutParams = (ViewGroup.MarginLayoutParams) rootView.getLayoutParams();
                            rootLayoutParams.setMargins(0, 0, 0, px);
                            rootView.requestLayout();
                        }
        }

        @Override
        public void onClick(View view) {
        mDebtListener.DebtClick(getAdapterPosition(),mDebt);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.debts_list,parent,false);
        return new ViewHolder(view,mDebtListener, mApplication);
    }

    @Override
    public void onBindViewHolder(DebtListAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position){
        if(position == (getItemCount()-1)){
            return 1; // check if last item
        }
        return 0;
    }

    public static class DebtDiff extends DiffUtil.ItemCallback<Debt> {

        @Override
        public boolean areItemsTheSame(@NonNull Debt oldItem, @NonNull Debt newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Debt oldItem, @NonNull Debt newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }


    public interface OnDebtListener{
        public void DebtClick(Integer position,Debt debt);
    }

}
