package com.yiqiji.money.modules.myModule.manager;

/**
 * Created by ${huangweishui} on 2017/6/29.
 * address huang.weishui@71dai.com
 */
public interface ThirdLoginMessaePerecenter extends ThirdLoginBaseMessaePerecenter {
    @Override
    void thirdLoginInfo(String openid, String profile_image_url, String screen_name, String share_media_name);

//    @Override
//    void thirdLoginInfo(ThirdLoginInfo thirdLoginInfo);
}
