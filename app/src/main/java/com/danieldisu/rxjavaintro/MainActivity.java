package com.danieldisu.rxjavaintro;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.common.collect.Lists;
import com.jakewharton.rxbinding.widget.RxAdapterView;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "RX_JAVA_INTRO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListFragment listFragment = (ListFragment) getSupportFragmentManager().findFragmentById(R.id.listFragment);

        RestClient.HNService hnService = RestClient.getHNService();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        listFragment.setListAdapter(adapter);

        hnService.getNewStories()
                .subscribeOn(Schedulers.io())
                .map(newStories -> getItemObservables(hnService, newStories))
                .flatMap(Observable::merge)
                .doOnNext(hnItem1 -> Log.d(TAG, "item received " + hnItem1.id))
                .take(10)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((hnItem) -> addItemToList(hnItem, adapter),
                        Throwable::printStackTrace);

        ListView listView = listFragment.getListView();
        RxAdapterView.itemClicks(listView)
                .subscribe(position -> Log.d(TAG, "Item at position " + position + " pressed"));

    }

    private void addItemToList(RestClient.HNItem hnItem, ArrayAdapter<String> adapter) {
        adapter.add(hnItem.title);
    }

    private List<Observable<RestClient.HNItem>> getItemObservables(RestClient.HNService hnService,
                                                                   RestClient.NewStories newStories) {
        return Lists.transform(newStories, hnService::getItem);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
