package com.example.apapbackend.Info;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InfoRepository extends JpaRepository<Info, Long>, JpaSpecificationExecutor<Info> {

}
