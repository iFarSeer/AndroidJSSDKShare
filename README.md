### JsNativeAndroidShareSDK

JsNativeAndroidShareSDK是基于mob ShareSDK实现与js通信的分享SDK。

### 描述

目前是基于mob ShareSDK2.7.3版本实现。支持`新浪微博`,`微信`,`QQ`,`QQ空间`和`邮件`等分享方法。
 

### 集成JsNativeAndroidShareSDK步骤

#### 准备mob ShareSDK
1. 登录[mob.com](http://www.mob.com/),登录开发者账号,SDK下载 --> ShareSDK For Android --> SDK下载;
2. 选择`新浪微博`,`微信`,`QQ`,`QQ空间`和`邮件`分享平台，获得ShareSDK-Android-2.7.3-*.zip文件；
3. 解压zip文件，命令行执行java -jar QuickIntegrater.jar,填入主项目名称和applicationId，以及选择分享平台生成Sample代码；

#### 主项目配置

1. 拷贝Sample中assets的ShareSDK.xml到主项目的assets中；
2. 配置主module的build.gradle文件;

``` java
compile 'com.farseer.jssdk:core:lastVersion'
```
    
### JsNativeAndroidShareSDK的使用方式


#### 分享
方法格式:
shareText(params, callback)

params说明:
``` json
{
    "title": "标题",
    "text": "内容",
    "url": "http://www.baidu.com",
    "images": [
        ""
    ]
}
```

callback's params说明:
``` json
{
    "state":"",
    "msg":""
}
```

* state: success表示分享成功;failed表示分享失败;
* msg:分享成功,分享失败,分享取消


### 参考链接

* [mob AndroidShareSdk 快速集成指南](http://wiki.mob.com/android_%E5%BF%AB%E9%80%9F%E9%9B%86%E6%88%90%E6%8C%87%E5%8D%97/)

 