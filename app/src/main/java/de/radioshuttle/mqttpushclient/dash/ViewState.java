/*
 *	$Id$
 *	This is an unpublished work copyright (c) 2019 HELIOS Software GmbH
 *	30827 Garbsen, Germany.
 */

package de.radioshuttle.mqttpushclient.dash;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.radioshuttle.utils.Utils;

import static de.radioshuttle.mqttpushclient.AccountListActivity.PREFS_NAME;

public class ViewState {

    public static ViewState getInstance(Application app) {
        if (mViewState == null) {
            mViewState = new ViewState(app);
        }
        return mViewState;
    }

    public int getLastState(String account) {
        ViewInfo vi = getViewInfo(account);
        return vi == null ? VIEW_MESSAGES : vi.lastView;
    }

    public int getLastZoomLevel(String account) {
        ViewInfo vi = getViewInfo(account);
        return vi == null ? 1 : vi.lastZoomLevel;
    }

    protected ViewInfo getViewInfo(String account) {
        ViewInfo vi;
        if (account != null && mState.containsKey(account)) {
            vi = mState.get(account);
        } else {
            vi = null;
        }
        return vi;
    }

    public void setLastState(String account, int newState) {
        Log.d(VIEW_STATE, "set state: " + account + " " + newState);
        if (account != null) {
            ViewInfo vi = getViewInfo(account);
            if (vi == null) {
                vi = new ViewInfo();
                vi.lastView = 0;
                vi.lastZoomLevel = 0;
            }
            if (vi.lastView != newState) {
                vi.lastView = newState;
                mState.put(account, vi);
                writeState();
            }
        }
    }

    public void setLastZoomLevel(String account, int newZoomLevel) {
        if (account != null) {
            ViewInfo vi = getViewInfo(account);
            if (vi == null) {
                vi = new ViewInfo();
                vi.lastView = 0;
                vi.lastZoomLevel = 0;
            }
            if (vi.lastZoomLevel != newZoomLevel) {
                vi.lastZoomLevel = newZoomLevel;
                mState.put(account, vi);
                writeState();
            }
        }
    }

    public void removeAccount(String account) {
        if (mState.containsKey(account)) {
            mState.remove(account);
            writeState();
        }
    }

    private ViewState(Application app) {
        mApp = app;
        readStates();
    }

    private void readStates() {
        mState = new HashMap<>();
        SharedPreferences settings = mApp.getSharedPreferences(DASHBOARD_PREFS, Activity.MODE_PRIVATE);
        String json = settings.getString(VIEW_STATE, null);
        if (!Utils.isEmpty(json)) {
            try {
                JSONObject vs = new JSONObject(json);
                Iterator<String> it = vs.keys();
                JSONObject viewInfo;
                String account;
                while(it.hasNext()) {
                    account = it.next();
                    vs.opt(account);
                    viewInfo = vs.optJSONObject(account);
                    if (viewInfo != null) {
                        ViewInfo vi = new ViewInfo();
                        vi.lastView = viewInfo.optInt("last_view", VIEW_MESSAGES);
                        vi.lastZoomLevel = viewInfo.optInt("last_zoom_level", 1);
                        mState.put(account, vi);
                    }
                }
            } catch (JSONException e) {
                Log.e(VIEW_STATE, "Parsing view states failed." , e);
            }
        }
    }

    private void writeState() {
        JSONObject vs = new JSONObject();
        JSONObject val;
        String out = null;
        Iterator<Map.Entry<String, ViewInfo>> it;
        Map.Entry<String, ViewInfo> entry;
        for(it = mState.entrySet().iterator(); it.hasNext();) {
            entry = it.next();
            try {
                val = new JSONObject();
                val.put("last_view", entry.getValue().lastView);
                val.put("last_zoom_level", entry.getValue().lastZoomLevel);
                vs.put(entry.getKey(), val);
            } catch (JSONException e) {
                Log.e(VIEW_STATE, "Setting view states failed." , e);
            }
        }
        if (vs.length() > 0) {
            out = vs.toString();
        }
        SharedPreferences settings = mApp.getSharedPreferences(DASHBOARD_PREFS, Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = settings.edit();
        editor.putString(VIEW_STATE, out);
        editor.commit();

    }

    private HashMap<String, ViewInfo> mState;
    private Application mApp;
    static ViewState mViewState;

    private static class ViewInfo {
        int lastView;
        int lastZoomLevel;
    }

    public final static int VIEW_MESSAGES = 1;
    public final static int VIEW_DASHBOARD = 2;
    public final static String VIEW_STATE = "VIEW_STATE";

    public final static String DASHBOARD_PREFS = "dashboard_prefs";
}