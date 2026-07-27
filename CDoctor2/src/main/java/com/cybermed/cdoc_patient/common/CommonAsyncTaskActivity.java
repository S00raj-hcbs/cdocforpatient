package com.cybermed.cdoc_patient.common;

import android.os.AsyncTask;
import android.util.Log;

import com.cdfortis.datainterface.soap.VectorCallLog;
import com.cdfortis.datainterface.soap.WebService;
import com.cdfortis.datainterface.soap.model.VectorFamily;

public class CommonAsyncTaskActivity extends BaseActivity{
    public final static String ARRIVED_STATUS = "6";
    public final static String NOT_SEEN_STATUS = "0";
    public final static String SEEN_STATUS = "2";
    public final static String CANCELLED = "1";

    private static AsyncTask getPatientOnlineStatusTask, cancelCall2ProvoderTask, leavingRoomAsGuestTask,
            mUpdateFamilyMemberTask, mGetFamilyListTask, mRemoveFamilyMemberTask, mGetProviderOnlineStatusTask, getPatientCallLogTask
            ;

    public interface GetPatientOnlineStatus {
        void GetPatientOnlineStatusResult(Integer integer);
    }

    public interface SetStatusResult{
        void onLineStatusResult(int result);
    }

    public interface SetLeavinRoomAsGuest{
        void leavingRoomAsGuestResult(int result);
    }

    public interface SetCancelCallToProvider{
        void cancelCallToProviderResult(int cancelCallResult);
    }

    public interface UpdateFamilyMember{
        void  updateFamilyMemberResult(int result);
    }

    public interface GetPatientFamilyMember{
        void  GetPatientFamilyMemberResult(VectorFamily vectorFamily);
    }

    public interface RemoveFamilyMember{
        void  RemoveFamilyMemberResult(Integer integer);
    }

    public interface GetProviderOnlineStatus{
        void  GetProviderOnlineStatusResult(Integer integer);
    }



    public interface GetPatientCallLog{
        void  GetPatientCallLogResult(VectorCallLog vectorCallLog);
    }

    public void getPatientOnlineStatus(String user_id, GetPatientOnlineStatus getPatientOnlineStatus) {
        if(getPatientOnlineStatusTask == null) {
            getPatientOnlineStatusTask = getPatientOnlineStatusAsyncTask(user_id, getPatientOnlineStatus);
        }
    }

    public void getCancelCallToProviderResult(String orgCode,String providerCode,
                                              String roomNum,SetCancelCallToProvider setCancelCallToProvider){
        if (cancelCall2ProvoderTask == null){
            cancelCall2ProvoderTask = cancelCall2ProviderAsyncTask(orgCode,providerCode,roomNum,setCancelCallToProvider);
        }
    }

    public void UpdateFamilyMember(String user_id, String email, String relationship, UpdateFamilyMember updateFamilyMember) {
        if (mUpdateFamilyMemberTask == null) {
            mUpdateFamilyMemberTask = UpdateFamilyMemberAsyncTask(user_id, email, relationship, updateFamilyMember);
        }
    }

    public void GetPatientFamilyMember(String user_id, GetPatientFamilyMember getPatientFamilyMember) {
        if (mGetFamilyListTask == null) {
            mGetFamilyListTask = GetPatientFamilyMemberAsyncTask(user_id, getPatientFamilyMember);
        }
    }

    public void RemoveFamilyMember(String user_id, String email, RemoveFamilyMember removeFamilyMember) {
        if (mRemoveFamilyMemberTask == null) {
            mRemoveFamilyMemberTask = RemoveFamilyMemberAsyncTask(user_id, email, removeFamilyMember);
        }
    }

    public void GetProviderOnlineStatus(String provider_id, String org_code, GetProviderOnlineStatus getProviderOnlineStatus) {
        if (mGetProviderOnlineStatusTask == null) {
            mGetProviderOnlineStatusTask = getProviderOnlineStatusTask(provider_id, org_code, getProviderOnlineStatus);
        }
    }



    public void GetPatientCallLog(String user_id, String date_to_search, GetPatientCallLog getPatientCallLog) {
        if (getPatientCallLogTask == null) {
            getPatientCallLogTask = getPatientCallLogAsyncTask(user_id,  date_to_search, getPatientCallLog);
        }
    }

    private static AsyncTask getPatientCallLogAsyncTask(final String user_id, final String date_to_search, final GetPatientCallLog getPatientCallLog) {
        return new AsyncTask<Void, Void, VectorCallLog>() {
            Exception e;

            @Override
            protected VectorCallLog doInBackground(Void... params) {
                try {
                    return WebService.getInstance().getPatientCallLogAsDataSet(1, 100, user_id, date_to_search);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(VectorCallLog callLog) {
                super.onPostExecute(callLog);
                getPatientCallLogTask = null;
                if (e == null) {
                    getPatientCallLog.GetPatientCallLogResult(callLog);
                }
            }
        }.execute();
    }





    private static AsyncTask getProviderOnlineStatusTask(final String provider_id, final String org_code, final GetProviderOnlineStatus getProviderOnlineStatus) {
        return new AsyncTask<Void, Void, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().getProviderOnlineStatus(provider_id, org_code);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                mGetProviderOnlineStatusTask = null;
                if (e == null) {
                    getProviderOnlineStatus.GetProviderOnlineStatusResult(integer);
                }
            }
        }.execute();
    }

    private static AsyncTask RemoveFamilyMemberAsyncTask(final String user_id, final String email, final RemoveFamilyMember removeFamilyMember) {
        return new AsyncTask<Object, Object, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Object... params) {
                try {
                    return WebService.getInstance().RemoveFamilyMember(user_id, email);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                mRemoveFamilyMemberTask = null;
                if (e == null) {
                    removeFamilyMember.RemoveFamilyMemberResult(integer);
                }
            }
        }.execute();
    }


    private static AsyncTask UpdateFamilyMemberAsyncTask(final String user_id, final String email, final String relationship, final UpdateFamilyMember updateFamilyMember) {
        return new AsyncTask<Object, Object, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Object... params) {
                try {
                    return WebService.getInstance().UpdateFamilyMember(user_id, email, relationship);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                mUpdateFamilyMemberTask = null;
                if (e == null) {
                    updateFamilyMember.updateFamilyMemberResult(integer);
                }
            }
        }.execute();
    }

    private static AsyncTask GetPatientFamilyMemberAsyncTask(final String user_id, final GetPatientFamilyMember getPatientFamilyMember) {
        return new AsyncTask<Object, Object, VectorFamily>() {
            Exception e;

            @Override
            protected VectorFamily doInBackground(Object... params) {
                try {
                    return WebService.getInstance().GetFamilyList(user_id);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(VectorFamily familyVector) {
                mGetFamilyListTask = null;
                if (e == null) {
                    getPatientFamilyMember.GetPatientFamilyMemberResult(familyVector);
                }
            }
        }.execute();
    }

    //取消呼叫
    private static AsyncTask cancelCall2ProviderAsyncTask(final String orgCode, final String providerCode,
                                                   final String roomNum, final SetCancelCallToProvider setCancelCallToProvider){
        return new AsyncTask<Void,Void,Integer>() {
            Exception e;
            @Override
            protected Integer doInBackground(Void... params) {
                try {
                    return WebService.getInstance().CancelCall2Provider_Android(orgCode,providerCode,roomNum);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                cancelCall2ProvoderTask = null;
                if (e == null){
                    setCancelCallToProvider.cancelCallToProviderResult(integer);
                }else {
                    Log.e("commonasynctask",e.getMessage());
                }
            }
        }.execute();
    }

    private static AsyncTask getPatientOnlineStatusAsyncTask(final String user_id, final GetPatientOnlineStatus getPatientOnlineStatus) {
        return new AsyncTask<Object, Object, Integer>() {
            Exception e;

            @Override
            protected Integer doInBackground(Object... params) {
                try {
                    return WebService.getInstance().getPatientOnlineStatus(user_id);
                } catch (Exception e) {
                    this.e = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                getPatientOnlineStatusTask = null;
                if (e == null) {
                    getPatientOnlineStatus.GetPatientOnlineStatusResult(integer);
                }
            }
        }.execute();
    }
}
