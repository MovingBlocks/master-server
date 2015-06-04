/*
 * Copyright 2015 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasology.web.artifactory;

/**
 * Describes a module based on its URL in Artifactory.
 */
public class ModuleInfo implements Comparable<ModuleInfo> {
    private final String uri;
    private final String version;
    private final String artifact;
    private final String module;

    public ModuleInfo(String moduleName, String versionName, String artifactName, String downloadUri) {
        this.module = moduleName;
        this.version = versionName;
        this.artifact = artifactName;
        this.uri = downloadUri;
    }

    public ModuleInfo(String uri) {
        String[] parts = uri.split("/");
        int count = parts.length;
        artifact = parts[count - 1];
        version = parts[count - 2];
        module = parts[count - 3];

        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public String getArtifact() {
        return artifact;
    }

    public String getModule() {
        return module;
    }

    @Override
    public int compareTo(ModuleInfo o) {
        return String.CASE_INSENSITIVE_ORDER.compare(version, o.version);
    }
}