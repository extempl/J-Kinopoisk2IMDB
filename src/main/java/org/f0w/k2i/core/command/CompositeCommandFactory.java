package org.f0w.k2i.core.command;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.f0w.k2i.core.model.entity.ImportProgress;

import java.util.Arrays;
import java.util.List;

public class CompositeCommandFactory {
    private final Provider<ParseIDCommand> parseIDCommandProvider;
    private final Provider<AddToWatchlistCommand> addToWatchlistCommandProvider;
    private final Provider<SetRatingCommand> setRatingCommandProvider;

    @Inject
    public CompositeCommandFactory(
            Provider<ParseIDCommand> parseIDCommandProvider,
            Provider<AddToWatchlistCommand> addToWatchlistCommandProvider,
            Provider<SetRatingCommand> setRatingCommandProvider
    ) {
        this.parseIDCommandProvider = parseIDCommandProvider;
        this.addToWatchlistCommandProvider = addToWatchlistCommandProvider;
        this.setRatingCommandProvider = setRatingCommandProvider;
    }

    public MovieCommand make(MovieCommand.Type commandType) {
        switch (commandType) {
            case ADD_TO_WATCHLIST:
                return makeAddToWatchListCommand();
            case SET_RATING:
                return makeSetRatingCommand();
            case COMBINED:
                return makeCombinedCommand();
            default:
                throw new IllegalArgumentException("Unexpected comparator type!");
        }
    }

    private MovieCommand makeAddToWatchListCommand() {
        return getCompositeCommand(Arrays.asList(
                parseIDCommandProvider.get(),
                addToWatchlistCommandProvider.get()
        ));
    }

    private MovieCommand makeSetRatingCommand() {
        return getCompositeCommand(Arrays.asList(
                parseIDCommandProvider.get(),
                setRatingCommandProvider.get()
        ));
    }

    private MovieCommand makeCombinedCommand() {
        return getCompositeCommand(Arrays.asList(
                parseIDCommandProvider.get(),
                setRatingCommandProvider.get(),
                addToWatchlistCommandProvider.get()
        ));
    }

    private MovieCommand getCompositeCommand(List<MovieCommand> commands) {
        return ip -> commands.forEach(c -> c.execute(ip));
    }
}
