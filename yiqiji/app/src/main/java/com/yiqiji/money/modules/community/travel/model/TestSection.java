package com.yiqiji.money.modules.community.travel.model;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * Created by huangweishui on 2017/10/17.
 */

public class TestSection extends SectionEntity<DecorateHomeEntity> {
    public TestSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public TestSection(DecorateHomeEntity o) {
        super(o);
    }
}
