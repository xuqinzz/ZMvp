package com.hubert.xu.zmvp.module.contract;

import com.hubert.xu.zmvp.base.BaseContract;

/**
 * Author: Hubert.Xu
 * Date  : 2017/8/17
 * Desc  :
 */

public interface OriginalContract extends BaseContract {

    interface View<DiscussBean> extends BaseView {
        void setData(DiscussBean data, boolean isRefresh);
    }


    interface Presenter extends BasePresenter {
        void getData(int i);
    }
}
