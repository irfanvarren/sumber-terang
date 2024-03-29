package com.irfanvarren.myapplication.ViewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.irfanvarren.myapplication.Database.DebtRepository;
import com.irfanvarren.myapplication.Model.Debt;

import java.util.List;

public class DebtViewModel extends AndroidViewModel {
    private DebtRepository mRepository;
    //private final LiveData<List<Debt>> mAllDebtSync;

    public DebtViewModel (Application application) {
        super(application);
        mRepository = new DebtRepository(application);

    }
    

    public LiveData<List<Debt>> getAll(List<Integer> status){
        return mRepository.getAll(status);
    }

    public LiveData<Double> getTotalDebt(){
        return mRepository.getTotalDebt();
    }

    public LiveData<Integer> getTotalTransaction(){
        return mRepository.getTotalTransaction();
    }


}
