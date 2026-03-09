package service;

import entity.AppConfig;
import exception.DomainException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import repository.AppConfigRepository;

import java.time.LocalDateTime;

@ApplicationScoped
public class AppConfigService {

    @Inject
    AppConfigRepository repository;

    // ================= READ =================
    public AppConfig getConfigByKey(String key, boolean adminOnly) {
        if (key == null || key.isBlank())
            throw new DomainException("Key tidak boleh kosong", 400);

        if (!key.matches("[a-zA-Z0-9_]+"))
            throw new DomainException("Key mengandung karakter tidak valid", 400);

        AppConfig config = repository.find("configKey", key).firstResult();

        if (config == null)
            throw new DomainException("Config key '" + key + "' not found", 404);

        return config;
    }

    // ================= CREATE =================
    @Transactional
    public AppConfig createConfig(String key, String value, String description) {

        if (repository.find("configKey", key).firstResult() != null)
            throw new DomainException("Config key '" + key + "' sudah ada", 409);

        AppConfig config = new AppConfig();
        config.setConfigKey(key);
        config.setConfigValue(value);
        config.setDescription(description);
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());

        repository.persist(config);

        return config;
    }

    // ================= UPDATE =================
    @Transactional
    public AppConfig updateConfig(String key, String value, String description) {
        AppConfig config = repository.find("configKey", key).firstResult();

        if (config == null)
            throw new DomainException("Config key '" + key + "' not found", 404);

        config.setConfigValue(value);
        config.setDescription(description);
        config.setUpdatedAt(LocalDateTime.now());

        repository.persist(config);

        return config;
    }

    // ================= DELETE =================
    @Transactional
    public void deleteConfig(String key) {
        AppConfig config = repository.find("configKey", key).firstResult();

        if (config == null)
            throw new DomainException("Config key '" + key + "' not found", 404);

        repository.delete(config);
    }
}