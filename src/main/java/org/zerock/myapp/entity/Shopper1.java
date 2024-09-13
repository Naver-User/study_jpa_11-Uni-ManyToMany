package org.zerock.myapp.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.zerock.myapp.listener.CommonEntityLifecyleListener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j

@Data

@EntityListeners(CommonEntityLifecyleListener.class)

@Entity(name = "Shopper1")
@Table(name="shopper1")
public class Shopper1 implements Serializable {
	@Serial private static final long serialVersionUID = 1L;

	// 1. Set PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "shopper_id")
	private Long id;				// PK
	
	
	// 2. 일반속성들
	@Basic(optional = false)		// Not Null Constraint
	private String name;
	
	
	// 3. 연관관계 선언
	
	// ====================================================
	// N (T1, Shopper1) : M (T2, Product1), UNI (T1 -> T2)
	// Using Cross Table With Composite Key (PK: T1 FK + T2 FK)
	// ====================================================

	// ----------------------
	// (1) @ManyToMany 어노테이션 선언: 다 대 다 관계임을 표시하는데 사용
	// ----------------------
	
	@ManyToMany(targetEntity = Product1.class)

	
	// ----------------------
	// (2) @JoinTable 어노테이션 선언: 다 대 다 관계에서, 교차릴레이션(= Cross Table) 선언
	// ----------------------
	
	/**
	 * -----------------------------------------------------------
	 * 1. 주석을 다 뺀, 교차테이블과 그 복합키(PK = T1 FK + T2 FK) 선언
	 * -----------------------------------------------------------
	*/
	 
	@JoinTable(
		name = "SHOPPER1_PRODUCT1",
		
		joinColumns = 
			@JoinColumn(
				name = "shopper_id",
				referencedColumnName = "shopper_id",
				nullable = true),
			
		inverseJoinColumns = 
			@JoinColumn(
				name = "product_id",
				referencedColumnName = "product_id",
				nullable = true)
	)
	
	/**
	 * -----------------------------------------------------------
	 * 2. 주석을 달아놓은, 교차테이블과 그 복합키(PK = T1 FK + T2 FK) 선언
	 * -----------------------------------------------------------
	 
		@JoinTable(
			// (1) Cross Table(교차 릴레이션)의 이름 설정
			//     Default: T1's name + '_' + T2's name
			name = "SHOPPER1_PRODUCT1",
			
			// (2) 다(T1) 대 다(T2) 관계에서, T1에 대한 FK를 선언하는데 사용
			joinColumns = // T1 의 FK 컬럼정보 선언
			
				// @JoinColumn 어노테이션의 목적: FK 컬럼을 정의하는데 사용 (***)	
				@JoinColumn(
					// 가. 생성될 FK 컬럼명 지정
					name = "shopper_id",
					
					// 나. 생성될 FK 컬럼이 참조하는 부모테이블의 PK 컬럼명 지정
					referencedColumnName = "shopper_id",
					
					// 다. 생성될 FK 컬럼이 NULL 값을 허용할지 여부:
					//     비지니스 로직과 관계타입(주문하다)의 성격에 맞게 허용여부 결정
					nullable = true),
	
			// (2) 다(T1) 대 다(T2) 관계에서, T2에 대한 FK를 선언하는데 사용
			inverseJoinColumns = // T2 의 FK 컬럼정보 선언	
			
				// @JoinColumn 어노테이션의 목적: FK 컬럼을 정의하는데 사용 (***)
				@JoinColumn( 
					// 가. 생성될 FK 컬럼명 지정
					name = "product_id", 
					
					// 나. 생성될 FK 컬럼이 참조하는 부모테이블의 PK 컬럼명 지정
					referencedColumnName = "product_id",
							
					// 다. 생성될 FK 컬럼이 NULL 값을 허용할지 여부:
					//     비지니스 로직과 관계타입(주문하다)의 성격에 맞게 허용여부 결정
					nullable = true)
		) // @JoinTable
	*/
	
	// 다대다 관계에서, T2에 대한 Children 역할을 할 속성 선언 필요
	private List<Product1> products = new ArrayList<>();	
	
	
	// 상품(product)을 새로이 주문할 때마다, 주문한 상품정보를
	// List<Product1> 컬렉션에, 연관관계에 맞게 넣어줄 Setter 메소드
	public void order(Product1 newProduct) {
		log.trace("order({}) invoked.", newProduct);
		
		if(Objects.nonNull(newProduct)) {
			this.getProducts().add(newProduct);
		} else return;
	} // order
	
	
} // end class


