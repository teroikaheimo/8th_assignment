package com.example.a8th_assingment.room;

import android.annotation.SuppressLint;
import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.a8th_assingment.CustomAdapter;

import java.util.ArrayList;
import java.util.List;

public class ModelListItem extends AndroidViewModel {
    private RepositoryListItem mRepository;

    public ModelListItem(@NonNull Application application) {
        super(application);
        this.mRepository = new RepositoryListItem(application);
    }

    public void insert(EntityListItem entityListItem) {
        mRepository.insert(entityListItem);
    }

    @SuppressLint("StaticFieldLeak")
    public void showData(final CustomAdapter adapter) {
        new AsyncTask<Void, Void, List<EntityListItem>>() {
            @Override
            protected List<EntityListItem> doInBackground(Void... params) {
                //runs on background thread
                return mRepository.getAllListItemsList();
            }

            @Override
            protected void onPostExecute(List<EntityListItem> items) {
                //runs on main thread
                adapter.setListItems((ArrayList<EntityListItem>) items);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    public void showLastAddition(final CustomAdapter adapter) {
        new AsyncTask<Void, Void, List<EntityListItem>>() {
            @Override
            protected List<EntityListItem> doInBackground(Void... params) {
                //runs on background thread
                return mRepository.getLastItem();
            }

            @Override
            protected void onPostExecute(List<EntityListItem> items) {
                //runs on main thread
                adapter.addItem((ArrayList<EntityListItem>) items);
            }
        }.execute();
    }
}
