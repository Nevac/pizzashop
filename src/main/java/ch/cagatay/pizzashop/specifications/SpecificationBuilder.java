package ch.cagatay.pizzashop.specifications;

import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationBuilder<T> {

    private final List<SearchCriteria> params;

    public SpecificationBuilder() {
        params = new ArrayList<>();
    }

    public final SpecificationBuilder with(final String orPredicate, final String key, final String operation,
                                                final Object value) {
        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            params.add(new SearchCriteria(orPredicate, key, op, value));
        }
        return this;
    }

    public Specification<T> build() {
        if (params.size() == 0)
            return null;

        Specification<T> result = new GeneralSpecification<>(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new GeneralSpecification<>(params.get(i)))
                    : Specification.where(result).and(new GeneralSpecification<>(params.get(i)));
        }

        return result;
    }

    public final SpecificationBuilder with(GeneralSpecification spec) {
        params.add(spec.getCriteria());
        return this;
    }

    public final SpecificationBuilder with(SearchCriteria criteria) {
        params.add(criteria);
        return this;
    }

    public static<V> Specification<V> buildSpecificationFromString(String search) {
        SpecificationBuilder<V> builder = new SpecificationBuilder<>();;
        String operationSetExper = String.join("|", SearchOperation.SIMPLE_OPERATION_SET);
        Pattern pattern = Pattern
                .compile("(\\p{Punct}?)(\\w+?)(" + operationSetExper + ")(\\w+( +\\w+)*$?),");
        Matcher matcher = pattern.matcher(search + ",");
        while (matcher.find()) {
            builder.with(matcher.group(1), matcher.group(2), matcher.group(3), matcher.group(4));
        }

        return builder.build();
    }
}
