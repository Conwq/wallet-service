package org.example.walletservice.repository.rep.impl;

import org.example.walletservice.model.ent.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoggerRep extends JpaRepository<LogEntity, Integer> {

}
