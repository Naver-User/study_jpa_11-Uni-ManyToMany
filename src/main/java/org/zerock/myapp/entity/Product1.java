package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecyleListener;

import lombok.Data;


@Data

@EntityListeners(CommonEntityLifecyleListener.class)

@Entity(name = "Product1")
@Table(name="product1")
public class Product1 implements Serializable {
	@Serial private static final long serialVersionUID = 1L;

	// 1. Set PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "product_id")
	private Long id;				// PK
	
	
	// 2. 일반속성들
	@Basic(optional = false)		// Not Null Constraint
	private String name;
	
	
	// 3. 연관관계 선언
	
	// ====================================================
	// N (T1, Shopper1) : M (T2, Product1), UNI (T1 -> T2)
	// Using Cross Table With Composite Key (PK: T1 FK + T2 FK)
	// ====================================================
	
	// Nothing to do.
	
	
   
} // end class
