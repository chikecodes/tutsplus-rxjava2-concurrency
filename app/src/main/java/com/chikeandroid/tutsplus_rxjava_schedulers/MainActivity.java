package com.chikeandroid.tutsplus_rxjava_schedulers;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String[] STATES = { "Lagos", "Abuja", "Abia",
            "Edo", "Enugu", "Niger", "Anambra"};

    private Disposable mDisposable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Observable<String> observable = Observable.create(dataSource())
                .subscribeOn(Schedulers.newThread())
                .subscribeOn(Schedulers.io()) // has no effect
                .doOnNext(s -> {
                    SaveToCache(s); // executed on Schedulers.computation()
                })
                .doOnComplete(() -> Log.d("MainActivity", "Complete"));

        mDisposable = observable.subscribe(s -> {
            Log.d("MainActivity", "received " + s + " on thread " + Thread.currentThread().getName());
        });
    }

    private void SaveToCache(String s) {
        Log.d("MainActivity", "thread name is " + Thread.currentThread().getName());
    }

    private ObservableOnSubscribe<String> dataSource() {
       return(emitter -> {
            for(String state : STATES) {
                emitter.onNext(state);
                Log.d("MainActivity", "emitting " + state + " on thread " + Thread.currentThread().getName());
                Thread.sleep(600);
            }
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
