/*
 * Copyright 2012 Google Inc.
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

package barqsoft.footballscores.utils;

import android.util.Log;

import barqsoft.footballscores.BuildConfig;

public class LogUtils {
    private static final int MAX_LOG_TAG_LENGTH = 30;

    private static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH) {
            return str.substring(0, MAX_LOG_TAG_LENGTH - 1);
        }

        return str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class<?> cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void LOGI(final String tag, String message) {
    	if (BuildConfig.DEBUG) {
    		Log.i(tag, message);
    	}
    }

    public static void LOGE(final String tag, String message) {
    	if (BuildConfig.DEBUG) {
    		Log.e(tag, message);
    	}
    }

    private LogUtils() {
    }
}
