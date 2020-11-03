package com.lm.common.adapter;


import com.lm.common.R;

/**
 * @author ：LiMing
 * @date ：2020-04-13
 * @desc ：空状态布局全局配置器
 */
public class EmptyViewConfig {

    public static int emptyViewResource = R.layout.item_empty;

    /**
     * 设置全局的空状态布局
     *
     * @param emptyViewResource
     */
    public static void setEmptyViewResource(int emptyViewResource) {
        EmptyViewConfig.emptyViewResource = emptyViewResource;
    }
}
