/*
 *    Copyright 2016 ifarseer
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.farseer.jssdk.share;

import android.content.Context;
import android.webkit.JavascriptInterface;
import com.farseer.jssdk.JSModule;
import com.farseer.jssdk.Module;
import com.farseer.jssdk.share.event.LoginEvent;
import com.farseer.jssdk.share.event.LogoutEvent;
import com.farseer.jssdk.share.event.ShareTextEvent;
import com.farseer.jssdk.internal.Dispatcher;

/**
 * 通用的javascript功能模块
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 16/6/19
 */

@Module(name = "AccountManager", namespace = "cn.com.itomix")
public class ShareJsModule extends JSModule {

    public ShareJsModule(Context context, Dispatcher dispatcher, String moduleName, String namespace) {
        super(context, dispatcher, moduleName, namespace);
    }

    public void init() {
        for (String function : Constants.FUNCTION_LIST) {
            addJSFunction(function);
        }
    }

    @JavascriptInterface
    public void shareText(String json) {
        ShareTextEvent event = new ShareTextEvent(getName(), Constants.SHARE_TEXT);
        event.processData(json);
        postEvent(event);
    }

    @JavascriptInterface
    public void login(String json) {
        LoginEvent event = new LoginEvent(getName(), Constants.LOGIN);
        event.processData(json);
        postEvent(event);
    }

    @JavascriptInterface
    public void logout(String json) {
        LogoutEvent event = new LogoutEvent(getName(), Constants.LOGOUT);
        event.processData(json);
        postEvent(event);
    }
}
