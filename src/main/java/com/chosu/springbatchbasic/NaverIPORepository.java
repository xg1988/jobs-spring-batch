package com.chosu.springbatchbasic;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverIPORepository extends JpaRepository<NaverIPODto, String> {

    public int deleteByRegistDate(String date);
}
