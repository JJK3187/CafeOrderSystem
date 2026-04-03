package com.cafeorder.domain.menu.repository;

import com.cafeorder.domain.menu.entity.Menu;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface MenuRepository extends JpaRepository<Menu, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT m FROM Menu m WHERE m.id IN :menuIds ORDER BY m.id")
	List<Menu> findAllByIdInWithLock(@Param("menuIds") Collection<Long> menuIds);
}
