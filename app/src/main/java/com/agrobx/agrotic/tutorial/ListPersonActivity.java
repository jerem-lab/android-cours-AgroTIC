package com.agrobx.agrotic.tutorial;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;

public class ListPersonActivity extends AppCompatActivity {
    ArrayList<PersonDataModel> personDataModels;

    // initialisation de notre BDD

//    public void loadData(Context context) {
//        PersonDataSourceManager personDataSourceManager = new PersonDataSourceManager(context);
//        personDataSourceManager.open();
//        if (personDataSourceManager.getCount() == 0) {
//            personDataModels = new ArrayList<PersonDataModel>();
//            for (int i = 0; i < 100; i++) {
//
//                personDataSourceManager.add(new PersonDataModel("firstName_" + i, "lastName_" + i, null));
//            }
//            personDataModels = personDataSourceManager.getAll();
//        } else {
//            personDataModels = personDataSourceManager.getAll();
//        }
//        personDataSourceManager.close();
//        DataBaseHandler.closeInstance();
//    }

    public void loadData(Context context) {
        personDataModels = new ArrayList<PersonDataModel>();

        ContentObserver personContentResolverObserver = null;
        getContentResolver().registerContentObserver(PersonContentProvider.Contract.CONTENT_URI,
                true, personContentResolverObserver = new ContentObserver(null) {
                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        Log.i("mytag", "onChange: " + uri.toString());
                    }
                });

        getContentResolver().delete(PersonContentProvider.Contract.CONTENT_URI,
                null, null);

        for (int i = 0; i < 100; i++) {
            ContentValues values = new ContentValues();
            values.put(PersonContentProvider.Contract.COLUMN_FIRST_NAME, "bla_" + i);
            values.put(PersonContentProvider.Contract.COLUMN_LAST_NAME, "blabla_" + i);
            Uri personURI = getContentResolver().insert(PersonContentProvider.Contract.CONTENT_URI, values);
        }


        // on get all via le content provider
        String[] projection = new String[]{PersonContentProvider.Contract.COLUMN_FIRST_NAME,
                PersonContentProvider.Contract.COLUMN_LAST_NAME};
        Cursor cursor = getContentResolver().query(PersonContentProvider.Contract.CONTENT_URI,
                projection, null, null, null);
        while (cursor.moveToNext()) {
            PersonDataModel personDataModel = new PersonDataModel
                    (

                            cursor.getString(cursor.getColumnIndexOrThrow(PersonContentProvider.Contract.COLUMN_FIRST_NAME)),

                            cursor.getString(cursor.getColumnIndexOrThrow(PersonContentProvider.Contract.COLUMN_LAST_NAME)),
                            null
                    );
            personDataModels.add(personDataModel);
        }
        getContentResolver().unregisterContentObserver(personContentResolverObserver);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_person);
//1
        ListView mListView = (ListView) findViewById(R.id.listPersonView);


        if (savedInstanceState == null) {
            loadData(this);
//            personDataModels = new ArrayList<PersonDataModel>();
//
//            for (int i = 0; i < 5; i++) {
//                personDataModels.add(new PersonDataModel("firstName_" + i, "lastName_" + i, null));
//            }

        } else {

            personDataModels = (ArrayList<PersonDataModel>) savedInstanceState.getSerializable("PersonDataModels");
        }


        //3
//        ArrayAdapter<PersonDataModel> adapter = new ArrayAdapter<PersonDataModel>(ListPersonActivity.this, android.R.layout.simple_list_item_1, personDataModels);
        ArrayAdapter<PersonDataModel> adapter = new PersonAdapter(this, personDataModels);
//4
        mListView.setAdapter(adapter);

        //5
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("MyTag", "Item Click:" + position);
            }
        });

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("myTag", "Item Long Click:" + position);
                view.setSelected(true);
                return true;
            }
        });

        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        setChoiceListener(mListView, personDataModels);

        mListView.setEmptyView(findViewById(R.id.emptyListView));

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListPersonActivity.this, ViewerPersonActivity.class);
                intent.putExtra("PersonDataModel", (Serializable) personDataModels.get(position));

                startActivity(intent);
            }
        });


    }

    private void setChoiceListener(final ListView listView, final ArrayList<PersonDataModel>
            personDataModels) {
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.menu_context_text, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_remove: {
                        SparseBooleanArray positions = listView.getCheckedItemPositions();

                        int count = positions.size();
                        ArrayList<PersonDataModel> checkedModels = new ArrayList<PersonDataModel>();
                        final ArrayList<PersonDataModel> backupModels = new ArrayList<PersonDataModel>(personDataModels);
                        for (int i = 0; i < count; i++) {

                            if (positions.valueAt(i) == true) {
                                checkedModels.add(personDataModels.get(positions.keyAt(i)));
                                Log.i("myTag", "Index:" + i + " Key:" + positions.keyAt(i) + " Value: " + positions.valueAt(i));
                            }
                        }
                        PersonDataSourceManager personDataSourceManager = new PersonDataSourceManager(ListPersonActivity.this);
                        personDataSourceManager.open();
                        for (PersonDataModel model : checkedModels) {
//                            personDataModels.remove(model);
                            personDataSourceManager.remove(model);

                        }
                        ((ArrayAdapter<PersonDataModel>) listView.getAdapter()).clear();
                        ((ArrayAdapter<PersonDataModel>) listView.getAdapter()).addAll(personDataSourceManager.getAll());
                        personDataSourceManager.close();

                        ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();

                        Snackbar snackbar = Snackbar.make(listView, count + " persons removed", Snackbar.LENGTH_LONG);

//                        snackbar.setAction("NON, NON, NON !!", new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                ((ArrayAdapter<PersonDataModel>) listView.getAdapter()).clear();
//                                ((ArrayAdapter<PersonDataModel>) listView.getAdapter()).addAll(backupModels);
//                            }
//                        });

                        snackbar.show();

                        mode.finish();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
            }
        });
    }

    private class PersonAdapter extends ArrayAdapter<PersonDataModel> {
        public PersonAdapter(Context context, ArrayList<PersonDataModel> personDataModels) {
            super(context, 0, personDataModels);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
//1
            if (rowView == null) {

                LayoutInflater inflater = (LayoutInflater)
                        super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.person_item_layout, parent, false);
            }

// View Holder use
            PersonViewHolder viewHolder = (PersonViewHolder) rowView.getTag();
            if (viewHolder == null) {
                viewHolder = new PersonViewHolder();
                viewHolder.firstNameView = (TextView) rowView.findViewById(R.id.firstNameId);
                viewHolder.lastNameView = (TextView) rowView.findViewById(R.id.lastNameId);
                viewHolder.displayPictureView = (ImageView) rowView.findViewById(R.id.displayPictureId);
                rowView.setTag(viewHolder);
            }
            PersonDataModel itemDataModel = super.getItem(position);
            viewHolder.firstNameView.setText(itemDataModel.getFirstName());
            viewHolder.lastNameView.setText(itemDataModel.getLastName());
            if (position % 2 == 0) {
                viewHolder.displayPictureView.setImageResource(R.drawable.logo_agrobx);
            } else {
                viewHolder.displayPictureView.setImageResource(R.drawable.logo_supagro);
            }

            return rowView;
        }
    }

    class PersonViewHolder {
        public TextView firstNameView;
        public TextView lastNameView;
        public ImageView displayPictureView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("PersonDataModels", personDataModels);
    }
}
