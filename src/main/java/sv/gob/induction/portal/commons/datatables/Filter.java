package sv.gob.induction.portal.commons.datatables;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Predicate;

interface Filter {

    Predicate createPredicate(From<?, ?> from, CriteriaBuilder criteriaBuilder, String attributeName) throws NoSuchFieldException;

}
