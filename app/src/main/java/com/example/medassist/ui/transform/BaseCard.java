package com.example.medassist.ui.transform;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.navigation.Navigation;

public abstract class BaseCard{
    protected final Context context;
    protected final ViewGroup container;
    protected View cardView;

    public BaseCard(Context context, ViewGroup container){
        this.context = context;
        this.container = container;
    }

    // Defines template
    public final void render(){
        cardView = createView();
        bindData(cardView);
        setupActions();
        container.addView(cardView);
    }

    protected abstract View createView(); //For subclasses to implement
    protected abstract void bindData(View cardView); //For subclasses to implement
    protected abstract void setupActions(); //For click handlers etc.

    public View getView(){
        return cardView;
    }

    protected void navigateTo(@IdRes int destinationId) {
        try {
            Navigation.findNavController(cardView).navigate(destinationId);
        } catch (Exception e) {
            Log.e("Navigation", "Failed to navigate", e);
        }
    }

}
