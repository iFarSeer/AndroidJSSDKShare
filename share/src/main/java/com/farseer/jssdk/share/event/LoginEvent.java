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

package com.farseer.jssdk.share.event;

import com.farseer.jssdk.JSEvent;
import com.farseer.jssdk.share.Constants;
import com.farseer.tool.JsonTool;
import com.farseer.tool.LogTool;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

/**
 * class description
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 16/6/27
 */
public class LoginEvent extends JSEvent {

    private Data data;

    public LoginEvent(String module, String function) {
        super(module, function);
    }


    public Data getData() {
        return data;
    }

    @Override
    public void processData(String data) {
        log(data);
        this.data = JsonTool.fromJsonString(data, new TypeToken<Data>() {}.getType());
        if (this.data == null || !this.data.check()) {
            LogTool.error(String.format("normalShare 's params of the module are not support", getModule()));
        }


    }

    public static class Data {
        @SerializedName("accountType")
        private int accountType;

        public int getAccountType() {
            return accountType;
        }

        public void setAccountType(int accountType) {
            this.accountType = accountType;
        }

        public boolean check() {
            if (Constants.ACCOUNT_TYPE_WECHAT == accountType
                    || Constants.ACCOUNT_TYPE_QQ == accountType
                    || Constants.ACCOUNT_TYPE_WEIBO == accountType) {
                return true;
            }
            return false;
        }
    }

    public static class Result {
        @SerializedName("state")
        private String state;
        @SerializedName("msg")
        private String msg;
        @SerializedName("data")
        private String data;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }
    }
}
