package com.irfanvarren.myapplication.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;
import com.irfanvarren.myapplication.Model.Debt;

import java.util.Date;
import java.util.List;


public class DebtRepository {
    private DebtDao mDebtDao;
    private AppDatabase db;
    private LiveData<List<Debt>> mAllDebts;
    
    public DebtRepository(Application application) {
        db = AppDatabase.getDatabase(application);
        mDebtDao = db.debtDao();
    }
    
    public LiveData<List<Debt>> getAll(List<Integer> status) {
        return mDebtDao.getAllWithStatus(status);
    }

    public LiveData<Double> getTotalDebt(){
        return mDebtDao.getTotalDebt();
    }

    public LiveData<Integer> getTotalTransaction(){
        return mDebtDao.getTotalTransaction();
    }
    
    public long insert(Debt debt) {
        if (debt.getCreatedAt() == null) {
            debt.setCreatedAt(new Date());
        }
        debt.setUpdatedAt(new Date());
        long id = db.debtDao().insert(debt);
        return id;
    }
    
    public void update(Debt debt) {
        debt.setUpdatedAt(new Date());
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDebtDao.update(debt);
        });
    }
    
    public void delete(Debt debt) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            mDebtDao.delete(debt);
        });
    }
}
