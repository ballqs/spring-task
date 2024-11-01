package org.sparta.springtask.domain.store.repository;

import org.sparta.springtask.domain.store.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
