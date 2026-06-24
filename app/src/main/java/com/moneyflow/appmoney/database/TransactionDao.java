package com.moneyflow.app.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.moneyflow.app.model.Transaction;

import java.util.List;

@Dao
public interface TransactionDao {

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    LiveData<List<Transaction>> getAll();

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    LiveData<List<Transaction>> getByType(String type);

    @Query("SELECT * FROM transactions WHERE id = :id")
    Transaction getById(long id);

    @Query("SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE type = 'income'")
    double getTotalIncome();

    @Query("SELECT COALESCE(ABS(SUM(amount)), 0) FROM transactions WHERE type = 'expense'")
    double getTotalExpense();

    @Query("SELECT SUM(amount) FROM transactions WHERE type='expense' AND category=:category")
    Double getSpentByCategory(String category);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Transaction transaction);

    @Update
    void update(Transaction transaction);

    @Delete
    void delete(Transaction transaction);

    @Query("DELETE FROM transactions WHERE id = :id")
    void deleteById(long id);
}