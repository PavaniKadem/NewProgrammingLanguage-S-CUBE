package com.code.Asgmnt1;

import java.util.HashMap;
import java.util.Map;

public class VariableMemory {
	
    private static VariableMemory instance = null;
    private Map<String, ReturnValue> variables;

    private VariableMemory() {
    	variables = new HashMap<String, ReturnValue>();
    }

    public static VariableMemory getInstance() {
        if (instance == null) {
            instance = new VariableMemory();
        }
        return instance;
    }

    public ReturnValue getValue(String symbol) {
    	if(variables.get(symbol)== null) {
    		ReturnValue val = new ReturnValue(0, true);
    		variables.put(symbol, val);
    	};
    	
        return variables.get(symbol);
    }

    public void setValue(String symbol, ReturnValue value) {
    	variables.put(symbol, value);
    }

    public boolean contains(String symbol) {
        return variables.containsKey(symbol);
    }

}
