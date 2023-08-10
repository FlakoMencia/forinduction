package sv.gob.induction.portal.dao;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


/**
 * 			Repositorio de datos genérico que contiene una implementación de
 *         Spring Data para el manejo de paginacion desde el objeto y filtros con
 *         la API Specification a través de la interfaz JpaSpecificationExecutor<T>.
 *         Incluye la inyeccion del datasource para la creacion de un objeto de tipo SimpleJpaRepository,
 *         el cual es creado en demanda mediante la definicion de su Tipo y Llave <T,ID>
 *         Esta clase es inyectable a cualquier nivel de paquetes manejado por Spring.
 */

@Repository
@Transactional
public class JpaJsGridDaoImpl<T, ID> implements JpaJsGridDao<T, ID> {

	private static final Logger logger = LoggerFactory.getLogger(JpaJsGridDaoImpl.class);
	private static final String P_MSJ_LOG = "No se pudo realizar la transacción... ";

	@PersistenceContext
	EntityManager entityManager;

	private SimpleJpaRepository<T, ID> simpleJpaRepository;

	@Override
	public Page<T> findByFilters(T object, Pageable page) {

		Class clazz = object.getClass();

		simpleJpaRepository = new SimpleJpaRepository<T, ID>(clazz, entityManager);

		return simpleJpaRepository.findAll(specificationFilters(object.getClass(), object), page);
	}

	/**
	 * @param Class<?> clazz, Referencia a la clase que se encuentra siendo tratada
	 * @return Filtros con la specification API para los grids con jsgrid
	 */
	public Specification<T> specificationFilters(final Class<?> clazz, T object) {

		return new Specification<T>() {

			private static final long serialVersionUID = 1L;

			@Override
			@SuppressWarnings("all")
			public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query,
										 CriteriaBuilder cb) {
				Predicate p = cb.conjunction();

				for(Field f : clazz.getDeclaredFields()) {
					try {
						Method m = clazz.getDeclaredMethod("get" + f.getName().substring(0,1).toUpperCase() + f.getName().substring(1));

						if(f.getType() == Integer.class) {
							if(m.invoke(object) != null)
								p = cb.and(p, cb.equal(root.get(f.getName()), m.invoke(object)));
						}

						if(f.getType() == Boolean.class) {
							if(m.invoke(object) != null)
								p = cb.and(p, cb.equal(root.get(f.getName()), m.invoke(object)));
						}

						if(f.getType() == String.class) {
							if(!m.invoke(object).toString().equals(""))
								p = cb.and(p, cb.like(cb.lower(root.get(f.getName())), "%" + m.invoke(object).toString() + "%"));
						}

						if(f.getType() == Date.class || f.getType() == Calendar.class) {
							if(m.invoke(object) != null)
								p = cb.and(p, cb.equal(root.<Date>get(f.getName()), m.invoke(object)));	
						}

						if(f.getType() == BigDecimal.class) {
							if(m.invoke(object) != null)
								p = cb.and(p, cb.equal(root.get(f.getName()), m.invoke(object)));
						}
					} catch (Exception e) {
						logger.error(P_MSJ_LOG, e);
					}
				}

				return p;
			};
		};
	}


	public SimpleJpaRepository<T, ID> getSimpleJpaRepository() {
		return simpleJpaRepository;
	}

	public void setSimpleJpaRepository(SimpleJpaRepository<T, ID> simpleJpaRepository) {
		this.simpleJpaRepository = simpleJpaRepository;
	}

	/**
	 * @param T object
	 * @return Conteo de registros utilizados por JsGrid para la paginacion
	 */
	public Long countByFilters(T object) {
		Class clazz = object.getClass();

		simpleJpaRepository = new SimpleJpaRepository<T, ID>(clazz, entityManager);

		return simpleJpaRepository.count(specificationFilters(object.getClass(), object));
	}

}
