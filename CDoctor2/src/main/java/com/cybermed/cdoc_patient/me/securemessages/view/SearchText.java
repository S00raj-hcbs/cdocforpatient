package com.cybermed.cdoc_patient.me.securemessages.view;

import android.text.TextUtils;
import android.widget.Filter;

import com.cybermed.cdoc_patient.me.securemessages.Filterable;
import com.cybermed.cdoc_patient.me.securemessages.adapter.SearchAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class SearchText<T extends Filterable> extends Filter {

    private final List<T> originalList;
    private SearchAdapter countryNameAdapter;
    private boolean showAsLocale;

    public SearchText(SearchAdapter countryNameAdapter,
                      List<T> list, boolean showAsLocale) {
        this.countryNameAdapter = countryNameAdapter;
        this.originalList = new ArrayList<>();
        this.originalList.addAll(list);
        this.showAsLocale=showAsLocale;
    }


    @Override
    protected FilterResults performFiltering(CharSequence searchKey) {
        FilterResults results = new FilterResults();

        if (originalList == null || originalList.isEmpty())
            return null;

        if (!TextUtils.isEmpty(searchKey)) {
            searchKey = searchKey.toString().toUpperCase(Locale.getDefault());

            List<T> searchResult = new ArrayList<>();

            for (int i = 0; i < originalList.size(); i++) {

                String title = String.valueOf(originalList.get(i).getFilter(showAsLocale));

                if (title.length() >= searchKey.length()) {

                    if (title.toUpperCase(Locale.getDefault()).startsWith(searchKey.toString())) {
                        searchResult.add(originalList.get(i));
                    }
                }
            }

            results.count = searchResult.size();
            results.values = searchResult;
        } else {

            results.count = originalList.size();
            results.values = originalList;
        }

        return results;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results != null && results.values instanceof ArrayList)
            countryNameAdapter.setItemList((ArrayList) results.values);
        else
            countryNameAdapter.setItemList(null);
    }

}
