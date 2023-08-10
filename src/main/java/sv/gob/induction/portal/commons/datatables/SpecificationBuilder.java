package sv.gob.induction.portal.commons.datatables;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.FetchParent;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.lang.NonNull;

import lombok.SneakyThrows;
import sv.gob.induction.portal.commons.datatables.mapping.DataTablesInput;

public class SpecificationBuilder<T> extends AbstractPredicateBuilder<Specification<T>> {

    public SpecificationBuilder(DataTablesInput input) {
        super(input);
    }

    @Override
    public Specification<T> build() {
        return new DataTablesSpecification<>();
    }

    private class DataTablesSpecification<S> implements Specification<S> {
        private transient List<Predicate> columnPredicates = new ArrayList<>();
        private transient List<Predicate> globalPredicates = new ArrayList<>();

        @SneakyThrows
        @Override
        public Predicate toPredicate(@NonNull Root<S> root, @NonNull CriteriaQuery<?> query, @NonNull CriteriaBuilder criteriaBuilder) {
            initPredicatesRecursively(tree, root, root, criteriaBuilder);

            boolean isCountQuery = query.getResultType() == Long.class;
            if (isCountQuery) {
                root.getFetches().clear();
            }

            return createFinalPredicate(criteriaBuilder);
        }

        private void initPredicatesRecursively(Node<Filter> node, From<S, S> from, FetchParent<S, S> fetch, CriteriaBuilder criteriaBuilder) throws NoSuchFieldException {
            if (node.isLeaf()) {
                boolean hasColumnFilter = node.getData() != null;
                if (hasColumnFilter) {
                    Filter columnFilter = node.getData();
                    columnPredicates.add(columnFilter.createPredicate(from, criteriaBuilder, node.getName()));
                } else if (hasGlobalFilter) {
                    Filter globalFilter = tree.getData();
                    globalPredicates.add(globalFilter.createPredicate(from, criteriaBuilder, node.getName()));
                }
            }
            for (Node<Filter> child : node.getChildren()) {

                if (child.isLeaf()) {
                    initPredicatesRecursively(child, from, fetch, criteriaBuilder);
                } else {
                    Fetch<S, S> childFetch = fetch.fetch(child.getName(), JoinType.LEFT);
                    initPredicatesRecursively(child, (From<S, S>) childFetch, childFetch, criteriaBuilder);
                }
            }
        }

        private Predicate createFinalPredicate(CriteriaBuilder criteriaBuilder) {
            List<Predicate> allPredicates = new ArrayList<>(columnPredicates);

            if (!globalPredicates.isEmpty()) {
                allPredicates.add(criteriaBuilder.or(globalPredicates.toArray(new Predicate[0])));
            }

            return allPredicates.isEmpty() ? criteriaBuilder.conjunction() : criteriaBuilder.and(allPredicates.toArray(new Predicate[0]));
        }
    }

}
