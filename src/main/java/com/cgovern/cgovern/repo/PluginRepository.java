package com.cgovern.cgovern.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.Plugin;

public interface PluginRepository extends JpaRepository<Plugin, Long> {
}
