package com.anthem.emep.dckr.microsvc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.anthem.emep.dckr.microsvc.model.Discount;

public interface IDiscountRepository extends JpaRepository<Discount, Integer> {

}
