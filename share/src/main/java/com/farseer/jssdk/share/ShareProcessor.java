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
import android.os.Looper;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import com.farseer.jssdk.JSConstants;
import com.farseer.jssdk.JSProcessor;
import com.farseer.jssdk.Processor;
import com.farseer.jssdk.internal.JSInvoker;
import com.farseer.jssdk.share.event.LoginEvent;
import com.farseer.jssdk.share.event.LogoutEvent;
import com.farseer.jssdk.share.event.ShareTextEvent;
import com.farseer.tool.JsonTool;
import com.farseer.tool.LogTool;
import com.squareup.otto.Subscribe;

import java.util.HashMap;

/**
 * class description
 *
 * @author zhaosc
 * @version 1.0.0
 * @since 16/6/27
 */

@Processor(name = "AccountManager")
public class ShareProcessor extends JSProcessor {

    public ShareProcessor(Context context, JSInvoker jsInvoker, String name) {
        super(context, jsInvoker, name);
    }

    @Subscribe
    public void processShareTextEvent(final ShareTextEvent event) {

        ShareTextEvent.Data data = event.getData();
        final ShareTextEvent.Result result = new ShareTextEvent.Result();

        if (data == null || !data.check()) {
            result.setState(JSConstants.STATUS_FAILURE);
            result.setMsg(getContext().getString(R.string.jssdk_share_failed));
            getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            return;
        }

        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(data.getTitle());
        // text是分享文本，所有平台都需要这个字段
        oks.setText(data.getText());
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl(data.getUrl());
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                LogTool.debug("分享操作成功 i = " + i);
                LogTool.debug("是否为主线程 = " + (Looper.getMainLooper() == Looper.myLooper()));
                result.setState(JSConstants.STATUS_SUCCESS);
                result.setMsg(getContext().getString(R.string.jssdk_share_success));
                getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                LogTool.debug("分享操作失败 i = " + i);
                LogTool.debug("是否为主线程 = " + (Looper.getMainLooper() == Looper.myLooper()));

                if (throwable != null) {
                    LogTool.error(throwable.getMessage());
                }
                result.setState(JSConstants.STATUS_FAILURE);
                result.setMsg(getContext().getString(R.string.jssdk_share_failed));
                getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            }

            @Override
            public void onCancel(Platform platform, int i) {
                LogTool.debug("用户取消分享操作 i = " + i);
                LogTool.debug("是否为主线程 = " + (Looper.getMainLooper() == Looper.myLooper()));
                result.setState(JSConstants.STATUS_FAILURE);
                result.setMsg(getContext().getString(R.string.jssdk_share_cancel));
                getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            }
        });

        // 启动分享GUI
        oks.show(getContext());
    }


    @Subscribe
    public void processLoginEvent(final LoginEvent event) {

        LoginEvent.Data data = event.getData();
        final LoginEvent.Result result = new LoginEvent.Result();

        if (data == null || !data.check()) {
            result.setState(JSConstants.STATUS_FAILURE);
            result.setMsg(getContext().getString(R.string.jssdk_login_failed));
            getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            return;
        }

        String platform = null;
        if (Constants.ACCOUNT_TYPE_WECHAT == data.getAccountType()) {
            platform = Wechat.NAME;
        } else if (Constants.ACCOUNT_TYPE_QQ == data.getAccountType()) {
            platform = QQ.NAME;
        } else if (Constants.ACCOUNT_TYPE_WEIBO == data.getAccountType()) {
            platform = SinaWeibo.NAME;
        }

        //初始化SDK
        ShareSDK.initSDK(getContext());
        Platform plat = ShareSDK.getPlatform(platform);
        if (plat == null) {
            result.setState(JSConstants.STATUS_FAILURE);
            result.setMsg(getContext().getString(R.string.jssdk_login_failed));
            getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            return;
        }

        //如果已经登录,自动登出,再次登录
        if (plat.isAuthValid()) {
            plat.removeAccount(true);
        }

        //使用SSO授权，通过客户单授权
        plat.SSOSetting(false);
        plat.setPlatformActionListener(new PlatformActionListener() {
            public void onComplete(Platform plat, int action, HashMap<String, Object> res) {
                if (action == Platform.ACTION_USER_INFOR) {
                    result.setState(JSConstants.STATUS_SUCCESS);
                    result.setMsg(getContext().getString(R.string.jssdk_login_success));
                    result.setData(JsonTool.toJsonString(res));
                    LogTool.debug("data = " + result.getData());
                    getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
                }
            }

            public void onError(Platform plat, int action, Throwable t) {
                if (action == Platform.ACTION_USER_INFOR) {
                    LogTool.error(t.getMessage());
                    result.setState(JSConstants.STATUS_FAILURE);
                    result.setMsg(getContext().getString(R.string.jssdk_login_failed));
                    getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
                }
            }

            public void onCancel(Platform plat, int action) {
                if (action == Platform.ACTION_USER_INFOR) {
                    result.setState(JSConstants.STATUS_FAILURE);
                    result.setMsg(getContext().getString(R.string.jssdk_login_cancel));
                    getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
                }
            }
        });
        plat.showUser(null);
    }

    @Subscribe
    public void processLogoutEvent(final LogoutEvent event) {
        LogoutEvent.Data data = event.getData();
        final LogoutEvent.Result result = new LogoutEvent.Result();

        if (data == null || !data.check()) {
            result.setState(JSConstants.STATUS_FAILURE);
            result.setMsg(getContext().getString(R.string.jssdk_logout_failed));
            getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
            return;
        }

        String platform = null;
        if (Constants.ACCOUNT_TYPE_WECHAT == data.getAccountType()) {
            platform = Wechat.NAME;
        } else if (Constants.ACCOUNT_TYPE_QQ == data.getAccountType()) {
            platform = QQ.NAME;
        } else if (Constants.ACCOUNT_TYPE_WEIBO == data.getAccountType()) {
            platform = SinaWeibo.NAME;
        }

        //初始化SDK
        ShareSDK.initSDK(getContext());
        Platform plat = ShareSDK.getPlatform(platform);
        if (plat != null && plat.isAuthValid()) {
            plat.removeAccount(true);
            result.setState(JSConstants.STATUS_SUCCESS);
            result.setMsg(getContext().getString(R.string.jssdk_logout_success));
            getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
        }else {
            result.setState(JSConstants.STATUS_FAILURE);
            result.setMsg(getContext().getString(R.string.jssdk_logout_failed));
            getJsInvoker().onJsInvoke(event.getCallback(JsonTool.toJsonString(result)));
        }
    }
}
