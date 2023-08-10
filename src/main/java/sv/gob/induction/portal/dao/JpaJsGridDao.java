package sv.gob.induction.portal.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface JpaJsGridDao <T, ID> {
	
	 public Page<T> findByFilters(T object, Pageable page);
	 
	 public Long countByFilters(T object);

}
