package org.f0w.k2i.core.comparators;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.html.HtmlEscapers;
import org.f0w.k2i.core.model.entity.Movie;
import org.f0w.k2i.core.utils.NumericToWord;

import java.util.*;

class SmartTitleComparator implements EqualityComparator<Movie> {
    private static final List<StringModifier> modifiers;

    @FunctionalInterface
    interface StringModifier {
        String modify(String string);
    }

    static {
        List<StringModifier> list = new ArrayList<>();

        // Original string
        list.add(s -> s);

        // Original string without commas
        list.add(s -> s.replaceAll(",", ""));

        // Original string without colon
        list.add(s -> s.replaceAll(":", ""));

        // Original string without apostrophes
        list.add(s -> s.replaceAll("/([\'\\\\x{0027}]|&#39;|&#x27;)/u", ""));

        // Original string without special symbols like unicode etc
        list.add(s -> s.replaceAll("/\\\\u([0-9a-z]{4})/", ""));

        // Original string with part before dash symbol
        list.add(s -> {
            String[] parts = s.split("-");

            return parts[0].trim();
        });

        // Original string with part after dash symbol
        list.add(s -> {
            String[] parts = s.split("-");

            return parts.length > 0 ? parts[parts.length - 1].trim() : "";
        });

        // The + Original string
        list.add(s -> "The " + s);

        // Original string with all whitespace characters replaced with plain backspace
        list.add(s -> s.replaceAll("/\\s+/", " "));

        // Original string with replaced foreign characters
        list.add(s -> HtmlEscapers.htmlEscaper()
                .escape(s)
                .replaceAll("~&([a-z]{1,2})(acute|cedil|circ|grave|lig|orn|ring|slash|th|tilde|uml);~i", "$1")
        );

        // Original string with XML symbols replaced with backspace
        list.add(s -> {
            String[] symbols = {"&#xB;", "&#xC;", "&#x1A;", "&#x1B;"};

            for (String symbol : symbols) {
                s = s.replaceAll(symbol, " ");
            }

            return s;
        });

        // Original string with numeric replaced to text representation
        list.add(s -> {
            String[] words = s.split(" ");

            for (int i = 0; i < words.length; i++) {
                try {
                    words[i] = NumericToWord.convert(Integer.parseInt(words[i]));
                } catch (NumberFormatException e) {}
            }

            return Joiner.on(" ").join(words);
        });

        // Modifiers using symbols mix
        String[] symbolsMix = {"&", "and", "et"};
        for (final String firstSymbol : symbolsMix) {
            for (final String secondSymbol : symbolsMix) {
                if (firstSymbol.equals(secondSymbol)) {
                    continue;
                }

                list.add(s -> s.replaceAll(firstSymbol, secondSymbol));
            }
        }

        modifiers = ImmutableList.copyOf(list);
    }

    @Override
    public boolean areEqual(Movie obj1, Movie obj2) {
        boolean result = false;

        for (StringModifier m1 : modifiers) {
            for (StringModifier m2 : modifiers) {
                String m1Title = m1.modify(obj1.getTitle());
                String m2Title = m2.modify(obj2.getTitle());

                if (m1Title.equals(m2Title)) {
                    result = true;
                    break;
                }
            }
        }

        return result;
    }
}