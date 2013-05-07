/*
 * PureArt Archetype. Make any work easier.
 *
 * Copyright (C) 2011  pureart.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.pureart.flexworks.plugin.lifecyclemapping;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.maven.lifecycle.mapping.Lifecycle;

/**
 * 
 * @author pureart.org
 */
public abstract class AbstractActionScriptLifecycleMapping {

    private Map<String, Lifecycle> lifecycleMap;

    public AbstractActionScriptLifecycleMapping() {
        super();
    }

    public Map<String, Lifecycle> getLifecycles() {
        if (lifecycleMap != null)
            return lifecycleMap;

        lifecycleMap = new LinkedHashMap<String, Lifecycle>();
        Lifecycle lifecycle = new Lifecycle();

        lifecycle.setId("default");
        Map<String, String> phases = new LinkedHashMap<String, String>();
        phases.put("process-resources",
                "org.apache.maven.plugins:maven-resources-plugin:resources");
        phases.put("compile", getCompiler());
        phases.put("process-test-resources",
                "org.apache.maven.plugins:maven-resources-plugin:testResources");
        phases.put("test-compile",
                "org.pureart.maven.plugins:flexworks-maven-plugin:testCompile");
        phases.put("test",
                "org.pureart.maven.plugins:flexworks-maven-plugin:testRun");
        if (getPackage() != null)
            phases.put("package", getPackage());
        phases.put("install",
                "org.apache.maven.plugins:maven-install-plugin:install");
        phases.put("deploy",
                "org.apache.maven.plugins:maven-deploy-plugin:deploy");
        lifecycle.setPhases(phases);

        lifecycleMap.put("default", lifecycle);
        return lifecycleMap;
    }

    protected String getPackage() {
        return null;
    }

    public abstract String getCompiler();

    public List<String> getOptionalMojos(String lifecycle) {
        return null;
    }

    public Map<String, String> getPhases(String lifecycle) {
        Lifecycle lifecycleMapping = getLifecycles().get(lifecycle);

        if (lifecycleMapping != null)
            return lifecycleMapping.getPhases();

        return null;
    }
}
