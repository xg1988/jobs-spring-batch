package com.chosu.springbatchbasic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IPORepository extends JpaRepository<IPODto, String> {

    public int deleteByRegistDate(String date);
}
