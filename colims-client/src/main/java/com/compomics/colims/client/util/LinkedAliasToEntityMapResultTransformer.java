package com.compomics.colims.client.util;

import org.hibernate.transform.BasicTransformerAdapter;

import java.util.LinkedHashMap;


/**
 * Created by Davy Maddelein on 05/08/2015.
 */


public class LinkedAliasToEntityMapResultTransformer extends BasicTransformerAdapter {

    private static LinkedAliasToEntityMapResultTransformer instance = new LinkedAliasToEntityMapResultTransformer();


    public static LinkedAliasToEntityMapResultTransformer INSTANCE(){
        return instance;
    }

    private LinkedAliasToEntityMapResultTransformer(){

    }

    @Override
    public Object transformTuple(Object[] tuple, String[] aliases) {
        LinkedHashMap<String,Object> returnMap = new LinkedHashMap<>();
        for (int i = 0; i < tuple.length; i++){
            returnMap.put(aliases[i],tuple[i]);
        }
        return returnMap;
    }
}
