package com.cgovern.cgovern.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cgovern.cgovern.domain.RemoteServer;

public interface RemoteServerRepository extends JpaRepository<RemoteServer, Long> {
}
