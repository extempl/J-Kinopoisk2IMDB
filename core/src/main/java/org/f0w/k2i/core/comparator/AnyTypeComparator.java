package org.f0w.k2i.core.comparator;

import lombok.NonNull;
import org.f0w.k2i.core.model.entity.Movie;

final class AnyTypeComparator extends AbstractMovieComparator {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean areEqual(@NonNull Movie movie1, @NonNull Movie movie2) {
        LOG.debug(
                "Comparing types disabled, tried to compare type '{}' with type '{}''",
                movie1.getType(),
                movie2.getType()
        );

        return true;
    }
}
