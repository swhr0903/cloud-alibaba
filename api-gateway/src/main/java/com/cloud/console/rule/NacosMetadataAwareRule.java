package com.cloud.console.rule;

import com.cloud.console.predicate.NacosMetadataAwarePredicate;

public class NacosMetadataAwareRule extends DiscoveryEnabledRule {

    public NacosMetadataAwareRule() {
        super(new NacosMetadataAwarePredicate());
    }
}
