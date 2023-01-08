package com.intellij.idea.plugin.hybris.settings;

import com.intellij.idea.plugin.hybris.common.HybrisConstants;
import com.intellij.idea.plugin.hybris.common.services.CommonIdeaService;
import com.intellij.idea.plugin.hybris.settings.HybrisRemoteConnectionSettings.Type;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.intellij.idea.plugin.hybris.common.HybrisConstants.STORAGE_HYBRIS_DEVELOPER_SPECIFIC_PROJECT_SETTINGS;

@State(name = "HybrisDeveloperSpecificProjectSettings", storages = {@Storage(STORAGE_HYBRIS_DEVELOPER_SPECIFIC_PROJECT_SETTINGS)})
public class HybrisDeveloperSpecificProjectSettingsComponent implements PersistentStateComponent<HybrisDeveloperSpecificProjectSettings> {

    private final MessageBus myMessageBus;
    private final HybrisDeveloperSpecificProjectSettings state = new HybrisDeveloperSpecificProjectSettings();


    public HybrisDeveloperSpecificProjectSettingsComponent(final Project project) {
        myMessageBus = project.getMessageBus();
    }

    public static HybrisDeveloperSpecificProjectSettingsComponent getInstance(@NotNull final Project project) {
        return project.getService(HybrisDeveloperSpecificProjectSettingsComponent.class);
    }

    @Nullable
    @Override
    public HybrisDeveloperSpecificProjectSettings getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull final HybrisDeveloperSpecificProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this.state);
    }

    public List<HybrisRemoteConnectionSettings> getHacRemoteConnectionSettings() {
        return getHybrisRemoteConnectionSettings(Type.Hybris);
    }

    public List<HybrisRemoteConnectionSettings> getSolrRemoteConnectionSettings() {
        return getHybrisRemoteConnectionSettings(Type.SOLR);
    }

    private List<HybrisRemoteConnectionSettings> getHybrisRemoteConnectionSettings(
        final Type type
    ) {
        if (getState() == null) return Collections.emptyList();
        return getState().getRemoteConnectionSettingsList().stream()
                         .filter(it -> it.getType() == type)
                         .collect(Collectors.toList());
    }

    public HybrisRemoteConnectionSettings getActiveHacRemoteConnectionSettings(final Project project) {
        final HybrisDeveloperSpecificProjectSettings state = getState();
        if (state == null) {
            return getDefaultHacRemoteConnectionSettings(project);
        }
        final var instances = getHacRemoteConnectionSettings();
        if (instances.isEmpty()) {
            return getDefaultHacRemoteConnectionSettings(project);
        }
        final String id = state.getActiveRemoteConnectionID();

        return instances.stream()
                         .filter(e -> Objects.equals(id, e.getUuid()))
                         .findFirst()
                         .orElseGet(() -> instances.get(0));
    }

    public HybrisRemoteConnectionSettings getActiveSolrRemoteConnectionSettings(final Project project) {
        final HybrisDeveloperSpecificProjectSettings state = getState();
        if (state == null) {
            return getDefaultSolrRemoteConnectionSettings(project);
        }
        final var instances = getSolrRemoteConnectionSettings();
        if (instances.isEmpty()) {
            return getDefaultSolrRemoteConnectionSettings(project);
        }
        final String id = state.getActiveSolrConnectionID();

        return instances.stream()
                       .filter(e -> Objects.equals(id, e.getUuid()))
                       .findFirst()
                       .orElseGet(() -> instances.get(0));
    }

    @NotNull
    public HybrisRemoteConnectionSettings getDefaultHacRemoteConnectionSettings(final Project project) {
        final HybrisRemoteConnectionSettings item = new HybrisRemoteConnectionSettings();
        item.setType(HybrisRemoteConnectionSettings.Type.Hybris);
        item.setUuid(UUID.randomUUID().toString());
        item.setHostIP(HybrisConstants.DEFAULT_HOST_URL);
        item.setPort(HybrisConstants.DEFAULT_TOMCAT_SSL_PORT);
        item.setHacLogin("admin");
        item.setHacPassword("nimda");
        item.setSsl(true);
        item.setGeneratedURL(CommonIdeaService.getInstance().getHostHacUrl(project, item));
        return item;
    }

    @NotNull
    public HybrisRemoteConnectionSettings getDefaultSolrRemoteConnectionSettings(final Project project) {
        final HybrisRemoteConnectionSettings item = new HybrisRemoteConnectionSettings();
        item.setType(HybrisRemoteConnectionSettings.Type.SOLR);
        item.setUuid(UUID.randomUUID().toString());
        item.setHostIP(HybrisConstants.DEFAULT_HOST_URL);
        item.setPort(HybrisConstants.DEFAULT_SOLR_TOMCAT_SSL_PORT);
        item.setSolrWebroot("solr");
        item.setAdminLogin("solradmin");
        item.setAdminPassword("admin123");
        item.setSsl(true);
        item.setGeneratedURL(CommonIdeaService.getInstance().getSolrUrl(project, item));
        return item;
    }

    public void setActiveHacRemoteConnectionSettings(final HybrisRemoteConnectionSettings settings) {
        if (settings == null || getState() == null) {
            return;
        }
        getState().setActiveRemoteConnectionID(settings.getUuid());
        myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).hacActiveConnectionSettingsChanged();
    }

    public void setActiveSolrRemoteConnectionSettings(final HybrisRemoteConnectionSettings settings) {
        if (settings == null || getState() == null) {
            return;
        }
        getState().setActiveSolrConnectionID(settings.getUuid());
        myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).solrActiveConnectionSettingsChanged();
    }

    public void saveRemoteConnectionSettingsList(
        final Type type,
        final List<HybrisRemoteConnectionSettings> instances
    ) {
        if (getState() == null) {
            return;
        }
        final var newInstances = new ArrayList<>(instances);
        getState().getRemoteConnectionSettingsList().stream()
                  .filter(it -> it.getType() != type)
                  .forEach(newInstances::add);
        getState().setRemoteConnectionSettingsList(newInstances);
        switch (type) {
            case Hybris -> myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).hacConnectionSettingsChanged();
            case SOLR -> myMessageBus.syncPublisher(HybrisDeveloperSpecificProjectSettingsListener.TOPIC).solrConnectionSettingsChanged();
        }
    }
}
