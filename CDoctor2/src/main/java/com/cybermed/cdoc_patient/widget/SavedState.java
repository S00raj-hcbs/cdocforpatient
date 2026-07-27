package com.cybermed.cdoc_patient.widget;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.View;

class SavedState extends View.BaseSavedState {
    public SavedState(Parcelable superState) {
        super(superState);
    }


    public SparseArray childrenStates = null;

    public void writeToParcel(Parcel out, int flags) {
        super.writeToParcel(out, flags);
        if (childrenStates != null)
            out.writeSparseArray(childrenStates);

    }
}
