package com.erp.microerp.repository;

import com.erp.microerp.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {

    List<Produto> findByEstoqueLessThanEqual(Integer estoqueMinimo);
}