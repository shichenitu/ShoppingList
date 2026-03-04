package dk.it.writefile;

import android.content.Context;

import java.util.HashMap;

public class ItemsDB {
    private static ItemsDB sItemsDB;
    private final HashMap<String, String> itemsMap = new HashMap<String, String>();

    private ItemsDB() {
        fillItemsDB();
    }

    public static ItemsDB get(Context context) {
        if (sItemsDB == null) sItemsDB = new ItemsDB();
        return sItemsDB;
    }

    public String listItems() {
        String r = "";
        for (HashMap.Entry<String, String> item : itemsMap.entrySet())
            r = r + "\nBuy " + item.getKey() + " in: " + item.getValue();
        return r;
    }

    private void fillItemsDB() {
        itemsMap.put("coffee", "Irma");
        itemsMap.put("carrots", "Netto");
        itemsMap.put("milk", "Netto");
        itemsMap.put("bread", "bakery");
        itemsMap.put("butter", "Irma");
    }
}