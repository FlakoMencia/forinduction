package sv.gob.induction.portal.dao;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class JpaDataTableDaoImpl implements JpaDataTableDao {

    @Override
    public <T> List<T> customFindByFilters(Class<T> tClass, String filters, Object... params) throws IllegalAccessException, InstantiationException {
        //TODO: Implement for DataTables Filter Nomenclature
        return Collections.emptyList();
    }
}
