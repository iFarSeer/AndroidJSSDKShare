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

import android.text.TextUtils;
import com.farseer.jssdk.JSEvent;
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
public class ShareTextEvent extends JSEvent {

    private Data data;

    public ShareTextEvent(String module, String function) {
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
        @SerializedName("title")
        private String title;
        @SerializedName("text")
        private String text;
        @SerializedName("url")
        private String url;
        @SerializedName("images")
        private String images;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getImages() {
            return images;
        }

        public void setImages(String images) {
            this.images = images;
        }

        public boolean check() {
            if (!TextUtils.isEmpty(title)
                    && !TextUtils.isEmpty(text)) {
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
    }
}
