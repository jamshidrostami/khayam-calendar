/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.yeksefr.khayam;

import java.util.Locale;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

public abstract class AbstractCalendarActivity extends Activity {
    protected AsyncQueryService mService;

    public synchronized AsyncQueryService getAsyncQueryService() {
        if (mService == null) {
            mService = new AsyncQueryService(this);
        }
        return mService;
    }
    
    @Override
    protected void onCreate(Bundle arg0)
    {
    	super.onCreate(arg0);
    }
    
    @Override
    protected void onResume() {
    	Resources res = getResources();
    	DisplayMetrics dm = res.getDisplayMetrics();
    	android.content.res.Configuration conf = res.getConfiguration();
    	conf.locale = new Locale("fa");
    	res.updateConfiguration(conf, dm);
    	
    	super.onResume();    	
    }
}
