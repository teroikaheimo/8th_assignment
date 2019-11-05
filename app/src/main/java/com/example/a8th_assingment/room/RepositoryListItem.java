package com.example.a8th_assingment.room;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

public class RepositoryListItem {
    private DaoListItem daoListItem;

    public RepositoryListItem(Application app) {
        Database db = Database.getDatabase(app);
        daoListItem = db.daoListItem();
    }

    List<EntityListItem> getAllListItemsList() {
        return daoListItem.getAllList();
    }

    List<EntityListItem> getLastItem() {
        return daoListItem.getLastItem();
    }

    public void insert(EntityListItem entityListItem) {
        new insertAsyncTask(daoListItem).execute(entityListItem);
    }

    private static class insertAsyncTask extends AsyncTask<EntityListItem, Void, Void> {

        private DaoListItem mAsyncTaskDao;

        insertAsyncTask(DaoListItem dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final EntityListItem... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

}
