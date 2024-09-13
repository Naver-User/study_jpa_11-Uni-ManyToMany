package org.zerock.myapp.association;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.FlushModeType;
import javax.persistence.Persistence;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.zerock.myapp.entity.Product1;
import org.zerock.myapp.entity.Shopper1;
import org.zerock.myapp.util.PersistenceUnits;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


//@Log4j2
@Slf4j

@NoArgsConstructor

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class M2MUniDireactionalMappingTests {
	private EntityManagerFactory emf;
	private EntityManager em;
	
	
	@BeforeAll
	void beforeAll() {	// 1회성 전처리
		log.trace("beforeAll() invoked.");
		
		// -- 1 ------------
		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.H2);
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.ORACLE);
//		this.emf = Persistence.createEntityManagerFactory(PersistenceUnits.MYSQL);
		
		Objects.requireNonNull(this.emf);

		// -- 2 ------------
		this.em = this.emf.createEntityManager();
		assertNotNull(this.em);
		
		this.em.setFlushMode(FlushModeType.COMMIT);
	} // beforeAll
	
	@AfterAll
	void afterAll() {	// 1회성 후처리
		log.trace("afterAll() invoked.");
		
		if(this.em != null) this.em.clear();
		
		try { this.em.close(); } catch(Exception _ignored) {}
		try { this.emf.close();} catch(Exception _ignored) {}
	} // afterAll
	
	
//	@Disabled
	@Order(1)
	@Test
//	@RepeatedTest(1)
	@DisplayName("1. prepareData")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void prepareData() {
		log.trace("prepareData() invoked.");
		
		// -- 1 ------------------
		
		IntStream.rangeClosed(1, 7).forEachOrdered(seq -> {
			
			try { 
				this.em.getTransaction().begin();
				
				Product1 transientProduct = new Product1();
				transientProduct.setName("NAME-"+seq);
								
				this.em.persist(transientProduct);
				
				this.em.getTransaction().commit();
			} catch(Exception e) {
				this.em.getTransaction().rollback();
				
				throw e;
			} // try-catch
		});	// .forEachOrdered
		

		// -- 2 ------------------
		Product1 foundProduct1 = this.em.find(Product1.class, 1L);
		Product1 foundProduct2 = this.em.find(Product1.class, 2L);
		Product1 foundProduct3 = this.em.find(Product1.class, 3L);
		Product1 foundProduct4 = this.em.find(Product1.class, 4L);
		Product1 foundProduct5 = this.em.find(Product1.class, 5L);
		Product1 foundProduct6 = this.em.find(Product1.class, 6L);
		Product1 foundProduct7 = this.em.find(Product1.class, 7L);
		
		Objects.requireNonNull(foundProduct1);
		Objects.requireNonNull(foundProduct2);
		Objects.requireNonNull(foundProduct3);
		Objects.requireNonNull(foundProduct4);
		Objects.requireNonNull(foundProduct5);
		Objects.requireNonNull(foundProduct6);
		Objects.requireNonNull(foundProduct7);

		// -- 3 ------------------
		LongStream.of(1L, 2L, 3L).forEachOrdered(seq -> {
			
			try { 
				this.em.getTransaction().begin();
				
				Shopper1 transientShopper = new Shopper1();
				transientShopper.setName("NAME-"+seq);
				
				// 구매한 상품들을 1개이상 주문할 수 있다!!
				// Important: Cannot switch on a value of type long. 
				// Only convertible int values, strings or enum variables are permitted
				switch((int) seq) {
					case 1 -> {
						transientShopper.order(foundProduct1);
						transientShopper.order(foundProduct2);
						transientShopper.order(foundProduct5);
					}
					case 2 -> {
						transientShopper.order(foundProduct1);
						transientShopper.order(foundProduct3);
						transientShopper.order(foundProduct4);
						transientShopper.order(foundProduct5);
					}
					case 3 -> {
						transientShopper.order(foundProduct4);
						transientShopper.order(foundProduct6);
						transientShopper.order(foundProduct7);
					}
				} // switch expression
				
				this.em.persist(transientShopper);
				
				this.em.getTransaction().commit();
			} catch(Exception e) {
				this.em.getTransaction().rollback();
				
				throw e;
			} // try-catch
		});	// .forEachOrdered
		
	} // prepareData
	
	
//	@Disabled
	@Order(2)
	@Test
//	@RepeatedTest(1)
	@DisplayName("2. testM2MUniObjectGraphTraverseFromShopperToProduct")
	@Timeout(value=1L, unit = TimeUnit.MINUTES)
	void testM2MUniObjectGraphTraverseFromShopperToProduct() {
		log.trace("testM2MUniObjectGraphTraverseFromShopperToProduct() invoked.");
		
		// 고객번호 1 ~ 3 중에, 무작위로 고객번호를 선택해서,
		// 고객(Shopper)을 찾고(find), 이 고객의 주무내역을 출력하자!!!
		var shopperId = new Random().nextLong(1L, 3L);
		Shopper1 shopper = this.em.<Shopper1>find(Shopper1.class, shopperId);
		log.info("\t+ shopper: {}", shopper);
		
		assertNotNull(shopper);
		
//		shopper.getProducts().forEach(log::info);	// With @Log4j2
		shopper.getProducts().forEach(p -> log.info(p.toString()));		
	} // testM2MUniObjectGraphTraverseFromShopperToProduct
	
	
	

} // end class
