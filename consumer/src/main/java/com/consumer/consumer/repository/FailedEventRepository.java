package com.consumer.consumer.repository;

import com.consumer.consumer.domain.FailedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FailedEventRepository extends JpaRepository<FailedEvent,Long> {
}
