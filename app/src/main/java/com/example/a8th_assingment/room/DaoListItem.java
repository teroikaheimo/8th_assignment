package com.example.a8th_assingment.room;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DaoListItem {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(EntityListItem listItem);

    @Query("SELECT * FROM list_item_table")
    List<EntityListItem> getAllList();

    @Query("SELECT * FROM list_item_table ORDER BY id_list_item DESC LIMIT 1;")
    List<EntityListItem> getLastItem();
}
