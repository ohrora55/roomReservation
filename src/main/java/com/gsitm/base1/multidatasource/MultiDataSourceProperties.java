package com.gsitm.base1.multidatasource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * 다중의 datasource 정보를 설정하기 위한 Properties
 */
@ConfigurationProperties(MultiDataSourceProperties.PREFIX)
public class MultiDataSourceProperties
implements BeanClassLoaderAware, EnvironmentAware, InitializingBean {

    public static final String PREFIX = "base1.multi-datasource";

    /**
     * 기본 패키지 경로
     */
    private List<String> basePackages = new ArrayList<String>();
    
    /**
     * 기본 DataSource 명
     */
    private String defaultDatasourceName;
    
    private Map<String, DataSourceProperties> datasources = new HashMap<String, DataSourceProperties>();

    public List<String> getBasePackages() {
        return basePackages;
    }

    public void setBasePackages(List<String> basePackages) {
        this.basePackages = basePackages;
    }

    public String getDefaultDatasourceName() {
        return defaultDatasourceName;
    }

    public void setDefaultDatasourceName(String defaultDatasourceName) {
        this.defaultDatasourceName = defaultDatasourceName;
    }

    public Map<String, DataSourceProperties> getDatasources() {
        return datasources;
    }

    public void setDatasources(Map<String, DataSourceProperties> datasources) {
        this.datasources = datasources;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        for (DataSourceProperties dataSource : datasources.values()) {
            dataSource.setBeanClassLoader(classLoader);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        for (DataSourceProperties dataSource : datasources.values()) {
            dataSource.setEnvironment(environment);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (DataSourceProperties dataSource : datasources.values()) {
            dataSource.afterPropertiesSet();
        }
    }
}