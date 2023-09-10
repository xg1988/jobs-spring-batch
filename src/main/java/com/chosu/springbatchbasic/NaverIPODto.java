package com.chosu.springbatchbasic;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@ToString
@Entity
@Table(name="naver_ipo_info")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NaverIPODto {
	@Id
	public String id;
	public String compName;
	public String marketName;
	public String gongmoPrice;
	public String compCategory;
	public String gongmoComp;
	public String competition;
	public String requestTerm;
	public String listingDate;
	public String gongmoState;
	public String registDate;
	public LocalDateTime registTime;

}
