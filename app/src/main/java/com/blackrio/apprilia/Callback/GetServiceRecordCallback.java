package com.blackrio.apprilia.Callback;

import com.blackrio.apprilia.Bean.ServiceRecord;

import java.util.ArrayList;

/**
 * Created by Stefan on 04.06.2015.
 */
public interface GetServiceRecordCallback {

    public abstract void done(ArrayList<ServiceRecord> serviceRecordList);
}
