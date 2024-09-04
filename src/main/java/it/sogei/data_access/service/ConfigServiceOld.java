package it.sogei.data_access.service;


import it.sogei.data_access.repositories.ConfigRepository;
import it.sogei.structure.data.Config;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;

import java.util.List;


@Stateless
public class ConfigServiceOld {

    @Inject
    private ConfigRepository configRepository;

    public void createConfig(Config config) {
        configRepository.save(config);
    }

    public Config getConfig(Long id) {
        return configRepository.find(id);
    }

    public void updateConfig(Config config) {
        configRepository.update(config);
    }

    public void deleteConfig(Long id) {
        configRepository.delete(id);
    }

    public List<Config> getAllConfigs () {
        return configRepository.findAll();
    }
}
