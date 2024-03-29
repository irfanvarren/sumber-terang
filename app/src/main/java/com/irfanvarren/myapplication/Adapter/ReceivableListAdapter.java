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

import com.irfanvarren.myapplication.Database.CustomerRepository;
import com.irfanvarren.myapplication.Model.Customer;
import com.irfanvarren.myapplication.Model.Receivable;
import com.irfanvarren.myapplication.R;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;  
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReceivableListAdapter extends ListAdapter<Receivable,ReceivableListAdapter.ViewHolder> {
    private Context mContext;
    private OnReceivableListener mReceivableListener;
    private Application mApplication;


    public ReceivableListAdapter(DiffUtil.ItemCallback<Receivable> diffCallback, OnReceivableListener receivableListener, Application application) {
        super(diffCallback);
        this.mReceivableListener = receivableListener;
        this.mApplication = application;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private Context mContext;
        private View rootView;
        private Receivable mReceivable;
        private TextView txtName,txtDueDate,txtStatus,txtAmount;
        private OnReceivableListener mReceivableListener;
        private Application mApplication;
        public ViewHolder(View view,OnReceivableListener receivableListener, Application application){
            super(view);
            mReceivableListener = receivableListener;
            mContext = view.getContext();
            mApplication = application;
            txtName = view.findViewById(R.id.name);
            txtDueDate = view.findViewById(R.id.dueDate);
            txtStatus = view.findViewById(R.id.status);
            txtAmount = view.findViewById(R.id.amount);
            rootView = view;
        }
        public void bind(Receivable receivable){
            mReceivable = receivable;
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));
            Integer customerId = receivable.getCustomerId();
            if(customerId == null){
                customerId = 0;
            }
            CustomerRepository dRepository = new CustomerRepository(mApplication);
            Customer customer = dRepository.findById(customerId);
            if(customer != null){
                txtName.setText(customer.getName());
            }

            Double receivableAmount = receivable.getAmount();
            if(receivableAmount == null){
                receivableAmount = 0.0;
            }

            Double receivableAmountPaid = receivable.getAmountPaid();
            if(receivableAmountPaid == null){
                receivableAmountPaid = 0.0;
            }
      
            if(receivable.getStatus()){
                txtStatus.setText("Lunas");
                txtAmount.setTextColor(mContext.getResources().getColor(R.color.primary));
                txtAmount.setText("Rp. "+nf.format(receivableAmountPaid));
            }else{
                txtStatus.setText("Belum Lunas");
                txtAmount.setTextColor(mContext.getResources().getColor(R.color.red));
                txtAmount.setText("Rp. "+nf.format(receivableAmount - receivableAmountPaid));
            }  

           
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");  
            txtDueDate.setText("JT: "+dateFormat.format(receivable.getDueDate()));

            rootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   
                    mReceivableListener.ReceivableClick(getAdapterPosition(),mReceivable);
                   
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
        mReceivableListener.ReceivableClick(getAdapterPosition(),mReceivable);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.receivables_list,parent,false);
        return new ViewHolder(view,mReceivableListener, mApplication);
    }

    @Override
    public void onBindViewHolder(ReceivableListAdapter.ViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    @Override
    public int getItemViewType(int position){
        if(position == (getItemCount()-1)){
            return 1; // check if last item
        }
        return 0;
    }

    public static class ReceivableDiff extends DiffUtil.ItemCallback<Receivable> {

        @Override
        public boolean areItemsTheSame(@NonNull Receivable oldItem, @NonNull Receivable newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Receivable oldItem, @NonNull Receivable newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }


    public interface OnReceivableListener{
        public void ReceivableClick(Integer position,Receivable receivable);
    }

}
