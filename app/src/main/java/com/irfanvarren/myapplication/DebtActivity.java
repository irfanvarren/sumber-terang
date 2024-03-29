package com.irfanvarren.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator; 

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.util.Log;

import com.google.gson.Gson;
import com.irfanvarren.myapplication.Adapter.DebtListAdapter;
import com.irfanvarren.myapplication.Model.Debt;
import com.irfanvarren.myapplication.ViewModel.DebtViewModel;

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.text.NumberFormat;

import android.os.Bundle;

public class DebtActivity extends AppCompatActivity implements DebtListAdapter.OnDebtListener{
    private DebtViewModel mDebtViewModel;
    private LiveData<List<Debt>> mDebtsList;
    private final DebtListAdapter adapter = new DebtListAdapter(new DebtListAdapter.DebtDiff(), this,getApplication());
    private List<Debt> mDebts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debt);
        
        List<Integer> status = new ArrayList<Integer>();
        status.clear();

        NumberFormat nf = NumberFormat.getNumberInstance(new Locale("in", "ID"));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        recyclerView.setItemAnimator(null);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        mDebtViewModel = new ViewModelProvider(this).get(DebtViewModel.class);
        
        TextView txtTotalDebt = (TextView) findViewById(R.id.totalDebt);
        TextView txtTotalTransaction = (TextView) findViewById(R.id.totalTransaction);
        CheckBox ckUnpaid = (CheckBox) findViewById(R.id.ckUnpaid);
        CheckBox ckPaid = (CheckBox) findViewById(R.id.ckPaid);

        if(ckUnpaid.isChecked()){
            status.add(0);
        }

        if(ckPaid.isChecked()){
            status.add(1);
        }

        mDebtsList = mDebtViewModel.getAll(status);

        mDebtsList.observe(this, debts -> {
            mDebts = debts;
            adapter.submitList(debts);
        });
       
        
        ckUnpaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
             if(b){
                 if(!status.contains((Integer) 0)){
                     status.add((Integer) 0);
                 }
                mDebtsList = mDebtViewModel.getAll(status);
                mDebtsList.observe(DebtActivity.this, debts -> {
                    mDebts = debts;
                    adapter.submitList(mDebts);
                    adapter.notifyDataSetChanged();
                });
            }else{
                if(status.contains((Integer) 0)){
                    status.remove((Integer) 0);
                }
                mDebtsList = mDebtViewModel.getAll(status);
                mDebtsList.observe(DebtActivity.this, debts -> {
                    mDebts = debts;
                    adapter.submitList(mDebts);
                    adapter.notifyDataSetChanged();
                });
            }
            }
        });

        ckPaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    if(!status.contains((Integer) 1)){
                        status.add((Integer) 1);
                    }
                   mDebtsList = mDebtViewModel.getAll(status);
                   mDebtsList.observe(DebtActivity.this, debts -> {
                       mDebts = debts;
                       adapter.submitList(mDebts);
                       adapter.notifyDataSetChanged();
                   });
               }else{
                   if(status.contains((Integer) 1)){
                       status.remove((Integer) 1);
                   }
                   mDebtsList = mDebtViewModel.getAll(status);
                   mDebtsList.observe(DebtActivity.this, debts -> {
                       mDebts = debts;
                       adapter.submitList(mDebts);
                       adapter.notifyDataSetChanged();
                   });
               }
            }
        });
        

        mDebtViewModel.getTotalDebt().observe(this, totalDebt -> {
            if(totalDebt == null){
                totalDebt = new Double(0);
            }
            txtTotalDebt.setText("Rp. " + nf.format(totalDebt));
        });
        mDebtViewModel.getTotalTransaction().observe(this, totalTransaction -> {
            if(totalTransaction == null){
                totalTransaction = 0;
            }
            txtTotalTransaction.setText(String.valueOf(totalTransaction));
        });
        
  

        RelativeLayout addBtn = (RelativeLayout) findViewById(R.id.addBtn);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                
                Intent intent = new Intent(DebtActivity.this, DebtInputActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void DebtClick(Integer position,Debt debt){
        Intent intent = new Intent(DebtActivity.this, DebtDetailActivity.class);
        intent.putExtra("debt", debt);
        startActivity(intent);
    }

}