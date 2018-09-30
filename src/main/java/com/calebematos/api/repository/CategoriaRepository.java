package com.calebematos.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.calebematos.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long>{

}
