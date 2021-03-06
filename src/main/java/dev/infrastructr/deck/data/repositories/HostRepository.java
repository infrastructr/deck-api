package dev.infrastructr.deck.data.repositories;

import dev.infrastructr.deck.data.entities.Host;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface HostRepository extends CrudRepository<Host, UUID>, JpaSpecificationExecutor<Host> {
}
