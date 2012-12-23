package com.mb.intervention.context;

public abstract class ContextProvider {

    protected Context context;

    public ContextProvider() {
        
        context=new Context();
    }

    
    
    public abstract void build();

    public Context getContext() {
        return context;
    }

    
}
