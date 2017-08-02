package com.chikeandroid.tutsplus_rxjava_schedulers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;

import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Chike on 7/30/2017.
 */

public class ObserveOnActivity extends AppCompatActivity {

    private Disposable mDisposable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView textView = (TextView) findViewById(R.id.tv_main);

        Observable<String> observable = Observable.create(dataSource())
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.io())
                .doOnNext(s -> {
                    SaveToCache(s);
                    Log.d("ObserveOnActivity", "doOnNext() on thread " + Thread.currentThread().getName());
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> Log.d("ObserveOnActivity", "Complete"));

        mDisposable = observable.subscribe(s -> {
            Log.d("ObserveOnActivity", "received " + s + " on thread " + Thread.currentThread().getName());
            textView.setText(s);
        });

        final String[] states = {"Lagos", "Abuja", "Imo", "Enugu"};
        Observable<String> statesObservable = Observable.fromArray(states);

        statesObservable.flatMap(
                s -> Observable.create(getPopulation(s))
                .subscribeOn(Schedulers.io())
        ).subscribe(pair -> Log.d("MainActivity", pair.first + " population is " + pair.second));
    }

    private void SaveToCache(String s) {
        Log.d("MainActivity", "thread name is " + Thread.currentThread().getName());
    }

    private ObservableOnSubscribe<Pair> getPopulation(String state) {
        return(emitter -> {
            Random r = new Random();
            Thread.sleep(600);
            Log.d("MainActivity", "getPopulation() for " + state + " called on " + Thread.currentThread().getName());
            emitter.onNext(new Pair(state, r.nextInt(300000 - 10000) + 10000));
            emitter.onComplete();
        });
    }


    private ObservableOnSubscribe<String> dataSource() {
        return(emitter -> {
            Thread.sleep(800);
            emitter.onNext("Value");
            Log.d("ObserveOnActivity", "dataSource() on thread " + Thread.currentThread().getName());
            emitter.onComplete();
        });
    }

    @Override
    protected void onDestroy() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        super.onDestroy();
    }
}
