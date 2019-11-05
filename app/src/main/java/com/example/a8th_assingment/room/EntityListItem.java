package com.example.a8th_assingment.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "list_item_table")
public class EntityListItem {
    @PrimaryKey(autoGenerate = true)
    public int id_list_item;
    public int width;
    public int height;
    public String file;
    public String license;
    public String owner;
    public String tags;
    public String tagMode;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] image;
}
