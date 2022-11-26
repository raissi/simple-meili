package io.github.meilisearch.client.querybuilder;

public enum SortOrder {
    ASC {
        @Override
        public String toString() {
            return "asc";
        }
    },

    DESC {
        @Override
        public String toString() {
            return "desc";
        }
    }
}
