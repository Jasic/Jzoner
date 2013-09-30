package org.jasic.qzoner.core.handler.Filter;
import org.jasic.qzoner.core.entity.IData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * User: Jasic
 * Date: 13-9-29
 */
public class DefaultFilterLine implements FilterLine {
    /**
     * The logger for this class
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultFilterLine.class);

    /**
     *
     */
    private String name;

    /**
     *
     */
    private final IData data;

    /**
     * The chain head
     */
    private final EntryImpl head;

    /**
     * The chain tail
     */
    private final EntryImpl tail;

    /**
     * Create a new default chain, associated with a session. It will only contain a
     * HeadFilter and a TailFilter.
     *
     * @param data The session associated with the created filter chain
     */
    public DefaultFilterLine(IData data) {
        if (data == null) {
            throw new IllegalArgumentException("data");
        }

        this.data = data;

        head = new EntryImpl(null, null, "head", new Filter() {
            @Override
            public String getName() {
                return name + "-head";
            }

            @Override
            public void filter(IData data) {
            }
        });

        tail = new EntryImpl(head, null, "tail", new Filter() {
            @Override
            public String getName() {
                return name + "-tail";
            }

            @Override
            public void filter(IData data) {
            }
        });

        head.nextEntry = tail;
    }

    @Override
    public void fire(IData data) {
        fire0(this.head, data);
    }

    private void fire0(Entry entry, IData data) {
        entry.getFilter().filter(data);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void addBefore(String name, Filter filter) {
        EntryImpl currentIndex = head.nextEntry;
        EntryImpl newOne = new EntryImpl(head, currentIndex, name, filter);
        currentIndex.preEntry = newOne;
    }

    @Override
    public void addAfter(String name, Filter filter) {
        EntryImpl currentIndex = tail.nextEntry;
        EntryImpl newOne = new EntryImpl(currentIndex, tail, name, filter);
        currentIndex.preEntry = newOne;
    }

    private class EntryImpl implements Entry {

        private String name;

        private Filter filter;

        private EntryImpl preEntry;

        private EntryImpl nextEntry;

        public EntryImpl(EntryImpl prevEntry, EntryImpl nextEntry, String name, Filter filter) {

            this.preEntry = prevEntry;
            this.nextEntry = nextEntry;

            this.name = name;
            this.filter = filter;
        }

        @Override
        public String getName() {
            return this.name;
        }

        @Override
        public Filter getFilter() {
            return this.filter;
        }

        @Override
        public void addBefore(String name, Filter filter) {
            DefaultFilterLine.this.addBefore(name, filter);
        }

        @Override
        public void addAfter(String name, Filter filter) {
            DefaultFilterLine.this.addAfter(name, filter);
        }
    }
}
