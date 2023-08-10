package sv.gob.induction.portal.dao;

import java.util.List;

public interface JpaDataTableDao {
    <T> List<T> customFindByFilters(Class<T> tClass, String filters, Object... params) throws IllegalAccessException, InstantiationException;
}
