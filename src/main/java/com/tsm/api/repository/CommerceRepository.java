package com.tsm.api.repository;
import com.tsm.api.entity.Commerce;
import com.tsm.api.entity.CommerceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface CommerceRepository extends JpaRepository<Commerce, UUID>{
    List<Commerce> findByStatus(CommerceStatus status);
}
