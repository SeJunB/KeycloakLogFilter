package com.expansia.log.filter.extension.deployment;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import com.expansia.log.filter.extension.runtime.ipAddressFilter;
class LogFilterExtensionProcessor {

    private static final String FEATURE = "log-filters-extension";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    AdditionalBeanBuildItem additionalBeans() { 
        return AdditionalBeanBuildItem.builder().addBeanClass(ipAddressFilter.class).build();
    }
}
